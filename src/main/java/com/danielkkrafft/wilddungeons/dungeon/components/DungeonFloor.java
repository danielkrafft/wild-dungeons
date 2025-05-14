package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.process.PostProcessingStep;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDDimensions;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.danielkkrafft.wilddungeons.world.dimension.tools.InfiniverseAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorRegistry.DUNGEON_FLOOR_REGISTRY;
import static com.danielkkrafft.wilddungeons.registry.WDDimensions.WILDDUNGEON;

public class DungeonFloor {

    @Serializer.IgnoreSerialization private List<DungeonBranch> dungeonBranches = new ArrayList<>();
    private final String templateKey;
    private final BlockPos origin;
    private final ResourceKey<Level> LEVEL_KEY;
    private BlockPos spawnPoint;
    private final String sessionKey;
    private final int index;
    private final HashMap<ChunkPos, ArrayList<Vector2i>> chunkMap = new HashMap<>();
    private final HashMap<String, Boolean> playersInside = new HashMap<>();

    public DungeonFloorTemplate getTemplate() {return DUNGEON_FLOOR_REGISTRY.get(this.templateKey);}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public <T> T getProperty(HierarchicalProperty<T> property) { return this.getTemplate().get(property) == null ? this.getSession().getTemplate().get(property) : this.getTemplate().get(property); }
    public double getDifficulty() {return this.getSession().getTemplate().get(HierarchicalProperty.DIFFICULTY_MODIFIER) * Math.max(1,(this.getSession().getPlayers().size()) * this.getProperty(HierarchicalProperty.DIFFICULTY_MODIFIER) * Math.pow(0.9, this.getSession().getPlayers().size()));}
    public ServerLevel getLevel() {
        return DungeonSessionManager.getInstance().server.levels.get(this.LEVEL_KEY);
    }
    public List<WDPlayer> getActivePlayers() {return this.playersInside.entrySet().stream().map(e -> e.getValue() ? WDPlayerManager.getInstance().getOrCreateServerWDPlayer(e.getKey()) : null).filter(Objects::nonNull).toList();}
    public List<DungeonBranch> getBranches() {return this.dungeonBranches;}//we need to null check, because these are not serialized in the save file and will always null pointer when loading a save
    public BlockPos getOrigin() {return this.origin;}
    public ResourceKey<Level> getLevelKey() {return this.LEVEL_KEY;}
    public BlockPos getSpawnPoint() {return this.spawnPoint;}
    public String getSessionKey() {return this.sessionKey;}
    public int getIndex() {return this.index;}
    public HashMap<ChunkPos, ArrayList<Vector2i>> getChunkMap() {return this.chunkMap;}
    public List<BoundingBox> halfGeneratedRooms = new ArrayList<>();
    @Serializer.IgnoreSerialization public List<CompletableFuture<Void>> generationFutures = new ArrayList<>();
    private final List<WDPlayer> playersWaitingToEnter = new ArrayList<>();
    public static ResourceKey<Level> buildFloorLevelKey(DungeonFloor floor) { return ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(DungeonSessionManager.buildDungeonSessionKey(floor.getSession().getEntranceUUID()) + "___" + floor.getTemplate().name() + "___" + floor.index)); }
    @Serializer.IgnoreSerialization public int failureCount = 0;
    public DungeonFloor(String templateKey, String sessionKey, BlockPos origin) {
        this.sessionKey = sessionKey;
        this.index = this.getSession().getFloors().size();
        this.getSession().getFloors().add(this);
        this.templateKey = templateKey;

        this.LEVEL_KEY = buildFloorLevelKey(this);
        InfiniverseAPI.get().getOrCreateLevel(DungeonSessionManager.getInstance().server, LEVEL_KEY, () -> WDDimensions.createLevel(WILDDUNGEON));
        this.origin = this.getTemplate().origin() == null ? origin : this.getTemplate().origin();
        //this.calculateBranchLayout();
    }

    /**
     * Called when a player attempts to enter this floor. Adds players to a "waiting list" if there aren't enough rooms to explore yet.
     *
     * @param wdPlayer The player to handle entry for
     */
    public void attemptEnter(WDPlayer wdPlayer) {
        if (getBranches().stream().mapToInt(b -> b.getRooms().size()).sum() <= 10) {
            playersWaitingToEnter.add(wdPlayer); return;
        }
        onEnter(wdPlayer);
    }

    /**
     * Called when the player is actually being moved to this floor.
     *
     * @param wdPlayer The player to handle entry for
     */
    public void onEnter(WDPlayer wdPlayer) {
        playersInside.computeIfAbsent(wdPlayer.getUUID(), key -> {
            getSession().getStats(key).floorsFound += 1;
            return true;
        });
        this.playersInside.put(wdPlayer.getUUID(), true);
        wdPlayer.setCurrentDungeon(getSession());
        wdPlayer.travelToFloor(wdPlayer, wdPlayer.getCurrentFloor(), this);
        wdPlayer.getServerPlayer().setGameMode(wdPlayer.getLastGameMode());
        PacketDistributor.sendToPlayer(wdPlayer.getServerPlayer(), new SimplePacketManager.ClientboundTagPacket(ClientPacketHandler.Packets.NULL_SCREEN.asTag()));
        WDPlayerManager.syncAll(this.playersInside.keySet().stream().toList());
        wdPlayer.setSoundScape(this.getProperty(SOUNDSCAPE), this.getProperty(INTENSITY), true);
    }

    /**
     * Called when the player is being removed from this floor. Actual removal logic is handled at the session level
     *
     * @param wdPlayer The player to handle removal for
     */
    public void onExit(WDPlayer wdPlayer) {
        this.playersInside.put(wdPlayer.getUUID(), false);
    }

    /**
     * Called every server tick
     */
    public void tick() {
        if (this.getLevel() == null) return;
        if (playersInside.values().stream().anyMatch(v -> v)) dungeonBranches.forEach(DungeonBranch::tick);
    }

    /**
     * Checks a list of proposed BoundingBoxes to make sure none of them will overlap with the existing BoundingBoxes in the chunkMap or go out of bounds.
     *
     * @param proposedBoxes The list of BoundingBoxes to check. If any are found to overlap with existing boxes, we return false.
     */
    protected boolean areBoundingBoxesValid(List<BoundingBox> proposedBoxes) {
        HashMap<ChunkPos, ArrayList<Vector2i>> chunkMap = getChunkMap();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (BoundingBox proposedBox : proposedBoxes) {
            if (proposedBox.minY() < EmptyGenerator.MIN_Y || proposedBox.maxY() > EmptyGenerator.MIN_Y + EmptyGenerator.GEN_DEPTH) {
                WildDungeons.getLogger().info("OUT OF BOUNDS!");
                return false;
            }
            mutableBlockPos.set(proposedBox.minX(), proposedBox.minY(), proposedBox.minZ());
            ChunkPos min = new ChunkPos(mutableBlockPos);
            mutableBlockPos.set(proposedBox.maxX(), proposedBox.maxY(), proposedBox.maxZ());
            ChunkPos max = new ChunkPos(mutableBlockPos);

            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    ChunkPos newPos = new ChunkPos(x, z);
                    boolean chunkExists = chunkMap.containsKey(newPos);
                    List<DungeonRoom> roomsInChunk = new ArrayList<>();
                    if (chunkExists) {
                        for (Vector2i v2 : chunkMap.get(newPos)) {
                            DungeonRoom dungeonRoom = getBranches().get(v2.x).getRooms().get(v2.y);
                            roomsInChunk.add(dungeonRoom);
                        }
                    }
                    for (DungeonRoom room : roomsInChunk) {
                        for (BoundingBox box : room.getBoundingBoxes()) {
                            if (proposedBox.intersects(box)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Called upon initial Floor creation, and during validation. Creates a CompletableFuture which attempts to place branches until the amount required by DungeonLayout is met.
     */
    public void asyncGenerateBranches() {

        int totalBranchCount = getTemplate().branchTemplates().size();
        int currentBranchCount = this.dungeonBranches.size();
        int restartCount = 0;
        failureCount = 0;
        while (currentBranchCount < totalBranchCount && this.getLevel() != null) {
            WildDungeons.getLogger().info("Generating branch {} of {}", currentBranchCount, totalBranchCount-1);
            if (failureCount> 25) {
                WildDungeons.getLogger().error("Failed to generate dungeon. Deleting all branches and starting over.");
                restartCount+= 1;
                if (failureCount > 3) {
                    break;
                }
                dungeonBranches.forEach(DungeonBranch::destroyRooms);
                failureCount = 0;
                this.dungeonBranches.clear();
                currentBranchCount = 0;

            }
            try { tryGenerateBranch(currentBranchCount);
            } catch (Exception e) { e.printStackTrace(); }

            currentBranchCount = this.dungeonBranches.size();
        }

        this.handlePostProcessing(PRE_GEN_PROCESSING_STEPS);
        this.dungeonBranches.forEach(dungeonBranch -> dungeonBranch.handlePostProcessing(PRE_GEN_PROCESSING_STEPS));
        this.dungeonBranches.forEach(b -> b.getRooms().forEach(dungeonRoom -> dungeonRoom.handlePostProcessing(PRE_GEN_PROCESSING_STEPS)));
        this.dungeonBranches.forEach(DungeonBranch::actuallyPlaceInWorld);
        this.handlePostProcessing(POST_GEN_PROCESSING_STEPS);
        this.dungeonBranches.forEach(dungeonBranch -> dungeonBranch.handlePostProcessing(POST_GEN_PROCESSING_STEPS));
        this.dungeonBranches.forEach(b -> b.getRooms().forEach(dungeonRoom -> dungeonRoom.handlePostProcessing(POST_GEN_PROCESSING_STEPS)));


//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//
//        }).handle((result, throwable) -> {
//            if (throwable != null) WildDungeons.getLogger().error("Error generating branches", throwable);
//            LockSupport.unpark(Thread.currentThread());
//            return null;
//        });
//        generationFutures.add(future);

    }

    public void handlePostProcessing(HierarchicalProperty<List<PostProcessingStep>> stepsProperty) {
        List<PostProcessingStep> steps = this.getTemplate().get(stepsProperty);
        if (steps == null) return;
        for (PostProcessingStep step : steps) {
            List<DungeonRoom> rooms = new ArrayList<>();
            for (DungeonBranch branch : this.getBranches()) {
                rooms.addAll(branch.getRooms());
            }
            step.handle(rooms);
        }
    }

    /**
     * Attempts to generate a DungeonBranch at the specified branchIndex. If it fails to place, it will delete itself and the previous branch. Retries are handled in generateBranches.
     *
     * @param branchIndex The index of the branch to place. Handled linearly.
     */
    private void tryGenerateBranch(int branchIndex) {
        DungeonBranchTemplate nextBranch = getTemplate().branchTemplates().get(branchIndex).getRandom();
        DungeonBranch newBranch = nextBranch.placeInWorld(this);

        if (newBranch == null) {
            if (branchIndex <= 0) {
                return;
            }
            failureCount++;
            int index = nextBranch.rootOriginBranchIndex() == -1 ? 1 : branchIndex - nextBranch.rootOriginBranchIndex();

            for (int i = 1; i <= index; i++) {
                DungeonBranch previousBranch = this.dungeonBranches.get(branchIndex - i);
                if (previousBranch.getIndex() == 0) continue;
                previousBranch.destroyRooms();
                this.dungeonBranches.remove(previousBranch);
            }
            return;
        }

        if (branchIndex == 0) this.spawnPoint = this.dungeonBranches.getFirst().getSpawnPoint();
    }

    /**
     * Called every time a branch is completed. Checks if players on the "waiting list" can be entered depending on whether enough rooms have been generated.
     */
    int totalRooms = 0;
    public void onBranchComplete(DungeonBranch branch) {
        totalRooms += branch.getRooms().size();
        if (!playersWaitingToEnter.isEmpty() && totalRooms > 10) {
            playersWaitingToEnter.forEach(this::onEnter);
            playersWaitingToEnter.clear();
        }
    }

    /**
     * Called on shutdown and on quit. Stops all CompletableFutures associated with this floor.
     */
    public void cancelGenerations() {
        if (this.generationFutures == null) return;
        generationFutures.forEach(future -> future.cancel(true));
    }
    /**
     * Called from the SaveSystem to add branches to the floor when loading a save
     * Null checks and then adds a branch to the dungeonBranches list
     */
    public void addBranchFromSave(DungeonBranch branch) {
        //we need to null check, because these are not serialized in the save file and will always null pointer when loading a save
        //we shouldn't inline this because there are 40+ calls to that method and it kept causing knock-on errors
        if (this.dungeonBranches == null) this.dungeonBranches = new ArrayList<>();
        this.dungeonBranches.add(branch);
    }
}