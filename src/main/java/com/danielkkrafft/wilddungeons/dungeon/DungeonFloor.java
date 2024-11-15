package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DungeonFloor {
    public List<DungeonRoom> roomPool;
    public DungeonRoom startingRoom;
    public int roomCount;

    public DungeonFloor(List<DungeonRoom> roomPool, DungeonRoom startingRoom, int roomCount) {
        this.roomPool = roomPool;
        this.roomCount = roomCount;
        this.startingRoom = startingRoom;
    }

    public PlacedDungeonFloor placeInWorld(ServerLevel level, BlockPos position) {
        return new PlacedDungeonFloor(this, level, position);
    }



    public static class PlacedDungeonFloor {
        public List<DungeonRoom.PlacedDungeonRoom> placedDungeonRooms = new ArrayList<>();
        public int tries = 0;

        public PlacedDungeonFloor(DungeonFloor floorTemplate, ServerLevel level, BlockPos position) {
            generateDungeonFloor(floorTemplate, level, position);
        }

        public void generateDungeonFloor(DungeonFloor floorTemplate, ServerLevel level, BlockPos position) {
            while (placedDungeonRooms.size() < floorTemplate.roomCount && tries < floorTemplate.roomCount * 2) {
                populateNextRoom(floorTemplate, level, position);
            }
            processConnectionPoints(level);
            WildDungeons.getLogger().info("TOTAL PLACED ROOMS: " + placedDungeonRooms.size());
        }

        public void populateNextRoom(DungeonFloor floorTemplate, ServerLevel level, BlockPos position) {
            tries++;

            if (maybePlaceInitialRoom(floorTemplate, level, position)) {return;}
            DungeonRoom nextRoom = DungeonRooms.getRandomFromPool(floorTemplate.roomPool);

            List<DungeonRoom.ConnectionPoint> entrancePoints = nextRoom.getConnectionPoints();
            DungeonRoom.ConnectionPoint entrancePoint = entrancePoints.get(new Random().nextInt(entrancePoints.size()));
            List<DungeonRoom.ConnectionPoint> exitPoints = populatePotentialExitPoints(entrancePoint);

            for (int i = 0; i < 5; i++) {
                if (exitPoints.isEmpty()) return;
                if (attemptPlaceRoom(exitPoints, entrancePoint, nextRoom, level)) return;
            }

        }

        public boolean maybePlaceInitialRoom(DungeonFloor floorTemplate, ServerLevel level, BlockPos position) {
            if (placedDungeonRooms.isEmpty()) {
                WildDungeons.getLogger().info("PLACING INITIAL ROOM");
                placedDungeonRooms.add(floorTemplate.startingRoom.placeInWorld(level, position, new StructurePlaceSettings()));
                return true;
            }
            return false;
        }

        public List<DungeonRoom.ConnectionPoint> populatePotentialExitPoints(DungeonRoom.ConnectionPoint entrancePoint) {
            List<DungeonRoom.ConnectionPoint> exitPoints = new ArrayList<>();
            for (DungeonRoom.PlacedDungeonRoom room : placedDungeonRooms) {
                for (DungeonRoom.ConnectionPoint point : room.connectionPoints) {
                    if (point.occupied) continue;
                    if (point.failures >= 3) continue;
                    if (!Objects.equals(entrancePoint.pool, point.pool)) continue;
                    if (entrancePoint.getDirection() == Direction.DOWN && point.getDirection() != Direction.UP) continue;
                    if (entrancePoint.getDirection() == Direction.UP && point.getDirection() != Direction.DOWN) continue;
                    if (!entrancePoint.getSize().equals(point.getSize())) continue;

                    exitPoints.add(point);
                }
            }
            return exitPoints;
        }

        public boolean attemptPlaceRoom(List<DungeonRoom.ConnectionPoint> exitPoints, DungeonRoom.ConnectionPoint entrancePoint, DungeonRoom nextRoom, ServerLevel level) {
            DungeonRoom.ConnectionPoint exitPoint = exitPoints.remove(new Random().nextInt(exitPoints.size()));
            StructurePlaceSettings settings = handlePointTransformation(entrancePoint, exitPoint);
            DungeonRoom.ConnectionPoint finalPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

            Vec3 exitAveragePoint = exitPoint.getAveragePosition();
            Vec3 entranceAveragePoint = finalPoint.getAveragePosition();
            Vec3 offset = exitAveragePoint.subtract(entranceAveragePoint);

            BlockPos blockOffset = new BlockPos((int) Math.round(offset.x), (int) Math.round(offset.y), (int) Math.round(offset.z)).offset(exitPoint.getDirection().getNormal());
            BoundingBox proposedBox = nextRoom.template.getBoundingBox(settings, blockOffset);

            if (!isBoundingBoxValid(proposedBox)) {
                exitPoint.failures += 1;
                return false;
            } else {
                exitPoint.occupied = true;
                entrancePoint.occupied = true;
                placedDungeonRooms.add(nextRoom.placeInWorld(level, blockOffset, settings));
                return true;
            }
        }

        public StructurePlaceSettings handlePointTransformation(DungeonRoom.ConnectionPoint entrancePoint, DungeonRoom.ConnectionPoint exitPoint) {
            StructurePlaceSettings settings = new StructurePlaceSettings();
            if (entrancePoint.lock) {
                settings.setMirror(exitPoint.room.settings.getMirror());
                settings.setRotation(exitPoint.room.settings.getRotation());

            } else {

                if (new Random().nextBoolean()) {
                    settings.setMirror(new Random().nextBoolean() ? Mirror.LEFT_RIGHT : Mirror.FRONT_BACK);
                }
                DungeonRoom.ConnectionPoint mirroredPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

                if (mirroredPoint.getDirection() == Direction.DOWN || mirroredPoint.getDirection() == Direction.UP) {
                    boolean b1 = new Random().nextBoolean();
                    boolean b2 = new Random().nextBoolean();
                    if (b1 && b2) {
                        settings.setRotation(Rotation.CLOCKWISE_180);
                    } else if (b1) {
                        settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
                    } else if (b2) {
                        settings.setRotation(Rotation.CLOCKWISE_90);
                    }

                } else if (exitPoint.getDirection() == mirroredPoint.getDirection()) {
                    settings.setRotation(Rotation.CLOCKWISE_180);
                } else if (exitPoint.getDirection() == mirroredPoint.getDirection().getClockWise()) {
                    settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
                } else if (exitPoint.getDirection() == mirroredPoint.getDirection().getCounterClockWise()) {
                    settings.setRotation(Rotation.CLOCKWISE_90);
                }

            }

            return settings;
        }

        public boolean isBoundingBoxValid(BoundingBox proposedBox) {
            for (DungeonRoom.PlacedDungeonRoom room : placedDungeonRooms) {
                if (proposedBox.intersects(room.boundingBox)) {
                    return false;
                } else if (proposedBox.minY() < EmptyGenerator.MIN_Y || proposedBox.maxY() > EmptyGenerator.MIN_Y + EmptyGenerator.GEN_DEPTH) {
                    return false;
                }
            }
            return true;
        }

        public void processConnectionPoints(ServerLevel level) {
            for (DungeonRoom.PlacedDungeonRoom room : this.placedDungeonRooms) {
                room.processConnectionPoints(level);
            }
        }
    }
}