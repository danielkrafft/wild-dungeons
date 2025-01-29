package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundLoadingScreenPacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<ClientboundLoadingScreenPacket> TYPE = new Type<>(WildDungeons.rl("clientbound_loading_screen"));

    public static final StreamCodec<ByteBuf, ClientboundLoadingScreenPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundLoadingScreenPacket::data,
            ClientboundLoadingScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(ClientPacketHandler::handleLoadingScreen);
    }
}
