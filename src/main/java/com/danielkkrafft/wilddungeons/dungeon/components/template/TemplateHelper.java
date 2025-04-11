package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBedrockBlock;
import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialRegistry;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.world.structure.WDStructureTemplate;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.DESTRUCTION_RULE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class TemplateHelper {
    public static final BlockPos EMPTY_BLOCK_POS = new BlockPos(0, 0, 0);
    public static final StructurePlaceSettings EMPTY_DUNGEON_SETTINGS = new StructurePlaceSettings();

    public static List<ConnectionPoint> locateConnectionPoints(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<ConnectionPoint> connectionPoints = new ArrayList<>();

        templates.forEach(template -> {

            BoundingBox boundingBox = template.getFirst().getBoundingBox(EMPTY_DUNGEON_SETTINGS, template.getSecond());

            List<StructureTemplate.StructureBlockInfo> CONNECTION_BLOCKS = template.getFirst().filterBlocks(template.getSecond(), EMPTY_DUNGEON_SETTINGS, WDBlocks.CONNECTION_BLOCK.get());
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
                    if (point.getEmptyDirection() != blockDirection) continue;
                    if (point.getPositions(TemplateOrientation.EMPTY, EMPTY_BLOCK_POS).stream().noneMatch(pos -> block.pos().closerThan(pos, 2.0))) continue;
                    targetPoint = point;
                }

                if (targetPoint == null) {
                    targetPoint = ConnectionPoint.create(block.pos(), blockDirection);
                    targetPoint.setPool(block.nbt().getString("pool"));
                    targetPoint.setType(block.nbt().getString("type"));
                    targetPoint.setIndex(connectionPoints.size());
                    connectionPoints.add(targetPoint);
                } else {
                    targetPoint.addPosition(block.pos());
                }
            }
        });

        return connectionPoints;
    }

    public static List<StructureTemplate.StructureBlockInfo> locateDataMarkers(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>();
        templates.forEach(template -> {
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), EMPTY_DUNGEON_SETTINGS, Blocks.STRUCTURE_BLOCK));
            result.removeIf(block -> {
                if (block.state().getValue(StructureBlock.MODE) != StructureMode.DATA) return true;
                CompoundTag nbt = block.nbt();
                if (nbt == null) return true;
                return !nbt.contains("metadata");
            });
        });
        return result;
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

    public static TemplateOrientation handleRoomTransformation(ConnectionPoint entrancePoint, ConnectionPoint exitPoint) {
        TemplateOrientation orientation = new TemplateOrientation();
        Direction enDirection = entrancePoint.getDirection(orientation);

        if (enDirection.getAxis() == Direction.Axis.Y) {
            orientation.setMirror(exitPoint.getRoom().getOrientation().getMirror());
            orientation.setRotation(exitPoint.getRoom().getOrientation().getRotation());
            return orientation;
        }

        Direction exDirection = exitPoint.getDirection(exitPoint.getRoom().getOrientation());

        orientation.setMirror(RandomUtil.randomFromList(Arrays.stream(Mirror.values()).toList()));
        if (exDirection == enDirection) {
            orientation.setRotation(Rotation.CLOCKWISE_180);
        } else if (exDirection == enDirection.getClockWise()) {
            orientation.setRotation(Rotation.COUNTERCLOCKWISE_90);
        } else if (exDirection == enDirection.getCounterClockWise()) {
            orientation.setRotation(Rotation.CLOCKWISE_90);
        }

        return orientation;
    }

    public static BlockPos transform(BlockPos input, DungeonRoom room) {
        return StructureTemplate.transform(input, room.getSettings().getMirror(), room.getSettings().getRotation(), EMPTY_BLOCK_POS).offset(room.getPosition());
    }

    public static List<BlockPos> locateSpawnPoint(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> SPAWN_BLOCKS = new ArrayList<>();
        templates.forEach(template -> {
            SPAWN_BLOCKS.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), WDBlocks.SPAWN_BLOCK.get()));
        });
        List<BlockPos> result = new ArrayList<>();
        SPAWN_BLOCKS.forEach(block -> {
            result.add(block.pos());
        });

        return SPAWN_BLOCKS.isEmpty() ? null : result;
    }

    public static List<Vec3> locateRifts(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<Vec3> result = new ArrayList<>();
        templates.forEach(template -> {
            template.getFirst().entityInfoList.forEach(structureEntityInfo -> {
                Optional<EntityType<?>> type = EntityType.by(structureEntityInfo.nbt);
                if (type.isPresent() && type.get().equals(WDEntities.OFFERING.get())) {
                    if (structureEntityInfo.nbt.getString("type").equals("RIFT")) {
                        WildDungeons.getLogger().info("FOUND RIFT WITH KEYS: {}", structureEntityInfo.nbt.getAllKeys());
                        result.add(structureEntityInfo.pos.add(template.getSecond().getX(), template.getSecond().getY(), template.getSecond().getZ()));
                    }
                }
            });
        });
        return result;
    }

    public static List<Vec3> locateOfferings(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<Vec3> result = new ArrayList<>();
        templates.forEach(template -> {
            template.getFirst().entityInfoList.forEach(structureEntityInfo -> {
                Optional<EntityType<?>> type = EntityType.by(structureEntityInfo.nbt);
                if (type.isPresent() && type.get().equals(WDEntities.OFFERING.get())) {
                    if (structureEntityInfo.nbt.getString("type").equals("ITEM") || structureEntityInfo.nbt.getString("type").equals("PERK")) {
                        WildDungeons.getLogger().info("FOUND OFFERING WITH KEYS: {}", structureEntityInfo.nbt.getAllKeys());
                        result.add(structureEntityInfo.pos.add(template.getSecond().getX(), template.getSecond().getY(), template.getSecond().getZ()));
                    }
                }
            });
        });
        return result;
    }

    public static List<StructureTemplate.StructureBlockInfo> locateLootTargets(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>();
        templates.forEach(template -> {
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.CHEST));
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.BARREL));
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.TRAPPED_CHEST));
        });
        return result;
    }


    public static BlockState fixBlockStateProperties(BlockState input, StructurePlaceSettings settings) {
        boolean rotated = settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90;

        if ((input.hasProperty(HORIZONTAL_FACING) || input.hasProperty(FACING))) {
            Direction facing = input.hasProperty(FACING) ? input.getValue(FACING) : input.getValue(HORIZONTAL_FACING);
            StairsShape stairsShape = input.hasProperty(STAIRS_SHAPE) ? input.getValue(STAIRS_SHAPE) : null;

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
                                case INNER_RIGHT -> stairsShape = StairsShape.INNER_LEFT;
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
                                case INNER_RIGHT -> stairsShape = StairsShape.INNER_LEFT;
                                case OUTER_LEFT -> stairsShape = StairsShape.OUTER_RIGHT;
                                case OUTER_RIGHT -> stairsShape = StairsShape.OUTER_LEFT;
                            }
                        }
                        break;
                }
            }

            if (input.hasProperty(STAIRS_SHAPE) && stairsShape != null) input = input.setValue(STAIRS_SHAPE, stairsShape);

            return input.hasProperty(FACING) ?
                    input.setValue(FACING, facing) :
                    input.setValue(HORIZONTAL_FACING, facing);
        }

        if (input.hasProperty(RAIL_SHAPE_STRAIGHT) || input.hasProperty(RAIL_SHAPE)) {
            RailShape railShape = input.hasProperty(RAIL_SHAPE) ? input.getValue(RAIL_SHAPE) : input.getValue(RAIL_SHAPE_STRAIGHT);
            switch (settings.getRotation()) {
                case NONE -> {
                }
                case CLOCKWISE_90 -> {
                    railShape = switch (railShape) {
                        case NORTH_SOUTH -> RailShape.EAST_WEST;
                        case EAST_WEST -> RailShape.NORTH_SOUTH;
                        case ASCENDING_EAST -> RailShape.ASCENDING_SOUTH;
                        case ASCENDING_WEST -> RailShape.ASCENDING_NORTH;
                        case ASCENDING_NORTH -> RailShape.ASCENDING_EAST;
                        case ASCENDING_SOUTH -> RailShape.ASCENDING_WEST;
                        case SOUTH_EAST -> RailShape.SOUTH_WEST;
                        case SOUTH_WEST -> RailShape.NORTH_WEST;
                        case NORTH_WEST -> RailShape.NORTH_EAST;
                        case NORTH_EAST -> RailShape.SOUTH_EAST;
                    };
                }
                case CLOCKWISE_180 -> {
                    railShape = switch (railShape) {
                        case ASCENDING_EAST -> RailShape.ASCENDING_WEST;
                        case ASCENDING_WEST -> RailShape.ASCENDING_EAST;
                        case ASCENDING_NORTH -> RailShape.ASCENDING_SOUTH;
                        case ASCENDING_SOUTH -> RailShape.ASCENDING_NORTH;
                        case SOUTH_EAST -> RailShape.NORTH_EAST;
                        case SOUTH_WEST -> RailShape.NORTH_WEST;
                        case NORTH_WEST -> RailShape.SOUTH_WEST;
                        case NORTH_EAST -> RailShape.SOUTH_EAST;
                        default -> railShape;
                    };
                }
                case COUNTERCLOCKWISE_90 -> {
                    railShape = switch (railShape) {
                        case NORTH_SOUTH -> RailShape.EAST_WEST;
                        case EAST_WEST -> RailShape.NORTH_SOUTH;
                        case ASCENDING_EAST -> RailShape.ASCENDING_NORTH;
                        case ASCENDING_WEST -> RailShape.ASCENDING_SOUTH;
                        case ASCENDING_NORTH -> RailShape.ASCENDING_WEST;
                        case ASCENDING_SOUTH -> RailShape.ASCENDING_EAST;
                        case SOUTH_EAST -> RailShape.NORTH_EAST;
                        case SOUTH_WEST -> RailShape.SOUTH_EAST;
                        case NORTH_WEST -> RailShape.SOUTH_WEST;
                        case NORTH_EAST -> RailShape.NORTH_WEST;
                    };
                }
            }
            switch (settings.getMirror()) {
                case NONE -> {
                }
                case LEFT_RIGHT -> {
                    railShape = switch (railShape) {
                        case ASCENDING_EAST -> RailShape.ASCENDING_WEST;
                        case ASCENDING_WEST -> RailShape.ASCENDING_EAST;
                        case SOUTH_EAST -> RailShape.SOUTH_WEST;
                        case SOUTH_WEST -> RailShape.SOUTH_EAST;
                        case NORTH_WEST -> RailShape.NORTH_EAST;
                        case NORTH_EAST -> RailShape.NORTH_WEST;
                        default -> railShape;
                    };
                }
                case FRONT_BACK -> {
                    railShape = switch (railShape) {
                        case ASCENDING_NORTH -> RailShape.ASCENDING_SOUTH;
                        case ASCENDING_SOUTH -> RailShape.ASCENDING_NORTH;
                        case SOUTH_EAST -> RailShape.NORTH_EAST;
                        case SOUTH_WEST -> RailShape.NORTH_WEST;
                        case NORTH_WEST -> RailShape.SOUTH_WEST;
                        case NORTH_EAST -> RailShape.SOUTH_EAST;
                        default -> railShape;
                    };
                }
            }
            if (input.hasProperty(RAIL_SHAPE)) return input.setValue(RAIL_SHAPE, railShape);
            if (input.hasProperty(RAIL_SHAPE_STRAIGHT)) return input.setValue(RAIL_SHAPE_STRAIGHT, railShape);
        }
        return input;
    }

    public static boolean placeInWorld(DungeonRoom room, StructureTemplate template, DungeonMaterial material, ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, int flags) {
        if (template.palettes.isEmpty()) return false;
        BoundingBox innerBox = template.getBoundingBox(settings, pos).inflatedBy(-1);

        List<StructureTemplate.StructureBlockInfo> list = settings.getRandomPalette(template.palettes, offset).blocks();
        if ((!list.isEmpty() || !settings.isIgnoreEntities() && !template.entityInfoList.isEmpty()) && template.size.getX() >= 1 && template.size.getY() >= 1 && template.size.getZ() >= 1) {

            for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : StructureTemplate.processBlockInfos(serverLevel, offset, pos, settings, list, template)) {
                BlockState blockstate = structuretemplate$structureblockinfo.state();
                //if (blockstate.equals(Blocks.AIR.defaultBlockState())) continue;

                if (room.getProperty(DESTRUCTION_RULE) == DungeonRoomTemplate.DestructionRule.SHELL || room.getProperty(DESTRUCTION_RULE) == DungeonRoomTemplate.DestructionRule.SHELL_CLEAR) {
                    if ((blockstate == WDBlocks.WD_BASIC.get().defaultBlockState() || blockstate == WDBlocks.WD_BASIC_2.get().defaultBlockState() || blockstate == WDBlocks.WD_BASIC_3.get().defaultBlockState() || blockstate == WDBlocks.WD_BASIC_4.get().defaultBlockState()) && !innerBox.isInside(structuretemplate$structureblockinfo.pos())) {
                        if (!room.isPosInsideShell(structuretemplate$structureblockinfo.pos())) {
                            blockstate = material.replace(structuretemplate$structureblockinfo.state().mirror(settings.getMirror()).rotate(settings.getRotation()), room.getTemplate().wdStructureTemplate());
                            serverLevel.setBlock(structuretemplate$structureblockinfo.pos(), WDBedrockBlock.of(blockstate.getBlock()), 2);
                            continue;
                        }
                    }
                }

                if (blockstate.hasProperty(STAIRS_SHAPE) || blockstate.hasProperty(RAIL_SHAPE) || blockstate.hasProperty(RAIL_SHAPE_STRAIGHT)) {
                    blockstate = TemplateHelper.fixBlockStateProperties(material.replace(structuretemplate$structureblockinfo.state(), room.getTemplate().wdStructureTemplate()), settings);
                } else {
                    blockstate = material.replace(structuretemplate$structureblockinfo.state().mirror(settings.getMirror()).rotate(settings.getRotation()), room.getTemplate().wdStructureTemplate());
                }

                blockstate = replaceChest(blockstate, room);//todo this should be moved elsewhere when the NBT is controlling the room properties

                if (structuretemplate$structureblockinfo.nbt() != null) {
                    BlockEntity blockentity = serverLevel.getBlockEntity(structuretemplate$structureblockinfo.pos());
                    Clearable.tryClear(blockentity);
                    serverLevel.setBlock(structuretemplate$structureblockinfo.pos(), Blocks.BARRIER.defaultBlockState(), 2);
                }

                if (serverLevel.setBlock(structuretemplate$structureblockinfo.pos(), blockstate, flags)) {
                    if (structuretemplate$structureblockinfo.nbt() != null) {
                        BlockEntity blockentity1 = serverLevel.getBlockEntity(structuretemplate$structureblockinfo.pos());
                        if (blockentity1 != null) {
                            blockentity1.loadWithComponents(structuretemplate$structureblockinfo.nbt(), serverLevel.registryAccess());
                            if (blockentity1 instanceof SpawnerBlockEntity spawnerBlockEntity) {//todo replace this with the special spawner block entity and a system that randomizes the spawner
                                spawnerBlockEntity.setEntityId(EntityType.ZOMBIE, serverLevel.getRandom());
                            }
                        }
                    }

                    FluidState fluidstate = settings.shouldApplyWaterlogging() ? serverLevel.getFluidState(structuretemplate$structureblockinfo.pos()) : null;
                    if (fluidstate != null) {
                        if (blockstate.getFluidState().isSource()) {
                        } else if (blockstate.getBlock() instanceof LiquidBlockContainer) {
                            ((LiquidBlockContainer) blockstate.getBlock()).placeLiquid(serverLevel, structuretemplate$structureblockinfo.pos(), blockstate, fluidstate);
                        }
                    }
                }
            }

            if (!settings.isIgnoreEntities()) {
                addEntitiesToWorld(template, serverLevel, offset, settings, true);
            }

            return true;
        } else {
            return false;
        }
    }

    public static BlockState replaceChest(BlockState blockState,  DungeonRoom room){
        //IMPORTANT: do NOT replace Trapped Chests as this will break some rooms
        //todo replace with a property in the wd template
        if (blockState.is(Blocks.CHEST))  {blockState = RandomUtil.randFloatBetween(0, 1) < room.getProperty(HierarchicalProperty.CHEST_SPAWN_CHANCE) ? Blocks.CHEST.defaultBlockState().setValue(HORIZONTAL_FACING, blockState.getValue(HORIZONTAL_FACING)) : Blocks.AIR.defaultBlockState();}
        else if (blockState.is(Blocks.BARREL)) {blockState = RandomUtil.randFloatBetween(0, 1) < room.getProperty(HierarchicalProperty.CHEST_SPAWN_CHANCE) ? Blocks.BARREL.defaultBlockState().setValue(FACING, blockState.getValue(FACING)) : Blocks.AIR.defaultBlockState();}
        return blockState;
    }

  /**
         * Places a template in the world with specified material
         * @param template Either a WDStructureTemplate or a StructureTemplate
         * @param materialIndex Material index to use for replacement
         * @param serverLevel The server level
         * @param clickedPos The position where the template should be placed
         * @param settings The structure place settings
         */
        public static void wandPlaceInWorld(Object template, int materialIndex, ServerLevel serverLevel, BlockPos clickedPos, StructurePlaceSettings settings) {
            DungeonMaterial material = DungeonMaterialRegistry.dungeonMaterials.get(materialIndex);

            if (template instanceof WDStructureTemplate wdStructureTemplate) {
                // Handle WDStructureTemplate by processing each inner template
                for (Pair<StructureTemplate, BlockPos> innerTemplatePair : wdStructureTemplate.innerTemplates) {
                    StructureTemplate innerTemplate = innerTemplatePair.getFirst();
                    BlockPos templateOffset = innerTemplatePair.getSecond();
                    BlockPos offset = templateOffset.rotate(settings.getRotation());
                    if (settings.getMirror() != Mirror.NONE) {
                        offset = StructureTemplate.transform(templateOffset, settings.getMirror(), settings.getRotation(), BlockPos.ZERO);
                    }

                    placeTemplateWithMaterial(innerTemplate, material, wdStructureTemplate, serverLevel, clickedPos, offset, settings);
                }
            } else if (template instanceof StructureTemplate structureTemplate) {
                // Handle direct StructureTemplate
                placeTemplateWithMaterial(structureTemplate, material, null, serverLevel, clickedPos, BlockPos.ZERO, settings);
            }
        }

        /**
         * Helper method to place a template with material replacement
         */
        private static void placeTemplateWithMaterial(StructureTemplate template, DungeonMaterial material,
                                                     WDStructureTemplate wdTemplate, ServerLevel serverLevel,
                                                     BlockPos clickedPos, BlockPos offset, StructurePlaceSettings settings) {
            if (template.palettes.isEmpty()) return;

            List<StructureTemplate.StructureBlockInfo> list = settings.getRandomPalette(template.palettes, offset).blocks();
            if ((!list.isEmpty() || !settings.isIgnoreEntities() && !template.entityInfoList.isEmpty())
                    && template.size.getX() >= 1 && template.size.getY() >= 1 && template.size.getZ() >= 1) {

                for (StructureTemplate.StructureBlockInfo structureBlockInfo :
                        StructureTemplate.processBlockInfos(serverLevel, offset, clickedPos, settings, list, template)) {
                    BlockState blockstate = structureBlockInfo.state();
                    BlockPos pos = structureBlockInfo.pos().offset(clickedPos);

                    if (blockstate.hasProperty(STAIRS_SHAPE) || blockstate.hasProperty(RAIL_SHAPE) || blockstate.hasProperty(RAIL_SHAPE_STRAIGHT)) {
                        blockstate = fixBlockStateProperties(material.replace(structureBlockInfo.state(), wdTemplate), settings);
                    } else {
                        blockstate = material.replace(blockstate.mirror(settings.getMirror()).rotate(settings.getRotation()), wdTemplate);
                    }

                    if (structureBlockInfo.nbt() != null) {
                        BlockEntity blockentity = serverLevel.getBlockEntity(pos);
                        Clearable.tryClear(blockentity);
                        serverLevel.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 2);
                    }

                    if (serverLevel.setBlock(pos, blockstate, 2)) {
                        if (structureBlockInfo.nbt() != null) {
                            BlockEntity blockentity1 = serverLevel.getBlockEntity(pos);
                            if (blockentity1 != null) {
                                blockentity1.loadWithComponents(structureBlockInfo.nbt(), serverLevel.registryAccess());
                            }
                        }

                        FluidState fluidstate = settings.shouldApplyWaterlogging() ? serverLevel.getFluidState(pos) : null;
                        if (fluidstate != null) {
                            if (blockstate.getFluidState().isSource()) {
                                // Empty block to maintain original structure
                            } else if (blockstate.getBlock() instanceof LiquidBlockContainer) {
                                ((LiquidBlockContainer) blockstate.getBlock()).placeLiquid(serverLevel, pos, blockstate, fluidstate);
                            }
                        }
                    }
                }

                if (!settings.isIgnoreEntities()) {
                    addEntitiesToWorld(template, serverLevel, clickedPos.offset(offset), settings, false);
                }
            }
        }

    public static void addEntitiesToWorld(StructureTemplate template, ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, StructurePlaceSettings placementIn, boolean ignoreOfferings) {
        for(StructureTemplate.StructureEntityInfo structuretemplate$structureentityinfo : StructureTemplate.processEntityInfos(template, serverLevelAccessor, blockPos, placementIn, template.entityInfoList)) {
            if (ignoreOfferings && structuretemplate$structureentityinfo.nbt.getString("id").equals(WDEntities.OFFERING.getId().toString())) continue;
            BlockPos blockpos = structuretemplate$structureentityinfo.blockPos;
            if (placementIn.getBoundingBox() == null || placementIn.getBoundingBox().isInside(blockpos)) {
                CompoundTag compoundtag = structuretemplate$structureentityinfo.nbt.copy();
                Vec3 vec31 = structuretemplate$structureentityinfo.pos;
                ListTag listtag = new ListTag();
                listtag.add(DoubleTag.valueOf(vec31.x));
                listtag.add(DoubleTag.valueOf(vec31.y));
                listtag.add(DoubleTag.valueOf(vec31.z));
                compoundtag.put("Pos", listtag);
                compoundtag.remove("UUID");
                StructureTemplate.createEntityIgnoreException(serverLevelAccessor, compoundtag).ifPresent((p_275190_) -> {
                    float f = p_275190_.rotate(placementIn.getRotation());
                    f += p_275190_.mirror(placementIn.getMirror()) - p_275190_.getYRot();
                    p_275190_.moveTo(vec31.x, vec31.y, vec31.z, f, p_275190_.getXRot());
                    if (placementIn.shouldFinalizeEntities() && p_275190_ instanceof Mob) {
                        ((Mob)p_275190_).finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(BlockPos.containing(vec31)), MobSpawnType.STRUCTURE, null);
                    }

                    serverLevelAccessor.addFreshEntityWithPassengers(p_275190_);
                });
            }
        }

    }


}