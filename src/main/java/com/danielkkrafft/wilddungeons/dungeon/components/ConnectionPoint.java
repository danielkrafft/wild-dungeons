package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Vector2i;

import java.util.*;

public class ConnectionPoint {
    private String pool;
    private String type;

    private DungeonRoom room;
    private Direction direction;
    private boolean connected;
    private int failures;
    private int score;
    private int distanceToBranchOrigin;
    private int distanceToFloorOrigin;
    private int distanceToYTarget;

    private BoundingBox boundingBox;
    private List<BlockPos> positions;
    private HashMap<BlockPos, BlockState> unBlockedBlockStates;

    public String getType() {return this.type;}
    public void setType(String type) {this.type = type;}
    public void setPool(String pool) {this.pool = pool;}

    public DungeonRoom getRoom() {return this.room;}
    public void setRoom(DungeonRoom room) {this.room = room;}

    public Direction getDirection() {return this.direction;}
    public Vec3i getNormal() {return this.direction.getNormal();}
    public Direction.Axis getAxis() {return this.direction.getAxis();}

    public boolean isConnected() {return this.connected;}
    public void setConnected(boolean connected) {this.connected = connected;}

    public void incrementFailures() {this.failures += 1;}
    public void addPosition(BlockPos pos) {this.positions.add(pos); this.boundingBox.encapsulate(pos);}
    public List<BlockPos> getPositions() {return this.positions;}


    private ConnectionPoint(BlockPos position, Direction direction) {
        this.pool = "all";
        this.type = "both";

        this.room = null;
        this.direction = direction;
        this.connected = false;
        this.failures = 0;
        this.score = 0;
        this.distanceToBranchOrigin = 0;
        this.distanceToFloorOrigin = 0;
        this.distanceToYTarget = 0;

        this.boundingBox = new BoundingBox(position);
        this.positions = new ArrayList<>();
        positions.add(position);
        this.unBlockedBlockStates = new HashMap<>();
    }

    private ConnectionPoint(ConnectionPoint point) {
        this.pool = point.pool;
        this.type = point.type;

        this.room = point.room;
        this.direction = point.direction;
        this.connected = point.connected;
        this.failures = point.failures;
        this.score = point.score;
        this.distanceToBranchOrigin = point.distanceToBranchOrigin;
        this.distanceToFloorOrigin = point.distanceToFloorOrigin;
        this.distanceToYTarget = point.distanceToYTarget;

        this.boundingBox = point.boundingBox;
        this.positions = point.positions;
        this.unBlockedBlockStates = point.unBlockedBlockStates;
    }

    public static ConnectionPoint create(BlockPos position, Direction direction) {
        return new ConnectionPoint(position, direction);
    }

    public static ConnectionPoint copy(ConnectionPoint point) {
        return new ConnectionPoint(point);
    }

    public void transform(StructurePlaceSettings settings, BlockPos position, BlockPos offset) {
        direction = TemplateHelper.mirrorDirection(direction, settings.getMirror());
        direction = TemplateHelper.rotateDirection(direction, settings.getRotation());

        positions = positions.stream().map(pos -> StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset).offset(position)).toList();
        boundingBox = new BoundingBox(positions.getFirst());
        positions.forEach((pos) -> boundingBox.encapsulate(pos));

        HashMap<BlockPos, BlockState> newBlockStates = new HashMap<>();
        unBlockedBlockStates.forEach((pos, state) -> {
           newBlockStates.put(StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset).offset(position), state);
        });
        unBlockedBlockStates = newBlockStates;
    }

    public static boolean arePointsCompatible(ConnectionPoint en, ConnectionPoint ex) {
        List<Boolean> conditions = List.of(
                !ex.connected,
                !Objects.equals(ex.type, "entrance"),
                ex.failures < 10,
                Objects.equals(en.pool, ex.pool),
                en.getAxis() != Direction.Axis.Y || ex.direction == en.direction.getOpposite(),
                en.getSize().equals(ex.getSize())
        );
        return conditions.stream().allMatch(condition -> condition);
    }

    public static Pair<ConnectionPoint, StructurePlaceSettings> selectBestPoint(List<Pair<ConnectionPoint, StructurePlaceSettings>> pointPool, DungeonBranch branch, int yTarget, double branchWeight, double floorWeight, double heightWeight, double randomWeight) {
        int totalDistanceToBranchOrigin = 0;
        int totalDistanceToFloorOrigin = 0;
        int totalDistanceToYTarget = 0;

        for (ConnectionPoint point : pointPool.stream().map(Pair::getFirst).toList()) {
            point.score = 0;
            BlockPos pointOrigin = new BlockPos(point.boundingBox.minX(), point.boundingBox.minY(), point.boundingBox.minZ());

            point.distanceToYTarget = Math.abs(pointOrigin.getY() - yTarget);
            point.distanceToFloorOrigin = pointOrigin.distManhattan(branch.floor.origin);
            point.distanceToBranchOrigin = branch.dungeonRooms.isEmpty() ? 0 : pointOrigin.distManhattan(branch.dungeonRooms.getFirst().position);
            totalDistanceToBranchOrigin += point.distanceToBranchOrigin;
            totalDistanceToFloorOrigin += point.distanceToFloorOrigin;
            totalDistanceToYTarget += point.distanceToYTarget;
        }

        for (ConnectionPoint point : pointPool.stream().map(Pair::getFirst).toList()) {
            point.score += (int) (branchWeight * point.distanceToBranchOrigin / totalDistanceToBranchOrigin);
            point.score += (int) (floorWeight * point.distanceToFloorOrigin / totalDistanceToFloorOrigin);
            point.score -= (int) (heightWeight * point.distanceToYTarget / totalDistanceToYTarget);
            point.score += (int) (randomWeight * Math.random());
        }

        pointPool.sort(Comparator.comparingInt(a -> a.getFirst().score));
        return pointPool.getLast();
    }

    public static BlockPos getOffset(ConnectionPoint en, ConnectionPoint ex) {
        return new BlockPos(ex.boundingBox.minX() - en.boundingBox.minX(), ex.boundingBox.minY() - en.boundingBox.minY(), ex.boundingBox.minZ() - en.boundingBox.minZ());
    }

    public Vector2i getSize() {
        int x = this.boundingBox.getXSpan();
        int y = this.boundingBox.getYSpan();
        int z = this.boundingBox.getZSpan();

        return switch (this.direction) {
            case UP, DOWN -> new Vector2i(x, z);
            case NORTH, SOUTH -> new Vector2i(x, y);
            case EAST, WEST -> new Vector2i(z, y);
        };
    }

    public void setupBlockstates(ServerLevel level, StructurePlaceSettings settings) {
        for (BlockPos pos : this.positions) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                BlockState blockState;
                try { blockState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), connectionBlockEntity.unblockedBlockstate, true).blockState();
                } catch (CommandSyntaxException e) { blockState = Blocks.AIR.defaultBlockState();}

                if (blockState.getBlock().equals(Blocks.STONE_BRICKS)) blockState = this.room.material.getBasic(1);
                if (blockState.getBlock().equals(Blocks.STONE_BRICK_STAIRS)) blockState = this.room.material.getStair(1)
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                        .setValue(BlockStateProperties.HALF, blockState.getValue(BlockStateProperties.HALF))
                        .setValue(BlockStateProperties.STAIRS_SHAPE, blockState.getValue(BlockStateProperties.STAIRS_SHAPE));

                blockState = TemplateHelper.fixBlockStateProperties(blockState, settings);
                this.unBlockedBlockStates.put(pos, blockState);
            }
        }
    }

    public void block(ServerLevel level) {
        positions.forEach((pos) -> {
            WildDungeons.getLogger().info("BLOCKING POSITION {} THIS POINT IS CONNECTED? {}", pos, this.connected);
            level.setBlock(pos, this.room.material.getBasic(1), 2);
        });
    }

    public void unBlock(ServerLevel level) {
        unBlockedBlockStates.forEach((pos, blockState) -> {
            WildDungeons.getLogger().info("UNBLOCKING POSITION {} THIS POINT IS CONNECTED? {}", pos, this.connected);
            level.setBlock(pos, blockState, 2);
        });
    }
}