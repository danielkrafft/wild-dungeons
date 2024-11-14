package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.registry.WDDimensions;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import com.danielkkrafft.wilddungeons.world.dimension.tools.InfiniverseAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Dungeon {
    public String name;
    public String openBehavior;
    public DungeonFloor floor;

    public Dungeon(String name, String openBehavior, DungeonFloor floor) {
        this.name = name;
        this.openBehavior = openBehavior;
        this.floor = floor;
    }

    public void startDungeonDimension(MinecraftServer server) {
        final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(this.name));
        ServerLevel newLevel = InfiniverseAPI.get().getOrCreateLevel(server, LEVEL_KEY, () -> WDDimensions.createLevel(server));

        BlockPos placementPos = new BlockPos(0,-10,0);
        this.floor.placeInWorld(newLevel, placementPos);
    }

    public void enterDungeon(ServerPlayer player) {
        final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(this.name));

        //Set new transform and respawn
        WDPlayer.SavedTransform newRespawn = new WDPlayer.SavedTransform(new Vec3(0.0,-30.0,0.0), 0.0,0.0, LEVEL_KEY);
        player.setRespawnPosition(newRespawn.getDimension(), newRespawn.getBlockPos(), (float) newRespawn.getYaw(), true, false);
        CommandUtil.executeTeleportCommand(player, newRespawn);

    }

}
