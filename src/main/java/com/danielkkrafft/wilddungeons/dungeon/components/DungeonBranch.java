package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

public class DungeonBranch {
    private static final int OPEN_CONNECTIONS_TARGET = 6;

    public List<DungeonRoom> dungeonRooms = new ArrayList<>();
    public DungeonComponents.DungeonBranchTemplate branchTemplate;
    public DungeonFloor floor;
    public ServerLevel level;
    public BlockPos origin;
    public BlockPos spawnPoint;
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
        WildDungeons.getLogger().info("SELECTED ROOM {} WITH BOUNDING BOX {}", nextRoom.name(), nextRoom.getBoundingBox());
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
        } else if (openConnections < OPEN_CONNECTIONS_TARGET) {
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
                    exitPoints.add(point);
                }

            }
        }
        if (dungeonRooms.isEmpty() && !floor.dungeonBranches.isEmpty() && !floor.dungeonBranches.getLast().dungeonRooms.isEmpty()) {
            floor.dungeonBranches.getLast().dungeonRooms.getLast().connectionPoints.forEach((point) -> {
                if (isPointEligible(entrancePoint, point)) exitPoints.add(point);
            });
        }
        return exitPoints;
    }

    private boolean isPointEligible(ConnectionPoint en, ConnectionPoint ex) {
        List<Boolean> conditions = List.of(
                !ex.occupied,
                ex.failures < 10,
                Objects.equals(en.pool, ex.pool),
                en.direction.getAxis() != Direction.Axis.Y || ex.direction == en.direction.getOpposite(),
                en.getSize().equals(ex.getSize())
        );
        return conditions.stream().allMatch(condition -> condition);
    }

    private boolean attemptPlaceRoom(List<ConnectionPoint> exitPoints, List<ConnectionPoint> entrancePoints, ConnectionPoint entrancePoint, DungeonComponents.DungeonRoomTemplate nextRoom) {
        ConnectionPoint exitPoint = exitPoints.remove(new Random().nextInt(exitPoints.size()));

        StructurePlaceSettings settings = TemplateHelper.handleRoomTransformation(entrancePoint, exitPoint, level.getRandom());
        ConnectionPoint finalPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

        BoundingBox exitBox = exitPoint.boundingBox;
        BoundingBox enterBox = finalPoint.boundingBox;
        BlockPos blockOffset = new BlockPos(exitBox.minX() - enterBox.minX(), exitBox.minY() - enterBox.minY(), exitBox.minZ() - enterBox.minZ()).offset(exitPoint.direction.getNormal());

        BoundingBox proposedBox = nextRoom.template().getBoundingBox(settings, blockOffset);

        if (!floor.isBoundingBoxValid(proposedBox, dungeonRooms)) {
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
}