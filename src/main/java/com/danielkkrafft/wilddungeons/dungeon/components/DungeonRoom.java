package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DungeonRoom {
    public DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate;
    public ServerLevel level;
    public BlockPos position;
    public BlockPos offset;
    public BlockPos spawnPoint;
    public StructurePlaceSettings settings;
    public List<ConnectionPoint> connectionPoints = new ArrayList<>();
    public List<BlockPos> rifts = new ArrayList<>();
    public BoundingBox boundingBox;
    public DungeonBranch branch;
    public boolean rotated;
    public DungeonMaterial material;

    public DungeonRoom(DungeonBranch branch, DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, List<ConnectionPoint> inputPoints) {
        dungeonRoomTemplate.template().placeInWorld(level, position, offset, settings, DungeonSessionManager.getInstance().server.overworld().getRandom(), 2);
        this.dungeonRoomTemplate = dungeonRoomTemplate;
        this.branch = branch;
        this.level = level;
        this.position = position;
        this.offset = offset;
        this.settings = settings;
        this.rotated = settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90;
        this.boundingBox = dungeonRoomTemplate.template().getBoundingBox(settings, position);
        this.material = this.branch.floor.session.materials.get(RandomUtil.randIntBetween(0, this.branch.floor.session.materials.size()-1));
        dungeonRoomTemplate.rifts().forEach(pos -> {
            this.rifts.add(StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset).offset(position));
        });
        this.processMaterialBlocks(this.material);

        this.spawnPoint = dungeonRoomTemplate.spawnPoint();
        level.setBlock(TemplateHelper.transform(spawnPoint, this), Blocks.AIR.defaultBlockState(), 2);

        for (ConnectionPoint point : inputPoints) {
            point.room = this;
            point = point.transformed(settings, position, offset);
            this.connectionPoints.add(point);
        }
    }

    public void processConnectionPoints(DungeonMaterial material) {
        WildDungeons.getLogger().info("PROCESSING {} CONNECTION POINTS", connectionPoints.size());
        for (ConnectionPoint point : connectionPoints) {
            for (BlockPos pos : point.positions) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                    HashMap<String, String> toCheck = new HashMap<>();
                    HashMap<String, BlockState> blockStates = new HashMap<>();
                    toCheck.put("unBlocked", connectionBlockEntity.unblockedBlockstate);

                    toCheck.forEach((key, value) -> {
                        try {
                            blockStates.put(key, BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), value, true).blockState());
                        } catch (CommandSyntaxException e) {
                            blockStates.put(key, Blocks.AIR.defaultBlockState());
                            throw new RuntimeException(e);
                        }
                    });

                    blockStates.forEach((key, blockState) -> {
                        blockStates.put(key, fixBlockStateProperties(blockState));
                    });

                    point.unBlockedBlockStates.put(pos, blockStates.get("unBlocked"));

                    level.setBlock(pos, point.connected ? blockStates.get("unBlocked") : material.getBasic(1), 2);
                } else {
                    WildDungeons.getLogger().info("OOPS! NO BLOCK ENTITY");
                }
            }
        }
    }

    public BlockState fixBlockStateProperties(BlockState input) {
        if ((input.hasProperty(BlockStateProperties.HORIZONTAL_FACING) || input.hasProperty(BlockStateProperties.FACING))) {
            Direction facing = input.hasProperty(BlockStateProperties.FACING) ? input.getValue(BlockStateProperties.FACING) : input.getValue(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape stairsShape = input.hasProperty(BlockStateProperties.STAIRS_SHAPE) ? input.getValue(BlockStateProperties.STAIRS_SHAPE) : null;

            if (facing != Direction.UP && facing != Direction.DOWN) {
                switch (this.settings.getRotation()) {
                    case CLOCKWISE_90:
                        facing = facing.getClockWise();
                        break;
                    case CLOCKWISE_180:
                        facing = facing.getOpposite();
                        break;
                    case COUNTERCLOCKWISE_90:
                        facing = facing.getCounterClockWise();
                        break;
                }

                switch (settings.getMirror()) {
                    case LEFT_RIGHT:
                        if ((facing == Direction.NORTH || facing == Direction.SOUTH) && !this.rotated) {
                            facing = facing.getOpposite();
                        }
                        if ((facing == Direction.EAST || facing == Direction.WEST) && this.rotated) {
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
                        if ((facing == Direction.EAST || facing == Direction.WEST) && !this.rotated) {
                            facing = facing.getOpposite();
                        }
                        if ((facing == Direction.NORTH || facing == Direction.SOUTH) && this.rotated) {
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

    public void processMaterialBlocks(DungeonMaterial material) {
        List<StructureTemplate.StructureBlockInfo> materialBlocks = this.dungeonRoomTemplate.materialBlocks();
        materialBlocks.forEach(structureBlockInfo -> {
            BlockPos newPos = TemplateHelper.transform(structureBlockInfo.pos(), this);
            level.setBlock(newPos, fixBlockStateProperties(material.replace(structureBlockInfo.state())), 2);
        });
    }

}
