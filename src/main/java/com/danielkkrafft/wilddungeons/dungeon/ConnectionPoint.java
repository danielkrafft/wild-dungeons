package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionPoint {
    public DungeonRoom room = null;
    public BoundingBox boundingBox;
    public List<BlockPos> positions;
    public HashMap<BlockPos, BlockState> lockedBlockStates = new HashMap<>();
    public HashMap<BlockPos, BlockState> unlockedBlockStates = new HashMap<>();
    public Direction direction;

    public String pool = "all";
    public boolean rotated = false;
    public boolean locked = true;
    public boolean occupied = false;
    public int failures = 0;

    public ConnectionPoint(BlockPos position, Direction direction) {
        this.boundingBox = new BoundingBox(position);
        this.positions = new ArrayList<>();
        positions.add(position);
        this.direction = direction;
    }

    public ConnectionPoint(ConnectionPoint point) {
        this.room = point.room;
        this.boundingBox = point.boundingBox;
        this.positions = point.positions;
        this.direction = point.direction;

        this.pool = point.pool;
        this.rotated = point.rotated;
        this.locked = point.locked;
        this.occupied = point.occupied;
        this.failures = point.failures;
    }

    public ConnectionPoint transformed(StructurePlaceSettings settings, BlockPos position, BlockPos offset) {
        ConnectionPoint newPoint = new ConnectionPoint(this);

        newPoint.direction = mirrorDirection(newPoint.direction, settings.getMirror());
        newPoint.direction = rotateDirection(newPoint.direction, settings.getRotation());

        newPoint.rotated = settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90;
        newPoint.positions = newPoint.positions.stream().map(pos -> StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset).offset(position)).toList();
        newPoint.boundingBox = new BoundingBox(newPoint.positions.getFirst());
        newPoint.positions.forEach((pos) -> newPoint.boundingBox.encapsulate(pos));

        WildDungeons.getLogger().info("CONVERTING INPUT POINT: {} INTO TRANSFORMED POINT {}", this.direction, newPoint.direction);
        return newPoint;
    }

    public void lock(ServerLevel level) {
        lockedBlockStates.forEach((pos, blockState) -> {
            level.setBlock(pos, blockState, 2);
        });
    }

    public void unlock(ServerLevel level) {
        unlockedBlockStates.forEach((pos, blockState) -> {
            level.setBlock(pos, blockState, 2);
        });
    }

    public Direction mirrorDirection(Direction direction, Mirror mirror) {
        switch (mirror) {
            case FRONT_BACK:
                if (direction == Direction.WEST || direction == Direction.EAST) {return direction.getOpposite();}
                break;
            case LEFT_RIGHT:
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {return direction.getOpposite();}
                break;
        }
        return direction;
    }

    public Direction rotateDirection(Direction direction, Rotation rotation) {
        if (direction.getAxis().equals(Direction.Axis.Y)) return direction;
        return switch (rotation) {
            case CLOCKWISE_90 -> direction.getClockWise();
            case CLOCKWISE_180 -> direction.getOpposite();
            case COUNTERCLOCKWISE_90 -> direction.getCounterClockWise();
            default -> direction;
        };
    }

//    public Vec3i getAveragePosition() {
//        int totalX = 0;
//        int totalY = 0;
//        int totalZ = 0;
//
//        for (BlockPos pos : positions) {
//            totalX += pos.getX();
//            totalY += pos.getY();
//            totalZ += pos.getZ();
//        }
//
//        return new Vec3i(totalX / positions.size(), totalY / positions.size(), totalZ / positions.size());
//    }

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

    public static List<ConnectionPoint> locateConnectionPoints(StructureTemplate template, BoundingBox boundingBox) {
        List<ConnectionPoint> connectionPoints = new ArrayList<>();

        List<StructureTemplate.StructureBlockInfo> CONNECTION_BLOCKS = template.filterBlocks(new BlockPos(0, 0, 0), new StructurePlaceSettings(), WDBlocks.CONNECTION_BLOCK.get());
        for (StructureTemplate.StructureBlockInfo block : CONNECTION_BLOCKS) {

            Direction blockDirection;
            if (block.pos().getY() == boundingBox.maxY()) {
                blockDirection = Direction.UP;
            } else if (block.pos().getY() == boundingBox.minY()) {
                blockDirection = Direction.DOWN;
            } else if (block.pos().getZ() == boundingBox.minZ()) {
                blockDirection = Direction.NORTH;
            } else if (block.pos().getZ() == boundingBox.maxZ()) {
                blockDirection = Direction.SOUTH;
            } else if (block.pos().getX() == boundingBox.minX()) {
                blockDirection = Direction.WEST;
            } else if (block.pos().getX() == boundingBox.maxX()) {
                blockDirection = Direction.EAST;
            } else {
                continue;
            }

            ConnectionPoint targetPoint = null;
            for (ConnectionPoint point : connectionPoints) {
                if (point.direction != blockDirection) continue;
                if (point.positions.stream().noneMatch(pos -> block.pos().closerThan(pos, 2.0))) continue;
                targetPoint = point;
            }

            if (targetPoint == null) {
                targetPoint = new ConnectionPoint(block.pos(), blockDirection);
                targetPoint.pool = block.nbt().getString("pool");
                connectionPoints.add(targetPoint);
            } else {
                targetPoint.positions.add(block.pos());
                targetPoint.boundingBox.encapsulate(block.pos());
            }
        }

        return connectionPoints;
    }
}
