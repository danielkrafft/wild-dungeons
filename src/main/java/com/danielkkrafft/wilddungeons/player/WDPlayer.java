package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class WDPlayer {

    private HashMap<String, Integer> essenceTotals = new HashMap<>();
    private HashMap<Integer, SavedTransform> respawns = new HashMap<>();
    private HashMap<Integer, SavedTransform> positions = new HashMap<>();
    private String recentEssence = "essence:overworld";
    private String UUID;
    private int riftCooldown = 0;

    private String currentDungeon = "none";
    private int currentFloor = -1;
    private int currentBranch = -1;
    private int currentRoom = -1;
    private int currentLives = 0;

    private long blockPos = 0;

    public int getEssenceTotal(String key) {return this.essenceTotals.getOrDefault(key, 0);}
    public void setEssenceTotal(String key, int value) {this.essenceTotals.put(key, value);}
    public String getRecentEssence() {return this.recentEssence;}
    public void setRecentEssence(String recentEssence) {this.recentEssence = recentEssence;}


    public int getRiftCooldown() {return this.riftCooldown;}
    public void setRiftCooldown(int cooldown) {this.riftCooldown = cooldown;}
    public HashMap<Integer, SavedTransform> getPositions() {return this.positions;}
    public HashMap<Integer, SavedTransform> getRespawns() {return this.respawns;}

    public String getUUID() {return this.UUID;}

    public DungeonSession getCurrentDungeon() {return Objects.equals(this.currentDungeon, "none") ? null : DungeonSessionManager.getInstance().getDungeonSession(this.currentDungeon);}
    public void setCurrentDungeon(DungeonSession session) {
        this.currentDungeon = session == null ? "none" : DungeonSessionManager.buildDungeonSessionKey(session.getEntranceUUID());
        WildDungeons.getLogger().info("SETTING CURRENT DUNGEON TO {}", this.currentDungeon);
    }
    public DungeonFloor getCurrentFloor() {return this.currentFloor == -1 ? null : this.getCurrentDungeon().getFloors().get(this.currentFloor);}
    public void setCurrentFloor(DungeonFloor floor) {this.currentFloor = floor == null ? -1 : floor.getIndex();}
    public DungeonBranch getCurrentBranch() {return this.currentBranch == -1 ? null : this.getCurrentFloor().getBranches().get(this.currentBranch);}
    public void setCurrentBranch(DungeonBranch branch) {this.currentBranch = branch == null ? -1 : branch.getIndex();}
    public DungeonRoom getCurrentRoom() {return this.currentRoom == -1 ? null : this.getCurrentBranch().getRooms().get(this.currentRoom);}
    public void setCurrentRoom(DungeonRoom room) {this.currentRoom = room == null ? -1 : room.getIndex();}
    public int getCurrentLives() {return this.currentLives;}
    public void setCurrentLives(int currentLives) {this.currentLives = currentLives;}

    public ServerPlayer getServerPlayer() {return DungeonSessionManager.getInstance().server.getPlayerList().getPlayer(java.util.UUID.fromString(this.UUID));}

    public WDPlayer(String playerUUID){this.UUID = playerUUID;}

    public void tick() {
        if (riftCooldown > 0) {
            riftCooldown -= 1;
        }

        if (getServerPlayer() != null && !getServerPlayer().blockPosition().equals(BlockPos.of(blockPos))) {
            this.onPlayerMovedBlocks();
            this.blockPos = getServerPlayer().blockPosition().asLong();
        }
    }

    public void rootRespawn(MinecraftServer server) {
        if (respawns.isEmpty() || positions.isEmpty()) return;
        WildDungeons.getLogger().info("ROOT RESPAWNING");
        SavedTransform newPosition = positions.get(-1);
        WDPlayer.setRespawnPosition(respawns.get(-1), getServerPlayer(server));
        this.setCurrentDungeon(null);
        this.currentFloor = -1;
        this.currentBranch = -1;
        this.currentRoom = -1;
        this.currentLives = 0;
        this.riftCooldown = 100;
        respawns = new HashMap<>();
        positions = new HashMap<>();
        CommandUtil.executeTeleportCommand(getServerPlayer(server), newPosition);
    }

    public ServerPlayer getServerPlayer(MinecraftServer server) {
        return server.getPlayerList().getPlayer(java.util.UUID.fromString(UUID));
    }

    public void giveEssencePoints(String key, int points) {
        this.setRecentEssence(key);
        this.setEssenceTotal(key, Mth.clamp(this.getEssenceTotal(key) + points, 0, Integer.MAX_VALUE));
    }

    public float getEssenceLevel(String key) {
        int total = this.getEssenceTotal(key); //-5
        float level = 0;
        int need = 0;

        while (total > 0) {
            need = pointsNeededForNextLevel((int) level);

            if (need <= total) {
                level += 1;
                total -= need;
            } else {
                level += (float) total / (float) need;
                total = 0;
            }

        }

        return level;
    }

    public int pointsNeededForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    public void giveEssenceLevels(int levels, String key) {
        int currentLevels = Mth.floor(getEssenceLevel(key));
        int startingLevel = Math.min(currentLevels + levels, currentLevels);
        int targetLevel = Math.max(currentLevels + levels, currentLevels);
        this.giveEssencePoints(key, pointsBetweenRange(startingLevel, targetLevel));
    }

    public int pointsBetweenRange(int startingLevel, int targetLevel) {
        int totalAdded = 0;
        for (int i = startingLevel; i < targetLevel; i++) {
            totalAdded += pointsNeededForNextLevel(i);
        }
        return totalAdded;
    }

    public void onPlayerMovedBlocks() {
        this.handleCurrentRoom();
    }

    public void handleCurrentRoom() {
        if (Objects.equals(this.currentDungeon, "none") || this.getCurrentFloor() == null) return;
        DungeonRoom oldRoom = this.getCurrentRoom();
        DungeonBranch oldBranch = this.getCurrentBranch();
        Vec3i position = getServerPlayer().blockPosition();
        List<DungeonRoom> rooms = this.getCurrentFloor().getChunkMap().getOrDefault(getServerPlayer().chunkPosition(), new ArrayList<>()).stream().map(v -> this.getCurrentFloor().getBranches().get(v.x).getRooms().get(v.y)).toList();
        for (DungeonRoom room : rooms) {
            for (BoundingBox box : room.getBoundingBoxes()) {
                if (box.isInside(position)) {
                    this.setCurrentRoom(room);
                    if (room != oldRoom) {
                        room.onEnter(this);
                        if (oldRoom != null) oldRoom.onExit(this);
                        if (room.getBranch() != oldBranch) {
                            this.setCurrentBranch(room.getBranch());
                            room.getBranch().onEnter(this);
                            if (oldBranch != null) oldBranch.onExit(this);
                        }
                    }
                    return;
                }
            }
        }
        this.setCurrentRoom(null);
        this.setCurrentBranch(null);
    }

    public static void setRespawnPosition(SavedTransform transform, ServerPlayer player) {
        player.setRespawnPosition(transform.getDimension(), transform.getBlockPos(), (float) transform.getYaw(), true, false);
    }

    public static double calcYaw(Player player) {
        return Math.toDegrees(Math.atan2(-player.getLookAngle().x, player.getLookAngle().z));
    }

    public static double calcPitch(Player player) {
        return Math.toDegrees(-Math.asin(player.getLookAngle().y));
    }

    public void travelToFloor(WDPlayer wdPlayer, DungeonFloor oldFloor, DungeonFloor newFloor) {
        WildDungeons.getLogger().info("TRAVELING TO FLOOR {}", newFloor.getTemplate().name());
        ServerPlayer serverPlayer = this.getServerPlayer();

        SavedTransform oldRespawn = SavedTransform.fromRespawn(serverPlayer);
        SavedTransform oldPosition = new SavedTransform(serverPlayer);
        if (oldFloor == null) {
            respawns.put(-1, oldRespawn);
            positions.put(-1, oldPosition);
        } else {
            respawns.put(oldFloor.getIndex(), oldRespawn);
            positions.put(oldFloor.getIndex(), oldPosition);
        }

        SavedTransform newPosition = positions.getOrDefault(newFloor.getIndex(), new SavedTransform(new Vec3(newFloor.getSpawnPoint().getX(), newFloor.getSpawnPoint().getY(), newFloor.getSpawnPoint().getZ()), 0.0, 0.0, newFloor.getLevelKey()));
        WDPlayer.setRespawnPosition(newPosition, serverPlayer);

        if (wdPlayer.getCurrentFloor() != null) wdPlayer.getCurrentFloor().onExit(wdPlayer);
        if (wdPlayer.getCurrentBranch() != null) wdPlayer.getCurrentBranch().onExit(wdPlayer);
        if (wdPlayer.getCurrentRoom() != null) wdPlayer.getCurrentRoom().onExit(wdPlayer);
        wdPlayer.setCurrentFloor(newFloor);
        wdPlayer.setCurrentBranch(null);
        wdPlayer.setCurrentRoom(null);

        CommandUtil.executeTeleportCommand(serverPlayer, newPosition);
        wdPlayer.setRiftCooldown(100);
    }
}
