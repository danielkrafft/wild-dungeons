package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.*;

import static net.minecraft.world.level.block.state.properties.StructureMode.DATA;

public class TemplateHelper {
    public static final BlockPos EMPTY_BLOCK_POS = new BlockPos(0, 0, 0);
    public static final StructurePlaceSettings EMPTY_DUNGEON_SETTINGS = new StructurePlaceSettings();

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
                    if (point.getDirection(EMPTY_DUNGEON_SETTINGS) != blockDirection) continue;
                    if (point.getPositions(EMPTY_DUNGEON_SETTINGS, EMPTY_BLOCK_POS).stream().noneMatch(pos -> block.pos().closerThan(pos, 2.0))) continue;
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

    public static List<StructureTemplate.StructureBlockInfo> locateDataMarkers(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>();
        templates.forEach(template -> {
            result.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), Blocks.STRUCTURE_BLOCK));
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

    public static StructurePlaceSettings handleRoomTransformation(ConnectionPoint entrancePoint, ConnectionPoint exitPoint) {
        StructurePlaceSettings settings = new StructurePlaceSettings();

        if (entrancePoint.getDirection(settings).getAxis() == Direction.Axis.Y) {
            settings.setMirror(exitPoint.getRoom().getSettings().getMirror());
            settings.setRotation(exitPoint.getRoom().getSettings().getRotation());
            return settings;
        }

        settings.setMirror(RandomUtil.randomFromList(Arrays.stream(Mirror.values()).toList()));
        if (exitPoint.getDirection(exitPoint.getRoom().getSettings()) == entrancePoint.getDirection(settings)) {
            settings.setRotation(Rotation.CLOCKWISE_180);
        } else if (exitPoint.getDirection(exitPoint.getRoom().getSettings()) == entrancePoint.getDirection(settings).getClockWise()) {
            settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
        } else if (exitPoint.getDirection(exitPoint.getRoom().getSettings()) == entrancePoint.getDirection(settings).getCounterClockWise()) {
            settings.setRotation(Rotation.CLOCKWISE_90);
        }

        return settings;
    }

    public static BlockPos transform(BlockPos input, DungeonRoom room) {
        return StructureTemplate.transform(input, room.getSettings().getMirror(), room.getSettings().getRotation(), EMPTY_BLOCK_POS).offset(room.getPosition());
    }

    public static BlockPos locateSpawnPoint(List<Pair<StructureTemplate, BlockPos>> templates) {
        List<StructureTemplate.StructureBlockInfo> SPAWN_BLOCKS = new ArrayList<>();
        templates.forEach(template -> {
            SPAWN_BLOCKS.addAll(template.getFirst().filterBlocks(template.getSecond(), new StructurePlaceSettings(), WDBlocks.SPAWN_BLOCK.get()));
        });
        return SPAWN_BLOCKS.isEmpty() ? null : SPAWN_BLOCKS.getFirst().pos();
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

            if (input.hasProperty(BlockStateProperties.STAIRS_SHAPE) && stairsShape != null) input = input.setValue(BlockStateProperties.STAIRS_SHAPE, stairsShape);


            WDProfiler.INSTANCE.logTimestamp("TemplateHelper::fixBlockStateProperties");
            return input.hasProperty(BlockStateProperties.FACING) ?
                    input.setValue(BlockStateProperties.FACING, facing) :
                    input.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        return input;
    }

    public static boolean placeInWorld(StructureTemplate template, DungeonRoom room, DungeonMaterial material, ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, int flags) {
        if (template.palettes.isEmpty()) {
            return false;
        } else {
            List<StructureTemplate.StructureBlockInfo> list = settings.getRandomPalette(template.palettes, offset).blocks();
            if ((!list.isEmpty() || !settings.isIgnoreEntities() && !template.entityInfoList.isEmpty()) && template.size.getX() >= 1 && template.size.getY() >= 1 && template.size.getZ() >= 1) {
                BoundingBox boundingbox = settings.getBoundingBox();
                List<BlockPos> list1 = Lists.newArrayListWithCapacity(settings.shouldApplyWaterlogging() ? list.size() : 0);
                List<BlockPos> list2 = Lists.newArrayListWithCapacity(settings.shouldApplyWaterlogging() ? list.size() : 0);
                List<Pair<BlockPos, CompoundTag>> list3 = Lists.newArrayListWithCapacity(list.size());
                int i = Integer.MAX_VALUE;
                int j = Integer.MAX_VALUE;
                int k = Integer.MAX_VALUE;
                int l = Integer.MIN_VALUE;
                int i1 = Integer.MIN_VALUE;
                int j1 = Integer.MIN_VALUE;
                for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : StructureTemplate.processBlockInfos(serverLevel, offset, pos, settings, list, template)) {
                    if (boundingbox == null || boundingbox.isInside(structuretemplate$structureblockinfo.pos())) {
                        FluidState fluidstate = settings.shouldApplyWaterlogging() ? serverLevel.getFluidState(structuretemplate$structureblockinfo.pos()) : null;
                        //BlockState blockstate = structuretemplate$structureblockinfo.state().mirror(settings.getMirror()).rotate(settings.getRotation());
                        //BlockState blockstate = TemplateHelper.fixBlockStateProperties(material.replace(structuretemplate$structureblockinfo.state().mirror(settings.getMirror()).rotate(settings.getRotation())), settings);
                        BlockState blockstate = material.replace(structuretemplate$structureblockinfo.state().mirror(settings.getMirror()).rotate(settings.getRotation()));
                        if (structuretemplate$structureblockinfo.nbt() != null) {
                            BlockEntity blockentity = serverLevel.getBlockEntity(structuretemplate$structureblockinfo.pos());
                            Clearable.tryClear(blockentity);
                            serverLevel.setBlock(structuretemplate$structureblockinfo.pos(), Blocks.BARRIER.defaultBlockState(), 20);
                        }

                        if (serverLevel.setBlock(structuretemplate$structureblockinfo.pos(), blockstate, flags)) {
                            i = Math.min(i, structuretemplate$structureblockinfo.pos().getX());
                            j = Math.min(j, structuretemplate$structureblockinfo.pos().getY());
                            k = Math.min(k, structuretemplate$structureblockinfo.pos().getZ());
                            l = Math.max(l, structuretemplate$structureblockinfo.pos().getX());
                            i1 = Math.max(i1, structuretemplate$structureblockinfo.pos().getY());
                            j1 = Math.max(j1, structuretemplate$structureblockinfo.pos().getZ());
                            list3.add(Pair.of(structuretemplate$structureblockinfo.pos(), structuretemplate$structureblockinfo.nbt()));
                            if (structuretemplate$structureblockinfo.nbt() != null) {
                                BlockEntity blockentity1 = serverLevel.getBlockEntity(structuretemplate$structureblockinfo.pos());
                                if (blockentity1 != null) {
//                                    if (blockentity1 instanceof RandomizableContainer) {
//                                        structuretemplate$structureblockinfo.nbt().putLong("LootTableSeed", random.nextLong());
//                                    }

                                    blockentity1.loadWithComponents(structuretemplate$structureblockinfo.nbt(), serverLevel.registryAccess());
                                }
                            }

                            if (fluidstate != null) {
                                if (blockstate.getFluidState().isSource()) {
                                    list2.add(structuretemplate$structureblockinfo.pos());
                                } else if (blockstate.getBlock() instanceof LiquidBlockContainer) {
                                    ((LiquidBlockContainer)blockstate.getBlock()).placeLiquid(serverLevel, structuretemplate$structureblockinfo.pos(), blockstate, fluidstate);
                                    if (!fluidstate.isSource()) {
                                        list1.add(structuretemplate$structureblockinfo.pos());
                                    }
                                }
                            }
                        }
                    }
                }

                boolean flag = true;
                Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                while(flag && !list1.isEmpty()) {
                    flag = false;
                    Iterator<BlockPos> iterator = list1.iterator();

                    while(iterator.hasNext()) {
                        BlockPos blockpos3 = (BlockPos)iterator.next();
                        FluidState fluidstate2 = serverLevel.getFluidState(blockpos3);

                        for(int i2 = 0; i2 < adirection.length && !fluidstate2.isSource(); ++i2) {
                            BlockPos blockpos1 = blockpos3.relative(adirection[i2]);
                            FluidState fluidstate1 = serverLevel.getFluidState(blockpos1);
                            if (fluidstate1.isSource() && !list2.contains(blockpos1)) {
                                fluidstate2 = fluidstate1;
                            }
                        }

                        if (fluidstate2.isSource()) {
                            BlockState blockstate1 = serverLevel.getBlockState(blockpos3);
                            Block block = blockstate1.getBlock();
                            if (block instanceof LiquidBlockContainer) {
                                ((LiquidBlockContainer)block).placeLiquid(serverLevel, blockpos3, blockstate1, fluidstate2);
                                flag = true;
                                iterator.remove();
                            }
                        }
                    }
                }

                if (i <= l) {
                    if (!settings.getKnownShape()) {
                        DiscreteVoxelShape discretevoxelshape = new BitSetDiscreteVoxelShape(l - i + 1, i1 - j + 1, j1 - k + 1);
                        int k1 = i;
                        int l1 = j;
                        int j2 = k;

                        for(Pair<BlockPos, CompoundTag> pair1 : list3) {
                            BlockPos blockpos2 = (BlockPos)pair1.getFirst();
                            discretevoxelshape.fill(blockpos2.getX() - k1, blockpos2.getY() - l1, blockpos2.getZ() - j2);
                        }

                        StructureTemplate.updateShapeAtEdge(serverLevel, flags, discretevoxelshape, k1, l1, j2);
                    }

                    for(Pair<BlockPos, CompoundTag> pair : list3) {
                        BlockPos blockpos4 = (BlockPos)pair.getFirst();
                        if (!settings.getKnownShape()) {
                            BlockState blockstate2 = serverLevel.getBlockState(blockpos4);
                            BlockState blockstate3 = Block.updateFromNeighbourShapes(blockstate2, serverLevel, blockpos4);
                            if (blockstate2 != blockstate3) {
                                serverLevel.setBlock(blockpos4, blockstate3, flags & -2 | 16);
                            }

                            serverLevel.blockUpdated(blockpos4, blockstate3.getBlock());
                        }

                        if (pair.getSecond() != null) {
                            BlockEntity blockentity2 = serverLevel.getBlockEntity(blockpos4);
                            if (blockentity2 != null) {
                                blockentity2.setChanged();
                            }
                        }
                    }
                }

                if (!settings.isIgnoreEntities()) {
                    addEntitiesToWorld(template, serverLevel, offset, settings);
                }

                WDProfiler.INSTANCE.logTimestamp("TemplateHelper::placeInWorld");
                return true;
            } else {
                WDProfiler.INSTANCE.logTimestamp("TemplateHelper::placeInWorld");
                return false;
            }
        }
    }

    public static void addEntitiesToWorld(StructureTemplate template, ServerLevelAccessor p_74524_, BlockPos p_74525_, StructurePlaceSettings placementIn) {
        for(StructureTemplate.StructureEntityInfo structuretemplate$structureentityinfo : StructureTemplate.processEntityInfos(template, p_74524_, p_74525_, placementIn, template.entityInfoList)) {
            if (structuretemplate$structureentityinfo.nbt.getString("id").equals(WDEntities.OFFERING.getId().toString())) continue;
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
                StructureTemplate.createEntityIgnoreException(p_74524_, compoundtag).ifPresent((p_275190_) -> {
                    float f = p_275190_.rotate(placementIn.getRotation());
                    f += p_275190_.mirror(placementIn.getMirror()) - p_275190_.getYRot();
                    p_275190_.moveTo(vec31.x, vec31.y, vec31.z, f, p_275190_.getXRot());
                    if (placementIn.shouldFinalizeEntities() && p_275190_ instanceof Mob) {
                        ((Mob)p_275190_).finalizeSpawn(p_74524_, p_74524_.getCurrentDifficultyAt(BlockPos.containing(vec31)), MobSpawnType.STRUCTURE, (SpawnGroupData)null);
                    }

                    p_74524_.addFreshEntityWithPassengers(p_275190_);
                });
            }
        }

    }


}