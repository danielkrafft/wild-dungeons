package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

    public DungeonRoom(DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, List<ConnectionPoint> inputPoints) {
        dungeonRoomTemplate.template().placeInWorld(level, position, offset, settings, DungeonSessionManager.getInstance().server.overworld().getRandom(), 2);
        this.dungeonRoomTemplate = dungeonRoomTemplate;
        this.level = level;
        this.position = position;
        this.offset = offset;
        this.settings = settings;
        this.boundingBox = dungeonRoomTemplate.template().getBoundingBox(settings, position);
        dungeonRoomTemplate.rifts().forEach(pos -> {
            this.rifts.add(StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset).offset(position));
        });

        this.spawnPoint = dungeonRoomTemplate.spawnPoint();
        level.setBlock(StructureTemplate.transform(spawnPoint, settings.getMirror(), settings.getRotation(), offset).offset(position), Blocks.AIR.defaultBlockState(), 2);

        for (ConnectionPoint point : inputPoints) {
            point.room = this;
            point = point.transformed(settings, position, offset);
            this.connectionPoints.add(point);
        }
    }

    public void processConnectionPoints(ServerLevel level) {
        WildDungeons.getLogger().info("PROCESSING {} CONNECTION POINTS", connectionPoints.size());
        for (ConnectionPoint point : connectionPoints) {
            for (BlockPos pos : point.positions) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                    HashMap<String, String> toCheck = new HashMap<>();
                    HashMap<String, BlockState> blockStates = new HashMap<>();
                    toCheck.put("locked", connectionBlockEntity.lockedBlockstate);
                    toCheck.put("unlocked", connectionBlockEntity.unlockedBlockstate);

                    toCheck.forEach((key, value) -> {
                        try {
                            blockStates.put(key, BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), value, true).blockState());
                        } catch (CommandSyntaxException e) {
                            blockStates.put(key, Blocks.AIR.defaultBlockState());
                            throw new RuntimeException(e);
                        }
                    });

                    blockStates.forEach((key, blockState) -> {
                        if ((blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) || blockState.hasProperty(BlockStateProperties.FACING))) {
                            Direction facing = blockState.hasProperty(BlockStateProperties.FACING) ? blockState.getValue(BlockStateProperties.FACING) : blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

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
                                        if ((facing == Direction.NORTH || facing == Direction.SOUTH) && !point.rotated) {
                                            facing = facing.getOpposite();
                                        }
                                        if ((facing == Direction.EAST || facing == Direction.WEST) && point.rotated) {
                                            facing = facing.getOpposite();
                                        }
                                        break;
                                    case FRONT_BACK:
                                        if ((facing == Direction.EAST || facing == Direction.WEST) && !point.rotated) {
                                            facing = facing.getOpposite();
                                        }
                                        if ((facing == Direction.NORTH || facing == Direction.SOUTH) && point.rotated) {
                                            facing = facing.getOpposite();
                                        }
                                        break;
                                }
                            }

                            blockStates.put(key, blockState.hasProperty(BlockStateProperties.FACING) ?
                                    blockState.setValue(BlockStateProperties.FACING, facing) :
                                    blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
                        }
                    });

                    point.lockedBlockStates.put(pos, blockStates.get("locked"));
                    point.unlockedBlockStates.put(pos, blockStates.get("unlocked"));

                    level.setBlock(pos, point.locked ? blockStates.get("locked") : blockStates.get("unlocked"), 2);
                } else {
                    WildDungeons.getLogger().info("OOPS! NO BLOCK ENTITY");
                }
            }
        }
    }

}
