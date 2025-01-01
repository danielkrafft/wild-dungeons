package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

public class DungeonBranch {
    private static final int OPEN_CONNECTIONS_TARGET = 6;
    private static final int Y_TARGET = 64;

    public List<DungeonRoom> dungeonRooms = new ArrayList<>();
    public WeightedPool<DungeonMaterial> materials;
    public DungeonComponents.DungeonBranchTemplate template;
    public DungeonFloor floor;
    public ServerLevel level;
    public BlockPos origin;
    public BlockPos spawnPoint;
    public int openConnections = 0;
    public int index;
    public BoundingBox boundingBox;
    public Set<WDPlayer> players = new HashSet<>();

    public DungeonBranch(DungeonComponents.DungeonBranchTemplate template, DungeonFloor floor, ServerLevel level, BlockPos origin) {
        this.template = template;
        this.materials = template.materials() == null ? floor.materials : template.materials();
        this.floor = floor;
        this.level = level;
        this.origin = origin;
        generateDungeonBranch();
        setupBoundingBox();
    }

    private void generateDungeonBranch() {
        WildDungeons.getLogger().info("STARTING A NEW BRANCH. THIS WILL BE BRANCH #{}",floor.dungeonBranches.size());
        int tries = 0;
        while (dungeonRooms.size() < template.roomTemplates().size() && tries < template.roomTemplates().size() * 4) {
            populateNextRoom();
            tries++;
        }
        if (dungeonRooms.size() < template.roomTemplates().size()) {
            forceLastRoom();
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
        List<ConnectionPoint> exitPoints = this.dungeonRooms.isEmpty() ? floor.dungeonBranches.getLast().dungeonRooms.getLast().getValidExitPoints(entrancePoint) : getValidExitPoints(entrancePoint);
        WildDungeons.getLogger().info("FOUND {} POTENTIAL EXIT POINTS", exitPoints.size());

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

        Pair<ConnectionPoint, StructurePlaceSettings> exitPoint = ConnectionPoint.selectBestPoint(validPoints, this, Y_TARGET, 75.0, 125.0, 100.0, 50.0);
        placeRoom(exitPoint.getFirst(), exitPoint.getSecond(), templateConnectionPoints, entrancePoint, nextRoom);

    }

    private void forceLastRoom() {
        WildDungeons.getLogger().info("FORCING LAST ROOM");
        DungeonComponents.DungeonRoomTemplate nextRoom = template.roomTemplates().getLast().getRandom();
        List<ConnectionPoint> templateConnectionPoints = new ArrayList<>();
        for (ConnectionPoint point : nextRoom.connectionPoints()) {
            templateConnectionPoints.add(ConnectionPoint.copy(point));
        }
        List<ConnectionPoint> entrancePoints = templateConnectionPoints.stream().filter(point -> !Objects.equals(point.getType(), "exit")).toList();
        ConnectionPoint entrancePoint = entrancePoints.get(new Random().nextInt(entrancePoints.size()));

        List<ConnectionPoint> exitPoints = getValidExitPoints(entrancePoint);

        int i = floor.dungeonBranches.size() - 1;
        while (exitPoints.isEmpty()) {
            exitPoints.addAll(floor.dungeonBranches.get(i).getValidExitPoints(entrancePoint));
            WildDungeons.getLogger().info("ADDING MORE EXIT POINTS. SIZE IS NOW {}", exitPoints.size());
            i -= 1;
            if (i == 0) return;
        }

        ConnectionPoint exitPoint = exitPoints.getLast();
        StructurePlaceSettings settings = TemplateHelper.handleRoomTransformation(entrancePoint, exitPoint, level.getRandom());
        ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
        proposedPoint.transform(settings, TemplateHelper.EMPTY_BLOCK_POS, TemplateHelper.EMPTY_BLOCK_POS);
        BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
        position.set(ConnectionPoint.getOffset(proposedPoint, exitPoint).offset(exitPoint.getNormal()));

        int iterations = 0;
        while (!validateNextPoint(exitPoint, settings, position, nextRoom)) {
            iterations += 1;
            WildDungeons.getLogger().info("PLACING AIR, ITERATION {}", iterations);
            for (BlockPos pos : exitPoint.getPositions()) {
                BlockPos newPos = pos.offset(exitPoint.getNormal().getX() * iterations, exitPoint.getNormal().getY() * iterations, exitPoint.getNormal().getZ() * iterations);
                WildDungeons.getLogger().info("SETTING BLOCK AT {}", newPos);
                level.setBlock(newPos, Blocks.AIR.defaultBlockState(), 2);
            }
            position.offset(exitPoint.getNormal());
            if (iterations > 1000) return;
        }
        placeRoom(exitPoint, settings, templateConnectionPoints, entrancePoint, nextRoom);
    }

    private boolean maybePlaceInitialRoom(List<ConnectionPoint> templateConnectionPoints) {
        if (dungeonRooms.isEmpty() && floor.dungeonBranches.isEmpty()) {
            DungeonRoom room = template.roomTemplates().getLast().getRandom().placeInWorld(this, level, origin, new StructurePlaceSettings(), templateConnectionPoints);
            room.index = dungeonRooms.size();
            dungeonRooms.add(room);

            openConnections += room.connectionPoints.size();
            this.spawnPoint = dungeonRooms.getFirst().spawnPoint;
            return true;
        }
        return false;
    }

    private DungeonComponents.DungeonRoomTemplate selectNextRoom() {
        DungeonComponents.DungeonRoomTemplate nextRoom = template.roomTemplates().get(dungeonRooms.size()).getRandom();

        if (openConnections < OPEN_CONNECTIONS_TARGET) {
            int tries = 0;
            while (nextRoom.connectionPoints().size() < 3 && tries < 15) {
                nextRoom = template.roomTemplates().get(dungeonRooms.size()).getRandom();
                tries++;
            }
        }

        return nextRoom;
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

    private List<ConnectionPoint> getValidExitPoints(ConnectionPoint entrancePoint) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (DungeonRoom room : dungeonRooms) {
            exitPoints.addAll(room.getValidExitPoints(entrancePoint));
        }
        return exitPoints;
    }

    public void placeRoom(ConnectionPoint exitPoint, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints, ConnectionPoint entrancePoint, DungeonComponents.DungeonRoomTemplate nextRoom) {
        ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
        proposedPoint.transform(settings, TemplateHelper.EMPTY_BLOCK_POS, TemplateHelper.EMPTY_BLOCK_POS);
        BlockPos position = ConnectionPoint.getOffset(proposedPoint, exitPoint).offset(exitPoint.getNormal());

        exitPoint.setConnected(true);
        entrancePoint.setConnected(true);
        exitPoint.unBlock(level);
        openConnections += nextRoom.connectionPoints().size() - 2;

        DungeonRoom room = nextRoom.placeInWorld(this, level, position, settings, allConnectionPoints);
        room.index = dungeonRooms.size();
        dungeonRooms.add(room);
        room.onGenerate();
    }

    public void setupBoundingBox() {
        this.boundingBox = new BoundingBox(this.origin);
        for (DungeonRoom room : this.dungeonRooms) {
            for (BoundingBox box : room.boundingBoxes) {
                this.boundingBox.encapsulate(box);
            }
        }
    }

    public void onEnter(WDPlayer player) {
        this.players.add(player);
    }

    public void onExit(WDPlayer player) {
        this.players.remove(player);
    }

    public void tick() {
        if (!this.players.isEmpty()) dungeonRooms.forEach(DungeonRoom::tick);
    }
}