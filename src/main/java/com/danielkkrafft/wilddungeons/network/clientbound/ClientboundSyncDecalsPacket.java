package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSyncDecalsPacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<ClientboundSyncDecalsPacket> TYPE = new Type<>(WildDungeons.rl("clientbound_sync_decals"));

    public static final StreamCodec<ByteBuf, ClientboundSyncDecalsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundSyncDecalsPacket::data,
            ClientboundSyncDecalsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSyncDecals(data));
    }
}
