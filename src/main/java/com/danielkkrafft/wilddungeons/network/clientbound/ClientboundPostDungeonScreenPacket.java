package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public record ClientboundPostDungeonScreenPacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<ClientboundPostDungeonScreenPacket> TYPE = new Type<>(WildDungeons.rl("clientbound_post_dungeon_screen"));

    public static final StreamCodec<ByteBuf, ClientboundPostDungeonScreenPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundPostDungeonScreenPacket::data,
            ClientboundPostDungeonScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handlePostDungeonScreen(data));
    }
}
