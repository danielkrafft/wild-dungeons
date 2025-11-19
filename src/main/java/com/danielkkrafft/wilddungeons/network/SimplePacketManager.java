package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.packets.ThrowNautilusShieldHandler;
import com.danielkkrafft.wilddungeons.network.packets.ThrowNautilusShieldPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SimplePacketManager {

    public static void setup(PayloadRegistrar reg) {
        reg.playToClient(ClientboundTagPacket.TYPE, ClientboundTagPacket.STREAM_CODEC, ClientboundTagPacket::handle);
        reg.playToServer(ServerboundTagPacket.TYPE, ServerboundTagPacket.STREAM_CODEC, ServerboundTagPacket::handle);
        reg.playToServer(ThrowNautilusShieldPayload.TYPE, ThrowNautilusShieldPayload.STREAM_CODEC, ThrowNautilusShieldHandler.Client::handleDataOnNetwork);

    }

    public record ClientboundTagPacket(CompoundTag data) implements CustomPacketPayload {
        public static final Type<ClientboundTagPacket> TYPE = new Type<>(WildDungeons.rl("clientbound_tag"));

        public static final StreamCodec<ByteBuf, ClientboundTagPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundTagPacket::data,
                ClientboundTagPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public void handle(IPayloadContext context) {
            context.enqueueWork(() -> ClientPacketHandler.handleInbound(data));
        }
    }

    public record ServerboundTagPacket(CompoundTag data) implements CustomPacketPayload {
        public static final Type<ServerboundTagPacket> TYPE = new Type<>(WildDungeons.rl("serverbound_tag"));

        public static final StreamCodec<ByteBuf, ServerboundTagPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.TRUSTED_COMPOUND_TAG, ServerboundTagPacket::data,
                ServerboundTagPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {return TYPE;}

        public void handle(IPayloadContext context) {
            context.enqueueWork(() -> ServerPacketHandler.handleInbound(context, data));
        }
    }

}
