package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPoint {
    public List<BlockPos> positions;
    private Direction direction;
    public boolean occupied = false;
    public DungeonRoom room = null;
    public BoundingBox boundingBox;
    public int failures = 0;
    public String pool = "all";
    public boolean rotated = false;

    public ConnectionPoint(BlockPos position, Direction direction) {
        this.positions = new ArrayList<>();
        positions.add(position);
        this.direction = direction;
        this.boundingBox = new BoundingBox(position);
    }

    public ConnectionPoint(ConnectionPoint point) {
        this.positions = point.positions;
        this.direction = point.direction;
        this.occupied = point.occupied;
        this.room = point.room;
        this.boundingBox = point.boundingBox;
        this.pool = point.pool;
        this.rotated = point.rotated;
    }

    public void addPosition(BlockPos pos) {
        positions.add(pos);
    }

    public void setDirection(Direction direction) {this.direction = direction;}
    public Direction getDirection() {return this.direction;}

    public ConnectionPoint transformed(StructurePlaceSettings settings, BlockPos position, BlockPos offset) {
        List<BlockPos> transformedPositions = new ArrayList<>();
        ConnectionPoint newPoint = new ConnectionPoint(this);

        switch (settings.getMirror()) {
            case FRONT_BACK:
                if (newPoint.direction == Direction.WEST) {newPoint.direction = Direction.EAST;}
                if (newPoint.direction == Direction.EAST) {newPoint.direction = Direction.WEST;}
                break;
            case LEFT_RIGHT:
                if (newPoint.direction == Direction.NORTH) {newPoint.direction = Direction.SOUTH;}
                if (newPoint.direction == Direction.SOUTH) {newPoint.direction = Direction.NORTH;}
                break;
        }


        switch (settings.getRotation()) {
            case CLOCKWISE_90:
                if (newPoint.direction == Direction.UP || newPoint.direction == Direction.DOWN) break;
                newPoint.direction = newPoint.direction.getClockWise();
                break;
            case CLOCKWISE_180:
                if (newPoint.direction == Direction.UP || newPoint.direction == Direction.DOWN) break;
                newPoint.direction = newPoint.direction.getClockWise().getClockWise();
                break;
            case COUNTERCLOCKWISE_90:
                if (newPoint.direction == Direction.UP || newPoint.direction == Direction.DOWN) break;
                newPoint.direction = newPoint.direction.getCounterClockWise();
                break;
        }

        for (BlockPos pos : newPoint.positions) {
            BlockPos newPos = StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset);
            newPos = newPos.offset(position);
            transformedPositions.add(newPos);
        }

        if (settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90) {
            newPoint.rotated = true;
        }

        newPoint.positions = transformedPositions;

        return newPoint;
    }

    public Vec3 getAveragePosition() {
        float totalX = 0.0f;
        float totalY = 0.0f;
        float totalZ = 0.0f;

        for (BlockPos pos : positions) {
            totalX += pos.getX();
            totalY += pos.getY();
            totalZ += pos.getZ();
        }

        return new Vec3(totalX / positions.size(), totalY / positions.size(), totalZ / positions.size());
    }

    public Vector2i getSize() {
        int x = this.rotated ? this.boundingBox.getZSpan() : this.boundingBox.getXSpan();
        int y = this.boundingBox.getYSpan();
        int z = this.rotated ? this.boundingBox.getXSpan() : this.boundingBox.getZSpan();

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
            ConnectionPoint targetPoint = null;
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

            for (ConnectionPoint point : connectionPoints) {
                if (point.getDirection() == blockDirection) {

                    for (BlockPos pos : point.positions) {
                        if (block.pos().closerThan(pos, 2.0)) {
                            targetPoint = point;
                        }
                    }
                }
            }

            if (targetPoint == null) {
                targetPoint = new ConnectionPoint(block.pos(), blockDirection);
                targetPoint.pool = block.nbt().getString("pool");
                connectionPoints.add(targetPoint);
            } else {
                targetPoint.addPosition(block.pos());
                targetPoint.boundingBox.encapsulate(block.pos());
            }
        }

        return connectionPoints;
    }
}
