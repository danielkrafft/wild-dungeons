package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SavedTransform {

    private Vec3 position;
    private double yaw;
    private double pitch;
    private ResourceKey<Level> dimension;

    public SavedTransform(ServerPlayer player) {
        this.position = new Vec3(MathUtil.round(player.getX(), 2), MathUtil.round(player.getY(), 2), MathUtil.round(player.getZ(), 2));
        this.yaw = MathUtil.round(WDPlayer.calcYaw(player), 2);
        this.pitch = MathUtil.round(WDPlayer.calcPitch(player), 2);
        this.dimension = player.level().dimension();
    }

    public SavedTransform(Vec3 pos, double yaw, double pitch, ResourceKey<Level> dimension) {
        this.position = pos;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }

    public static SavedTransform fromRespawn(ServerPlayer player) {
        return new SavedTransform(player.position(), MathUtil.round(WDPlayer.calcYaw(player), 2), MathUtil.round(WDPlayer.calcPitch(player), 2), player.getRespawnDimension());
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
    public BlockPos getBlockPos() {return new BlockPos((int) this.position.x, (int) this.position.y, (int) this.position.z);}
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

