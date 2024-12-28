package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
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

    public DungeonBranch(DungeonComponents.DungeonBranchTemplate branchTemplate, DungeonFloor floor, ServerLevel level, BlockPos origin) {
        this.branchTemplate = branchTemplate;
        this.materials = branchTemplate.materials() == null ? floor.materials : branchTemplate.materials();
        this.floor = floor;
        this.level = level;
        this.origin = origin;
        generateDungeonBranch();
    }

    private void generateDungeonBranch() {
        WildDungeons.getLogger().info("STARTING A NEW BRANCH. THIS WILL BE BRANCH #{}",floor.dungeonBranches.size());
        int tries = 0;
        while (dungeonRooms.size() < branchTemplate.roomCount() && tries < branchTemplate.roomCount() * 4) {
            populateNextRoom();
            tries++;
        }
        this.dungeonRooms.forEach(DungeonRoom::processConnectionPoints);
        WildDungeons.getLogger().info("PLACED {} ROOMS IN {} TRIES", dungeonRooms.size(), tries);
    }

    private void populateNextRoom() {

        DungeonComponents.DungeonRoomTemplate nextRoom = selectNextRoom();
        List<ConnectionPoint> templateConnectionPoints = new ArrayList<>();
        for (ConnectionPoint point : nextRoom.connectionPoints()) {
            templateConnectionPoints.add(ConnectionPoint.copy(point));
        }
        if (maybePlaceInitialRoom(templateConnectionPoints)) {return;}

        List<ConnectionPoint> entrancePoints = templateConnectionPoints.stream().filter(point -> !Objects.equals(point.getType(), "exit")).toList();
        ConnectionPoint entrancePoint = entrancePoints.get(new Random().nextInt(entrancePoints.size()));
        List<ConnectionPoint> exitPoints = populatePotentialExitPoints(entrancePoint);

        List<Pair<ConnectionPoint, StructurePlaceSettings>> validPoints = new ArrayList<>();
        BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 50; i++) {
            if (exitPoints.isEmpty()) break;

            ConnectionPoint exitPoint = exitPoints.remove(new Random().nextInt(exitPoints.size()));
            StructurePlaceSettings settings = TemplateHelper.handleRoomTransformation(entrancePoint, exitPoint, level.getRandom());
            ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
            proposedPoint.transform(settings, TemplateHelper.EMPTY_BLOCK_POS, TemplateHelper.EMPTY_BLOCK_POS);
            position.set(ConnectionPoint.getOffset(proposedPoint, exitPoint).offset(exitPoint.getNormal()));

            if (validateNextPoint(exitPoint, settings, position, nextRoom)) {
                validPoints.add(new Pair<>(exitPoint, settings));
            }
            if (validPoints.size() >= 3) {
                break;
            }

        }

        if (validPoints.isEmpty()) return;

        Pair<ConnectionPoint, StructurePlaceSettings> exitPoint = ConnectionPoint.selectBestPoint(validPoints, this, Y_TARGET, 100.0, 100.0, 100.0, 50.0);
        placeRoom(exitPoint.getFirst(), exitPoint.getSecond(), templateConnectionPoints, entrancePoint, nextRoom);

    }

    private boolean maybePlaceInitialRoom(List<ConnectionPoint> templateConnectionPoints) {
        if (dungeonRooms.isEmpty() && floor.dungeonBranches.isEmpty()) {
            dungeonRooms.add(branchTemplate.endingRoom().placeInWorld(this, level, origin, new StructurePlaceSettings(), templateConnectionPoints));
            openConnections += branchTemplate.endingRoom().connectionPoints().size();
            this.spawnPoint = dungeonRooms.getFirst().spawnPoint;
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
                if (ConnectionPoint.arePointsCompatible(entrancePoint, point)) {
                    exitPoints.add(point);
                }

            }
        }

        // THE BRANCH HAS NOT STARTED YET
        if (dungeonRooms.isEmpty() && !floor.dungeonBranches.isEmpty() && !floor.dungeonBranches.getLast().dungeonRooms.isEmpty()) {
            for (DungeonBranch branch : floor.dungeonBranches) {
                    for (DungeonRoom room : branch.dungeonRooms) {
                        for (ConnectionPoint point : room.connectionPoints) {
                            if (ConnectionPoint.arePointsCompatible(entrancePoint, point)) exitPoints.add(point);
                        }
                    }
            }
        }

        return exitPoints;
    }

    private boolean validateNextPoint(ConnectionPoint exitPoint, StructurePlaceSettings settings, BlockPos position, DungeonComponents.DungeonRoomTemplate nextRoom) {

        List<BoundingBox> proposedBoxes = nextRoom.getBoundingBoxes(settings, position);
        if (!floor.isBoundingBoxValid(proposedBoxes, dungeonRooms)) {
            exitPoint.incrementFailures();
            return false;
        } else {
            return true;

        }
    }

    public void placeRoom(ConnectionPoint exitPoint, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints, ConnectionPoint entrancePoint, DungeonComponents.DungeonRoomTemplate nextRoom) {
        ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
        proposedPoint.transform(settings, TemplateHelper.EMPTY_BLOCK_POS, TemplateHelper.EMPTY_BLOCK_POS);
        BlockPos position = ConnectionPoint.getOffset(proposedPoint, exitPoint).offset(exitPoint.getNormal());

        exitPoint.setConnected(true);
        entrancePoint.setConnected(true);
        exitPoint.unBlock(level);

        dungeonRooms.add(nextRoom.placeInWorld(this, level, position, settings, allConnectionPoints));
        openConnections += nextRoom.connectionPoints().size() - 2;
    }
}