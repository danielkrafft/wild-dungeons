package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.components.room.DungeonRoom;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;

public class TemplateHelper {
    public static final BlockPos EMPTY_BLOCK_POS = new BlockPos(0, 0, 0);

    public static List<ConnectionPoint> locateConnectionPoints(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<ConnectionPoint> connectionPoints = new ArrayList<>();

        templates.forEach(template -> {

            BoundingBox boundingBox = template.getFirst().getBoundingBox(new StructurePlaceSettings(), template.getSecond());

            List<StructureTemplate.StructureBlockInfo> CONNECTION_BLOCKS = template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), WDBlocks.CONNECTION_BLOCK.get());
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
                    if (point.getDirection() != blockDirection) continue;
                    if (point.getPositions().stream().noneMatch(pos -> block.pos().closerThan(pos, 2.0))) continue;
                    targetPoint = point;
                }

                if (targetPoint == null) {
                    targetPoint = ConnectionPoint.create(block.pos(), blockDirection);
                    targetPoint.setPool(block.nbt().getString("pool"));
                    targetPoint.setType(block.nbt().getString("type"));
                    connectionPoints.add(targetPoint);
                } else {
                    targetPoint.addPosition(block.pos());
                }
            }
        });

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

        if (entrancePoint.getAxis() == Direction.Axis.Y) {
            settings.setMirror(exitPoint.getRoom().settings.getMirror());
            settings.setRotation(exitPoint.getRoom().settings.getRotation());
            return settings;
        }

        settings.setMirror(Util.getRandom(Mirror.values(), random));

        ConnectionPoint mirroredPoint = ConnectionPoint.copy(entrancePoint);
        mirroredPoint.transform(settings, EMPTY_BLOCK_POS, EMPTY_BLOCK_POS);
        if (exitPoint.getDirection() == mirroredPoint.getDirection()) {
            settings.setRotation(Rotation.CLOCKWISE_180);
        } else if (exitPoint.getDirection() == mirroredPoint.getDirection().getClockWise()) {
            settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
        } else if (exitPoint.getDirection() == mirroredPoint.getDirection().getCounterClockWise()) {
            settings.setRotation(Rotation.CLOCKWISE_90);
        }

        return settings;
    }

    public static BlockPos transform(BlockPos input, DungeonRoom room) {
        return StructureTemplate.transform(input, room.settings.getMirror(), room.settings.getRotation(), room.offset).offset(room.position);
    }

    public static BlockPos locateSpawnPoint(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> SPAWN_BLOCKS = new ArrayList<>();
        templates.forEach(template -> {
            SPAWN_BLOCKS.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), WDBlocks.SPAWN_BLOCK.get()));
        });
        return SPAWN_BLOCKS.isEmpty() ? null : SPAWN_BLOCKS.getFirst().pos();
    }

    public static List<BlockPos> locateRifts(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> RIFT_BLOCKS = new ArrayList<>();
        templates.forEach(template -> {
            RIFT_BLOCKS.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), WDBlocks.RIFT_BLOCK.get()));
        });
        List<BlockPos> result = new ArrayList<>();
        RIFT_BLOCKS.forEach(info -> {result.add(info.pos());});
        return result;
    }

    public static List<StructureTemplate.StructureBlockInfo> locateMaterialBlocks(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>();
        templates.forEach(template -> {
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.STONE_BRICKS));
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.STONE_BRICK_STAIRS));
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.STONE_BRICK_SLAB));
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.STONE_BRICK_WALL));
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.SEA_LANTERN));
        });
        return result;
    }

    public static BlockState fixBlockStateProperties(BlockState input, StructurePlaceSettings settings) {
        boolean rotated = settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90;

        if ((input.hasProperty(BlockStateProperties.HORIZONTAL_FACING) || input.hasProperty(BlockStateProperties.FACING))) {
            Direction facing = input.hasProperty(BlockStateProperties.FACING) ? input.getValue(BlockStateProperties.FACING) : input.getValue(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape stairsShape = input.hasProperty(BlockStateProperties.STAIRS_SHAPE) ? input.getValue(BlockStateProperties.STAIRS_SHAPE) : null;

            if (facing != Direction.UP && facing != Direction.DOWN) {
                facing = switch (settings.getRotation()) {
                    case CLOCKWISE_90 -> facing.getClockWise();
                    case CLOCKWISE_180 -> facing.getOpposite();
                    case COUNTERCLOCKWISE_90 -> facing.getCounterClockWise();
                    default -> facing;
                };

                switch (settings.getMirror()) {
                    case LEFT_RIGHT:
                        if ((facing == Direction.NORTH || facing == Direction.SOUTH) && !rotated) {
                            facing = facing.getOpposite();
                        }
                        if ((facing == Direction.EAST || facing == Direction.WEST) && rotated) {
                            facing = facing.getOpposite();
                        }
                        if (stairsShape != null) {
                            switch(stairsShape) {
                                case INNER_LEFT -> stairsShape = StairsShape.INNER_RIGHT;
                                case INNER_RIGHT -> stairsShape = StairsShape.INNER_RIGHT;
                                case OUTER_LEFT -> stairsShape = StairsShape.OUTER_RIGHT;
                                case OUTER_RIGHT -> stairsShape = StairsShape.OUTER_LEFT;
                            }
                        }
                        break;
                    case FRONT_BACK:
                        if ((facing == Direction.EAST || facing == Direction.WEST) && !rotated) {
                            facing = facing.getOpposite();
                        }
                        if ((facing == Direction.NORTH || facing == Direction.SOUTH) && rotated) {
                            facing = facing.getOpposite();
                        }
                        if (stairsShape != null) {
                            switch(stairsShape) {
                                case INNER_LEFT -> stairsShape = StairsShape.INNER_RIGHT;
                                case INNER_RIGHT -> stairsShape = StairsShape.INNER_RIGHT;
                                case OUTER_LEFT -> stairsShape = StairsShape.OUTER_RIGHT;
                                case OUTER_RIGHT -> stairsShape = StairsShape.OUTER_LEFT;
                            }
                        }
                        break;
                }
            }

            if (input.hasProperty(BlockStateProperties.STAIRS_SHAPE) && stairsShape != null) input = input.setValue(BlockStateProperties.STAIRS_SHAPE, stairsShape);

            return input.hasProperty(BlockStateProperties.FACING) ?
                    input.setValue(BlockStateProperties.FACING, facing) :
                    input.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        return input;
    }
}