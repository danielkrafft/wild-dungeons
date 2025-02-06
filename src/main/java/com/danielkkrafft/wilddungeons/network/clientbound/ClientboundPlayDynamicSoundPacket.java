package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundPlayDynamicSoundPacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<ClientboundPlayDynamicSoundPacket> TYPE = new Type<>(WildDungeons.rl("clientbound_play_dynamic_sound"));

    public static final StreamCodec<ByteBuf, ClientboundPlayDynamicSoundPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundPlayDynamicSoundPacket::data,
            ClientboundPlayDynamicSoundPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.playDynamicSound(data));
    }
}
