package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.room.DungeonRoom;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
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

    public WeightedTable<EntityType<?>> enemyTable;
    public double difficulty;

    public DungeonBranch(DungeonComponents.DungeonBranchTemplate template, DungeonFloor floor, ServerLevel level, BlockPos origin) {
        this.template = template;
        this.materials = template.materials() == null ? floor.materials : template.materials();
        this.floor = floor;
        this.enemyTable = template.enemyTable() == null ? floor.enemyTable : template.enemyTable();
        this.difficulty = floor.difficulty * template.difficulty() * Math.max(Math.pow(1.1, floor.dungeonBranches.size()), 1);
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
        List<ConnectionPoint> pointsToTry = new ArrayList<>(entrancePoints);

        while (!pointsToTry.isEmpty()) {

            ConnectionPoint entrancePoint = pointsToTry.remove(new Random().nextInt(pointsToTry.size()));
            List<ConnectionPoint> exitPoints = this.dungeonRooms.isEmpty() ? floor.dungeonBranches.getLast().dungeonRooms.getLast().getValidExitPoints(entrancePoint, false) : getValidExitPoints(entrancePoint, false);

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

            if (validPoints.isEmpty()) continue;

            Pair<ConnectionPoint, StructurePlaceSettings> exitPoint = ConnectionPoint.selectBestPoint(validPoints, this, Y_TARGET, 75.0, 125.0, 200.0, 50.0);
            placeRoom(exitPoint.getFirst(), exitPoint.getSecond(), templateConnectionPoints, entrancePoint, nextRoom, 1);
            break;

        }

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

        List<ConnectionPoint> exitPoints = getValidExitPoints(entrancePoint, true);

        int i = floor.dungeonBranches.size() - 1;
        while (exitPoints.isEmpty()) {
            exitPoints.addAll(floor.dungeonBranches.get(i).getValidExitPoints(entrancePoint, true));
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
            WildDungeons.getLogger().info("TESTING POSITION {}", position);
            iterations += 1;
            WildDungeons.getLogger().info("PLACING AIR, ITERATION {}", iterations);
            for (BlockPos pos : exitPoint.getPositions()) {
                BlockPos newPos = pos.offset(exitPoint.getNormal().getX() * iterations, exitPoint.getNormal().getY() * iterations, exitPoint.getNormal().getZ() * iterations);
                level.setBlock(newPos, Blocks.AIR.defaultBlockState(), 2);
            }
            position.move(exitPoint.getDirection(), 1);
            if (iterations > 200) return;
        }
        placeRoom(exitPoint, settings, templateConnectionPoints, entrancePoint, nextRoom, iterations+1);
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

    private List<ConnectionPoint> getValidExitPoints(ConnectionPoint entrancePoint, boolean bypassFailures) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (DungeonRoom room : dungeonRooms) {
            exitPoints.addAll(room.getValidExitPoints(entrancePoint, bypassFailures));
        }
        return exitPoints;
    }

    public void placeRoom(ConnectionPoint exitPoint, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints, ConnectionPoint entrancePoint, DungeonComponents.DungeonRoomTemplate nextRoom, int offset) {
        ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
        proposedPoint.transform(settings, TemplateHelper.EMPTY_BLOCK_POS, TemplateHelper.EMPTY_BLOCK_POS);
        BlockPos position = ConnectionPoint.getOffset(proposedPoint, exitPoint).offset(exitPoint.getNormal().multiply(offset));

        exitPoint.setConnectedPoint(entrancePoint);
        entrancePoint.setConnectedPoint(exitPoint);
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
        for (DungeonRoom room : this.dungeonRooms) {
            room.onBranchEnter(player);
        }
    }

    public void onExit(WDPlayer player) {
        this.players.remove(player);
    }

    public void tick() {
        if (!this.players.isEmpty()) dungeonRooms.forEach(DungeonRoom::tick);
    }
}