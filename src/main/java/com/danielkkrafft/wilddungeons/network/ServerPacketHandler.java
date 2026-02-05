package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPacketHandler {
    public enum Packets {
        RESTORE_PLAYER_GAMEMODE, UPDATE_CONNECTION_BLOCK, ROOM_EXPORT_WAND_CLOSE
    }

    public static void handleInbound(IPayloadContext context, CompoundTag data) { //TODO - Fix send messages and set defaults to correct values
        switch (Packets.valueOf(data.getStringOr("packet", ""))) {
            case RESTORE_PLAYER_GAMEMODE -> {
                WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer((ServerPlayer) context.player());
                wdPlayer.getServerPlayer().setGameMode(wdPlayer.getLastGameMode());
            }
            case UPDATE_CONNECTION_BLOCK -> {
                ServerLevel level = (ServerLevel) context.player().level();
                BlockEntity blockEntity = level.getBlockEntity(new BlockPos(data.getIntOr("x", 0), data.getIntOr("y", 0), data.getIntOr("z", 0)));
                if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                    connectionBlockEntity.unblockedBlockstate = data.getStringOr("unblockedBlockstate", "");
                    connectionBlockEntity.pool = data.getStringOr("pool", "");
                    connectionBlockEntity.type = data.getStringOr("type", "");
                    level.getServer().saveEverything(true, false, true);
                }
            }
            case ROOM_EXPORT_WAND_CLOSE -> {
                ItemStack itemStack = context.player().getItemInHand(context.player().getUsedItemHand());
                if (itemStack.is(WDItems.ROOM_EXPORT_WAND)){
                    StructureMode structureMode = StructureMode.values()[data.getIntOr("mode", 0)];
                    RoomExportWand.setMode(itemStack, structureMode);
                    
                    boolean confirmAction = data.getBooleanOr("confirmAction", false);

                    switch (StructureBlockEntity.UpdateType.valueOf(data.getStringOr("updateType", ""))) {
                        case UPDATE_DATA -> {

                        }
                        case SAVE_AREA -> {
                            RoomExportWand.setName(itemStack, data.getStringOr("roomName", ""));
                            boolean success = RoomExportWand.saveStructure(itemStack, (ServerLevel) context.player().level(), data.getListOrEmpty("dungeonMaterials"), confirmAction);
                            if (success && confirmAction) {
                                //context.player().sendSystemMessage(Component.translatable("message.room_export_wand.save.success",RoomExportWand.getRoomName(itemStack)));
                            } else if (confirmAction) {
                                //context.player().sendSystemMessage(Component.translatable("message.room_export_wand.save.failure"));
                            }
                        }
                        case LOAD_AREA -> {
                            if (confirmAction){
                                RoomExportWand.setName(itemStack, data.getStringOr("roomName", ""));
                                if (data.getBooleanOr("loadWithMaterials", false)) {
                                    RoomExportWand.placeWithMaterials(itemStack, data.getIntOr("materialIndex", 0));
                                } else {
                                    RoomExportWand.placeWithMaterials(itemStack, -1);
                                }
                                RoomExportWand.setAdditiveRoomLoading(itemStack, data.getBooleanOr("additiveRoomLoading", false));
                                //context.player().sendSystemMessage(Component.translatable("message.room_export_wand.load.success", RoomExportWand.getRoomName(itemStack)));
                            }
                        }
                    }
                }
            }
        }
    }
}
