package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WDPlayer {

    private HashMap<String, Integer> essenceTotals = new HashMap<>();
    private List<SavedTransform> respawns = new ArrayList<>();
    private List<SavedTransform> positions = new ArrayList<>();
    private String recentEssence = "essence:overworld";
    private String UUID;
    private int riftCooldown = 0;

    private String currentDungeon = "none";
    private int currentFloor = -1;
    private int currentBranch = -1;
    private int currentRoom = -1;

    public int getEssenceTotal(String key) {return this.essenceTotals.getOrDefault(key, 0);}
    public void setEssenceTotal(String key, int value) {this.essenceTotals.put(key, value);}
    public String getRecentEssence() {return this.recentEssence;}
    public void setRecentEssence(String recentEssence) {this.recentEssence = recentEssence;}


    public int getRiftCooldown() {return this.riftCooldown;}
    public void setRiftCooldown(int cooldown) {this.riftCooldown = cooldown;}
    public List<SavedTransform> getPositions() {return this.positions;}
    public List<SavedTransform> getRespawns() {return this.respawns;}

    public String getUUID() {return this.UUID;}

    public void storeRespawn(SavedTransform transform) {respawns.add(transform);}
    public void storePosition(SavedTransform transform) {positions.add(transform);}

    public SavedTransform removeLastPosition() {return positions.removeLast();}
    public SavedTransform removeLastRespawn() {return respawns.removeLast();}
    public int getDepth() {return this.positions.size();};

    public DungeonSession getCurrentDungeon() {return DungeonSessionManager.getInstance().getDungeonSession(this.currentDungeon);}
    public void setCurrentDungeon(DungeonSession session) {this.currentDungeon = session == null ? "none" : DungeonSessionManager.buildDungeonSessionKey(session.entrance);}
    public DungeonFloor getCurrentFloor() {return this.getCurrentDungeon().floors.get(this.currentFloor);}
    public void setCurrentFloor(DungeonFloor floor) {this.currentFloor = floor == null ? -1 : floor.index;}
    public DungeonBranch getCurrentBranch() {return this.getCurrentFloor().dungeonBranches.get(this.currentBranch);}
    public void setCurrentBranch(DungeonBranch branch) {this.currentBranch = branch == null ? -1 : branch.index;}
    public DungeonRoom getCurrentRoom() {return this.getCurrentBranch().dungeonRooms.get(this.currentRoom);}
    public void setCurrentRoom(DungeonRoom room) {this.currentRoom = room == null ? -1 : room.index;}

    public WDPlayer(String playerUUID){this.UUID = playerUUID;}

    public void tick() {
        if (riftCooldown > 0) {
            riftCooldown -= 1;
        }
    }

    public void rootRespawn(MinecraftServer server) {
        if (respawns.isEmpty() || positions.isEmpty()) return;
        SavedTransform newPosition = positions.getFirst();
        WDPlayer.setRespawnPosition(respawns.getFirst(), getServerPlayer(server));
        this.currentDungeon = null;
        this.currentFloor = -1;
        this.currentBranch = -1;
        this.currentRoom = -1;
        respawns = new ArrayList<>();
        positions = new ArrayList<>();
        CommandUtil.executeTeleportCommand(getServerPlayer(server), newPosition);
    }

    public ServerPlayer getServerPlayer(MinecraftServer server) {
        return server.getPlayerList().getPlayer(java.util.UUID.fromString(UUID));
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        CompoundTag essenceTag = new CompoundTag();
        for (String key : essenceTotals.keySet()) {
            essenceTag.putInt(key, essenceTotals.get(key));
        }

        CompoundTag respawnsTag = new CompoundTag();
        for (int i = 0; i < respawns.size(); i++) {
            respawnsTag.put(""+i, respawns.get(i).serialize());
        }

        CompoundTag positionsTag = new CompoundTag();
        for (int i = 0; i < positions.size(); i++) {
            positionsTag.put(""+i, positions.get(i).serialize());
        }

        tag.put("essenceTotals", essenceTag);
        tag.put("respawns", respawnsTag);
        tag.put("positions", positionsTag);
        tag.putString("recentEssence", this.recentEssence);
        tag.putString("currentDungeon", this.currentDungeon);
        tag.putInt("currentFloor", this.currentFloor);
        tag.putInt("currentBranch", this.currentBranch);
        tag.putInt("currentRoom", this.currentRoom);
        tag.putString("uuid", this.UUID);
        return tag;
    }

    public WDPlayer(CompoundTag tag) {
        CompoundTag essenceTag = tag.getCompound("essenceTotals");
        HashMap<String, Integer> newEssenceTotals = new HashMap<>();
        for (String key : essenceTag.getAllKeys()) {
            newEssenceTotals.put(key, essenceTag.getInt(key));
        }

        CompoundTag respawnsTag = tag.getCompound("respawns");
        List<SavedTransform> newRespawns = new ArrayList<>();
        for (int i = 0; i < respawnsTag.size(); i++) {
            newRespawns.add(new SavedTransform(respawnsTag.getCompound(String.valueOf(i))));
        }

        CompoundTag positionsTag = tag.getCompound("positions");
        List<SavedTransform> newPositions = new ArrayList<>();
        for (int i = 0; i < positionsTag.size(); i++) {
            newPositions.add(new SavedTransform(positionsTag.getCompound(String.valueOf(i))));
        }

        this.essenceTotals = newEssenceTotals;
        this.respawns = newRespawns;
        this.positions = newPositions;
        this.recentEssence = tag.getString("recentEssence");
        this.currentDungeon = tag.getString("currentDungeon");
        this.currentFloor = tag.getInt("currentFloor");
        this.currentBranch = tag.getInt("currentBranch");
        this.currentRoom = tag.getInt("currentRoom");
        this.UUID = tag.getString("uuid");
    }

    public void giveEssencePoints(String key, int points) {
        WildDungeons.getLogger().info("ADDING " + points + " WORTH OF " + key + " EXPERIENCE");
        WildDungeons.getLogger().info("ESSENCE TOTAL IS CURRENTLY " + getEssenceTotal(key));
        WildDungeons.getLogger().info("ESSENCE LEVEL IS CURRENTLY " + getEssenceLevel(key));
        this.setRecentEssence(key);
        this.setEssenceTotal(key, Mth.clamp(this.getEssenceTotal(key) + points, 0, Integer.MAX_VALUE));

        WildDungeons.getLogger().info("ESSENCE TOTAL IS NOW " + getEssenceTotal(key));
        WildDungeons.getLogger().info("ESSENCE LEVEL IS NOW " + getEssenceLevel(key));
    }

    public float getEssenceLevel(String key) {
        int total = this.getEssenceTotal(key); //-5
        float level = 0;
        int need = 0;

        while (total > 0) {
            if (level >= 30) {
                need = 112 + ((int)level - 30) * 9;
            } else {
                need = (int)level >= 15 ? 37 + ((int)level - 15) * 5 : 7 + (int)level * 2;
            }

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

    public static void setRespawnPosition(SavedTransform transform, ServerPlayer player) {
        player.setRespawnPosition(transform.getDimension(), transform.getBlockPos(), (float) transform.getYaw(), true, false);
    }

    public static double calcYaw(Player player) {
        return Math.toDegrees(Math.atan2(-player.getLookAngle().x, player.getLookAngle().z));
    }

    public static double calcPitch(Player player) {
        return Math.toDegrees(-Math.asin(player.getLookAngle().y));
    }
}
