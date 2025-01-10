package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SavedTransform {

    private Vec3 position;
    private double yaw;
    private double pitch;
    private ResourceKey<Level> dimension;

    public SavedTransform() {}

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
        WildDungeons.getLogger().info("DIMENSION: {}", tag.getString("levelKey"));
        this.dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.bySeparator(tag.getString("levelKey"), ':'));

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
        result.putString("levelKey", this.dimension.location().toString());
        return result;
    }

    public double getX() {
        return this.position.x;
    }

    public double getY() {
        return this.position.y;
    }

    public double getZ() {
        return this.position.z;
    }

    public BlockPos getBlockPos() {
        return new BlockPos((int) this.position.x, (int) this.position.y, (int) this.position.z);
    }

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

}

