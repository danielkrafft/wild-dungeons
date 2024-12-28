package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

public class DungeonBranch {
    private static final int OPEN_CONNECTIONS_TARGET = 6;
    private static final int Y_TARGET = 64;

    public List<DungeonRoom> dungeonRooms = new ArrayList<>();
    public List<DungeonMaterial> materials;
    public DungeonComponents.DungeonBranchTemplate branchTemplate;
    public DungeonFloor floor;
    public ServerLevel level;
    public BlockPos origin;
    public BlockPos spawnPoint;
    public int openConnections = 0;
    public int branches = 2;

    public DungeonBranch(DungeonComponents.DungeonBranchTemplate branchTemplate, DungeonFloor floor, ServerLevel level, BlockPos origin) {
        this.branchTemplate = branchTemplate;
        this.materials = branchTemplate.materials() == null ? floor.materials : branchTemplate.materials();
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

        this.dungeonRooms.forEach(room -> room.processConnectionPoints(room.material));
        WildDungeons.getLogger().info("PLACED {} ROOMS IN {} TRIES", dungeonRooms.size(), tries);
    }

    private void populateNextRoom() {

        DungeonComponents.DungeonRoomTemplate nextRoom = selectNextRoom();
        List<ConnectionPoint> entrancePointsPool = new ArrayList<>();
        List<ConnectionPoint> exitPointsPool = new ArrayList<>();
        for (ConnectionPoint point : nextRoom.connectionPoints()) {
            if (!Objects.equals(point.type, "exit")) {
                WildDungeons.getLogger().info("ADDING A NEW POINT WITH TYPE: {}", point.type);
                entrancePointsPool.add(new ConnectionPoint(point));
            } else {
                exitPointsPool.add(new ConnectionPoint(point));
            }
        }

        WildDungeons.getLogger().info("SELECTED NEXT ROOM: {} WITH {} ENTRANCE POINTS", nextRoom.name(), entrancePointsPool.size());
        if (maybePlaceInitialRoom(entrancePointsPool, exitPointsPool)) {return;}

        ConnectionPoint entrancePoint = entrancePointsPool.get(new Random().nextInt(entrancePointsPool.size()));
        WildDungeons.getLogger().info("SELECTED ENTRANCE POINT {} WITH BOUNDING BOX {}", entrancePoint.direction, entrancePoint.boundingBox);

        List<ConnectionPoint> exitPoints = populatePotentialExitPoints(entrancePoint);
        WildDungeons.getLogger().info("TESTING {} POTENTIAL EXIT POINTS", exitPoints.size());

        List<ConnectionPoint> validPoints = new ArrayList<>();
        StructurePlaceSettings settings;
        ConnectionPoint finalPoint;
        BoundingBox exitBox;
        BoundingBox enterBox;
        BlockPos position;

        for (int i = 0; i < 50; i++) {
            if (exitPoints.isEmpty()) break;
            ConnectionPoint exitPoint = exitPoints.remove(new Random().nextInt(exitPoints.size()));
            WildDungeons.getLogger().info("TESTING {} EXIT POINT ON ROOM {}", exitPoint.direction, exitPoint.room);

            settings = TemplateHelper.handleRoomTransformation(entrancePoint, exitPoint, level.getRandom());
            finalPoint = entrancePoint.transformed(settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

            exitBox = exitPoint.boundingBox;
            enterBox = finalPoint.boundingBox;
            position = new BlockPos(exitBox.minX() - enterBox.minX(), exitBox.minY() - enterBox.minY(), exitBox.minZ() - enterBox.minZ()).offset(exitPoint.direction.getNormal());

            if (validateNextPoint(exitPoint, settings, position, nextRoom)) {
                validPoints.add(exitPoint);
                exitPoint.settings = settings;
                WildDungeons.getLogger().info("POINT IS VALID");
            } else {
                WildDungeons.getLogger().info("POINT IS NOT VALID");
            }
            if (validPoints.size() >= 3) {
                break;
            }

        }
        placeRoom(validPoints, entrancePointsPool, exitPointsPool, entrancePoint, nextRoom);

    }

    private boolean maybePlaceInitialRoom(List<ConnectionPoint> entrancePoints, List<ConnectionPoint> exitPoints) {
        if (dungeonRooms.isEmpty() && floor.dungeonBranches.isEmpty()) {
            entrancePoints.addAll(exitPoints);
            dungeonRooms.add(branchTemplate.endingRoom().placeInWorld(this, level, origin, new StructurePlaceSettings(), entrancePoints));
            openConnections += branchTemplate.endingRoom().connectionPoints().size();
            return true;
        }
        return false;
    }

    private DungeonComponents.DungeonRoomTemplate selectNextRoom() {
        DungeonComponents.DungeonRoomTemplate nextRoom = branchTemplate.getRandomRoom();

        if (dungeonRooms.size() == branchTemplate.roomCount() - 1) {
            nextRoom = branchTemplate.endingRoom();
        } else if (openConnections < OPEN_CONNECTIONS_TARGET) {
            while (nextRoom.connectionPoints().size() < 3) {
                nextRoom = branchTemplate.getRandomRoom();
            }
        }
        return nextRoom;
    }

    private List<ConnectionPoint> populatePotentialExitPoints(ConnectionPoint entrancePoint) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();

        // THE BRANCH HAS ALREADY STARTED
        for (DungeonRoom room : dungeonRooms) {
            for (ConnectionPoint point : room.connectionPoints) {
                if (isPointEligible(entrancePoint, point)) {
                    exitPoints.add(point);
                }

            }
        }

        // THE BRANCH HAS NOT STARTED YET
        if (dungeonRooms.isEmpty() && !floor.dungeonBranches.isEmpty() && !floor.dungeonBranches.getLast().dungeonRooms.isEmpty()) {
            for (DungeonBranch branch : floor.dungeonBranches) {
                if (branch.branches > 0) {
                    for (DungeonRoom room : branch.dungeonRooms) {
                        for (ConnectionPoint point : room.connectionPoints) {
                            if (isPointEligible(entrancePoint, point)) exitPoints.add(point);
                        }
                    }
                }
            }
        }

        return exitPoints;
    }

    private boolean isPointEligible(ConnectionPoint en, ConnectionPoint ex) {
        List<Boolean> conditions = List.of(
                !ex.connected,
                !Objects.equals(ex.type, "entrance"),
                ex.failures < 10,
                Objects.equals(en.pool, ex.pool),
                en.direction.getAxis() != Direction.Axis.Y || ex.direction == en.direction.getOpposite(),
                en.getSize().equals(ex.getSize())
        );
        return conditions.stream().allMatch(condition -> condition);
    }

    private boolean validateNextPoint(ConnectionPoint exitPoint, StructurePlaceSettings settings, BlockPos position, DungeonComponents.DungeonRoomTemplate nextRoom) {

        List<BoundingBox> proposedBoxes = nextRoom.getBoundingBoxes(settings, position);
        WildDungeons.getLogger().info("PROPOSED BOXES FOR {}: {}", nextRoom.name(), proposedBoxes);
        if (!floor.isBoundingBoxValid(proposedBoxes, dungeonRooms)) {
            exitPoint.failures += 1;
            return false;
        } else {
            return true;

        }
    }

    public void placeRoom(List<ConnectionPoint> validPoints, List<ConnectionPoint> entrancePoints, List<ConnectionPoint> exitPoints, ConnectionPoint entrancePoint, DungeonComponents.DungeonRoomTemplate nextRoom) {
        if (validPoints.isEmpty()) {
            return;
        }

        int totalDistanceToBranchOrigin = 0;
        int totalDistanceToFloorOrigin = 0;
        int totalDistanceToYTarget = 0;
        for (ConnectionPoint point : validPoints) {
            point.score = 0;
            BlockPos pointOrigin = new BlockPos(point.boundingBox.minX(), point.boundingBox.minY(), point.boundingBox.minZ());
            point.distanceToYTarget = Math.abs(pointOrigin.getY() - Y_TARGET);
            point.distanceToFloorOrigin = pointOrigin.distManhattan(floor.origin);
            point.distanceToBranchOrigin = this.dungeonRooms.isEmpty() ? 0 : pointOrigin.distManhattan(this.dungeonRooms.getFirst().position);
            totalDistanceToBranchOrigin += point.distanceToBranchOrigin;
            totalDistanceToFloorOrigin += point.distanceToFloorOrigin;
            totalDistanceToYTarget += point.distanceToYTarget;
        }

        for (ConnectionPoint point : validPoints) {
            point.score += (int) (100.0 * point.distanceToBranchOrigin / totalDistanceToBranchOrigin);
            point.score += (int) (100.0 * point.distanceToFloorOrigin / totalDistanceToFloorOrigin);
            point.score -= (int) (100.0 * point.distanceToYTarget / totalDistanceToYTarget);
            point.score += (int) (50.0 * Math.random());
        }
        validPoints.sort(Comparator.comparingInt(a -> a.score));

        validPoints.forEach(point -> {
            WildDungeons.getLogger().info("VALID POINT FOUND AT Y: {} WITH DISTANCE TO FLOOR ORIGIN: {} AND DISTANCE TO BRANCH ORIGIN: {} AND SCORE: {}", point.boundingBox.minY(), point.distanceToFloorOrigin, point.distanceToBranchOrigin, point.score);
        });
        ConnectionPoint exitPoint = validPoints.getLast();
        ConnectionPoint finalPoint = entrancePoint.transformed(exitPoint.settings, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

        BoundingBox exitBox = exitPoint.boundingBox;
        BoundingBox enterBox = finalPoint.boundingBox;
        BlockPos position = new BlockPos(exitBox.minX() - enterBox.minX(), exitBox.minY() - enterBox.minY(), exitBox.minZ() - enterBox.minZ()).offset(exitPoint.direction.getNormal());

        exitPoint.connected = true;
        if (dungeonRooms.isEmpty()) exitPoint.room.branch.branches -= 1;
        entrancePoint.connected = true;
        exitPoint.unBlock(level);
        entrancePoints.addAll(exitPoints);
        dungeonRooms.add(nextRoom.placeInWorld(this, level, position, exitPoint.settings, entrancePoints));
        openConnections += nextRoom.connectionPoints().size() - 2;
    }
}