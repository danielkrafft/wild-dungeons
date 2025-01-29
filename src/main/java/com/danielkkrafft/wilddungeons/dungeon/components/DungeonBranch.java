package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.IgnoreSerialization;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

public class DungeonBranch {

    private static final int OPEN_CONNECTIONS_TARGET = 6;
    private static final int Y_TARGET = 128;
    @IgnoreSerialization
    private List<DungeonRoom> branchRooms = new ArrayList<>();
    private String templateKey;
    private int floorIndex;
    private String sessionKey;
    private BlockPos origin;
    private BlockPos spawnPoint;
    private int openConnections = 0;
    private int index;
    private BoundingBox boundingBox;
    private HashMap<String, DungeonSession.PlayerStatus> playerStatuses = new HashMap<>();

    @IgnoreSerialization
    private DungeonFloor floor = null;
    public void setTempFloor(DungeonFloor floor) {this.floor = floor;}

    public DungeonBranchTemplate getTemplate() {return DungeonRegistry.DUNGEON_BRANCH_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonFloor getFloor() {return floor != null ? floor : getSession().getFloors().get(this.floorIndex);}
    public WeightedPool<DungeonMaterial> getMaterials() {return this.getTemplate().materials() == null ? this.getFloor().getMaterials() : this.getTemplate().materials();}
    public WeightedTable<EntityType<?>> getEnemyTable() {return this.getTemplate().enemyTable() == null ? this.getFloor().getEnemyTable() : this.getTemplate().enemyTable();}
    public double getDifficulty() {
        int totalBranches = 0;
        for (int i = 0; i < this.getFloor().getIndex(); i++) {
            totalBranches += this.getFloor().getSession().getFloors().get(i).getBranches().size();
        }
        totalBranches += this.getIndex();
        return this.getFloor().getDifficulty() * this.getTemplate().difficulty() * Math.max(Math.pow(this.getSession().getTemplate().difficultyScaling(), totalBranches), 1);
    }
    public List<WDPlayer> getActivePlayers() {return this.playerStatuses.entrySet().stream().map(e -> {
        if (e.getValue().inside) return WDPlayerManager.getInstance().getOrCreateWDPlayer(e.getKey());
        return null;
    }).filter(Objects::nonNull).toList();}
    public List<DungeonRoom> getRooms() {return this.branchRooms;}
    public BlockPos getSpawnPoint() {return this.spawnPoint;}
    public int getIndex() {return this.index;}
    public void setIndex(int index) {this.index = index;}

    public DungeonBranch(String templateKey, DungeonFloor floor, BlockPos origin) {
        this.floor = floor;
        this.setIndex(this.floor.getBranches().size());
        this.floor.getBranches().add(this);
        this.templateKey = templateKey;

        this.floorIndex = floor.getIndex();
        this.sessionKey = floor.getSessionKey();
        this.origin = origin;

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::new");
    }

    public boolean generateDungeonBranch() {
        WildDungeons.getLogger().info("STARTING A NEW BRANCH. THIS WILL BE BRANCH #{}", this.index);
        int tries = 0;
        while (branchRooms.size() < getTemplate().roomTemplates().size() && tries < 50) {
            if (populateNextRoom()) {
                tries = 0;
            } else {
                tries++;
            }
        }
        if (branchRooms.size() < getTemplate().roomTemplates().size()) {
            WildDungeons.getLogger().info("FAILED TO GENERATE ALL ROOMS. RESETTING BRANCH #{}", this.index);
            destroy();
            return false;
        }

        this.branchRooms.forEach(room -> room.processConnectionPoints(floor));

        setupBoundingBox();
        this.spawnPoint = floor.getBranches().size() == 1 ? this.branchRooms.getFirst().getSpawnPoint(floor.getLevel()) : floor.getBranches().getLast().branchRooms.getLast().getSpawnPoint(floor.getLevel());

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::generateDungeonBranch");
        return true;
    }

    public void destroy() {
        getFloor().getBranches().get(this.index-1).getRooms().forEach(dungeonRoom -> {
                    dungeonRoom.getConnectionPoints().forEach(connectionPoint -> {
                        if (connectionPoint.isConnected() && connectionPoint.getConnectedBranchIndex() == this.index) {
                            connectionPoint.unSetConnectedPoint();
                        }
                    });
                });
        branchRooms.forEach(DungeonRoom::destroy);
        branchRooms.clear();
        openConnections = 0;
    }

    private boolean populateNextRoom() {

        DungeonRoomTemplate nextRoom = selectNextRoom();

        List<ConnectionPoint> templateConnectionPoints = new ArrayList<>();
        for (ConnectionPoint point : nextRoom.connectionPoints()) {
            point.setIndex(templateConnectionPoints.size());
            templateConnectionPoints.add(ConnectionPoint.copy(point));
        }
        if (maybePlaceInitialRoom(templateConnectionPoints)) {return true;}

        List<ConnectionPoint> entrancePoints = templateConnectionPoints.stream().filter(point -> !Objects.equals(point.getType(), "exit")).toList();
        List<ConnectionPoint> pointsToTry = new ArrayList<>(entrancePoints);


        while (!pointsToTry.isEmpty()) {

            ConnectionPoint entrancePoint = pointsToTry.remove(new Random().nextInt(pointsToTry.size()));
            List<ConnectionPoint> exitPoints = this.branchRooms.isEmpty() ?
                    floor.getBranches().get(floor.getBranches().size()-2).branchRooms.getLast().getValidExitPoints(TemplateHelper.EMPTY_DUNGEON_SETTINGS, TemplateHelper.EMPTY_BLOCK_POS, nextRoom, entrancePoint, false)
                    : getValidExitPoints(TemplateHelper.EMPTY_DUNGEON_SETTINGS, nextRoom, entrancePoint, false);
            exitPoints.forEach(ConnectionPoint::resetFailures);//this probably isn't necessary and might cause the check later to always pass

            List<Pair<ConnectionPoint, StructurePlaceSettings>> validPoints = new ArrayList<>();
            BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();

            for (int i = 0; i < 50; i++) {
                if (exitPoints.isEmpty()) break;

                ConnectionPoint exitPoint = exitPoints.remove(new Random().nextInt(exitPoints.size()));


                StructurePlaceSettings settings = TemplateHelper.handleRoomTransformation(entrancePoint, exitPoint);


                ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
                position.set(ConnectionPoint.getOffset(settings, TemplateHelper.EMPTY_BLOCK_POS, proposedPoint, exitPoint).offset(exitPoint.getDirection(exitPoint.getRoom().getSettings()).getNormal()));


                if (validateNextPoint(exitPoint, settings, position, nextRoom)) {
                    validPoints.add(new Pair<>(exitPoint, settings));
                }
                if (validPoints.size() >= 3) {
                    break;
                }

            }


            if (validPoints.isEmpty()) continue;

            Pair<ConnectionPoint, StructurePlaceSettings> exitPoint = ConnectionPoint.selectBestPoint(validPoints, this, Y_TARGET, 70.0, 130.0, 200.0, 30.0);
            placeRoom(exitPoint.getFirst(), exitPoint.getSecond(), templateConnectionPoints, entrancePoint, nextRoom, 1);
            return true;
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::populateNextRoom");
        return false;
    }

    private boolean maybePlaceInitialRoom(List<ConnectionPoint> templateConnectionPoints) {
        if (branchRooms.isEmpty() && floor.getBranches().size() == 1) {
            WildDungeons.getLogger().info("ATTEMPTING TO PLACE INITIAL ROOM");
            DungeonRoom room = getTemplate().roomTemplates().getLast().getRandom().placeInWorld(this, floor.getLevel(), origin, new StructurePlaceSettings(), templateConnectionPoints);
            openConnections += room.getConnectionPoints().size();
            return true;
        }
        return false;
    }

    private DungeonRoomTemplate selectNextRoom() {
        DungeonRoomTemplate nextRoom = getTemplate().roomTemplates().get(branchRooms.size()).getRandom();

        if (openConnections < OPEN_CONNECTIONS_TARGET) {
            int tries = 0;
            while (nextRoom.connectionPoints().size() < 3 && tries < 15) {
                nextRoom = getTemplate().roomTemplates().get(branchRooms.size()).getRandom();
                tries++;
            }
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::selectNextRoom");
        return nextRoom;
    }

    private boolean validateNextPoint(ConnectionPoint exitPoint, StructurePlaceSettings settings, BlockPos position, DungeonRoomTemplate nextRoom) {

        List<BoundingBox> proposedBoxes = nextRoom.getBoundingBoxes(settings, position);
        boolean flag = true;
        if (!floor.isBoundingBoxValid(proposedBoxes)) {
            exitPoint.incrementFailures();
            flag = false;
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::validateNextPoint");
        return flag;
    }

    private List<ConnectionPoint> getValidExitPoints(StructurePlaceSettings settings, DungeonRoomTemplate nextRoom, ConnectionPoint entrancePoint, boolean bypassFailures) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (DungeonRoom room : branchRooms) {
            exitPoints.addAll(room.getValidExitPoints(settings, TemplateHelper.EMPTY_BLOCK_POS, nextRoom, entrancePoint, bypassFailures));
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::getValidExitPoints");
        return exitPoints;
    }

    public void placeRoom(ConnectionPoint exitPoint, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints, ConnectionPoint entrancePoint, DungeonRoomTemplate nextRoom, int offset) {
        ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
        BlockPos position = ConnectionPoint.getOffset(settings, TemplateHelper.EMPTY_BLOCK_POS, proposedPoint, exitPoint).offset(exitPoint.getDirection(exitPoint.getRoom().getSettings()).getNormal().multiply(offset));
        DungeonRoom room = nextRoom.placeInWorld(this, floor.getLevel(), position, settings, allConnectionPoints);

        ConnectionPoint newEntrancePoint = room.getConnectionPoints().get(entrancePoint.getIndex());

        newEntrancePoint.setRoom(room);
        exitPoint.setConnectedPoint(newEntrancePoint);
        newEntrancePoint.setConnectedPoint(exitPoint);
        exitPoint.unBlock(floor.getLevel());
        openConnections += nextRoom.connectionPoints().size() - 2;
        room.onGenerate();
        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::placeRoom");
    }

    public void setupBoundingBox() {
        this.boundingBox = new BoundingBox(this.origin);
        for (DungeonRoom room : this.branchRooms) {
            for (BoundingBox box : room.getBoundingBoxes()) {
                this.boundingBox.encapsulate(box);
            }
        }
        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::setupBoundingBox");
    }

    public void onEnter(WDPlayer player) {
        WildDungeons.getLogger().info("PLAYER ENTERED BRANCH: {}", this.getTemplate().name());
        playerStatuses.computeIfAbsent(player.getUUID(), key -> {getSession().getStats(key).branchesFound += 1; return new DungeonSession.PlayerStatus();});
        this.playerStatuses.get(player.getUUID()).inside = true;
        for (DungeonRoom room : this.branchRooms) {
            room.onBranchEnter(player);
        }
    }

    public void onExit(WDPlayer player) {
        this.playerStatuses.get(player.getUUID()).inside = false;
    }

    public void tick() {
        if (this.playerStatuses.values().stream().anyMatch(v -> v.inside)) branchRooms.forEach(DungeonRoom::tick);
    }

    public void addRoom(DungeonRoom room) {
        if (this.branchRooms == null) this.branchRooms = new ArrayList<>();
        this.branchRooms.add(room);
    }
    public void sortRooms() {
        this.branchRooms.sort(Comparator.comparingInt(DungeonRoom::getIndex));
    }
}