package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.components.room.DungeonRoom;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
    private String pool = "all";
    private String type = "both";

    private DungeonRoom room = null;
    private Direction direction;
    private ConnectionPoint connectedPoint = null;
    private int failures = 0;

    private BoundingBox boundingBox;
    private List<BlockPos> positions = new ArrayList<>();
    private HashMap<BlockPos, BlockState> unBlockedBlockStates = new HashMap<>();

    public String getType() {return this.type;}
    public void setType(String type) {this.type = type;}
    public void setPool(String pool) {this.pool = pool;}
    public DungeonRoom getRoom() {return this.room;}
    public void setRoom(DungeonRoom room) {this.room = room;}
    public Direction getDirection() {return this.direction;}
    public Vec3i getNormal() {return this.direction.getNormal();}
    public Direction.Axis getAxis() {return this.direction.getAxis();}
    public boolean isConnected() {return this.connectedPoint != null;}
    public ConnectionPoint getConnectedPoint() {return this.connectedPoint;}
    public void setConnectedPoint(ConnectionPoint connectedPoint) {this.connectedPoint = connectedPoint;}
    public void incrementFailures() {this.failures += 1;}
    public BlockPos getOrigin() {return new BlockPos(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ());}
    public void addPosition(BlockPos pos) {this.positions.add(pos); this.boundingBox.encapsulate(pos);}
    public List<BlockPos> getPositions() {return this.positions;}
    private ConnectionPoint() {}

    public static ConnectionPoint create(BlockPos position, Direction direction) {
        ConnectionPoint newPoint = new ConnectionPoint();
        newPoint.direction = direction;
        newPoint.boundingBox = new BoundingBox(position);
        newPoint.positions.add(position);

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::create");
        return newPoint;
    }

    public static ConnectionPoint copy(ConnectionPoint oldPoint) {
        ConnectionPoint newPoint = new ConnectionPoint();
        newPoint.pool = oldPoint.pool;
        newPoint.type = oldPoint.type;

        newPoint.room = oldPoint.room;
        newPoint.direction = oldPoint.direction;
        newPoint.connectedPoint = oldPoint.connectedPoint;
        newPoint.failures = oldPoint.failures;

        newPoint.boundingBox = oldPoint.boundingBox;
        newPoint.positions = oldPoint.positions;
        newPoint.unBlockedBlockStates = oldPoint.unBlockedBlockStates;

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::copy");
        return newPoint;
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
        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::transform");
    }

    public static boolean arePointsCompatible(DungeonComponents.DungeonRoomTemplate nextRoom, ConnectionPoint en, ConnectionPoint ex, boolean bypassFailures) {
        List<Boolean> conditions = List.of(
                !ex.isConnected(),
                !Objects.equals(ex.type, "entrance"),
                ex.failures < 10 || bypassFailures,
                Objects.equals(en.pool, ex.pool),
                en.getAxis() != Direction.Axis.Y || ex.direction == en.direction.getOpposite(),
                en.getSize().equals(ex.getSize()),
                nextRoom.getBoundingBoxes(TemplateHelper.EMPTY_DUNGEON_SETTINGS, TemplateHelper.EMPTY_BLOCK_POS).stream().allMatch(box -> Mth.abs(EmptyGenerator.MIN_Y - ex.boundingBox.minY()) > box.getYSpan())

        );

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::arePointsCompatible");
        return conditions.stream().allMatch(condition -> condition);
    }

    public static Pair<ConnectionPoint, StructurePlaceSettings> selectBestPoint(List<Pair<ConnectionPoint, StructurePlaceSettings>> pointPool, DungeonBranch branch, int yTarget, double branchWeight, double floorWeight, double heightWeight, double randomWeight) {
        int totalBranchDistance = pointPool.stream().mapToInt(pair -> branch.dungeonRooms.isEmpty() ? 0 : pair.getFirst().getOrigin().distManhattan(branch.dungeonRooms.getFirst().position)).sum();
        int totalFloorDistance = pointPool.stream().mapToInt(pair -> pair.getFirst().getOrigin().distManhattan(branch.floor.origin)).sum();
        int totalHeightDistance = pointPool.stream().mapToInt(pair -> Math.abs(pair.getFirst().getOrigin().getY() - yTarget)).sum();

        Pair<ConnectionPoint, StructurePlaceSettings> result = pointPool.stream().map(pair -> {
            int distanceToBranchOrigin = branch.dungeonRooms.isEmpty() ? 0 : pair.getFirst().getOrigin().distManhattan(branch.dungeonRooms.getFirst().position);
            int distanceToFloorOrigin = pair.getFirst().getOrigin().distManhattan(branch.floor.origin);
            int distanceToYTarget = Math.abs(pair.getFirst().getOrigin().getY() - yTarget);

            int score = 0;
            score += (int) (branchWeight * distanceToBranchOrigin / totalBranchDistance);
            score += (int) (floorWeight * distanceToFloorOrigin / totalFloorDistance);
            score += (int) (heightWeight * distanceToYTarget / totalHeightDistance);
            score += (int) (randomWeight * Math.random());

            return new Pair<>(pair, score);
        }).max(Comparator.comparingInt(Pair::getSecond)).map(Pair::getFirst).orElse(null);

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::selectBestPoint");
        return result;
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

                if (blockState.getBlock().equals(Blocks.STONE_BRICKS)) blockState = this.room.material.getBasic(0);
                if (blockState.getBlock().equals(Blocks.STONE_BRICK_STAIRS)) blockState = this.room.material.getStair(0)
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                        .setValue(BlockStateProperties.HALF, blockState.getValue(BlockStateProperties.HALF))
                        .setValue(BlockStateProperties.STAIRS_SHAPE, blockState.getValue(BlockStateProperties.STAIRS_SHAPE));

                WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::setupBlockstates");
                blockState = TemplateHelper.fixBlockStateProperties(blockState, settings);
                this.unBlockedBlockStates.put(pos, blockState);
            }
        }

        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::setupBlockstates");
    }

    public void block(ServerLevel level) {
        positions.forEach((pos) -> level.setBlock(pos, this.room.material.getBasic(0), 2));
        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::block");
    }

    public void hide(ServerLevel level) {
        positions.forEach((pos) -> level.setBlock(pos, this.room.material.getHidden(0), 2));
    }

    public void unBlock(ServerLevel level) {
        unBlockedBlockStates.forEach((pos, blockState) -> level.setBlock(pos, blockState, 2));
        WDProfiler.INSTANCE.logTimestamp("ConnectionPoint::unBlock");
    }
}