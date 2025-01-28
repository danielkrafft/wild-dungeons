package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.IgnoreSerialization;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.*;

public class DungeonRoom {
    private final String templateKey;
    private final BlockPos position;
    private final BlockPos spawnPoint;
    private final String mirror;
    private final String rotation;
    private final List<ConnectionPoint> connectionPoints = new ArrayList<>();
    private final List<String> riftUUIDs = new ArrayList<>();
    private final List<String> offeringUUIDs = new ArrayList<>();
    private final List<BoundingBox> boundingBoxes;
    private final int branchIndex;
    private final int floorIndex;
    private final String sessionKey;
    private final String materialKey;
    private int index;
    private boolean clear = false;
    private final HashMap<String, DungeonSession.PlayerStatus> playerStatuses = new HashMap<>();
    private final Set<BlockPos> alwaysBreakable = new HashSet<>();

    @IgnoreSerialization
    protected DungeonBranch branch = null;
    public void setTempBranch(DungeonBranch branch) {this.branch = branch;}

    public DungeonRoomTemplate getTemplate() {return DungeonRegistry.DUNGEON_ROOM_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonBranch getBranch() {return this.branch != null ? this.branch : this.getSession().getFloors().get(this.floorIndex).getBranches().get(this.branchIndex);}
    public DungeonMaterial getMaterial() {return DungeonRegistry.DUNGEON_MATERIAL_REGISTRY.get(this.materialKey);}
    public WeightedTable<EntityType<?>> getEnemyTable() {return this.getTemplate().enemyTable() == null ? this.getBranch().getEnemyTable() : this.getTemplate().enemyTable();}
    public double getDifficulty() {return this.getBranch().getDifficulty() * this.getTemplate().difficulty();}
    public boolean isRotated() {return rotation == Rotation.CLOCKWISE_90.getSerializedName() || rotation == Rotation.COUNTERCLOCKWISE_90.getSerializedName();}
    public StructurePlaceSettings getSettings() {return new StructurePlaceSettings().setMirror(Mirror.valueOf(this.mirror)).setRotation(Rotation.valueOf(this.rotation));}
    public List<ConnectionPoint> getConnectionPoints() {return this.connectionPoints;}
    public List<String> getRiftUUIDs() {return this.riftUUIDs;}
    public List<String> getOfferingUUIDs() {return this.offeringUUIDs;}
    public List<BoundingBox> getBoundingBoxes() {return this.boundingBoxes;}
    public int getIndex() {return this.index;}
    public void setIndex(int index) {this.index = index;}
    public List<WDPlayer> getActivePlayers() {return this.playerStatuses.entrySet().stream().map(e -> {
        if (e.getValue().inside) return WDPlayerManager.getInstance().getOrCreateWDPlayer(e.getKey());
        return null;
    }).filter(Objects::nonNull).toList();}
    public boolean isClear() {return this.clear;}
    public Set<BlockPos> getAlwaysBreakable() {return this.alwaysBreakable;}
    public BlockPos getPosition() {return this.position;}

    public DungeonRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        this.branch = branch;
        this.setIndex(this.branch.getRooms().size());
        this.branch.getRooms().add(this);

        this.templateKey = templateKey;
        WildDungeons.getLogger().info("BRANCH ROOM MATERIALS: {}", branch.getMaterials().size());
        if (this.getTemplate().materials() != null) WildDungeons.getLogger().info("THIS ROOM MATERIALS: {}", this.getTemplate().materials().size());
        this.materialKey = this.getTemplate().materials() == null ? branch.getMaterials().getRandom().name() : this.getTemplate().materials().getRandom().name();
        this.sessionKey = branch.getSession().getSessionKey();
        this.mirror = settings.getMirror().name();
        this.rotation = settings.getRotation().name();
        this.position = position;

        for (ConnectionPoint point : allConnectionPoints) {
            ConnectionPoint newPoint = ConnectionPoint.copy(point);
            newPoint.setRoom(this);
            newPoint.setIndex(this.connectionPoints.size());
            this.connectionPoints.add(newPoint);
        }

        this.branchIndex = branch.getIndex();
        this.floorIndex = branch.getFloor().getIndex();
        this.boundingBoxes = this.getTemplate().getBoundingBoxes(settings, position);

        getTemplate().templates().forEach(template -> {
            BlockPos newOffset = StructureTemplate.transform(template.getSecond(), settings.getMirror(), settings.getRotation(), TemplateHelper.EMPTY_BLOCK_POS);
            BlockPos newPosition = position.offset(newOffset);
            TemplateHelper.placeInWorld(template.getFirst(), this, this.getMaterial(), level, newPosition, template.getSecond(), settings, DungeonSessionManager.getInstance().server.overworld().getRandom(), 2);
        });

        getTemplate().rifts().forEach(pos -> {
            Offering rift = new Offering(this.getBranch().getFloor().getLevel(), Offering.Type.RIFT, 1, "next", Offering.CostType.XP_LEVEL, 0);
            Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.position.getX(), this.position.getY(), this.position.getZ());            WildDungeons.getLogger().info("ADDING RIFT AT {}", pos1);
            rift.setPos(pos1);
            WildDungeons.getLogger().info("ADDING RIFT AT {}", pos1);
            level.addFreshEntity(rift);
            this.riftUUIDs.add(rift.getStringUUID());
        });

        List<DungeonRegistry.OfferingTemplate> entries = DungeonRegistry.OFFERING_TEMPLATE_TABLE_REGISTRY.get("BASIC_SHOP_TABLE").randomResults(this.getTemplate().offerings().size(), (int) this.getDifficulty() * this.getTemplate().offerings().size(), 1.2f);
        getTemplate().offerings().forEach(pos -> {
            if (entries.isEmpty()) return;
            Offering next = entries.removeFirst().asOffering(this.getBranch().getFloor().getLevel());
            Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.position.getX(), this.position.getY(), this.position.getZ());
            WildDungeons.getLogger().info("ADDING OFFERING AT {}", pos1);
            next.setPos(pos1);
            level.addFreshEntity(next);
            this.offeringUUIDs.add(next.getStringUUID());
        });

        this.processLootBlocks();

        if (getTemplate().spawnPoint() != null) {
            this.spawnPoint = TemplateHelper.transform(getTemplate().spawnPoint(), this);
            level.setBlock(spawnPoint, Blocks.AIR.defaultBlockState(), 2);
        } else {this.spawnPoint = null;}

        this.handleChunkMap();
        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::new");
    }

    public void processConnectionPoints(DungeonFloor floor) {
        WildDungeons.getLogger().info("PROCESSING {} CONNECTION POINTS", connectionPoints.size());
        for (ConnectionPoint point : connectionPoints) {
            point.setupBlockstates(getSettings(), getPosition(), this.getBranch().getFloor().getLevel());
            if (point.isConnected()) point.unBlock(floor.getLevel());
            if (!point.isConnected()) point.block(floor.getLevel());
            point.complete();
        }
        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::processConnectionPoints");
    }

    public BlockPos getSpawnPoint(ServerLevel level) {
        return this.spawnPoint == null ? this.sampleSpawnablePositions(level, 1).getFirst() : this.spawnPoint;
    }

    public List<BlockPos> sampleSpawnablePositions(ServerLevel level, int count) {
        List<BlockPos> result = new ArrayList<>();
        int tries = count*10;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        while (result.size() < count && tries > 0) {
            BoundingBox innerShell = this.boundingBoxes.get(RandomUtil.randIntBetween(0, this.boundingBoxes.size()-1)).inflatedBy(-1);
            int randX = RandomUtil.randIntBetween(innerShell.minX(), innerShell.maxX());
            int randZ = RandomUtil.randIntBetween(innerShell.minZ(), innerShell.maxZ());

            for (int y = innerShell.minY(); y < innerShell.maxY(); y++) {
                mutableBlockPos.set(randX, y, randZ);
                if (level.getBlockState(mutableBlockPos) == Blocks.AIR.defaultBlockState())
                {
                    mutableBlockPos.set(randX, y+1, randZ);
                    if (level.getBlockState(mutableBlockPos) == Blocks.AIR.defaultBlockState())
                    {
                        result.add(mutableBlockPos.below());
                        break;
                    }
                }
            }
            tries--;
        }

        return result.isEmpty() ? List.of(this.spawnPoint) : result;
    }

    public void processLootBlocks() {
        if (this.getTemplate().lootBlocks().isEmpty()) return;

        WildDungeons.getLogger().info("SETTING UP LOOT");

        List<StructureTemplate.StructureBlockInfo> lootBlocks = this.getTemplate().lootBlocks();
        List<StructureTemplate.StructureBlockInfo> chosenBlocks = new ArrayList<>();
        int maxChests = 3;

        while (chosenBlocks.size() < maxChests) {
            chosenBlocks.add(lootBlocks.get(RandomUtil.randIntBetween(0, lootBlocks.size()-1)));
        }

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int remainingChests = maxChests;
        List<DungeonRegistry.LootEntry> entries = DungeonRegistry.LOOT_TABLE_REGISTRY.get("BASIC_LOOT_TABLE").randomResults(5, (int) (5 * this.getDifficulty()), 1.5f);

        for (StructureTemplate.StructureBlockInfo structureBlockInfo : chosenBlocks) {
            mutableBlockPos.set(TemplateHelper.transform(structureBlockInfo.pos(), this));

            if (branch.getFloor().getLevel().getBlockEntity(mutableBlockPos) instanceof BaseContainerBlockEntity container) {

                int slots = container.getContainerSize();
                for (int i = 0; i < entries.size() / remainingChests; i++) {
                    container.setItem(RandomUtil.randIntBetween(0, slots-1), entries.removeFirst().asItemStack());
                }
                remainingChests -= 1;
            }
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::processLootBlocks");
    }

    public List<ConnectionPoint> getValidExitPoints(StructurePlaceSettings settings, BlockPos position, DungeonRoomTemplate nextRoom, ConnectionPoint entrancePoint, boolean bypassFailures) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (ConnectionPoint point : connectionPoints) {
            point.setRoom(this);
            if (ConnectionPoint.arePointsCompatible(settings, position, nextRoom, entrancePoint, point, bypassFailures)) {
                exitPoints.add(point);
            }
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::getValidExitPoints");
        return exitPoints;
    }

    public void handleChunkMap() {
        Set<ChunkPos> chunkPosSet = new HashSet<>();
        for (BoundingBox box : this.boundingBoxes) {
            ChunkPos min = new ChunkPos(new BlockPos(box.minX(), box.minY(), box.minZ()));
            ChunkPos max = new ChunkPos(new BlockPos(box.maxX(), box.maxY(), box.maxZ()));

            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    ChunkPos newPos = new ChunkPos(x, z);
                    chunkPosSet.add(newPos);
                }
            }
        }

        chunkPosSet.forEach(pos -> {
            WildDungeons.getLogger().info("ADDING ROOM {} TO SET AT CHUNKPOS {} WITH BRANCH INDEX {} AND ROOM INDEX {}", this.getTemplate().name(), pos, branch.getIndex(), this.index);
            branch.getFloor().getChunkMap().computeIfAbsent(pos, k -> new ArrayList<>()).add(new Vector2i(branch.getIndex(), this.index));
        });
        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::handleChunkMap");
    }

    public boolean isPosInsideShell(BlockPos pos) {
        for (BoundingBox box : this.boundingBoxes) {
            if (box.isInside(pos)) {
                if (box.inflatedBy(-1).isInside(pos)) {
                    return true;
                }
                for (BoundingBox otherBox : this.boundingBoxes) {
                    if (otherBox == box) continue;
                    boolean xConnected = otherBox.inflatedBy(1, 0, 0).isInside(pos);
                    boolean yConnected = otherBox.inflatedBy(0, 1, 0).isInside(pos);
                    boolean zConnected = otherBox.inflatedBy(0, 0, 1).isInside(pos);

                    //Only one axis is connected, indicating it's adjacent to another box, but not a corner
                    if ((xConnected ? 1 : 0) + (yConnected ? 1: 0) + (zConnected ? 1 : 0) == 1) {
                        if (box.inflatedBy(xConnected ? 0 : -1, yConnected ? 0 : -1, zConnected ? 0 : -1).isInside(pos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



    public enum DestructionRule {
        DEFAULT, SHELL, NONE
    }
    public DestructionRule getDestructionRule() {return DestructionRule.SHELL;}

    public void onGenerate() {}
    public void onEnter(WDPlayer player) {
        WildDungeons.getLogger().info("ENTERING ROOM {} OF CLASS {}", this.getTemplate().name(), this.getClass().getSimpleName());
        playerStatuses.computeIfAbsent(player.getUUID(), key -> {getSession().getStats(key).roomsFound += 1; return new DungeonSession.PlayerStatus();});
        this.playerStatuses.get(player.getUUID()).inside = true;
    }
    public void onBranchEnter(WDPlayer player) {}
    public void onEnterInner(WDPlayer player) {
    }
    public void onExit(WDPlayer player) {
        this.playerStatuses.get(player.getUUID()).inside = false;
        this.playerStatuses.get(player.getUUID()).insideShell = false;
    }
    public void onClear() {
        this.clear = true;
    }
    public void reset() {}
    public void tick() {
        if (this.playerStatuses.values().stream().noneMatch(v -> v.inside)) return;
        playerStatuses.entrySet().forEach((entry) -> {
            if (!entry.getValue().insideShell) {
                WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(entry.getKey());
                if (this.isPosInsideShell(wdPlayer.getServerPlayer().blockPosition())) {
                    entry.getValue().insideShell = true;
                    this.onEnterInner(wdPlayer);
                }
            }
        });
    }
}
