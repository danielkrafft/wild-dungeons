package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundAddDecalPacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<ClientboundAddDecalPacket> TYPE = new Type<>(WildDungeons.rl("clientbound_add_decal"));

    public static final StreamCodec<ByteBuf, ClientboundAddDecalPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundAddDecalPacket::data,
            ClientboundAddDecalPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleAddDecal(data));
    }
}
