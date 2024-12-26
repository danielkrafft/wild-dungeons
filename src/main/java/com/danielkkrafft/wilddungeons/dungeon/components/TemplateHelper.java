package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;

public class TemplateHelper {
    public static final BlockPos EMPTY_BLOCK_POS = new BlockPos(0, 0, 0);

    public static List<ConnectionPoint> locateConnectionPoints(StructureTemplate template) {
        List<ConnectionPoint> connectionPoints = new ArrayList<>();
        BoundingBox boundingBox = template.getBoundingBox(new StructurePlaceSettings(), EMPTY_BLOCK_POS);

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

    public static Direction mirrorDirection(Direction direction, Mirror mirror) {
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

    public static Direction rotateDirection(Direction direction, Rotation rotation) {
        if (direction.getAxis().equals(Direction.Axis.Y)) return direction;
        return switch (rotation) {
            case CLOCKWISE_90 -> direction.getClockWise();
            case CLOCKWISE_180 -> direction.getOpposite();
            case COUNTERCLOCKWISE_90 -> direction.getCounterClockWise();
            default -> direction;
        };
    }

    public static StructurePlaceSettings handleRoomTransformation(ConnectionPoint entrancePoint, ConnectionPoint exitPoint, RandomSource random) {
        StructurePlaceSettings settings = new StructurePlaceSettings();

        if (entrancePoint.direction.getAxis() == Direction.Axis.Y) {
            settings.setMirror(exitPoint.room.settings.getMirror());
            settings.setRotation(exitPoint.room.settings.getRotation());
            return settings;
        }

        settings.setMirror(Util.getRandom(Mirror.values(), random));

        ConnectionPoint mirroredPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));
        if (exitPoint.direction == mirroredPoint.direction) {
            settings.setRotation(Rotation.CLOCKWISE_180);
        } else if (exitPoint.direction == mirroredPoint.direction.getClockWise()) {
            settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
        } else if (exitPoint.direction == mirroredPoint.direction.getCounterClockWise()) {
            settings.setRotation(Rotation.CLOCKWISE_90);
        }

        return settings;
    }

    public static BlockPos locateSpawnPoint(StructureTemplate template) {
        List<StructureTemplate.StructureBlockInfo> SPAWN_BLOCKS = template.filterBlocks(new BlockPos(0, 0, 0), new StructurePlaceSettings(), WDBlocks.SPAWN_BLOCK.get());
        return SPAWN_BLOCKS.isEmpty() ? null : SPAWN_BLOCKS.getFirst().pos();
    }

    public static List<BlockPos> locateRifts(StructureTemplate template) {
        List<StructureTemplate.StructureBlockInfo> RIFT_BLOCKS = template.filterBlocks(new BlockPos(0, 0, 0), new StructurePlaceSettings(), WDBlocks.RIFT_BLOCK.get());
        List<BlockPos> result = new ArrayList<>();
        RIFT_BLOCKS.forEach(info -> {result.add(info.pos());});
        return result;
    }
}
