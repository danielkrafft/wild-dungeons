package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBedrockBlock;
import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.room.TargetPurgeRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import com.danielkkrafft.wilddungeons.dungeon.registries.LootTableRegistry;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.danielkkrafft.wilddungeons.block.WDBedrockBlock.MIMIC;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialRegistry.DUNGEON_MATERIAL_REGISTRY;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomRegistry.DUNGEON_ROOM_REGISTRY;
import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateTableRegistry.BASIC_SHOP_TABLE;

public class DungeonRoom {
    private final String templateKey;
    private final BlockPos position;
    private final BlockPos spawnPoint;
    private final TemplateOrientation orientation;
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
    private final HashMap<String, Boolean> playersInside = new HashMap<>();
    private final Set<BlockPos> alwaysBreakable = new HashSet<>();
    private boolean lootGenerated = false;
    @Serializer.IgnoreSerialization protected DungeonBranch branch = null;

    public <T> T getProperty(HierarchicalProperty<T> property) { return this.getTemplate().get(property) == null ? this.getBranch().getProperty(property) : this.getTemplate().get(property); }
    public DungeonRoomTemplate getTemplate() {return DUNGEON_ROOM_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonBranch getBranch() {return this.branch != null ? this.branch : this.getSession().getFloors().get(this.floorIndex).getBranches().get(this.branchIndex);}
    public DungeonMaterial getMaterial() {return DUNGEON_MATERIAL_REGISTRY.get(this.materialKey);}
    public double getDifficulty() {return this.getBranch().getDifficulty() * this.getProperty(DIFFICULTY_MODIFIER);}
    public boolean isRotated() {return Objects.equals(orientation.getRotation(), Rotation.CLOCKWISE_90) || Objects.equals(orientation.getRotation(), Rotation.COUNTERCLOCKWISE_90);}
    public StructurePlaceSettings getSettings() {return new StructurePlaceSettings().setMirror(orientation.getMirror()).setRotation(orientation.getRotation());}
    public TemplateOrientation getOrientation() {return orientation;}
    public List<ConnectionPoint> getConnectionPoints() {return this.connectionPoints;}
    public List<String> getOfferingUUIDs() {return this.offeringUUIDs;}
    public List<BoundingBox> getBoundingBoxes() {return this.boundingBoxes;}
    public int getIndex() {return this.index;}
    public void setIndex(int index) {this.index = index;}
    public List<WDPlayer> getActivePlayers() {return this.playersInside.entrySet().stream().map(e -> e.getValue() ? WDPlayerManager.getInstance().getOrCreateServerWDPlayer(e.getKey()) : null).filter(Objects::nonNull).toList();}
    public boolean isClear() {return this.clear;}
    public Set<BlockPos> getAlwaysBreakable() {return this.alwaysBreakable;}
    public BlockPos getPosition() {return this.position;}

    public DungeonRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        ServerLevel level = branch.getFloor().getLevel();
        this.branch = branch;
        this.setIndex(this.getBranch().getRooms().size());
        this.branch.getRooms().add(this);
        this.templateKey = templateKey;
        WildDungeons.getLogger().info("ADDING ROOM: {} AT INDEX {}, {}", getTemplate().name(), this.getBranch().getIndex(), this.getIndex());
        this.materialKey = this.getTemplate().get(MATERIAL) == null ? branch.getProperty(MATERIAL).getRandom().name() : this.getTemplate().get(MATERIAL).getRandom().name();
        this.sessionKey = branch.getSession().getSessionKey();
        this.orientation = orientation;
        this.position = position;

        for (ConnectionPoint point : this.getTemplate().connectionPoints()) {
            ConnectionPoint newPoint = ConnectionPoint.copy(point);
            newPoint.setRoom(this);
            newPoint.setIndex(this.connectionPoints.size());
            this.connectionPoints.add(newPoint);
        }

        this.branchIndex = branch.getIndex();
        this.floorIndex = branch.getFloor().getIndex();
        this.boundingBoxes = this.getTemplate().getBoundingBoxes(orientation, position);

        if (getTemplate().spawnPoint() != null) {
            this.spawnPoint = TemplateHelper.transform(getTemplate().spawnPoint(), this);
            getTemplate().spawnPoints().forEach(spawnPoint -> {
                level.setBlock(TemplateHelper.transform(spawnPoint, this), Blocks.AIR.defaultBlockState(), 130);
            });
        } else {this.spawnPoint = null;}

        getChunkPosSet(this.boundingBoxes, 0).forEach(pos -> {
            getBranch().getFloor().getChunkMap().putIfAbsent(pos, new ArrayList<>());
            getBranch().getFloor().getChunkMap().get(pos).add(new Vector2i(getBranch().getIndex(), this.getIndex()));
        });
        WildDungeons.getLogger().info("FINISHED ROOM: {}", getTemplate().name());
    }

    public void actuallyPlaceInWorld() {
        WildDungeons.getLogger().info("PLACING ROOM IN WORLD: {} AT INDEX {}, {}", getTemplate().name(), this.getBranch().getIndex(), this.getIndex());
        getTemplate().templates().forEach(template -> {
            BlockPos newOffset = StructureTemplate.transform(template.getSecond(), getSettings().getMirror(), getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS);
            BlockPos newPosition = position.offset(newOffset);
            TemplateHelper.placeInWorld(this, template.getFirst(), this.getMaterial(), getBranch().getFloor().getLevel(), newPosition, template.getSecond(), getSettings(), 0);
        });

        this.processRifts();
        if (!(this instanceof TargetPurgeRoom)) this.processOfferings();

        if (getTemplate().spawnPoint() != null) {
            getTemplate().spawnPoints().forEach(spawnPoint -> {
                getBranch().getFloor().getLevel().setBlock(TemplateHelper.transform(spawnPoint, this), Blocks.AIR.defaultBlockState(), 0);
            });
        }
        this.processConnectionPoints(getBranch().getFloor());
        this.onBranchComplete();

        getChunkPosSet(this.boundingBoxes, 1).forEach(chunkPos -> forceUpdateChunk(getBranch().getFloor().getLevel(), chunkPos));
        WildDungeons.getLogger().info("FINISHED ROOM: {}", getTemplate().name());
    }

    public static void forceUpdateChunk(ServerLevel level, ChunkPos chunkPos) {
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        if (chunk == null) return;

        ChunkMap chunkMap = level.getChunkSource().chunkMap;
        for (ServerPlayer player : chunkMap.getPlayers(chunkPos, false)) {
            player.connection.send(new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null));
        }
    }

    /**
     * Returns all connection points in this room which are compatible with the input entrance point.
     *
     * @param entrancePoint The ConnectionPoint to test compatibility with.
     */
    public List<ConnectionPoint> getValidExitPoints(ConnectionPoint entrancePoint) {
        return this.connectionPoints.stream().filter(point -> ConnectionPoint.arePointsCompatible(entrancePoint, point)).toList().stream().map(point -> {point.setRoom(this); return point;}).toList();
    }

    /**
     * Handles bedrock shells, and Protected Rooms with custom bedrock shells
     */
    public void processShell() {
        if (this.getProperty(HAS_BEDROCK_SHELL)) this.surroundWith(Blocks.BEDROCK.defaultBlockState());
    }

    /**
     * Surrounds the entire room with 1 layer of a specified blockstate
     *
     * @param blockState The blockstate to surround the room with
     */
    public void surroundWith(BlockState blockState) {
        this.boundingBoxes.forEach(box -> fillShellWith(this.getBranch().getFloor(), this, box, blockState, 1, (floor, room, pos) -> true));
    }

    /**
     * Toggles the protective bedrock shell which safeguards rooms from griefing and cheating
     */
    public void removeProtection() {
        this.getBoundingBoxes().forEach(box -> fillShellWith(this.getBranch().getFloor(), this, box, WDBedrockBlock.of(Blocks.DIAMOND_BLOCK), 0, handleRemoveProtectedShell()));
    }

    /**
     * Handles the actual placement of a shell
     *
     * @param floor The DungeonFloor where the shell will be placed
     * @param room The DungeonRoom which the shell will be associated with
     * @param box The bounding box to surround with a shell
     * @param blockState The blockstate to build the shell out of
     * @param shellDepth How far outside the bounding box to begin placing blocks. 0 will replace the room's shell, 1 will surround the room
     * @param predicate The test function to determine whether the block should be placed or skipped
     */
    public static void fillShellWith(DungeonFloor floor, DungeonRoom room, BoundingBox box, BlockState blockState, int shellDepth, TriFunction<DungeonFloor, DungeonRoom, BlockPos, Boolean> predicate) {
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

                    if (predicate.apply(floor, room, mutableBlockPos) && !floor.getLevel().getServer().isShutdown()) floor.getLevel().setBlock(mutableBlockPos, blockState, 0);
                }
            }
        }
    }

    /**
     * Used as a predicate for fillShellWith.
     * Tests whether the suggested blockPos will conflict with any existing BoundingBoxes
     */
    public static TriFunction<DungeonFloor, DungeonRoom, BlockPos, Boolean> isSafeForBoundingBoxes() {
        return (floor, room, blockPos) -> {
            List<BoundingBox> potentialConflicts = new ArrayList<>();
            floor.getChunkMap().getOrDefault(new ChunkPos(blockPos), new ArrayList<>()).forEach(vector2i -> {
                potentialConflicts.addAll(floor.getBranches().get(vector2i.x).getRooms().get(vector2i.y).getBoundingBoxes());
            });
            return potentialConflicts.stream().noneMatch(potentialConflict -> potentialConflict.isInside(blockPos));
        };
    }

    /**
     * Used as a predicate for fillShellWith.
     * Always returns false, skips the regular shell creation in order to match the new blockstate to the Bedrock blockstate which is being removed.
     */
    public static TriFunction<DungeonFloor, DungeonRoom, BlockPos, Boolean> handleRemoveProtectedShell() {
        return (floor, room, blockPos) -> {
            BlockState blockState = floor.getLevel().getBlockState(blockPos);
            if (blockState.hasProperty(MIMIC)) floor.getLevel().setBlock(blockPos, BuiltInRegistries.BLOCK.byId(blockState.getValue(MIMIC)).defaultBlockState(), 128);
            return false;
        };
    }

    public boolean isPosInsideShell(BlockPos pos) {
        for (BoundingBox box : this.boundingBoxes) {
            if (!box.isInside(pos)) continue;
            if (pos.getX() >= box.minX() + 1 && pos.getX() <= box.maxX() - 1 && pos.getZ() >= box.minZ() + 1 && pos.getZ() <= box.maxZ() - 1 && pos.getY() >= box.minY() + 1 && pos.getY() <= box.maxY() - 1) {
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
        return false;
    }

    public static @NotNull Set<ChunkPos> getChunkPosSet(List<BoundingBox> boundingBoxes, int inflation) {
        Set<ChunkPos> chunkPosSet = new HashSet<>();
        for (BoundingBox box : boundingBoxes) {
            BoundingBox inflatedBox = box.inflatedBy(inflation);
            ChunkPos min = new ChunkPos(new BlockPos(inflatedBox.minX(), inflatedBox.minY(), inflatedBox.minZ()));
            ChunkPos max = new ChunkPos(new BlockPos(inflatedBox.maxX(), inflatedBox.maxY(), inflatedBox.maxZ()));

            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    ChunkPos newPos = new ChunkPos(x, z);
                    chunkPosSet.add(newPos);
                }
            }
        }
        return chunkPosSet;
    }

    public void destroy() {
        unsetAttachedPoints();
        Set<ChunkPos> chunkPosSet = getChunkPosSet(this.boundingBoxes, 0);
        chunkPosSet.forEach(chunkPos -> {
            getBranch().getFloor().getChunkMap().get(chunkPos).remove(new Vector2i(this.getBranch().getIndex(), this.getIndex()));
        });
    }

    public void unsetAttachedPoints() {
        getConnectionPoints().forEach(connectionPoint -> {
            if (connectionPoint.isConnected()) {
                connectionPoint.getConnectedPoint().unSetConnectedPoint();
            }
        });
    }

    public void processConnectionPoints(DungeonFloor floor) {
        WildDungeons.getLogger().info("PROCESSING {} CONNECTION POINTS", connectionPoints.size());
        for (ConnectionPoint point : connectionPoints) {
            point.setupBlockstates(getOrientation(), getPosition(), this.getBranch().getFloor().getLevel());
            if (point.isConnected()) {
                templateBasedUnblock(floor, point);
                point.getConnectedPoint().unBlock(floor.getLevel());
            }
            if (!point.isConnected()) {
                point.block(floor.getLevel(), 0);
                point.removeDecal(this.getDecalTexture(), this.getDecalColor());
            }
        }
    }

    public void templateBasedUnblock(DungeonFloor floor, ConnectionPoint point) {
        point.unBlock(floor.getLevel());
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
            if (entries.isEmpty()) {
                return;
            }
            Offering next = entries.removeFirst().asOffering(this.getBranch().getFloor().getLevel());
            Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.position.getX(), this.position.getY(), this.position.getZ());
            WildDungeons.getLogger().info("ADDING OFFERING AT {}", pos1);
            next.setPos(pos1);
            this.getBranch().getFloor().getLevel().addFreshEntity(next);
            this.offeringUUIDs.add(next.getStringUUID());
        });
    }

    public void processLootBlocks() {
        if (this.getTemplate().lootBlocks().isEmpty()) {
            return;
        }
        //get all loot blocks from template
        List<StructureTemplate.StructureBlockInfo> lootBlocks = this.getTemplate().lootBlocks();
        //transform them to the real room's position
        List<BlockPos> potentialLootBlockPositions = lootBlocks.stream().map(b -> TemplateHelper.transform(b.pos(), this)).toList();
        //get all block entities at those positions
        List<BlockEntity> lootBlockEntities = new ArrayList<>(potentialLootBlockPositions.stream().map(pos -> this.getBranch().getFloor().getLevel().getBlockEntity(pos)).toList());
        //remove all null entities and entities that are not loot blocks, just in case
        lootBlockEntities.removeIf(entity -> Objects.isNull(entity) || !(entity instanceof BaseContainerBlockEntity));
        if (lootBlockEntities.isEmpty()) {
            return;
        }
        //get a random number, between 1 and the number of loot blocks, but not more than 5
        int countedChests = RandomUtil.randIntBetween(1, Math.min(5, lootBlockEntities.size()));
        //determine the amount of items, between 3 and 7 times the number of counted chests
        int maxItems = RandomUtil.randIntBetween(3 * countedChests, 7 * countedChests);
        //get loot entries from the loot table registry
        List<DungeonRegistration.ItemTemplate> entries = LootTableRegistry.BASIC_LOOT_TABLE.randomResults(maxItems, (int) (5 * this.getDifficulty()), 2f);
        //for each entry, place it in a random chest

        entries.forEach(entry -> {
            if (lootBlockEntities.isEmpty()) return;
            ItemStack lootStack = entry.asItemStack();
            //get a random chest
            BaseContainerBlockEntity lootBlock = (BaseContainerBlockEntity) lootBlockEntities.get(RandomUtil.randIntBetween(0, lootBlockEntities.size() - 1));
            ArrayList<Integer> emptySlots = new ArrayList<>();
            boolean isFull = true;
            do {
                //check to see if the chest is full
                for (int i = 0; i < lootBlock.getContainerSize(); i++) {
                    ItemStack stack = lootBlock.getItem(i);
                    if (stack.isEmpty()) {
                        isFull = false;
                        emptySlots.add(i);
                    } else if (stack.getItem() == lootStack.getItem() && stack.getCount()+lootStack.getCount() <= stack.getMaxStackSize()) {
                        stack.grow(lootStack.getCount());
                        lootBlock.setItem(i, stack);
                        return;
                    }
                }
                if (isFull) {
                    lootBlockEntities.remove(lootBlock);
                    if (lootBlockEntities.isEmpty()) return;
                    lootBlock = (BaseContainerBlockEntity) lootBlockEntities.get(RandomUtil.randIntBetween(0, lootBlockEntities.size() - 1));
                }
            } while (isFull);
            //place the loot in a random empty slot
            int slot = emptySlots.get(RandomUtil.randIntBetween(0, emptySlots.size() - 1));
            lootBlock.setItem(slot, lootStack);
        });
    }

    public void processDataMarkers(){
        if (this.getTemplate().dataMarkers().isEmpty()) {
            return;
        }
        this.getTemplate().dataMarkers().forEach(marker -> {
            BlockPos pos = TemplateHelper.transform(marker.pos(), this);
            assert marker.nbt() != null;//we null check when we register the template
            processDataMarker(pos, marker.nbt().getString("metadata"));
        });
    }

    public void processDataMarker(BlockPos pos, String metadata) {}

    public BlockPos getSpawnPoint(ServerLevel level) {
        return this.spawnPoint == null ? this.sampleSpawnablePositions(level, 1, 1).getFirst() : this.spawnPoint;
    }

    public List<BlockPos> sampleSpawnablePositions(ServerLevel level, int count, int deflation) {
        List<BlockPos> result = new ArrayList<>();
        int tries = count*4;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        while (result.size() < count && tries > 0) {
            BoundingBox randomBox = this.boundingBoxes.get(RandomUtil.randIntBetween(0, this.boundingBoxes.size()-1));
            deflation = Math.min(deflation, randomBox.getXSpan());
            deflation = Math.min(deflation, randomBox.getYSpan());
            deflation = Math.min(deflation, randomBox.getZSpan());
            BoundingBox innerShell = randomBox.inflatedBy(-deflation); //TODO cheating

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
        return result.isEmpty() ? spawnPoint == null ? Collections.singletonList(this.boundingBoxes.getFirst().getCenter()) : Collections.singletonList(spawnPoint) : result;
    }

    public BlockPos calculateFurthestPoint(List<BlockPos> validPoints, int maxDistance) {
        return validPoints.stream().map(pos -> {
            int score = 0;

            for (WDPlayer wdPlayer : getActivePlayers()) {
                ServerPlayer player = wdPlayer.getServerPlayer();
                if (player!=null) {
                    int dist = pos.distManhattan(player.blockPosition());
                    score += dist;
                    if (dist > maxDistance) score -= 1000;
                }
            }

            return new Pair<>(pos, score);

        }).max(Comparator.comparingInt(Pair::getSecond)).get().getFirst();
    }

    public BlockPos calculateClosestPoint(List<BlockPos> validPoints, int minDistance) {
        return validPoints.stream().map(pos -> {
            int score = 0;

            for (WDPlayer wdPlayer : getActivePlayers()) {
                ServerPlayer player = wdPlayer.getServerPlayer();
                if (player!=null){
                    int dist = pos.distManhattan(player.blockPosition());
                    score += dist;
                    if (dist < minDistance) score -= 1000;
                }
            }

            return new Pair<>(pos, score);
        }).min(Comparator.comparingInt(Pair::getSecond)).get().getFirst();
    }

    public ResourceLocation getDecalTexture() {return null;}

    public int getDecalColor() {return 0xFFFFFFFF;}

    public void onGenerate() {}

    public void onEnter(WDPlayer player) {
        WildDungeons.getLogger().info("ENTERING ROOM {} OF CLASS {}", this.getTemplate().name(), this.getClass().getSimpleName());
        playersInside.computeIfAbsent(player.getUUID(), key -> {
            getSession().getStats(key).roomsFound += 1;
            return true;
        });
        this.playersInside.put(player.getUUID(), true);
        player.setSoundScape(this.getProperty(SOUNDSCAPE), this.getProperty(INTENSITY), false);
    }

    public void onBranchEnter(WDPlayer player) {
        if (!lootGenerated){
            this.processLootBlocks();
            lootGenerated = true;
        }
    }

    public void onBranchComplete(){
        processDataMarkers();
    }

    public void onExit(WDPlayer player) {
        this.playersInside.put(player.getUUID(), false);
    }

    public void onClear() {
        this.clear = true;
        if (this.getProperty(DESTRUCTION_RULE) == DungeonRoomTemplate.DestructionRule.SHELL_CLEAR) CompletableFuture.runAsync(() -> this.removeProtection());
    }

    public void reset() {}

    public void tick() {}
}