package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
    public List<DungeonRoom> dungeonRooms = new ArrayList<>();

    public DungeonFloor(DungeonComponents.DungeonFloorTemplate floorTemplate, ServerLevel level, BlockPos position) {
        generateDungeonFloor(floorTemplate, level, position);
    }

    private void generateDungeonFloor(DungeonComponents.DungeonFloorTemplate floorTemplate, ServerLevel level, BlockPos position) {
        int tries = 0;
        while (dungeonRooms.size() < floorTemplate.roomCount() && tries < floorTemplate.roomCount() * 2) {
            populateNextRoom(floorTemplate, level, position);
            tries++;
        }
        this.dungeonRooms.forEach(room -> room.processConnectionPoints(level));
        WildDungeons.getLogger().info("PLACED {} ROOMS IN {} TRIES", dungeonRooms.size(), tries);
    }

    private void populateNextRoom(DungeonComponents.DungeonFloorTemplate floorTemplate, ServerLevel level, BlockPos position) {

        if (maybePlaceInitialRoom(floorTemplate, level, position)) return;
        DungeonComponents.DungeonRoomTemplate nextRoom = floorTemplate.roomPool().getRandom();
        List<ConnectionPoint> entrancePoints = new ArrayList<>();
        nextRoom.connectionPoints().forEach(point -> entrancePoints.add(new ConnectionPoint(point)));
        ConnectionPoint entrancePoint = entrancePoints.get(new Random().nextInt(entrancePoints.size()));
        List<ConnectionPoint> exitPoints = populatePotentialExitPoints(entrancePoint);

        for (int i = 0; i < 5; i++) {
            if (exitPoints.isEmpty()) return;
            if (attemptPlaceRoom(exitPoints, entrancePoints, entrancePoint, nextRoom, level)) return;
        }

    }

    private boolean maybePlaceInitialRoom(DungeonComponents.DungeonFloorTemplate floorTemplate, ServerLevel level, BlockPos position) {
        if (dungeonRooms.isEmpty()) {
            dungeonRooms.add(floorTemplate.startingRoom().placeInWorld(level, position, new StructurePlaceSettings(), floorTemplate.startingRoom().connectionPoints()));
            return true;
        }
        return false;
    }

    private List<ConnectionPoint> populatePotentialExitPoints(ConnectionPoint entrancePoint) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (DungeonRoom room : dungeonRooms) {
            for (ConnectionPoint point : room.connectionPoints) {
                if (isPointEligible(entrancePoint, point)) exitPoints.add(point);
            }
        }
        return exitPoints;
    }

    private boolean isPointEligible(ConnectionPoint en, ConnectionPoint ex) {
        return !ex.occupied && ex.failures < 3 && Objects.equals(en.pool, ex.pool) && (en.direction.getAxis() != Direction.Axis.Y || ex.direction == en.direction.getOpposite()) && en.getSize().equals(ex.getSize());
    }

    private boolean attemptPlaceRoom(List<ConnectionPoint> exitPoints, List<ConnectionPoint> entrancePoints, ConnectionPoint entrancePoint, DungeonComponents.DungeonRoomTemplate nextRoom, ServerLevel level) {
        ConnectionPoint exitPoint = exitPoints.remove(new Random().nextInt(exitPoints.size()));
        StructurePlaceSettings settings = handlePointTransformation(entrancePoint, exitPoint, level.getRandom());
        ConnectionPoint finalPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

        Vec3 exitAveragePoint = exitPoint.getAveragePosition();
        Vec3 entranceAveragePoint = finalPoint.getAveragePosition();
        Vec3 offset = exitAveragePoint.subtract(entranceAveragePoint);

        BlockPos blockOffset = new BlockPos((int) Math.round(offset.x), (int) Math.round(offset.y), (int) Math.round(offset.z)).offset(exitPoint.direction.getNormal());
        BoundingBox proposedBox = nextRoom.template().getBoundingBox(settings, blockOffset);

        if (!isBoundingBoxValid(proposedBox)) {
            exitPoint.failures += 1;
            return false;
        } else {
            exitPoint.occupied = true;
            entrancePoint.occupied = true;
            dungeonRooms.add(nextRoom.placeInWorld(level, blockOffset, settings, entrancePoints));
            return true;
        }
    }

    private StructurePlaceSettings handlePointTransformation(ConnectionPoint entrancePoint, ConnectionPoint exitPoint, RandomSource random) {
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

    private boolean isBoundingBoxValid(BoundingBox proposedBox) {
        for (DungeonRoom room : dungeonRooms) {
            if (proposedBox.intersects(room.boundingBox)) {
                return false;
            } else if (proposedBox.minY() < EmptyGenerator.MIN_Y || proposedBox.maxY() > EmptyGenerator.MIN_Y + EmptyGenerator.GEN_DEPTH) {
                return false;
            }
        }
        return true;
    }
}