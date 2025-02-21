package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBedrockBlock;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.render.DecalRenderer;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;

public class ConnectionPoint {
    public static final ResourceLocation SWORD_TEXTURE = WildDungeons.rl("textures/item/white_sword.png");
    public static final ResourceLocation CHEST_TEXTURE = WildDungeons.rl("textures/item/white_chest.png");

    private String pool = "all";
    private String type = "both";

    private int roomIndex;
    private int connectedBranchIndex;
    private int connectedRoomIndex;
    private int branchIndex;
    private int floorIndex;
    private String sessionKey;
    private int index;
    private int connectedPointIndex = -1;
    private String direction;

    private BoundingBox boundingBox;
    private HashMap<BlockPos, String> unBlockedBlockStates = new HashMap<>();

    @Serializer.IgnoreSerialization private DungeonRoom room = null;
    @Serializer.IgnoreSerialization public StructurePlaceSettings tempSettings = null;

    public String getType() {return this.type;}
    public void setType(String type) {this.type = type;}
    public void setPool(String pool) {this.pool = pool;}
    public int getRoomIndex() {return this.roomIndex;}
    public int getBranchIndex() {return this.branchIndex;}
    public DungeonRoom getRoom() {
        return this.room != null ? this.room :
                DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey)
                        .getFloors().get(this.floorIndex)
                        .getBranches().get(this.branchIndex)
                        .getRooms().get(this.roomIndex);
    }
    public void setRoom(DungeonRoom room) {this.room = room; this.roomIndex = room.getIndex(); this.branchIndex = room.getBranch().getIndex(); this.floorIndex = room.getBranch().getFloor().getIndex(); this.sessionKey = room.getSession().getSessionKey();}
    public boolean isConnected() {return this.connectedPointIndex != -1;}
    public ConnectionPoint getConnectedPoint() {
        if (!this.isConnected()) return null;
        return this.getRoom().getBranch().getFloor().getBranches().get(this.connectedBranchIndex).getRooms().get(this.connectedRoomIndex).getConnectionPoints().get(this.connectedPointIndex);
    }
    public void setConnectedPoint(ConnectionPoint connectedPoint) {
        this.connectedPointIndex = connectedPoint.index;
        this.connectedRoomIndex = connectedPoint.getRoom().getIndex();
        this.connectedBranchIndex = connectedPoint.getRoom().getBranch().getIndex();}
    public BlockPos getOrigin(StructurePlaceSettings settings, BlockPos position) {BoundingBox transBox = getBoundingBox(settings, position); return new BlockPos(transBox.minX(), transBox.minY(), transBox.minZ());}
    public void addPosition(BlockPos pos) {this.boundingBox.encapsulate(pos);}
    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return this.index;}
    private ConnectionPoint() {}

    public BoundingBox getBoundingBox(StructurePlaceSettings settings, BlockPos position) {
        BlockPos min = StructureTemplate.transform(new BlockPos(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ()), settings.getMirror(), settings.getRotation(), TemplateHelper.EMPTY_BLOCK_POS);
        BlockPos max = StructureTemplate.transform(new BlockPos(this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ()), settings.getMirror(), settings.getRotation(), TemplateHelper.EMPTY_BLOCK_POS);

        BoundingBox result = new BoundingBox(Math.min(min.getX(), max.getX()) + position.getX(), Math.min(min.getY(), max.getY()) + position.getY(), Math.min(min.getZ(), max.getZ()) + position.getZ(), Math.max(max.getX(), min.getX()) + position.getX(), Math.max(max.getY(), min.getY()) + position.getY(), Math.max(max.getZ(), min.getZ()) + position.getZ());
        return result;
    }

    public List<BlockPos> getPositions(StructurePlaceSettings settings, BlockPos position) {
        List<BlockPos> result = new ArrayList<>();
        BoundingBox transBox = getBoundingBox(settings, position);
        for (int x = transBox.minX(); x <= transBox.maxX(); x++) {
            for (int y = transBox.minY(); y <= transBox.maxY(); y++) {
                for (int z = transBox.minZ(); z <= transBox.maxZ(); z++) {
                    result.add(new BlockPos(x, y , z));
                }
            }
        }
        return result;
    }

    public Direction getDirection(StructurePlaceSettings settings) {
        Direction direction;
        direction = TemplateHelper.mirrorDirection(Direction.byName(this.direction), settings.getMirror());
        direction = TemplateHelper.rotateDirection(Direction.byName(direction.getName()), settings.getRotation());
        return direction;
    }

    public static ConnectionPoint create(BlockPos position, Direction direction) {
        ConnectionPoint newPoint = new ConnectionPoint();
        newPoint.direction = direction.getName();
        newPoint.boundingBox = new BoundingBox(position);

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::create");
        return newPoint;
    }

    public static ConnectionPoint copy(ConnectionPoint oldPoint) {
        CompoundTag oldTag = Serializer.toCompoundTag(oldPoint);
        ConnectionPoint newPoint = Serializer.fromCompoundTag(oldTag);
        newPoint.room = oldPoint.room;
        return newPoint;
    }

    public static BlockState blockStateFromString(String state) {
        BlockState blockState;
        try { blockState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), state, true).blockState();
        } catch (CommandSyntaxException e) { blockState = Blocks.AIR.defaultBlockState();}
        return blockState;
    }

    public static String toString(BlockState state) {
        return BlockStateParser.serialize(state);
    }

    public static boolean arePointsCompatible(ConnectionPoint en, ConnectionPoint ex) {
        List<Boolean> conditions = List.of(
                !ex.isConnected(),
                !Objects.equals(ex.type, "entrance"),
                Objects.equals(en.pool, ex.pool),
                en.getDirection(TemplateHelper.EMPTY_DUNGEON_SETTINGS).getAxis() != Direction.Axis.Y || ex.getDirection(ex.getRoom().getSettings()).getName().equals(en.getDirection(TemplateHelper.EMPTY_DUNGEON_SETTINGS).getOpposite().getName()),
                en.getSize(TemplateHelper.EMPTY_DUNGEON_SETTINGS, TemplateHelper.EMPTY_BLOCK_POS).equals(ex.getSize(ex.getRoom().getSettings(), ex.getRoom().getPosition()))
        );

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::arePointsCompatible");
        return conditions.stream().allMatch(condition -> condition);
    }

    public static ConnectionPoint selectBestPoint(List<ConnectionPoint> pointPool, DungeonBranch branch, int yTarget, double branchWeight, double floorWeight, double heightWeight, double randomWeight) {
        int totalBranchDistance = pointPool.stream().mapToInt(point -> branch.getRooms().isEmpty() ? 0 : point.getOrigin(point.getRoom().getSettings(), point.getRoom().getPosition()).distManhattan(branch.getRooms().getFirst().getPosition())).sum();
        int totalFloorDistance = pointPool.stream().mapToInt(point -> point.getOrigin(point.getRoom().getSettings(), point.getRoom().getPosition()).distManhattan(branch.getFloor().getOrigin())).sum();
        int totalHeightDistance = pointPool.stream().mapToInt(point -> Math.abs(point.getOrigin(point.getRoom().getSettings(), point.getRoom().getPosition()).getY() - yTarget)).sum();

        return pointPool.stream().map(point -> {
            int distanceToBranchOrigin = branch.getRooms().isEmpty() ? 0 : point.getOrigin(point.getRoom().getSettings(), point.getRoom().getPosition()).distManhattan(branch.getRooms().getFirst().getPosition());
            int distanceToFloorOrigin = point.getOrigin(point.getRoom().getSettings(), point.getRoom().getPosition()).distManhattan(branch.getFloor().getOrigin());
            int distanceToYTarget = Math.abs(point.getOrigin(point.getRoom().getSettings(), point.getRoom().getPosition()).getY() - yTarget);

            int score = 0;
            score += (int) (branchWeight * distanceToBranchOrigin / totalBranchDistance);
            score += (int) (floorWeight * distanceToFloorOrigin / totalFloorDistance);
            score += (int) (heightWeight * distanceToYTarget / totalHeightDistance);
            score += (int) (randomWeight * Math.random());

            return new Pair<>(point, score);
        }).max(Comparator.comparingInt(Pair::getSecond)).map(Pair::getFirst).orElse(null);
    }

    public static BlockPos getOffset(StructurePlaceSettings settings, BlockPos position, ConnectionPoint en, ConnectionPoint ex) {
        BoundingBox enTransBox = en.getBoundingBox(settings, position);
        BoundingBox exTransBox = ex.getBoundingBox(ex.room.getSettings(), ex.room.getPosition());
        return new BlockPos(exTransBox.minX() - enTransBox.minX(), exTransBox.minY() - enTransBox.minY(), exTransBox.minZ() - enTransBox.minZ());
    }

    public Vector2i getSize(StructurePlaceSettings settings, BlockPos position) {
        int x = this.getBoundingBox(settings, position).getXSpan();
        int y = this.getBoundingBox(settings, position).getYSpan();
        int z = this.getBoundingBox(settings, position).getZSpan();

        Vector2i result = switch (getDirection(settings)) {
            case UP, DOWN -> new Vector2i(x, z);
            case NORTH, SOUTH -> new Vector2i(x, y);
            case EAST, WEST -> new Vector2i(z, y);
        };

        return result;
    }

    public void setupBlockstates(StructurePlaceSettings settings, BlockPos position, ServerLevel level) {
        for (BlockPos pos : this.getPositions(settings, position)) {
            BlockEntity blockEntity = level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);

            if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                BlockState blockState = blockStateFromString(connectionBlockEntity.unblockedBlockstate);
                blockState = this.getRoom().getMaterial().replace(blockState);
                connectionBlockEntity.unblockedBlockstate = toString(blockState);
                this.unBlockedBlockStates.put(pos, connectionBlockEntity.unblockedBlockstate);
            }
        }

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::setupBlockstates");
    }

    public void block(ServerLevel level, int flags) {
        this.getRoom().getActivePlayers().forEach(wdPlayer -> {
            ServerPlayer player = wdPlayer.getServerPlayer();
            if (player!=null && this.getBoundingBox(this.getRoom().getSettings(), this.getRoom().getPosition()).isInside(player.blockPosition())) {
                Vec3 position = wdPlayer.getServerPlayer().position();
                Vec3i normal = this.getConnectedPoint().getDirection(this.getConnectedPoint().getRoom().getSettings()).getNormal();
                WildDungeons.getLogger().info("CONNECTED POINT NORMAL: {}", normal);
                Vec3 newPosition = new Vec3(
                        position.get(Direction.Axis.X) + normal.getX() * 1.5f,
                        position.get(Direction.Axis.Y) + normal.getY() * 1.5f,
                        position.get(Direction.Axis.Z) + normal.getZ() * 1.5f);
                WildDungeons.getLogger().info("MOVING FROM {} TO {}", position, newPosition);
                wdPlayer.getServerPlayer().moveTo(newPosition);
            }
        });
        getPositions(this.getRoom().getSettings(), this.getRoom().getPosition()).forEach((pos) -> {
            if (this.getRoom().getProperty(HierarchicalProperty.DESTRUCTION_RULE).equals(DungeonRoomTemplate.DestructionRule.SHELL) || (this.getRoom().getProperty(HierarchicalProperty.DESTRUCTION_RULE).equals(DungeonRoomTemplate.DestructionRule.SHELL_CLEAR) && !this.getRoom().isClear())) {
                level.setBlock(pos, WDBedrockBlock.of(this.getRoom().getMaterial().getBasic(getRoom().getProperty(HierarchicalProperty.BLOCKING_MATERIAL_INDEX)).getBlock()), flags);
            } else {
                level.setBlock(pos, this.getRoom().getMaterial().getBasic(getRoom().getProperty(HierarchicalProperty.BLOCKING_MATERIAL_INDEX)), flags);
            }
        });
        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::block");
    }

    public Vector3f getAveragePosition() {
        BoundingBox box = this.getBoundingBox(this.getRoom().getSettings(), this.getRoom().getPosition());
        return new Vector3f(box.minX() + (float) box.getXSpan() /2, box.minY() + (float) box.getYSpan() /2, box.minZ() + (float) box.getZSpan() /2);
    }

    public void complete() {
        this.room = null;
    }

    public void hide(ServerLevel level) {
        getPositions(this.getRoom().getSettings(), this.getRoom().getPosition()).forEach((pos) -> level.setBlock(pos, this.getRoom().getMaterial().getHidden(0), 2));
    }

    public void unBlock(ServerLevel level) {
        unBlockedBlockStates.forEach((pos, blockState) -> level.setBlock(pos, TemplateHelper.fixBlockStateProperties(blockStateFromString(blockState), this.getRoom().getSettings()), 2));
    }

    public void loadingBlock(ServerLevel level) {
        unBlockedBlockStates.forEach((pos, blockState) -> level.setBlock(pos, WDBedrockBlock.of(Blocks.REDSTONE_BLOCK), 2));
    }

    public void addDecal(ResourceLocation texture, int color) {
        DecalRenderer.addServerDecal(this.getDecal(texture, color));
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ClientPacketHandler.Packets.ADD_DECAL.toString());
        tag.put("decal", Serializer.toCompoundTag(this.getDecal(texture, color)));
        PacketDistributor.sendToAllPlayers(new SimplePacketManager.ClientboundTagPacket(tag));
    }

    public void removeDecal(ResourceLocation texture, int color) {
        DecalRenderer.removeServerDecal(this.getDecal(texture, color));
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ClientPacketHandler.Packets.REMOVE_DECAL.toString());
        tag.put("decal", Serializer.toCompoundTag(this.getDecal(texture, color)));
        PacketDistributor.sendToAllPlayers(new SimplePacketManager.ClientboundTagPacket(tag));
    }

    public DecalRenderer.Decal getDecal(ResourceLocation texture, int color) {
        if (texture == null) return null;
        Vector3f avgPosition = this.getAveragePosition();
        BoundingBox box = this.getBoundingBox(this.getRoom().getSettings(), this.getRoom().getPosition());
        Direction.Axis axis = this.getDirection(this.getRoom().getSettings()).getAxis();
        float width = 1.0f;
        float height = 1.0f;
        switch (axis) {
            case X -> {
                width = box.getZSpan();
                height = box.getYSpan();
            }
            case Y -> {
                width = box.getXSpan();
                height = box.getZSpan();
            }
            case Z -> {
                width = box.getXSpan();
                height = box.getYSpan();
            }
        }
        return new DecalRenderer.Decal(texture, avgPosition.x, avgPosition.y, avgPosition.z, Math.min(width, height) * 0.75f, Math.min(width, height) * 0.75f, axis, color, this.getRoom().getBranch().getFloor().getLevelKey());
    }

    public void unSetConnectedPoint() {
        if (this.getRoom().getDecalTexture() != null) this.removeDecal(this.getRoom().getDecalTexture(), this.getRoom().getDecalColor());
        this.connectedPointIndex = -1;
        this.connectedBranchIndex = -1;
        this.connectedRoomIndex = -1;
    }

    public int getConnectedBranchIndex() {
        return this.connectedBranchIndex;
    }
}