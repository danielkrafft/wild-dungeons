package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.IgnoreSerialization;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonBranchRegistry.DUNGEON_BRANCH_REGISTRY;

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
    private boolean fullyGenerated = false;

    @IgnoreSerialization
    private DungeonFloor floor = null;

    public DungeonBranchTemplate getTemplate() {return DUNGEON_BRANCH_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonFloor getFloor() {return floor != null ? floor : getSession().getFloors().get(this.floorIndex);}
    public WeightedPool<DungeonMaterial> getMaterials() {return this.getTemplate().materials() == null ? this.getFloor().getMaterials() : this.getTemplate().materials();}
    public boolean hasBedrockShell() {return this.getTemplate().hasBedrockShell() == null ? this.getFloor().hasBedrockShell() : this.getTemplate().hasBedrockShell();}
    public DungeonRoomTemplate.DestructionRule getDestructionRule() {return this.getTemplate().getDestructionRule() == null ? this.getFloor().getDestructionRule() : this.getTemplate().getDestructionRule();}
    public WeightedTable<DungeonRegistration.TargetTemplate> getEnemyTable() {return this.getTemplate().enemyTable() == null ? this.getFloor().getEnemyTable() : this.getTemplate().enemyTable();}
    public double getDifficultyScaling(){
        double difficultyScaling = this.getTemplate().difficultyScaling();
        if (difficultyScaling == -1) difficultyScaling = this.getFloor().getDifficultyScaling();
        return difficultyScaling;
    }
    public double getDifficulty() {
        int totalBranches = 0;
        for (int i = 0; i < this.getFloor().getIndex(); i++) {
            totalBranches += this.getFloor().getSession().getFloors().get(i).getBranches().size();
        }
        totalBranches += this.getIndex();
        return (this.getFloor().getDifficulty() * this.getTemplate().difficulty()) * Math.max(Math.pow(this.getDifficultyScaling(), totalBranches), 1);
    }
    public List<WDPlayer> getActivePlayers() {return this.playerStatuses.entrySet().stream().map(e -> {
        if (e.getValue().inside) return WDPlayerManager.getInstance().getOrCreateServerWDPlayer(e.getKey());
        return null;
    }).filter(Objects::nonNull).toList();}
    public List<DungeonRoom> getRooms() {return this.branchRooms;}
    public BlockPos getSpawnPoint() {return this.spawnPoint;}
    public int getIndex() {return this.index;}
    public void setIndex(int index) {this.index = index;}
    public boolean isFullyGenerated() {return this.fullyGenerated;}
    public boolean hasPlayerVisited(String uuid) {return this.playerStatuses.containsKey(uuid);}
    public int blockingMaterialIndex() {return this.getTemplate().blockingMaterialIndex() == -1 ? this.getFloor().blockingMaterialIndex() : this.getTemplate().blockingMaterialIndex();}

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
        fullyGenerated = false;
        int tries = 0;
        while (getRooms().size() < getTemplate().roomTemplates().size() && tries < 50) {
            if (populateNextRoom()) {
                tries = 0;
            } else {
                tries++;
            }
        }
        if (getRooms().size() < getTemplate().roomTemplates().size()) {
            WildDungeons.getLogger().info("FAILED TO GENERATE ALL ROOMS. RESETTING BRANCH #{}", this.index);
            destroy();
            return false;
        }

        this.getRooms().forEach(room -> {
            room.processConnectionPoints(floor);
            room.processShell();
            room.onBranchComplete();
        });

        setupBoundingBox();
        this.spawnPoint = floor.getBranches().size() == 1 ? this.getRooms().getFirst().getSpawnPoint(floor.getLevel()) : getFloor().getBranches().getLast().getRooms().getLast().getSpawnPoint(floor.getLevel());

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::generateDungeonBranch");
        fullyGenerated = true;
        return true;
    }

    public void destroy() {
        branchRooms.forEach(DungeonRoom::destroy);
        branchRooms.clear();
        openConnections = 0;
    }

    private boolean populateNextRoom() {

        DungeonRoomTemplate nextRoom = selectNextRoom();
        //in order to keep the openConnections from getting too low, we will try to find a room with at least 3 connection points
        if (openConnections < OPEN_CONNECTIONS_TARGET) {
            int tries = 0;
            while (nextRoom.connectionPoints().size() < 3 && tries < 15) {
                nextRoom = selectNextRoom();
                tries++;
            }
        }

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
            List<ConnectionPoint> exitPoints = new ArrayList<>();
            if (this.getRooms().isEmpty()){
                exitPoints = new ArrayList<>(getFloor().getBranches().get(this.getIndex() - 1).getRooms().getLast().getValidExitPoints(TemplateHelper.EMPTY_DUNGEON_SETTINGS, TemplateHelper.EMPTY_BLOCK_POS, nextRoom, entrancePoint, false));
                /*DungeonBranch lastBranch = floor.getBranches().get(floor.getBranches().size() - 2);
                for (int i = 0; i < 3 ; i++) {//
                    int index = lastBranch.branchRooms.size() - (1 + i);
                    if (index < 0) break;
                    DungeonRoom room = lastBranch.branchRooms.get(index);
                    if (room!=null) {
                        exitPoints.addAll(room.getValidExitPoints(TemplateHelper.EMPTY_DUNGEON_SETTINGS, TemplateHelper.EMPTY_BLOCK_POS, nextRoom, entrancePoint, false));
                    }
                }*/
            } else exitPoints = getValidExitPoints(TemplateHelper.EMPTY_DUNGEON_SETTINGS, nextRoom, entrancePoint, false);

            List<Pair<ConnectionPoint, StructurePlaceSettings>> validPoints = new ArrayList<>();
            BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();

            while (!exitPoints.isEmpty()){
                ConnectionPoint exitPoint = exitPoints.removeLast();
                StructurePlaceSettings settings = TemplateHelper.handleRoomTransformation(entrancePoint, exitPoint);
                ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
                position.set(ConnectionPoint.getOffset(settings, TemplateHelper.EMPTY_BLOCK_POS, proposedPoint, exitPoint).offset(exitPoint.getDirection(exitPoint.getRoom().getSettings()).getNormal()));
                if (validateNextPoint(exitPoint, settings, position, nextRoom)) {
                    validPoints.add(new Pair<>(exitPoint, settings));
                }
                /*if (validPoints.size() >= 3) {
                    break;
                }*/
            }
            if (validPoints.isEmpty()) continue;

            Pair<ConnectionPoint, StructurePlaceSettings> exitPoint = ConnectionPoint.selectBestPoint(validPoints, this, Y_TARGET, 70.0, 200.0, 200.0, 30.0);
            placeRoom(exitPoint.getFirst(), exitPoint.getSecond(), templateConnectionPoints, entrancePoint, nextRoom, 1);
            return true;
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::populateNextRoom");
        return false;
    }

    private boolean maybePlaceInitialRoom(List<ConnectionPoint> templateConnectionPoints) {
        if (getRooms().isEmpty() && getFloor().getBranches().size() == 1) {
            WildDungeons.getLogger().info("ATTEMPTING TO PLACE INITIAL ROOM");
            DungeonRoom room = getTemplate().roomTemplates().getLast().getRandom().placeInWorld(this, getFloor().getLevel(), origin, new StructurePlaceSettings(), templateConnectionPoints);
            openConnections += room.getConnectionPoints().size();
            return true;
        }
        return false;
    }
    private boolean shouldPlaceMandatoryRoom(int mandatoryRoomsLeft, int roomsLeftToGenerate) {
        double probability = (double) mandatoryRoomsLeft / roomsLeftToGenerate;
        return RandomUtil.randFloatBetween(0, 1) < probability;
    }

    private DungeonRoomTemplate selectNextRoom() {
        List<Pair<DungeonRoomTemplate, Integer>> mandatoryRooms = new ArrayList<>(getTemplate().mandatoryRooms());
        for (Pair<DungeonRoomTemplate, Integer> mandatoryRoom : mandatoryRooms) {
            //check how many of the mandatory rooms are already placed
            int placedRooms = 0;
            for (DungeonRoom room : getRooms()) {
                if (room.getTemplate().equals(mandatoryRoom.getFirst())) {
                    placedRooms++;
                }
            }
            if (placedRooms >= mandatoryRoom.getSecond()) continue;
            mandatoryRoom = new Pair<>(mandatoryRoom.getFirst(), mandatoryRoom.getSecond() - placedRooms);
            if (mandatoryRoom.getSecond() > 0 && shouldPlaceMandatoryRoom(mandatoryRoom.getSecond(), getTemplate().roomTemplates().size() - getRooms().size())) {
//                WildDungeons.getLogger().info("PLACING MANDATORY ROOM: {}", mandatoryRoom.getFirst().name());
                return mandatoryRoom.getFirst();
            }
        }
        DungeonRoomTemplate nextRoom = getTemplate().roomTemplates().get(getRooms().size()).getRandom();

        boolean overPlaced = false;
        do {
            overPlaced = false;
            //if next room is in limited rooms, count how many times we already placed it
            for (Pair<DungeonRoomTemplate, Integer> limitedRoom : getTemplate().limitedRooms()) {
                if (limitedRoom.getFirst().equals(nextRoom)) {
                    int placedRooms = 0;
                    for (DungeonRoom room : getRooms()) {
                        if (room.getTemplate().equals(nextRoom)) {
                            placedRooms++;
                        }
                    }
                    if (placedRooms > limitedRoom.getSecond()) {
//                        WildDungeons.getLogger().info("OVERPLACED LIMITED ROOM: {}", nextRoom.name());
                        nextRoom = getTemplate().roomTemplates().get(getRooms().size()).getRandom();
                        overPlaced = true;
                        break;
                    } else {
                        overPlaced = false;
                    }
                }
            }
        } while (overPlaced);

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::selectNextRoom");
        return nextRoom;
    }

    private boolean validateNextPoint(ConnectionPoint exitPoint, StructurePlaceSettings settings, BlockPos position, DungeonRoomTemplate nextRoom) {
        List<BoundingBox> proposedBoxes = nextRoom.getBoundingBoxes(settings, position);
        boolean flag = true;
        if (!getFloor().isBoundingBoxValid(proposedBoxes)) {
            exitPoint.incrementFailures();
            flag = false;
        }
        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::validateNextPoint");
        return flag;
    }

    private List<ConnectionPoint> getValidExitPoints(StructurePlaceSettings settings, DungeonRoomTemplate nextRoom, ConnectionPoint entrancePoint, boolean bypassFailures) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (DungeonRoom room : getRooms()) {
            exitPoints.addAll(room.getValidExitPoints(settings, TemplateHelper.EMPTY_BLOCK_POS, nextRoom, entrancePoint, bypassFailures));
        }

        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::getValidExitPoints");
        return exitPoints;
    }

    public void placeRoom(ConnectionPoint exitPoint, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints, ConnectionPoint entrancePoint, DungeonRoomTemplate nextRoom, int offset) {
        ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
        BlockPos position = ConnectionPoint.getOffset(settings, TemplateHelper.EMPTY_BLOCK_POS, proposedPoint, exitPoint).offset(exitPoint.getDirection(exitPoint.getRoom().getSettings()).getNormal().multiply(offset));
        DungeonRoom room = nextRoom.placeInWorld(this, getFloor().getLevel(), position, settings, allConnectionPoints);

        ConnectionPoint newEntrancePoint = room.getConnectionPoints().get(entrancePoint.getIndex());

        newEntrancePoint.setRoom(room);
        exitPoint.setConnectedPoint(newEntrancePoint);
        newEntrancePoint.setConnectedPoint(exitPoint);
        exitPoint.unBlock(getFloor().getLevel());
        if (this.getRooms().size() == 1){
            exitPoint.loadingBlock(getFloor().getLevel());
        }
        openConnections += nextRoom.connectionPoints().size() - 2;
        room.onGenerate();
        WDProfiler.INSTANCE.logTimestamp("DungeonBranch::placeRoom");
    }

    public void setupBoundingBox() {
        this.boundingBox = new BoundingBox(this.origin);
        for (DungeonRoom room : this.getRooms()) {
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
        for (DungeonRoom room : this.getRooms()) {
            room.onBranchEnter(player);
        }
    }

    public void onExit(WDPlayer player) {
        this.playerStatuses.get(player.getUUID()).inside = false;
        this.playerStatuses.get(player.getUUID()).insideShell = false;
    }

    public void tick() {
        if (this.playerStatuses.values().stream().anyMatch(v -> v.inside)) getRooms().forEach(DungeonRoom::tick);
    }

    public void addRoom(DungeonRoom room) {
        if (this.branchRooms == null) this.branchRooms = new ArrayList<>();
        this.branchRooms.add(room);
    }
    public void sortRooms() {
        this.branchRooms.sort(Comparator.comparingInt(DungeonRoom::getIndex));
    }
}