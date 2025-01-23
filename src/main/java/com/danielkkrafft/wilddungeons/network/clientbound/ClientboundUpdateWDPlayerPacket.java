package com.danielkkrafft.wilddungeons.network.clientbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.Serializer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdateWDPlayerPacket(CompoundTag data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundUpdateWDPlayerPacket> TYPE = new CustomPacketPayload.Type<>(WildDungeons.rl("clientbound_update_wdplayer"));

    public static final StreamCodec<ByteBuf, ClientboundUpdateWDPlayerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundUpdateWDPlayerPacket::data,
            ClientboundUpdateWDPlayerPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            WDPlayerManager.getInstance().replaceWDPlayer(Minecraft.getInstance().player.getStringUUID(), Serializer.fromCompoundTag(data));
        });
    }
}
