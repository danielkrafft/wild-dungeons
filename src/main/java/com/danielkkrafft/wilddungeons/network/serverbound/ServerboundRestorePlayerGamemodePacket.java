package com.danielkkrafft.wilddungeons.network.serverbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundRestorePlayerGamemodePacket(CompoundTag data) implements CustomPacketPayload {
    public static final Type<ServerboundRestorePlayerGamemodePacket> TYPE = new Type<>(WildDungeons.rl("serverbound_restore_player_gamemode"));

    public static final StreamCodec<ByteBuf, ServerboundRestorePlayerGamemodePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ServerboundRestorePlayerGamemodePacket::data,
            ServerboundRestorePlayerGamemodePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer((ServerPlayer) context.player());
            wdPlayer.getServerPlayer().setGameMode(wdPlayer.getLastGameMode());
        });
    }
}
