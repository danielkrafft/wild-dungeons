package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonBranchRegistry.DUNGEON_BRANCH_REGISTRY;

public class DungeonBranch {

    private static final int OPEN_CONNECTIONS_TARGET = 6;
    private static final int Y_TARGET = 128;

    @Serializer.IgnoreSerialization private List<DungeonRoom> branchRooms = new ArrayList<>();
    private final String templateKey;
    private final int floorIndex;
    private final String sessionKey;
    private final BlockPos origin;
    private BlockPos spawnPoint;
    private int openConnections = 0;
    private int index;
    private final HashMap<String, Boolean> playersInside = new HashMap<>();
    private boolean fullyGenerated = false;
    @Serializer.IgnoreSerialization private DungeonFloor floor = null;

    public DungeonBranchTemplate getTemplate() {return DUNGEON_BRANCH_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonFloor getFloor() {return floor != null ? floor : getSession().getFloors().get(this.floorIndex);}
    public <T> T getProperty(HierarchicalProperty<T> property) { return this.getTemplate().get(property) == null ? this.getFloor().getProperty(property) : this.getTemplate().get(property); }
    public List<WDPlayer> getActivePlayers() {return this.playersInside.entrySet().stream().map(e -> e.getValue() ? WDPlayerManager.getInstance().getOrCreateServerWDPlayer(e.getKey()) : null).filter(Objects::nonNull).toList();}
    public List<DungeonRoom> getRooms() {return this.branchRooms;}
    public BlockPos getSpawnPoint() {return this.spawnPoint;}
    public int getIndex() {return this.index;}
    public void setIndex(int index) {this.index = index;}
    public boolean isFullyGenerated() {return this.fullyGenerated;}
    public boolean hasPlayerVisited(String uuid) {return this.playersInside.containsKey(uuid);}

    public DungeonBranch(String templateKey, DungeonFloor floor, BlockPos origin) {
        this.floor = floor;
        this.setIndex(this.floor.getBranches().size());
        this.floor.getBranches().add(this);
        this.templateKey = templateKey;
        this.floorIndex = floor.getIndex();
        this.sessionKey = floor.getSessionKey();
        this.origin = origin;
    }

    /**
     * Calculates this branch's difficulty using floor multiplier * branch multiplier * totalBranches^difficulty scaling
     */
    public double getDifficulty() {
        int totalBranches = 0;
        for (int i = 0; i < this.getFloor().getIndex(); i++) {
            totalBranches += this.getFloor().getSession().getFloors().get(i).getBranches().size();
        }
        totalBranches += this.getIndex();
        return (this.getFloor().getDifficulty() * this.getProperty(HierarchicalProperty.DIFFICULTY_MODIFIER) * Math.max(Math.pow(this.getProperty(HierarchicalProperty.DIFFICULTY_SCALING), totalBranches), 1)); //TODO the difficulty scaling thing doesn't work, it's just another multiplier.. but exponential
    }

    /**
     * Attempts to generate this DungeonBranch by placing DungeonRooms in the world.
     * If successful, post-processing steps will be handled.
     */
    public boolean generateDungeonBranch() {
        WildDungeons.getLogger().info("STARTING A NEW BRANCH. THIS WILL BE BRANCH #{}", this.index);

        int tries = 0;
        while (getRooms().size() < getTemplate().roomTemplates().size() && tries < 25) {
            tries = populateNextRoom() ? 0 : tries + 1;
        }

        if (getRooms().size() < getTemplate().roomTemplates().size()) {
            WildDungeons.getLogger().info("FAILED TO GENERATE ALL ROOMS. RESETTING BRANCH #{}", this.index);
            destroyRooms();
            return false;
        }

        this.spawnPoint = floor.getBranches().size() == 1 ? this.getRooms().getFirst().getSpawnPoint(floor.getLevel()) : getFloor().getBranches().getLast().getRooms().getLast().getSpawnPoint(floor.getLevel());
        return true;
    }

    public void actuallyPlaceInWorld() {
        this.getRooms().forEach(room -> {
            room.actuallyPlaceInWorld();
        });
        fullyGenerated = true;
        getFloor().onBranchComplete(this);
    }

    /**
     * Handles the majority of room placement, such as choosing the next room, finding eligible ConnectionPoints, and calculating rotations/offsets
     */
    private boolean populateNextRoom() {

        // Select the next room, attempting to pick one with at least 2 connection points so we don't get stuck

        DungeonRoomTemplate nextRoom = selectNextRoom();
        if (openConnections < OPEN_CONNECTIONS_TARGET) {
            int tries = 0;
            while (nextRoom.connectionPoints().size() < 2 && tries < 15) {
                nextRoom = selectNextRoom();
                tries++;
            }
        }

        // Place the first room directly if there are no rooms on this floor

        if (getRooms().isEmpty() && getFloor().getBranches().size() == 1) {
            DungeonRoom room = getTemplate().roomTemplates().getLast().getRandom().placeInWorld(this, origin, new StructurePlaceSettings());
            if (room != null) openConnections += room.getConnectionPoints().size();
            return true;
        }

        // Compile an iterable list of potential "Entrance Points" (the ConnectionPoints on the room to be added)

        List<ConnectionPoint> entrancePoints = nextRoom.connectionPoints().stream().filter(point -> !Objects.equals(point.getType(), "exit")).toList();
        List<ConnectionPoint> pointsToTry = new ArrayList<>(entrancePoints);

        while (!pointsToTry.isEmpty()) {

            // Compile an iterable list of potential "Exit Points" (the ConnectionPoints on the rooms we will attach to)
            // If this is the first room of the branch, we will look at the last room of the previous branch
            // We can also override the rootOriginBranchIndex to start this branch anywhere on the floor. This is useful for "forking" branches which split into multiple options.
            // If this is not the first room, "Exit Points" are sourced from getValidExitPoints()

            ConnectionPoint entrancePoint = pointsToTry.remove(new Random().nextInt(pointsToTry.size()));
            List<ConnectionPoint> exitPoints;
            if (this.getRooms().isEmpty()){
                int branchIndex = this.getTemplate().rootOriginBranchIndex() == -1 ? this.getFloor().getBranches().size() - 2 : this.getTemplate().rootOriginBranchIndex();
                DungeonBranch lastBranch = getFloor().getBranches().get(branchIndex);
                exitPoints = new ArrayList<>(lastBranch.getRooms().getLast().getValidExitPoints(entrancePoint));
            } else exitPoints = getValidExitPoints(entrancePoint);

            // Compile an iterable list of "Valid Points" (Exit Points which are confirmed not to result in BoundingBox conflicts)
            // We calculate rotation, mirror, and offset before checking the room's proposed bounding boxes against the rest of the rooms in the DungeonFloor

            List<ConnectionPoint> validPoints = new ArrayList<>();
            BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
            while (!exitPoints.isEmpty()){
                ConnectionPoint exitPoint = exitPoints.removeLast();
                StructurePlaceSettings settings = TemplateHelper.handleRoomTransformation(entrancePoint, exitPoint);
                ConnectionPoint proposedPoint = ConnectionPoint.copy(entrancePoint);
                position.set(ConnectionPoint.getOffset(settings, TemplateHelper.EMPTY_BLOCK_POS, proposedPoint, exitPoint).offset(exitPoint.getDirection(exitPoint.getRoom().getSettings()).getNormal()));
                if (getFloor().areBoundingBoxesValid(nextRoom.getBoundingBoxes(settings, position))) {
                    exitPoint.tempSettings = settings;
                    validPoints.add(exitPoint);
                }
            }
            if (validPoints.isEmpty()) continue;

            // Assuming we have found at least one valid point, points are scored based on a variety of weights, and the room is finally placed into the world

            ConnectionPoint exitPoint = ConnectionPoint.selectBestPoint(validPoints, this, Y_TARGET, 70.0, 200.0, 200.0, 30.0);
            placeRoom(exitPoint, entrancePoint, nextRoom);
            return true;
        }
        return false;
    }

    /**
     * Selects the next room to be placed. Supports probability based mandatory room inclusion and hard-limits for room placement count.
     */
    private DungeonRoomTemplate selectNextRoom() {

        for (Map.Entry<DungeonRoomTemplate, Integer> entry : getTemplate().mandatoryRooms().entrySet()) {
            int placedRooms = getRooms().stream().filter(room -> room.getTemplate().equals(entry.getKey())).toList().size();
            if (placedRooms >= entry.getValue()) continue;

            double probability = (double) (entry.getValue() - placedRooms) / (getTemplate().roomTemplates().size() - getRooms().size());
            if (RandomUtil.randFloatBetween(0, 1) < probability) {
                return entry.getKey();
            }
        }

        DungeonRoomTemplate nextRoom = getTemplate().roomTemplates().get(getRooms().size()).getRandom();

        if (getTemplate().limitedRooms().containsKey(nextRoom)) {
            final DungeonRoomTemplate limitedTemplate = nextRoom;
            int placedRooms = getRooms().stream().filter(room -> room.getTemplate().equals(limitedTemplate)).toList().size();
            if (placedRooms > getTemplate().limitedRooms().get(nextRoom)) nextRoom = getTemplate().roomTemplates().get(getRooms().size()).getRandom();
        }

        return nextRoom;
    }

    /**
     * Compiles an iterable list of all compatible "Exit Points" in this branch, given an input "Entrance Point".
     *
     * @param entrancePoint The Entrance Point to test compatibility with
     */
    private List<ConnectionPoint> getValidExitPoints(ConnectionPoint entrancePoint) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        getRooms().forEach(room -> exitPoints.addAll(room.getValidExitPoints(entrancePoint)));
        return exitPoints;
    }

    /**
     * Actually places a DungeonRoomTemplate in the world. Handles ConnectionPoint post-processing
     *
     * @param exitPoint The existing ConnectionPoint which the new room will be attached to
     * @param entrancePoint The new ConnectionPoint which will attach to the Exit Point
     * @param nextRoom The DungeonRoomTemplate for the room to be placed
     */
    public void placeRoom(ConnectionPoint exitPoint, ConnectionPoint entrancePoint, DungeonRoomTemplate nextRoom) {
        BlockPos position = ConnectionPoint.getOffset(exitPoint.tempSettings, TemplateHelper.EMPTY_BLOCK_POS, entrancePoint, exitPoint).offset(exitPoint.getDirection(exitPoint.getRoom().getSettings()).getNormal().multiply(1));
        DungeonRoom room = nextRoom.placeInWorld(this, position, exitPoint.tempSettings);
        if (room == null) return;

        ConnectionPoint newEntrancePoint = room.getConnectionPoints().get(entrancePoint.getIndex());
        newEntrancePoint.setRoom(room);
        exitPoint.setConnectedPoint(newEntrancePoint);
        newEntrancePoint.setConnectedPoint(exitPoint);
        exitPoint.unBlock(getFloor().getLevel());
        if (this.getRooms().size() == 1) exitPoint.loadingBlock(getFloor().getLevel());
        openConnections += nextRoom.connectionPoints().size() - 2;
        room.onGenerate();
    }

    /**
     * Destroys all rooms within this branch. Actual branch deletion occurs in DungeonBranchTemplate
     */
    public void destroyRooms() {
        branchRooms.forEach(DungeonRoom::destroy);
        branchRooms.clear();
        openConnections = 0;
    }

    /**
     * Respawns a player at this branch's spawn point
     *
     * @param wdPlayer The player to respawn
     */
    public void respawn(WDPlayer wdPlayer) {
        SavedTransform savedTransform = new SavedTransform(Vec3.atCenterOf(getSpawnPoint()), 0, 0, this.getFloor().getLevelKey());
        CommandUtil.executeTeleportCommand(wdPlayer.getServerPlayer(), savedTransform);
    }

    /**
     * Called when a player enters this branch
     *
     * @param player The player who entered
     */
    public void onEnter(WDPlayer player) {
        playersInside.computeIfAbsent(player.getUUID(), key -> {getSession().getStats(key).branchesFound += 1; return true;});
        this.playersInside.put(player.getUUID(), true);
        for (DungeonRoom room : this.getRooms()) {
            room.onBranchEnter(player);
        }
    }

    /**
     * Called when a player exits this branch
     *
     * @param player The player who exited
     */
    public void onExit(WDPlayer player) {
        this.playersInside.put(player.getUUID(), false);
    }

    /**
     * Called every server tick
     */
    public void tick() {
        if (this.playersInside.values().stream().anyMatch(v -> v)) getRooms().forEach(DungeonRoom::tick);
    }
}