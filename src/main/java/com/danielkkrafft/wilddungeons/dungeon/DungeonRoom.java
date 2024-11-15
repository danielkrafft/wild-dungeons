package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class DungeonRoom {
    public ResourceLocation location;
    public StructureTemplate template;
    private List<ConnectionPoint> connectionPoints = new ArrayList<>();
    public BoundingBox boundingBox;

    public DungeonRoom(StructureTemplateManager templateManager, ResourceLocation location) {
        this.location = location;
        this.template = templateManager.getOrCreate(this.location);
        this.boundingBox = template.getBoundingBox(new StructurePlaceSettings(), new BlockPos(0,0,0));
        setupConnectionPoints();
    }

    public List<ConnectionPoint> getConnectionPoints() {
        return this.connectionPoints;
    }

    private void setupConnectionPoints() {

        List<StructureTemplate.StructureBlockInfo> CONNECTION_BLOCKS = template.filterBlocks(new BlockPos(0,0,0), new StructurePlaceSettings(), WDBlocks.CONNECTION_BLOCK.get());
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
                if (point.direction == blockDirection) {

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
                targetPoint.lock = block.nbt().getBoolean("lock");
                connectionPoints.add(targetPoint);
            } else {
                targetPoint.addPosition(block.pos());
                targetPoint.boundingBox.encapsulate(block.pos());
            }
        }
    }

    public PlacedDungeonRoom placeInWorld(ServerLevel level, BlockPos position, StructurePlaceSettings settings) {
        WildDungeons.getLogger().info("PLACING ROOM");
        PlacedDungeonRoom room = new PlacedDungeonRoom(this, level, position, new BlockPos(0,0,0), settings, level.random, 2, connectionPoints);
        for (ConnectionPoint point : connectionPoints) {
            point.occupied = false;
        }
        return room;
    }

    public static class PlacedDungeonRoom {
        public DungeonRoom dungeonRoomTemplate;
        public ServerLevel level;
        public BlockPos position;
        public BlockPos offset;
        public StructurePlaceSettings settings;
        public RandomSource random;
        public int flags;
        public List<ConnectionPoint> connectionPoints = new ArrayList<>();
        public BoundingBox boundingBox;

        public PlacedDungeonRoom(DungeonRoom dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, RandomSource random, int flags, List<ConnectionPoint> inputPoints) {
            dungeonRoomTemplate.template.placeInWorld(level, position, offset, settings, random, flags);
            this.dungeonRoomTemplate = dungeonRoomTemplate;
            this.level = level;
            this.position = position;
            this.offset = offset;
            this.settings = settings;
            this.random = random;
            this.flags = flags;
            this.boundingBox = dungeonRoomTemplate.template.getBoundingBox(settings, position);

            for (ConnectionPoint point : inputPoints) {
                point.room = this;
                point = point.transformed(settings, position, offset);
                this.connectionPoints.add(point);
            }
        }

        public void processConnectionPoints(ServerLevel level) {
            for (ConnectionPoint point : connectionPoints) {
                for (BlockPos pos : point.positions) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                        String toCheck = point.occupied ? connectionBlockEntity.occupiedBlockstate : connectionBlockEntity.unoccupiedBlockstate;
                        BlockStateParser.BlockResult blockResult;
                        BlockState blockState;
                        try {
                            blockResult = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), toCheck, true);
                            blockState = blockResult.blockState();

                            WildDungeons.getLogger().info("PROCESSING BLOCK AT: " + pos);

                            if ((blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) || blockState.hasProperty(BlockStateProperties.FACING))) {
                                Direction facing = blockState.hasProperty(BlockStateProperties.FACING) ? blockState.getValue(BlockStateProperties.FACING) : blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

                                WildDungeons.getLogger().info("INITIAL FACING DIRECTION: " + facing);

                                if (facing != Direction.UP && facing != Direction.DOWN) {

                                    WildDungeons.getLogger().info("APPLYING ROTATION: " + settings.getRotation());
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

                                    WildDungeons.getLogger().info("CURRENT FACING DIRECTION: " + facing);

                                    WildDungeons.getLogger().info("APPLYING MIRROR: " + settings.getMirror());
                                    switch (settings.getMirror()) {
                                        case LEFT_RIGHT:
                                            if ((facing == Direction.NORTH || facing == Direction.SOUTH) && !point.rotated) {facing = facing.getOpposite();}
                                            if ((facing == Direction.EAST || facing == Direction.WEST) && point.rotated) {facing = facing.getOpposite();}
                                            break;
                                        case FRONT_BACK:
                                            if ((facing == Direction.EAST || facing == Direction.WEST) && !point.rotated) {facing = facing.getOpposite();}
                                            if ((facing == Direction.NORTH || facing == Direction.SOUTH) && point.rotated) {facing = facing.getOpposite();}
                                            break;
                                    }

                                    WildDungeons.getLogger().info("FINAL FACING DIRECTION: " + facing);
                                }

                                blockState = blockState.hasProperty(BlockStateProperties.FACING) ?
                                        blockState.setValue(BlockStateProperties.FACING, facing) :
                                        blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
                            }


                        } catch (CommandSyntaxException e) {
                            blockState = Blocks.AIR.defaultBlockState();
                            throw new RuntimeException(e);
                        }

                        level.setBlock(pos, blockState, 2);
                    }
                }
            }
        }

    }

    public static class ConnectionPoint {
        public List<BlockPos> positions;
        private Direction direction;
        public boolean occupied = false;
        public PlacedDungeonRoom room = null;
        public BoundingBox boundingBox;
        public int failures = 0;
        public String pool = "all";
        public boolean lock = false;
        private boolean rotated = false;

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
            this.lock = point.lock;
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

        public void log() {
            WildDungeons.getLogger().info("CONNECTION POINT WITH DIRECTION: " + direction);
        }
    }
}
