package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPacketHandler {
    public enum Packets {
        RESTORE_PLAYER_GAMEMODE, UPDATE_CONNECTION_BLOCK, ROOM_EXPORT_WAND_SAVE
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
            case ROOM_EXPORT_WAND_SAVE -> {
                ItemStack itemStack = context.player().getItemInHand(context.player().getUsedItemHand());
                if (itemStack.is(WDItems.ROOM_EXPORT_WAND)){
                    RoomExportWand wand = (RoomExportWand) itemStack.getItem();
                    wand.setName(data.getString("roomName"));
                    boolean success = wand.saveStructure((ServerLevel) context.player().level());
                    if (success) {
                        context.player().sendSystemMessage(Component.translatable("message.room_export_wand.save.success",wand.getRoomName()));
                    } else {
                        context.player().sendSystemMessage(Component.translatable("message.room_export_wand.save.failure"));
                    }
                }
            }
        }
    }
}
