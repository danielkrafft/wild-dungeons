package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class WDPlayer {

    private HashMap<String, Integer> essenceTotals = new HashMap<>();
    private String recentEssence = "essence:overworld";

    public int getEssenceTotal(String key) {return this.essenceTotals.getOrDefault(key, 0);}
    public void setEssenceTotal(String key, int value) {this.essenceTotals.put(key, value);}

    public String getRecentEssence() {return this.recentEssence;}
    public void setRecentEssence(String recentEssence) {this.recentEssence = recentEssence;}

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        CompoundTag essenceTag = new CompoundTag();
        for (String key : essenceTotals.keySet()) {
            essenceTag.putInt(key, essenceTotals.get(key));
        }

        tag.put("essenceTotals", essenceTag);
        tag.putString("recentEssence", this.recentEssence);
        return tag;
    }

    public WDPlayer(){}
    public WDPlayer(CompoundTag tag) {
        CompoundTag essenceTag = tag.getCompound("essenceTotals");
        HashMap<String, Integer> newEssenceTotals = new HashMap<>();
        for (String key : essenceTag.getAllKeys()) {
            newEssenceTotals.put(key, essenceTag.getInt(key));
        }

        this.essenceTotals = newEssenceTotals;
        this.recentEssence = tag.getString("recentEssence");
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
