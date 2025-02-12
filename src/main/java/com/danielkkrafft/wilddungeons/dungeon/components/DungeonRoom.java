package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBedrockBlock;
import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.room.TargetPurgeRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.registries.LootTableRegistry;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.joml.Vector2i;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.danielkkrafft.wilddungeons.block.WDBedrockBlock.MIMIC;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialRegistry.DUNGEON_MATERIAL_REGISTRY;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomRegistry.DUNGEON_ROOM_REGISTRY;
import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateTableRegistry.BASIC_SHOP_TABLE;

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
    private boolean lootGenerated = false;

    @IgnoreSerialization
    protected DungeonBranch branch = null;

    public DungeonRoomTemplate getTemplate() {return DUNGEON_ROOM_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonBranch getBranch() {return this.branch != null ? this.branch : this.getSession().getFloors().get(this.floorIndex).getBranches().get(this.branchIndex);}
    public DungeonMaterial getMaterial() {return DUNGEON_MATERIAL_REGISTRY.get(this.materialKey);}
    public boolean hasBedrockShell() {return this.getTemplate().hasBedrockShell() == null ? this.getBranch().hasBedrockShell() : this.getTemplate().hasBedrockShell();}
    public DungeonRoomTemplate.DestructionRule getDestructionRule() {return this.getTemplate().getDestructionRule() == null ? this.getBranch().getDestructionRule() : this.getTemplate().getDestructionRule();}
    public WeightedTable<DungeonRegistration.TargetTemplate> getEnemyTable() {return this.getTemplate().enemyTable() == null ? this.getBranch().getEnemyTable() : this.getTemplate().enemyTable();}
    public double getDifficulty() {return this.getBranch().getDifficulty() * this.getTemplate().difficulty();}
    public boolean isRotated() {return Objects.equals(rotation, Rotation.CLOCKWISE_90.getSerializedName()) || Objects.equals(rotation, Rotation.COUNTERCLOCKWISE_90.getSerializedName());}
    public StructurePlaceSettings getSettings() {return new StructurePlaceSettings().setMirror(Mirror.valueOf(this.mirror)).setRotation(Rotation.valueOf(this.rotation));}
    public List<ConnectionPoint> getConnectionPoints() {return this.connectionPoints;}
    public List<String> getRiftUUIDs() {return this.riftUUIDs;}
    public List<String> getOfferingUUIDs() {return this.offeringUUIDs;}
    public List<BoundingBox> getBoundingBoxes() {return this.boundingBoxes;}
    public int getIndex() {return this.index;}
    public void setIndex(int index) {this.index = index;}
    public List<WDPlayer> getActivePlayers() {return this.playerStatuses.entrySet().stream().map(e -> {
        if (e.getValue().inside) return WDPlayerManager.getInstance().getOrCreateServerWDPlayer(e.getKey());
        return null;
    }).filter(Objects::nonNull).toList();}
    public boolean isClear() {return this.clear;}
    public Set<BlockPos> getAlwaysBreakable() {return this.alwaysBreakable;}
    public BlockPos getPosition() {return this.position;}
    public int blockingMaterialIndex() {return this.getTemplate().blockingMaterialIndex() == -1 ? this.getBranch().blockingMaterialIndex() : this.getTemplate().blockingMaterialIndex();}


    public DungeonRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        ServerLevel level = branch.getFloor().getLevel();
        this.branch = branch;
        this.setIndex(this.getBranch().getRooms().size());
        this.branch.getRooms().add(this);
        this.templateKey = templateKey;
        WildDungeons.getLogger().info("ADDING ROOM: {} AT INDEX {}, {}", getTemplate().name(), this.getBranch().getIndex(), this.getIndex());

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
            TemplateHelper.placeInWorld(template.getFirst(), this, this.getMaterial(), level, newPosition, template.getSecond(), settings, 2);
        });

        this.processRifts();

        if (!(this instanceof TargetPurgeRoom)) this.processOfferings();

        if (getTemplate().spawnPoint() != null) {
            this.spawnPoint = TemplateHelper.transform(getTemplate().spawnPoint(), this);
            level.setBlock(spawnPoint, Blocks.AIR.defaultBlockState(), 2);
        } else {this.spawnPoint = null;}

        this.handleChunkMap();
        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::new");
    }

    public void processShell() {
        if (this.hasBedrockShell()) this.surroundWith(Blocks.BEDROCK.defaultBlockState());
        if (this.getDestructionRule() == DungeonRoomTemplate.DestructionRule.SHELL || this.getDestructionRule() == DungeonRoomTemplate.DestructionRule.SHELL_CLEAR) {
            this.createProtectedShell(WDBedrockBlock.of(Blocks.DIAMOND_BLOCK));
            for (ConnectionPoint point : this.connectionPoints) {
                if (point.isConnected()) point.unBlock(this.getBranch().getFloor().getLevel());
            }
        }
    }

    public void surroundWith(BlockState blockState) {
        ServerLevel level = this.getBranch().getFloor().getLevel();
        for (BoundingBox box : this.getBoundingBoxes()) {
            fillShellWith(this.getBranch().getFloor(), this, level, box, blockState, 1, isSafeForBoundingBoxes());
        }
    }

    public void createProtectedShell(BlockState blockState) {
        ServerLevel level = this.getBranch().getFloor().getLevel();
        for (BoundingBox box : this.getBoundingBoxes()) {
            fillShellWith(this.getBranch().getFloor(), this, level, box, blockState, 0, handlePlaceProtectedShell());
        }
        WildDungeons.getLogger().info("PLACED A TOTAL OF {}", this.totalPlaced);
    }

    public void removeProtectedShell(BlockState blockState) {
        WildDungeons.getLogger().info("REMOVING PROTECTED SHELL");
        ServerLevel level = this.getBranch().getFloor().getLevel();
        for (BoundingBox box : this.getBoundingBoxes()) {
            fillShellWith(this.getBranch().getFloor(), this, level, box, blockState, 0, handleRemoveProtectedShell());
        }
        WildDungeons.getLogger().info("REMOVED A TOTAL OF {}", this.totalRemoved);
    }

    public static void fillShellWith(DungeonFloor floor, DungeonRoom room, ServerLevel level, BoundingBox box, BlockState blockState, int shellDepth, TriFunction<DungeonFloor, DungeonRoom, BlockPos, Boolean> predicate) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        int[] minX = {box.minX() - shellDepth, box.minX() - shellDepth, box.minY() - shellDepth + 1, box.minY() - shellDepth + 1, box.minZ() - shellDepth + 1, box.minZ() - shellDepth + 1};
        int[] minY = {box.minZ() - shellDepth, box.minZ() - shellDepth, box.minX() - shellDepth, box.minX() - shellDepth, box.minY() - shellDepth + 1, box.minY() - shellDepth + 1};
        int[] maxX = {box.maxX() + shellDepth, box.maxX() + shellDepth, box.maxY() + shellDepth - 1, box.maxY() + shellDepth - 1, box.maxZ() + shellDepth - 1, box.maxZ() + shellDepth - 1};
        int[] maxY = {box.maxZ() + shellDepth, box.maxZ() + shellDepth, box.maxX() + shellDepth, box.maxX() + shellDepth, box.maxY() + shellDepth - 1, box.maxY() + shellDepth -1};
        //determines how far outside the bounding box to start placing blocks
        int[] wallOffset = {box.minY() - shellDepth, box.maxY() + shellDepth, box.minZ() - shellDepth, box.maxZ() + shellDepth, box.minX() - shellDepth, box.maxX() + shellDepth};

        for (int i = 0; i < 6; i++) {
            for (int x = minX[i]; x <= maxX[i]; x++) {
                for (int y = minY[i]; y <= maxY[i]; y++) {
                    switch (i) {
                        case 0, 1 -> mutableBlockPos.set(x, wallOffset[i], y);
                        case 2, 3 -> mutableBlockPos.set(y, x, wallOffset[i]);
                        case 4, 5 -> mutableBlockPos.set(wallOffset[i], y, x);
                    }

                    if (predicate.apply(floor, room, mutableBlockPos) && !level.getServer().isShutdown()) level.setBlock(mutableBlockPos, blockState, 2);
                }
            }
        }
    }

    public static TriFunction<DungeonFloor, DungeonRoom, BlockPos, Boolean> isSafeForBoundingBoxes() {
        return (floor, room, blockPos) -> {
            List<BoundingBox> potentialConflicts = new ArrayList<>();
            floor.getChunkMap().getOrDefault(new ChunkPos(blockPos), new ArrayList<>()).forEach(vector2i -> {
                potentialConflicts.addAll(floor.getBranches().get(vector2i.x).getRooms().get(vector2i.y).getBoundingBoxes());
            });
            return potentialConflicts.stream().noneMatch(potentialConflict -> potentialConflict.isInside(blockPos));
        };
    }

    int totalPlaced = 0;
    public static TriFunction<DungeonFloor, DungeonRoom, BlockPos, Boolean> handlePlaceProtectedShell() {
        return (floor, room, blockPos) -> {
            if (!room.isPosInsideShell(blockPos)) {
                Block block = floor.getLevel().getBlockState(blockPos).getBlock();
                if (block != WDBlocks.WD_BEDROCK.get()) {
                    floor.getLevel().setBlock(blockPos, WDBedrockBlock.of(floor.getLevel().getBlockState(blockPos).getBlock()), 2);
                    room.totalPlaced++;
                }
            }
            return false;
        };
    }

    int totalRemoved = 0;
    public static TriFunction<DungeonFloor, DungeonRoom, BlockPos, Boolean> handleRemoveProtectedShell() {
        return (floor, room, blockPos) -> {
            if (!room.isPosInsideShell(blockPos)) {
                room.totalRemoved++;
                BlockState blockState = floor.getLevel().getBlockState(blockPos);
                if (blockState.hasProperty(MIMIC)) floor.getLevel().setBlock(blockPos, BuiltInRegistries.BLOCK.byId(floor.getLevel().getBlockState(blockPos).getValue(MIMIC)).defaultBlockState(), 2);
            }
            return false;
        };
    }

    public void processRifts() {
        getTemplate().rifts().forEach(pos -> {
            String destination;
            if (this.getBranch().getIndex() == 0) {
                destination = String.valueOf(this.getBranch().getFloor().getIndex()-1);
            } else if (this.getBranch().getFloor().getIndex() == this.getBranch().getFloor().getSession().getTemplate().floorTemplates().size()-1) {
                destination = "win";
            } else {
                destination = String.valueOf(this.getBranch().getFloor().getIndex()+1);
            }

            Offering rift = new Offering(this.getBranch().getFloor().getLevel(), Offering.Type.RIFT, 1, destination, Offering.CostType.XP_LEVEL, 0);
            Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.position.getX(), this.position.getY(), this.position.getZ());            WildDungeons.getLogger().info("ADDING RIFT AT {}", pos1);
            rift.setPos(pos1);
            this.getBranch().getFloor().getLevel().addFreshEntity(rift);
            this.riftUUIDs.add(rift.getStringUUID());
        });
    }

    public void processOfferings() {
        List<DungeonRegistration.OfferingTemplate> entries = BASIC_SHOP_TABLE.randomResults(this.getTemplate().offerings().size(), (int) this.getDifficulty() * this.getTemplate().offerings().size(), 1.2f);
        getTemplate().offerings().forEach(pos -> {
            if (entries.isEmpty()) return;
            Offering next = entries.removeFirst().asOffering(this.getBranch().getFloor().getLevel());
            Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.position.getX(), this.position.getY(), this.position.getZ());
            WildDungeons.getLogger().info("ADDING OFFERING AT {}", pos1);
            next.setPos(pos1);
            this.getBranch().getFloor().getLevel().addFreshEntity(next);
            this.offeringUUIDs.add(next.getStringUUID());
        });
    }

    public void processConnectionPoints(DungeonFloor floor) {
        WildDungeons.getLogger().info("PROCESSING {} CONNECTION POINTS", connectionPoints.size());
        for (ConnectionPoint point : connectionPoints) {
            point.setupBlockstates(getSettings(), getPosition(), this.getBranch().getFloor().getLevel());
            if (point.isConnected()) {
                templateBasedUnblock(floor, point);
                point.getConnectedPoint().unBlock(floor.getLevel());
            }
            if (!point.isConnected()) point.block(floor.getLevel());
            point.complete();
        }
        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::processConnectionPoints");
    }

    public void templateBasedUnblock(DungeonFloor floor, ConnectionPoint point) {
        switch (getTemplate().type()){
            case COMBAT -> point.combatRoomUnblock(floor.getLevel());
            case LOOT -> point.lootRoomUnblock(floor.getLevel());
            case null, default -> point.unBlock(floor.getLevel());
        }
    }

    public BlockPos getSpawnPoint(ServerLevel level) {
        return this.spawnPoint == null ? this.sampleSpawnablePositions(level, 1, -1).getFirst() : this.spawnPoint;
    }

    public List<BlockPos> sampleSpawnablePositions(ServerLevel level, int count, int inflation) {
        List<BlockPos> result = new ArrayList<>();
        int tries = count*10;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        while (result.size() < count && tries > 0) {
            BoundingBox innerShell = this.boundingBoxes.get(RandomUtil.randIntBetween(0, this.boundingBoxes.size()-1)).inflatedBy(inflation);
            int randX = RandomUtil.randIntBetween(innerShell.minX(), innerShell.maxX());
            int randZ = RandomUtil.randIntBetween(innerShell.minZ(), innerShell.maxZ());

            for (int y = innerShell.minY(); y < innerShell.maxY(); y++) {
                mutableBlockPos.set(randX, y, randZ);
                if (level.getBlockState(mutableBlockPos) == Blocks.AIR.defaultBlockState()) {
                    mutableBlockPos.set(randX, y + 1, randZ);
                    if (level.getBlockState(mutableBlockPos) == Blocks.AIR.defaultBlockState()) {
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
        //get all loot blocks from template
        List<StructureTemplate.StructureBlockInfo> lootBlocks = this.getTemplate().lootBlocks();
        //transform them to the real room's position
        List<BlockPos> potentialLootBlockPositions = lootBlocks.stream().map(b -> TemplateHelper.transform(b.pos(), this)).toList();
        //get all block entities at those positions
        List<BlockEntity> lootBlockEntities = new ArrayList<>(potentialLootBlockPositions.stream().map(pos -> this.getBranch().getFloor().getLevel().getBlockEntity(pos)).toList());
        //remove all null entities and entities that are not loot blocks, just in case
        lootBlockEntities.removeIf(entity -> Objects.isNull(entity) || !(entity instanceof BaseContainerBlockEntity));
        if (lootBlockEntities.isEmpty()) return;
        //get a random number, between 1 and the number of loot blocks, but not more than 5
        int countedChests = RandomUtil.randIntBetween(1, Math.min(5, lootBlockEntities.size()));
        //determine the amount of items, between 3 and 7 times the number of counted chests
        int maxItems = RandomUtil.randIntBetween(3 * countedChests, 7 * countedChests);
        //get loot entries from the loot table registry
        List<DungeonRegistration.ItemTemplate> entries = LootTableRegistry.BASIC_LOOT_TABLE.randomResults(maxItems, (int) (5 * this.getDifficulty()), 2f);
        //for each entry, place it in a random chest
        entries.forEach(entry -> {
            //get a random chest
            BaseContainerBlockEntity lootBlock = (BaseContainerBlockEntity) lootBlockEntities.get(RandomUtil.randIntBetween(0, lootBlockEntities.size() - 1));
            int slot = RandomUtil.randIntBetween(0, lootBlock.getContainerSize() - 1);
            //make sure the slot is empty to prevent overwriting existing loot
            while (!lootBlock.getItem(slot).isEmpty()) {
                slot = RandomUtil.randIntBetween(0, lootBlock.getContainerSize() - 1);
            }
            lootBlock.setItem(slot, entry.asItemStack());
        });
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
            getBranch().getFloor().getChunkMap().computeIfAbsent(pos, k -> new ArrayList<>()).add(new Vector2i(getBranch().getIndex(), this.getIndex()));
        });
        WDProfiler.INSTANCE.logTimestamp("DungeonRoom::handleChunkMap");
    }

    public void destroy() {
        if (this.hasBedrockShell()) this.surroundWith(Blocks.AIR.defaultBlockState());

        this.boundingBoxes.forEach(box -> {
            removeBlocks(this.getBranch().getFloor(), box);
        });
        unsetAttachedPoints();
        getBranch().getFloor().getChunkMap().forEach((key, value) -> {
            value.removeIf(v -> v.x == getBranch().getIndex() && v.y == this.getIndex());
        });

        this.boundingBoxes.forEach(box -> {
            fixContactedShells(getBranch().getFloor(), box);
        });
        destroyEntities();
    }

    public static void fixContactedShells(DungeonFloor floor, BoundingBox box) {
        BoundingBox inflatedBox = box.inflatedBy(2);
        List<DungeonRoom> touchingRooms = new ArrayList<>();
        floor.getChunkMap().forEach((key, value) -> {
            value.forEach(v -> {
                DungeonRoom room = floor.getBranches().get(v.x).getRooms().get(v.y);
                if (room.boundingBoxes.stream().anyMatch(inflatedBox::intersects)) {
                    touchingRooms.add(room);
                }
            });
        });
        touchingRooms.forEach(DungeonRoom::processShell);
    }

    public static void removeBlocks(DungeonFloor floor, BoundingBox box) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    mutableBlockPos.set(x, y, z);
                    floor.getLevel().setBlock(mutableBlockPos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    private void destroyEntities() {
        offeringUUIDs.forEach(uuid -> {
            Offering offering = (Offering) this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (offering != null) offering.remove(Entity.RemovalReason.DISCARDED);
        });
        riftUUIDs.forEach(uuid -> {
            Offering rift = (Offering) this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (rift != null) rift.remove(Entity.RemovalReason.DISCARDED);
        });
        offeringUUIDs.clear();
        riftUUIDs.clear();
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
                    if ((xConnected ? 1 : 0) + (yConnected ? 1 : 0) + (zConnected ? 1 : 0) == 1) {
                        if (box.inflatedBy(xConnected ? 0 : -1, yConnected ? 0 : -1, zConnected ? 0 : -1).isInside(pos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void onGenerate() {}
    public void onEnter(WDPlayer player) {
        WildDungeons.getLogger().info("ENTERING ROOM {} OF CLASS {}", this.getTemplate().name(), this.getClass().getSimpleName());
        playerStatuses.computeIfAbsent(player.getUUID(), key -> {
            getSession().getStats(key).roomsFound += 1;
            return new DungeonSession.PlayerStatus();
        });
        this.playerStatuses.get(player.getUUID()).inside = true;
    }

    public void onFloorEnter(WDPlayer player) {
    }

    public void onBranchEnter(WDPlayer player) {
        if (!lootGenerated){
            DungeonSessionManager.getInstance().server.execute(this::processLootBlocks);
            lootGenerated = true;
        }
    }

    public void onEnterInner(WDPlayer player) {
    }

    public void onFloorComplete(){
    }

    public void onBranchComplete(){
    }

    public void onExit(WDPlayer player) {
        this.playerStatuses.get(player.getUUID()).inside = false;
        this.playerStatuses.get(player.getUUID()).insideShell = false;
    }

    public void onClear() {
        this.clear = true;
        if (this.getDestructionRule() == DungeonRoomTemplate.DestructionRule.SHELL_CLEAR) {
            CompletableFuture.runAsync(() -> {
                this.removeProtectedShell(Blocks.AIR.defaultBlockState());
            });
        }

    }

    public void reset() {
    }

    public void tick() {
        if (this.playerStatuses.values().stream().noneMatch(v -> v.inside)) return;
        playerStatuses.forEach((key, value) -> {
            if (!value.insideShell) {
                WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(key);
                ServerPlayer player = wdPlayer.getServerPlayer();
                if (player != null && this.isPosInsideShell(player.blockPosition())) {
                    value.insideShell = true;
                    this.onEnterInner(wdPlayer);
                }
            }
        });
    }

    public void unsetAttachedPoints() {
        getConnectionPoints().forEach(connectionPoint -> {
            if (connectionPoint.isConnected()) {
                connectionPoint.getConnectedPoint().unSetConnectedPoint();
            }
        });
    }

    public void unsetConnectedPoints() {
        getConnectionPoints().forEach(connectionPoint -> {
            if (connectionPoint.isConnected() && connectionPoint.getConnectedBranchIndex() == this.getIndex()) {
                connectionPoint.unSetConnectedPoint();
            }
        });
    }
}
