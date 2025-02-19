package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPacketHandler {
    public enum Packets {
        RESTORE_PLAYER_GAMEMODE, UPDATE_CONNECTION_BLOCK
    }

    public static void handleInbound(IPayloadContext context, CompoundTag data) {
        switch (Packets.valueOf(data.getString("packet"))) {
            case RESTORE_PLAYER_GAMEMODE -> {
                WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer((ServerPlayer) context.player());
                wdPlayer.getServerPlayer().setGameMode(wdPlayer.getLastGameMode());
            }
            case UPDATE_CONNECTION_BLOCK -> {
                ServerLevel level = (ServerLevel) context.player().level();
                BlockEntity blockEntity = level.getBlockEntity(new BlockPos(data.getInt("x"), data.getInt("y"), data.getInt("z")));
                if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                    connectionBlockEntity.unblockedBlockstate = data.getString("unblockedBlockstate");
                    connectionBlockEntity.pool = data.getString("pool");
                    connectionBlockEntity.type = data.getString("type");
                    level.getServer().saveEverything(true, false, true);
                }
            }
        }
    }
}
