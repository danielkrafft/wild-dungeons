package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonFloor {
    public DungeonRoom room;
    public int roomCount;

    public DungeonFloor(DungeonRoom room, int roomCount) {
        this.room = room;
        this.roomCount = roomCount;
    }

    public PlacedDungeonFloor placeInWorld(ServerLevel level, BlockPos position) {
        return new PlacedDungeonFloor(this, level, position);
    }



    public static class PlacedDungeonFloor {
        public List<DungeonRoom.PlacedDungeonRoom> rooms = new ArrayList<>();
        public int tries = 0;

        public PlacedDungeonFloor(DungeonFloor floorTemplate, ServerLevel level, BlockPos position) {
            generateDungeonFloor(floorTemplate, level, position);
        }

        public void generateDungeonFloor(DungeonFloor floorTemplate, ServerLevel level, BlockPos position) {
            while (rooms.size() < floorTemplate.roomCount && tries < 100) {
                populateNextRoom(floorTemplate, level, position);
            }
        }

        public void populateNextRoom(DungeonFloor floorTemplate, ServerLevel level, BlockPos position) {
            tries++;

            if (rooms.isEmpty()) {
                WildDungeons.getLogger().info("PLACING INITIAL ROOM");
                rooms.add(floorTemplate.room.placeInWorld(level, position, new StructurePlaceSettings()));
                return;
            }

            WildDungeons.getLogger().info("PLACING SUBSEQUENT ROOM");

            List<DungeonRoom.ConnectionPoint> exitPoints = new ArrayList<>();
            for (DungeonRoom.PlacedDungeonRoom room : rooms) {
                for (DungeonRoom.ConnectionPoint point : room.connectionPoints) {
                    if (!point.occupied) {
                        exitPoints.add(point);
                    }
                }
            }
            DungeonRoom.ConnectionPoint exitPoint = exitPoints.get(new Random().nextInt(exitPoints.size()));

            WildDungeons.getLogger().info("SELECTED EXIT POINT: ");
            exitPoint.log();

            //GOOD

            //TODO this isn't gonna work with up/down connections
            //TODO also not gonna work with different connection sizes
            List<DungeonRoom.ConnectionPoint> entrancePoints = floorTemplate.room.getConnectionPoints();
            DungeonRoom.ConnectionPoint entrancePoint = entrancePoints.get(new Random().nextInt(entrancePoints.size()));

            WildDungeons.getLogger().info("SELECTED ENTRANCE POINT: ");
            entrancePoint.log();

            StructurePlaceSettings settings = new StructurePlaceSettings();
            if (new Random().nextBoolean()) {
                settings.setMirror(new Random().nextBoolean() ? Mirror.LEFT_RIGHT : Mirror.FRONT_BACK);
            }
            DungeonRoom.ConnectionPoint mirroredPoint = entrancePoint.transformed(settings, new BlockPos(0,0,0), new BlockPos(0,0,0));

            WildDungeons.getLogger().info("MIRRORED POINT: ");
            mirroredPoint.log();

            if (exitPoint.direction == mirroredPoint.direction) {
                settings.setRotation(Rotation.CLOCKWISE_180);
            } else if (exitPoint.direction == mirroredPoint.direction.getClockWise()) {
                settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
            } else if (exitPoint.direction == mirroredPoint.direction.getCounterClockWise()) {
                settings.setRotation(Rotation.CLOCKWISE_90);
            }
            DungeonRoom.ConnectionPoint rotatedPoint = entrancePoint.transformed(settings, new BlockPos(0,0,0), new BlockPos(0,0,0));

            WildDungeons.getLogger().info("ROTATED POINT: ");
            rotatedPoint.log();

            Vec3 exitAveragePoint = exitPoint.getAveragePosition();
            Vec3 entranceAveragePoint = rotatedPoint.getAveragePosition();

            Vec3 offset = exitAveragePoint.subtract(entranceAveragePoint);
            BlockPos blockOffset = new BlockPos((int) Math.round(offset.x), (int) Math.round(offset.y), (int) Math.round(offset.z));
            WildDungeons.getLogger().info("INITIAL BLOCK OFFSET DETERMINED");
            WildDungeons.getLogger().info(blockOffset.toString());

            blockOffset = blockOffset.offset(exitPoint.direction.getNormal());

            WildDungeons.getLogger().info("SECONDARY BLOCK OFFSET DETERMINED");
            WildDungeons.getLogger().info(blockOffset.toString());

            BoundingBox proposedBox = floorTemplate.room.template.getBoundingBox(settings, blockOffset);
            for (DungeonRoom.PlacedDungeonRoom room : rooms) {
                if (proposedBox.intersects(room.boundingBox)) {
                    WildDungeons.getLogger().info("BOUNDING BOX INTERSECTS");
                    WildDungeons.getLogger().info(proposedBox.toString());
                    return;
                }
            }

            exitPoint.occupied = true;
            entrancePoint.occupied = true;
            rooms.add(floorTemplate.room.placeInWorld(level, blockOffset, settings));

        }
    }
}
