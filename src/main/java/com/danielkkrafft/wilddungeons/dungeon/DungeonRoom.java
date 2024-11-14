package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DungeonRoom {
    public String type;
    public ResourceLocation location;
    public StructureTemplate template;
    private List<ConnectionPoint> connectionPoints = new ArrayList<>();
    public BoundingBox boundingBox;

    public DungeonRoom(StructureTemplateManager templateManager, String type, ResourceLocation location) {
        this.type = type;
        this.location = location;
        this.template = templateManager.getOrCreate(this.location);
        this.boundingBox = template.getBoundingBox(new StructurePlaceSettings(), new BlockPos(0,0,0));
        setupConnectionPoints();
    }

    public List<ConnectionPoint> getConnectionPoints() {
        return this.connectionPoints;
    }

    private void setupConnectionPoints() {

        List<StructureTemplate.StructureBlockInfo> DIAMOND_BLOCKS = template.filterBlocks(new BlockPos(0,0,0), new StructurePlaceSettings(), Blocks.DIAMOND_BLOCK);
        for (StructureTemplate.StructureBlockInfo block : DIAMOND_BLOCKS) {
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
                connectionPoints.add(targetPoint);
            } else {
                targetPoint.addPosition(block.pos());
            }
        }
    }

    public PlacedDungeonRoom placeInWorld(ServerLevel level, BlockPos position, StructurePlaceSettings settings) {
        WildDungeons.getLogger().info("PLACING ROOM");
        PlacedDungeonRoom room = new PlacedDungeonRoom(template, level, position, new BlockPos(0,0,0), settings, level.random, 2, connectionPoints);
        for (ConnectionPoint point : connectionPoints) {
            point.occupied = false;
        }
        return room;
    }

    public static class PlacedDungeonRoom {
        public StructureTemplate template;
        public ServerLevel level;
        public BlockPos position;
        public BlockPos offset;
        public StructurePlaceSettings settings;
        public RandomSource random;
        public int flags;
        public List<ConnectionPoint> connectionPoints = new ArrayList<>();
        public BoundingBox boundingBox;

        public PlacedDungeonRoom(StructureTemplate template, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, RandomSource random, int flags, List<ConnectionPoint> inputPoints) {
            template.placeInWorld(level, position, offset, settings, random, flags);
            this.template = template;
            this.level = level;
            this.position = position;
            this.offset = offset;
            this.settings = settings;
            this.random = random;
            this.flags = flags;
            this.boundingBox = template.getBoundingBox(settings, position);

            for (ConnectionPoint point : inputPoints) {
                point.room = this;
                point = point.transformed(settings, position, offset);
                point.log();
                this.connectionPoints.add(point);
            }
            processFlags(level);
        }

        private void processFlags(ServerLevel level) {
            List<StructureTemplate.StructureBlockInfo> DIAMOND_BLOCKS = template.filterBlocks(position, settings, Blocks.DIAMOND_BLOCK);
            for (StructureTemplate.StructureBlockInfo block : DIAMOND_BLOCKS) {
                level.setBlock(block.pos(), Blocks.EMERALD_BLOCK.defaultBlockState(), 2);
            }
        }
    }

    public static class ConnectionPoint {
        public List<BlockPos> positions;
        public Direction direction;
        public boolean occupied = false;
        public PlacedDungeonRoom room = null;

        public ConnectionPoint(BlockPos position, Direction direction) {
            this.positions = new ArrayList<>();
            positions.add(position);
            this.direction = direction;
        }

        public ConnectionPoint(ConnectionPoint point) {
            this.positions = point.positions;
            this.direction = point.direction;
            this.occupied = point.occupied;
            this.room = point.room;
        }

        public void addPosition(BlockPos pos) {
            positions.add(pos);
        }

        public ConnectionPoint transformed(StructurePlaceSettings settings, BlockPos position, BlockPos offset) {
            List<BlockPos> transformedPositions = new ArrayList<>();
            ConnectionPoint newPoint = new ConnectionPoint(this);

            WildDungeons.getLogger().info("CURRENT DIRECTION: " + newPoint.direction);
            WildDungeons.getLogger().info("APPLYING MIRROR: " + settings.getMirror());

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

            WildDungeons.getLogger().info("CURRENT DIRECTION: " + newPoint.direction);
            WildDungeons.getLogger().info("APPLYING ROTATION: " + settings.getRotation());

            switch (settings.getRotation()) {
                case CLOCKWISE_90:
                    WildDungeons.getLogger().info("APPLYING CLOCKWISE90");
                    newPoint.direction = newPoint.direction.getClockWise();
                    break;
                case CLOCKWISE_180:
                    WildDungeons.getLogger().info("APPLYING CLOCKWISE180");
                    newPoint.direction = newPoint.direction.getClockWise().getClockWise();
                    break;
                case COUNTERCLOCKWISE_90:
                    WildDungeons.getLogger().info("APPLYING COUNTERCLOCKWISE90");
                    newPoint.direction = newPoint.direction.getCounterClockWise();
                    break;
            }

            WildDungeons.getLogger().info("CURRENT DIRECTION: " + newPoint.direction);

            for (BlockPos pos : newPoint.positions) {
                BlockPos newPos = StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset);
                newPos = newPos.offset(position);
                transformedPositions.add(newPos);
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

        public void log() {
            WildDungeons.getLogger().info("CONNECTION POINT WITH DIRECTION: " + direction);
            for (BlockPos pos : positions) {
                WildDungeons.getLogger().info("-" + pos.toString());
            }
        }
    }
}
