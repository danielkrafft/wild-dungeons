package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundOpenConnectionBlockUIPacket(CompoundTag data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundOpenConnectionBlockUIPacket> TYPE = new CustomPacketPayload.Type<>(WildDungeons.rl("clientbound_open_connection_block_ui"));

    public static final StreamCodec<ByteBuf, ClientboundOpenConnectionBlockUIPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundOpenConnectionBlockUIPacket::data,
            ClientboundOpenConnectionBlockUIPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPacketHandler.handleOpenConnectionBlockUI(data);
        });
    }
}
