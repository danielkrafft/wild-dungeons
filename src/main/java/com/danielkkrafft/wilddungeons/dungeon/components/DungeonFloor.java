package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundNullScreenPacket;
import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDDimensions;
import com.danielkkrafft.wilddungeons.util.*;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.danielkkrafft.wilddungeons.world.dimension.tools.InfiniverseAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2i;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorRegistry.DUNGEON_FLOOR_REGISTRY;
import static com.danielkkrafft.wilddungeons.registry.WDDimensions.WILDDUNGEON;

public class DungeonFloor {
    @IgnoreSerialization
    private List<DungeonBranch> dungeonBranches = new ArrayList<>();
    private final String templateKey;
    private final BlockPos origin;
    private final ResourceKey<Level> LEVEL_KEY;
    private BlockPos spawnPoint;
    private final String sessionKey;
    private final int index;
    private final HashMap<ChunkPos, List<Vector2i>> chunkMap = new HashMap<>();
    private final HashMap<String, DungeonSession.PlayerStatus> playerStatuses = new HashMap<>();

    public DungeonFloorTemplate getTemplate() {return DUNGEON_FLOOR_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public WeightedPool<DungeonMaterial> getMaterials() {return this.getTemplate().materials() == null ? this.getSession().getTemplate().materials() : this.getTemplate().materials();}
    public boolean hasBedrockShell() {return this.getTemplate().hasBedrockShell() == null ? this.getSession().getTemplate().hasBedrockShell() : this.getTemplate().hasBedrockShell();}
    public DungeonRoomTemplate.DestructionRule getDestructionRule() {return this.getTemplate().getDestructionRule() == null ? this.getSession().getTemplate().getDestructionRule() : this.getTemplate().getDestructionRule();}
    public WeightedTable<DungeonRegistration.TargetTemplate> getEnemyTable() {return this.getTemplate().enemyTable() == null ? this.getSession().getTemplate().enemyTable() : this.getTemplate().enemyTable();}
    public double getDifficultyScaling(){
        double difficultyScaling = this.getTemplate().difficultyScaling();
        if (difficultyScaling == -1) difficultyScaling = this.getSession().getTemplate().difficultyScaling();
        return difficultyScaling;
    }
    public double getDifficulty() {return this.getSession().getTemplate().difficulty() * this.getSession().getPlayers().size() * this.getTemplate().difficulty();}
    public ServerLevel getLevel() {
        return DungeonSessionManager.getInstance().server.levels.get(this.LEVEL_KEY);
    }
    public List<WDPlayer> getActivePlayers() {return this.playerStatuses.entrySet().stream().map(e -> {
        if (e.getValue().inside) return WDPlayerManager.getInstance().getOrCreateServerWDPlayer(e.getKey());
        return null;
    }).filter(Objects::nonNull).toList();}
    public List<DungeonBranch> getBranches() {return this.dungeonBranches;}
    public String getTemplateKey() {return this.templateKey;}
    public BlockPos getOrigin() {return this.origin;}
    public ResourceKey<Level> getLevelKey() {return this.LEVEL_KEY;}
    public BlockPos getSpawnPoint() {return this.spawnPoint;}
    public String getSessionKey() {return this.sessionKey;}
    public int getIndex() {return this.index;}
    public HashMap<ChunkPos, List<Vector2i>> getChunkMap() {return this.chunkMap;}
    public List<BoundingBox> halfGeneratedRooms = new ArrayList<>();
    @IgnoreSerialization
    public List<CompletableFuture<Void>> generationFutures = new ArrayList<CompletableFuture<Void>>();
    List<WDPlayer> playersWaitingToEnter = new ArrayList<>();
    public boolean generating = true;


    public DungeonFloor(String templateKey, String sessionKey, BlockPos origin) {
        this.sessionKey = sessionKey;
        this.index = this.getSession().getFloors().size();
        this.getSession().getFloors().add(this);
        this.templateKey = templateKey;

        this.LEVEL_KEY = buildFloorLevelKey(this);
        InfiniverseAPI.get().getOrCreateLevel(DungeonSessionManager.getInstance().server, LEVEL_KEY, () -> WDDimensions.createLevel(WILDDUNGEON));
        this.origin = this.getTemplate().origin() == null ? origin : this.getTemplate().origin();
        WDProfiler.INSTANCE.logTimestamp("DungeonFloor::new");
    }


    public void shutdown() {
        InfiniverseAPI.get().markDimensionForUnregistration(DungeonSessionManager.getInstance().server, this.LEVEL_KEY);
        FileUtil.deleteDirectoryContents(FileUtil.getWorldPath().resolve("dimensions").resolve(WildDungeons.MODID).resolve(this.LEVEL_KEY.location().getPath()), true);
    }

    public static ResourceKey<Level> buildFloorLevelKey(DungeonFloor floor) {
        return ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(DungeonSessionManager.buildDungeonSessionKey(floor.getSession().getEntranceUUID()) + "___" + floor.getTemplate().name() + "___" + floor.index));
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
                    HashMap<ChunkPos, List<Vector2i>> chunkMap = getChunkMap();
                    boolean chunkExists = chunkMap.containsKey(newPos);
                    List<DungeonRoom> roomsInChunk = chunkExists
                            ? chunkMap.get(newPos).stream().map(v2 -> getBranches().get(v2.x).getRooms().get(v2.y)).toList()
                            : new ArrayList<>();
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

    public void attemptEnter(WDPlayer wdPlayer){
        if (this.generating) {
            playersWaitingToEnter.add(wdPlayer);
            return;
        }
        onEnter(wdPlayer);
    }

    public void onEnter(WDPlayer wdPlayer) {
        playerStatuses.computeIfAbsent(wdPlayer.getUUID(), key -> {
            getSession().getStats(key).floorsFound += 1;
            return new DungeonSession.PlayerStatus();
        });
        this.playerStatuses.get(wdPlayer.getUUID()).inside = true;
        wdPlayer.setCurrentDungeon(getSession());
        wdPlayer.travelToFloor(wdPlayer, wdPlayer.getCurrentFloor(), this);
        wdPlayer.getServerPlayer().setGameMode(wdPlayer.getLastGameMode());
        PacketDistributor.sendToPlayer(wdPlayer.getServerPlayer(), new ClientboundNullScreenPacket(new CompoundTag()));
        WDPlayerManager.syncAll(this.playerStatuses.keySet().stream().toList());
    }

    public void onExit(WDPlayer wdPlayer) {
        this.playerStatuses.get(wdPlayer.getUUID()).inside = false;
    }

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

    public void validate() {
        if (!halfGeneratedRooms.isEmpty()) {
            halfGeneratedRooms.forEach(box -> {
                DungeonSessionManager.getInstance().server.execute(() -> {
                    List<Entity> entities = getLevel().getEntitiesOfClass(Entity.class, AABB.of(box));
                    entities.removeIf(livingEntity -> livingEntity instanceof ServerPlayer);
                    entities.forEach(entity -> entity.remove(Entity.RemovalReason.DISCARDED));
                });

                DungeonRoom.fillShellWith(this, null, this.getLevel(), box, Blocks.AIR.defaultBlockState(), 1, DungeonRoom.isSafeForBoundingBoxes());

                DungeonRoom.removeBlocks(this, box);
            });
            for (DungeonBranch dungeonBranch : dungeonBranches) {
                for (DungeonRoom room : dungeonBranch.getRooms()) {
                    room.processShell();
                }
            }
            halfGeneratedRooms.clear();
        }

        generateBranches();
    }

    public void generateBranches() {
        DungeonFloorTemplate template = this.getTemplate();
        int branchTemplateCount = template.branchTemplates().size();
        if (generationFutures == null) generationFutures = new ArrayList<>();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            int branchCount = this.dungeonBranches.size();
            while (branchCount < branchTemplateCount) {
                WildDungeons.getLogger().info("Generating branch {} of {}", branchCount + 1, branchTemplateCount);
                generateSpecificBranch(branchCount);
                branchCount = this.dungeonBranches.size();
            }
        });
        generationFutures.add(future);
    }

    private void generateSpecificBranch(int branchIndex) {
        checkForInvalidPlayersInBranch(branchIndex);

        DungeonBranchTemplate nextBranch = getTemplate().branchTemplates().get(branchIndex).getRandom();
        DungeonBranch newBranch = nextBranch.placeInWorld(this, origin);
        if (newBranch == null) return;
        if (branchIndex==0)
            this.spawnPoint = this.dungeonBranches.getFirst().getSpawnPoint();
        onSequentialBranchGenerationComplete();
    }

    private void onSequentialBranchGenerationComplete() {
        if (!playersWaitingToEnter.isEmpty() && getBranches().stream().mapToInt(b -> b.getRooms().size()).sum() > 10) {
            spawnPlayers();
        }
    }

    private void spawnPlayers(){
        DungeonSessionManager.getInstance().server.execute(() -> {
            for (WDPlayer wdPlayer : playersWaitingToEnter) {
                onEnter(wdPlayer);
            }
            playersWaitingToEnter.clear();
            generating = false;
        });
    }

    private void checkForInvalidPlayersInBranch(int branchIndex) {
        this.playerStatuses.forEach((k, v) -> {
            WDPlayer player = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(k);
            if (player.getCurrentDungeon() == this.getSession() && player.getCurrentFloor() == this) {
                if (player.getCurrentBranch() == null && player.getCurrentBranchIndex() == branchIndex) {
                    //teleport the player to the previous branch
                    BlockPos blockPos = this.dungeonBranches.get(branchIndex - 1).getSpawnPoint();
                    Vec3 pos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    SavedTransform savedTransform = new SavedTransform(pos, 0, 0, this.getLevelKey());
                    ServerPlayer serverPlayer = player.getServerPlayer();
                    if (serverPlayer != null)
                        CommandUtil.executeTeleportCommand(serverPlayer, savedTransform);
                }
            }
        });
    }

    public void cancelGenerations() {
        if (generationFutures!=null)
            generationFutures.forEach(future -> {
                if (!future.isDone()) future.cancel(true);
            });
    }
}