package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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

public class DungeonBranch {
    public List<DungeonRoom> dungeonRooms = new ArrayList<>();
    public DungeonComponents.DungeonBranchTemplate branchTemplate;
    public DungeonFloor floor;
    public ServerLevel level;
    public BlockPos origin;
    public BlockPos spawnPoint;

    public static final int openConnectionsTarget = 6;

    public int openConnections = 0;

    public DungeonBranch(DungeonComponents.DungeonBranchTemplate branchTemplate, DungeonFloor floor, ServerLevel level, BlockPos origin) {
        this.branchTemplate = branchTemplate;
        this.floor = floor;
        this.level = level;
        this.origin = origin;
        generateDungeonBranch();
        if (!this.dungeonRooms.isEmpty()) {
            this.spawnPoint = this.dungeonRooms.getFirst().spawnPoint;
        }

    }

    private void generateDungeonBranch() {
        WildDungeons.getLogger().info("STARTING A NEW BRANCH. THIS WILL BE BRANCH #{}",floor.dungeonBranches.size());
        int tries = 0;
        while (dungeonRooms.size() < branchTemplate.roomCount() && tries < branchTemplate.roomCount() * 4) {
            populateNextRoom();
            tries++;
        }

        this.dungeonRooms.forEach(room -> room.processConnectionPoints(level));
        WildDungeons.getLogger().info("PLACED {} ROOMS IN {} TRIES", dungeonRooms.size(), tries);
    }

    private void populateNextRoom() {

        DungeonComponents.DungeonRoomTemplate nextRoom = selectNextRoom();
        List<ConnectionPoint> entrancePoints = nextRoom.connectionPoints().stream().map(ConnectionPoint::new).toList();
        WildDungeons.getLogger().info("SELECTED ROOM {} WITH BOUNDING BOX {}", nextRoom.name(), nextRoom.boundingBox());
        if (maybePlaceInitialRoom(nextRoom, entrancePoints)) {return;}

        ConnectionPoint entrancePoint = entrancePoints.get(new Random().nextInt(entrancePoints.size()));
        WildDungeons.getLogger().info("SELECTED ENTRANCE POINT {} WITH BOUNDING BOX {}", entrancePoint.direction, entrancePoint.boundingBox);

        List<ConnectionPoint> exitPoints = populatePotentialExitPoints(entrancePoint);

        for (int i = 0; i < 50; i++) {
            if (exitPoints.isEmpty()) break;
            if (attemptPlaceRoom(exitPoints, entrancePoints, entrancePoint, nextRoom)) {
                return;
            }
        }
        WildDungeons.getLogger().info("FAILED TO PLACE ROOM");
    }

    private boolean maybePlaceInitialRoom(DungeonComponents.DungeonRoomTemplate nextRoom, List<ConnectionPoint> entrancePoints) {
        if (dungeonRooms.isEmpty() && floor.dungeonBranches.isEmpty()) {
            WildDungeons.getLogger().info("PLACING INITIAL ROOM: {} AT BOUNDING BOX: {}", nextRoom.name(), nextRoom.template().getBoundingBox(new StructurePlaceSettings(), origin));
            dungeonRooms.add(branchTemplate.endingRoom().placeInWorld(level, origin, new StructurePlaceSettings(), entrancePoints));
            openConnections += branchTemplate.endingRoom().connectionPoints().size();
            return true;
        }
        return false;
    }

    private DungeonComponents.DungeonRoomTemplate selectNextRoom() {
        DungeonComponents.DungeonRoomTemplate nextRoom = branchTemplate.roomPool().getRandom();

        if (dungeonRooms.size() == branchTemplate.roomCount() - 1) {
            nextRoom = branchTemplate.endingRoom();
        } else if (openConnections < openConnectionsTarget) {
            while (nextRoom.connectionPoints().size() < 3) {
                nextRoom = branchTemplate.roomPool().getRandom();
            }
        }
        return nextRoom;
    }

    private List<ConnectionPoint> populatePotentialExitPoints(ConnectionPoint entrancePoint) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (DungeonRoom room : dungeonRooms) {
            for (ConnectionPoint point : room.connectionPoints) {
                if (isPointEligible(entrancePoint, point)) {
                    //WildDungeons.getLogger().info("FOUND {} EXIT WITH BOUNDING BOX {} TO BE ELIGIBLE", point.direction, point.boundingBox);
                    exitPoints.add(point);
                }

            }
        }

        if (dungeonRooms.isEmpty() && !floor.dungeonBranches.isEmpty() && !floor.dungeonBranches.getLast().dungeonRooms.isEmpty()) {
            floor.dungeonBranches.getLast().dungeonRooms.getLast().connectionPoints.forEach((point) -> {
                if (isPointEligible(entrancePoint, point)) exitPoints.add(point);
            });
        }
        WildDungeons.getLogger().info("FOUND {} ELIGIBLE EXIT POINTS", exitPoints.size());
        return exitPoints;
    }

    private boolean isPointEligible(ConnectionPoint en, ConnectionPoint ex) {
        if (ex.occupied) {
            //WildDungeons.getLogger().info("INELIGIBLE DUE TO OCCUPIED");
            return false;
        } else if (ex.failures > 10) {
            //WildDungeons.getLogger().info("INELIGIBLE DUE TO FAILURES");
            return false;
        } else if (!Objects.equals(en.pool, ex.pool)) {
            //WildDungeons.getLogger().info("INELIGIBLE DUE TO POOL");
            return false;
        } else if (en.direction.getAxis() == Direction.Axis.Y && ex.direction != en.direction.getOpposite()) {
            //WildDungeons.getLogger().info("INELIGIBLE DUE TO AXIS");
            return false;
        } else if (!en.getSize().equals(ex.getSize())) {
            //WildDungeons.getLogger().info("INELIGIBLE DUE TO SIZE EN: {} EX: {}", en.getSize(), ex.getSize());
            return false;
        }
        return true;
    }

    private boolean attemptPlaceRoom(List<ConnectionPoint> exitPoints, List<ConnectionPoint> entrancePoints, ConnectionPoint entrancePoint, DungeonComponents.DungeonRoomTemplate nextRoom) {

        ConnectionPoint exitPoint = exitPoints.remove(new Random().nextInt(exitPoints.size()));
        //WildDungeons.getLogger().info("SELECTED EXIT POINT {} WITH BOUNDING BOX {}", exitPoint.direction, exitPoint.boundingBox);

        StructurePlaceSettings settings = handleRoomTransformation(entrancePoint, exitPoint, level.getRandom());
        ConnectionPoint finalPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));
        //WildDungeons.getLogger().info("TRANSFORMED ENTRANCE POINT {} WITH BOUNDING BOX {}. ROOM BOUNDING BOX IS NOW {}", finalPoint.direction, finalPoint.boundingBox, nextRoom.template().getBoundingBox(settings, new BlockPos(0,0,0)));

        BoundingBox exitBox = exitPoint.boundingBox;
        BoundingBox enterBox = finalPoint.boundingBox;
        BlockPos blockOffset = new BlockPos(exitBox.minX() - enterBox.minX(), exitBox.minY() - enterBox.minY(), exitBox.minZ() - enterBox.minZ()).offset(exitPoint.direction.getNormal());
        //WildDungeons.getLogger().info("CALCULATED BLOCK OFFSET: {}", blockOffset);

        BoundingBox proposedBox = nextRoom.template().getBoundingBox(settings, blockOffset);
        //WildDungeons.getLogger().info("PROPOSED BOUNDING BOX: {}", proposedBox);

        if (!isBoundingBoxValid(proposedBox)) {
            WildDungeons.getLogger().info("FAILED TO PLACE");
            exitPoint.failures += 1;
            return false;
        } else {
            WildDungeons.getLogger().info("SUCCESSFULLY PLACED ROOM {} AT BOUNDING BOX {}", nextRoom.name(), proposedBox);
            exitPoint.locked = false;
            entrancePoint.locked = false;
            exitPoint.occupied = true;
            entrancePoint.occupied = true;
            exitPoint.unlock(level);
            dungeonRooms.add(nextRoom.placeInWorld(level, blockOffset, settings, entrancePoints));
            openConnections += nextRoom.connectionPoints().size() - 2;
            return true;
        }
    }

    private StructurePlaceSettings handleRoomTransformation(ConnectionPoint entrancePoint, ConnectionPoint exitPoint, RandomSource random) {
        StructurePlaceSettings settings = new StructurePlaceSettings();

        if (entrancePoint.direction.getAxis() == Direction.Axis.Y) {
            settings.setMirror(exitPoint.room.settings.getMirror());
            settings.setRotation(exitPoint.room.settings.getRotation());
            return settings;
        }

        settings.setMirror(Util.getRandom(Mirror.values(), random));
        //WildDungeons.getLogger().info("SELECTED MIRROR: {}", settings.getMirror());

        ConnectionPoint mirroredPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));
        //WildDungeons.getLogger().info("MIRRORED POINT TO DIRECTION: {}", mirroredPoint.direction);
        if (exitPoint.direction == mirroredPoint.direction) {
            settings.setRotation(Rotation.CLOCKWISE_180);
        } else if (exitPoint.direction == mirroredPoint.direction.getClockWise()) {
            settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
        } else if (exitPoint.direction == mirroredPoint.direction.getCounterClockWise()) {
            settings.setRotation(Rotation.CLOCKWISE_90);
        }
        //WildDungeons.getLogger().info("SELECTED ROTATION: {}", settings.getRotation());

        return settings;
    }

    private boolean isBoundingBoxValid(BoundingBox proposedBox) {
        for (DungeonBranch branch : floor.dungeonBranches) {
            for (DungeonRoom room : branch.dungeonRooms) {
                if (proposedBox.intersects(room.boundingBox)) {
                    WildDungeons.getLogger().info("BOUNDING BOX INTERSECTS " + room.boundingBox);
                    return false;
                } else if (proposedBox.minY() < EmptyGenerator.MIN_Y || proposedBox.maxY() > EmptyGenerator.MIN_Y + EmptyGenerator.GEN_DEPTH) {
                    WildDungeons.getLogger().info("BOUNDING BOX GOES OUT OF RANGE");
                    return false;
                }
            }
        }
        for (DungeonRoom room : dungeonRooms) {
            if (proposedBox.intersects(room.boundingBox)) {
                WildDungeons.getLogger().info("BOUNDING BOX INTERSECTS " + room.boundingBox);
                return false;
            } else if (proposedBox.minY() < EmptyGenerator.MIN_Y || proposedBox.maxY() > EmptyGenerator.MIN_Y + EmptyGenerator.GEN_DEPTH) {
                WildDungeons.getLogger().info("BOUNDING BOX GOES OUT OF RANGE");
                return false;
            }
        }
        return true;
    }
}