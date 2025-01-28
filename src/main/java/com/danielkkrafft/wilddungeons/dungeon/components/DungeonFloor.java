package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.registry.WDDimensions;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.util.IgnoreSerialization;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.danielkkrafft.wilddungeons.world.dimension.tools.InfiniverseAPI;
import com.danielkkrafft.wilddungeons.world.dimension.tools.QuietPacketDistributors;
import com.danielkkrafft.wilddungeons.world.dimension.tools.ReflectionBuddy;
import com.danielkkrafft.wilddungeons.world.dimension.tools.UpdateDimensionsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.joml.Vector2i;

import java.util.*;
import java.util.concurrent.Executor;

public class DungeonFloor {
    @IgnoreSerialization
    private List<DungeonBranch> dungeonBranches = new ArrayList<>();
    private final String templateKey;
    private final BlockPos origin;
    private final ResourceKey<Level> LEVEL_KEY;
    private final BlockPos spawnPoint;
    private final String sessionKey;
    private final int index;
    private final HashMap<ChunkPos, List<Vector2i>> chunkMap = new HashMap<>();
    private final HashMap<String, DungeonSession.PlayerStatus> playerStatuses = new HashMap<>();

    public DungeonFloorTemplate getTemplate() {return DungeonRegistry.DUNGEON_FLOOR_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public WeightedPool<DungeonMaterial> getMaterials() {return this.getTemplate().materials() == null ? this.getSession().getTemplate().materials() : this.getTemplate().materials();}
    public WeightedTable<EntityType<?>> getEnemyTable() {return this.getTemplate().enemyTable() == null ? this.getSession().getTemplate().enemyTable() : this.getTemplate().enemyTable();}
    public double getDifficulty() {return this.getSession().getTemplate().difficulty() * this.getTemplate().difficulty();}
    public ServerLevel getLevel() {
        return DungeonSessionManager.getInstance().server.levels.get(this.LEVEL_KEY);
    }
    public List<DungeonBranch> getBranches() {return this.dungeonBranches;}
    public String getTemplateKey() {return this.templateKey;}
    public BlockPos getOrigin() {return this.origin;}
    public ResourceKey<Level> getLevelKey() {return this.LEVEL_KEY;}
    public BlockPos getSpawnPoint() {return this.spawnPoint;}
    public String getSessionKey() {return this.sessionKey;}
    public int getIndex() {return this.index;}
    public HashMap<ChunkPos, List<Vector2i>> getChunkMap() {return this.chunkMap;}

    public DungeonFloor(String templateKey, String sessionKey, BlockPos origin, WeightedPool<String> destinations) {
        this.sessionKey = sessionKey;
        this.index = this.getSession().getFloors().size();
        this.getSession().getFloors().add(this);
        this.templateKey = templateKey;

        this.LEVEL_KEY = buildFloorLevelKey(this);
        InfiniverseAPI.get().getOrCreateLevel(DungeonSessionManager.getInstance().server, LEVEL_KEY, () -> WDDimensions.createLevel(DungeonSessionManager.getInstance().server));
        this.origin = origin;
        generateDungeonFloor();
        this.spawnPoint = this.dungeonBranches.getFirst().getSpawnPoint();

        if (!this.dungeonBranches.getFirst().getRooms().getFirst().getRiftUUIDs().isEmpty()) {
            Offering exitRift = (Offering) this.getLevel().getEntity(UUID.fromString(this.dungeonBranches.getFirst().getRooms().getFirst().getRiftUUIDs().getFirst()));
            if (exitRift != null) {exitRift.setOfferingId(""+(index-1));}
        }

        if (!this.dungeonBranches.getLast().getRooms().isEmpty() && !this.dungeonBranches.getLast().getRooms().getLast().getRiftUUIDs().isEmpty()) {
            Offering enterRift = (Offering) this.getLevel().getEntity(UUID.fromString(this.dungeonBranches.getLast().getRooms().getLast().getRiftUUIDs().getLast()));
            if (enterRift != null) {
                enterRift.setOfferingId(destinations.getRandom());
                WildDungeons.getLogger().info("PICKED RIFT DESTINATION FOR THIS FLOOR: {}", enterRift.getOfferingId());
            }
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonFloor::new");
    }

    public void shutdown() {
        InfiniverseAPI.get().markDimensionForUnregistration(DungeonSessionManager.getInstance().server, this.LEVEL_KEY);
        FileUtil.deleteDirectoryContents(FileUtil.getWorldPath().resolve("dimensions").resolve(WildDungeons.MODID).resolve(this.LEVEL_KEY.location().getPath()), true);
    }

    public static ResourceKey<Level> buildFloorLevelKey(DungeonFloor floor) {
        return ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(DungeonSessionManager.buildDungeonSessionKey(floor.getSession().getEntranceUUID()) + "_" + floor.getTemplate().name() + "_" + floor.index));
    }

    private void generateDungeonFloor() {
        int tries = 0;
        while (dungeonBranches.size() < getTemplate().branchTemplates().size() && tries < getTemplate().branchTemplates().size() * 2) {
            populateNextBranch();
            if (dungeonBranches.getLast().getRooms().isEmpty()) {break;}
            tries++;
        }
        WildDungeons.getLogger().info("PLACED {} BRANCHES IN {} TRIES", dungeonBranches.size(), tries);
    }

    private void populateNextBranch() {
        DungeonBranchTemplate nextBranch = getTemplate().branchTemplates().get(dungeonBranches.size()).getRandom();
        nextBranch.placeInWorld(this, origin);
    }

    protected boolean isBoundingBoxValid(List<BoundingBox> proposedBoxes) {
        for (BoundingBox proposedBox : proposedBoxes) {
            if (proposedBox.minY() < EmptyGenerator.MIN_Y || proposedBox.maxY() > EmptyGenerator.MIN_Y + EmptyGenerator.GEN_DEPTH) {
                WildDungeons.getLogger().info("OUT OF BOUNDS!");
                return false;
            }

            ChunkPos min = new ChunkPos(new BlockPos(proposedBox.minX(), proposedBox.minY(), proposedBox.minZ()));
            ChunkPos max = new ChunkPos(new BlockPos(proposedBox.maxX(), proposedBox.maxY(), proposedBox.maxZ()));

            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    ChunkPos newPos = new ChunkPos(x, z);
                    List<DungeonRoom> roomsInChunk = getChunkMap().containsKey(newPos) ? getChunkMap().get(newPos).stream().map(v2 -> getBranches().get(v2.x).getRooms().get(v2.y)).toList() : new ArrayList<>();
                    for (DungeonRoom room : roomsInChunk) {
                        for (BoundingBox box : room.getBoundingBoxes()) {
                            if (proposedBox.intersects(box)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public void onEnter(WDPlayer wdPlayer) {
        playerStatuses.computeIfAbsent(wdPlayer.getUUID(), key -> {getSession().getStats(key).floorsFound += 1; return new DungeonSession.PlayerStatus();});
        this.playerStatuses.get(wdPlayer.getUUID()).inside = true;
        wdPlayer.travelToFloor(wdPlayer, wdPlayer.getCurrentFloor(), this);
    }

    public void onExit(WDPlayer wdPlayer) {this.playerStatuses.get(wdPlayer.getUUID()).inside = false;}

    public void tick() {
        if (this.getLevel() == null) return;
        if (playerStatuses.values().stream().anyMatch(v -> v.inside)) dungeonBranches.forEach(DungeonBranch::tick);
    }

    public void addBranch(DungeonBranch branch) {
        if (this.dungeonBranches == null) this.dungeonBranches = new ArrayList<>();
        this.dungeonBranches.add(branch);
    }
    public void sortBranches() {
        this.dungeonBranches.sort(Comparator.comparingInt(DungeonBranch::getIndex));
    }
}