package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
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
    private String currentDungeon = "none";
    private String currentFloor = "none";
    private String UUID;
    private int riftCooldown = 0;

    public int getEssenceTotal(String key) {return this.essenceTotals.getOrDefault(key, 0);}
    public void setEssenceTotal(String key, int value) {this.essenceTotals.put(key, value);}

    public String getRecentEssence() {return this.recentEssence;}
    public void setRecentEssence(String recentEssence) {this.recentEssence = recentEssence;}

    public String getCurrentDungeon() {return this.currentDungeon;}
    public void setCurrentDungeon(String currentDungeon) {this.currentDungeon = currentDungeon;}
    public String getCurrentFloor() {return this.currentFloor;}
    public void setCurrentFloor(String currentFloor) {this.currentFloor = currentFloor;}
    public void setCurrentFloor(DungeonFloor floor) {this.currentFloor = floor.LEVEL_KEY.toString();}
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

    public WDPlayer(String playerUUID){this.UUID = playerUUID;}

    public void tick() {
        if (riftCooldown > 0) {
            riftCooldown -= 1;
        }
    }

    public void rootRespawn(MinecraftServer server) {
        if (respawns.isEmpty() || positions.isEmpty()) return;
        WDPlayer.SavedTransform newPosition = positions.getFirst();
        WDPlayer.setRespawnPosition(respawns.getFirst(), getServerPlayer(server));
        this.currentDungeon = "none";
        this.currentFloor = "none";
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
        tag.putString("currentFloor", this.currentFloor);
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
        this.currentFloor = tag.getString("currentFloor");
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



    public static class SavedTransform {

        private Vec3 position;
        private double yaw;
        private double pitch;
        private ResourceKey<Level> dimension;

        public SavedTransform(ServerPlayer player) {
            this.position = new Vec3(MathUtil.round(player.getX(), 2), MathUtil.round(player.getY(), 2), MathUtil.round(player.getZ(), 2));
            this.yaw = MathUtil.round(WDPlayer.calcYaw((Player) player), 2);
            this.pitch = MathUtil.round(WDPlayer.calcPitch((Player) player), 2);
            this.dimension = player.level().dimension();
        }

        public SavedTransform(ServerPlayer player, boolean respawn) {
            BlockPos spawnPos = player.getRespawnPosition();
            float spawnAngle = player.getRespawnAngle();
            ResourceKey<Level> spawnDimension = player.getRespawnDimension();

            //Initial login, player might not have respawn data yet
            if (spawnPos == null) {
                spawnPos = player.getServer().overworld().getSharedSpawnPos();
                spawnAngle = 180;
                spawnDimension = player.getServer().overworld().dimension();
            }

            this.position = new Vec3(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            this.yaw = spawnAngle;
            this.pitch = 10;
            this.dimension = spawnDimension;
        }

        public SavedTransform(Vec3 pos, double yaw, double pitch, ResourceKey<Level> dimension) {
            this.position = pos;
            this.yaw = yaw;
            this.pitch = pitch;
            this.dimension = dimension;
        }

        public SavedTransform(CompoundTag tag) {
            this.position = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
            this.yaw = tag.getDouble("yaw");
            this.pitch = tag.getDouble("pitch");
            this.dimension = ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(tag.getString("levelKey").split(":")[1]));
        }

        public static SavedTransform fromRespawn(ServerPlayer player) {
            return new SavedTransform(player.position(), MathUtil.round(WDPlayer.calcYaw(player), 2), MathUtil.round(WDPlayer.calcPitch(player), 2), player.getRespawnDimension());
        }

        public CompoundTag serialize() {
            CompoundTag result = new CompoundTag();

            result.putDouble("x", this.position.x);
            result.putDouble("y", this.position.y);
            result.putDouble("z", this.position.z);
            result.putDouble("yaw", this.yaw);
            result.putDouble("pitch", this.pitch);
            result.putString("levelKey", this.dimension.toString());
            return result;
        }

        public double getX() {return this.position.x;}
        public double getY() {return this.position.y;}
        public double getZ() {return this.position.z;}
        public BlockPos getBlockPos() {return new BlockPos((int) this.position.x, (int) this.position.y, (int) this.position.z);}

        public double getYaw() {return this.yaw;}
        public double getPitch() {return this.pitch;}

        public ResourceKey<Level> getDimension() {return this.dimension;}

    }

    public static double calcYaw(Player player) {
        return Math.toDegrees(Math.atan2(-player.getLookAngle().x, player.getLookAngle().z));
    }

    public static double calcPitch(Player player) {
        return Math.toDegrees(-Math.asin(player.getLookAngle().y));
    }
}
