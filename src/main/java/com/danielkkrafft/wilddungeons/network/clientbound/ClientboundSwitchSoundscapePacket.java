package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSwitchSoundscapePacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<ClientboundSwitchSoundscapePacket> TYPE = new Type<>(WildDungeons.rl("clientbound_switch_soundscape"));

    public static final StreamCodec<ByteBuf, ClientboundSwitchSoundscapePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundSwitchSoundscapePacket::data,
            ClientboundSwitchSoundscapePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSwitchSoundscape(data));
    }
}
