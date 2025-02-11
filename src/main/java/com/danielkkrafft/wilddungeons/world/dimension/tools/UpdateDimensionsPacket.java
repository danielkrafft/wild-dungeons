package com.danielkkrafft.wilddungeons.world.dimension.tools;

import com.danielkkrafft.wilddungeons.WildDungeons;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @param keys Keys to add or remove in the client's dimension list
 * @param add If true, keys are to be added; if false, keys are to be removed
 *
 * Credit:Commoble
 */
public record UpdateDimensionsPacket(Set<ResourceKey<Level>> keys, boolean add) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<UpdateDimensionsPacket> TYPE = new CustomPacketPayload.Type<>(WildDungeons.rl("update_dimensions"));

    public static final StreamCodec<ByteBuf, UpdateDimensionsPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()).map(Set::copyOf, List::copyOf), UpdateDimensionsPacket::keys,
            ByteBufCodecs.BOOL, UpdateDimensionsPacket::add,
            UpdateDimensionsPacket::new);

    public void handle(IPayloadContext context)
    {
        context.enqueueWork(() -> ClientHandler.handle(this));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private static class ClientHandler // making client calls in the static class prevents classloading errors
    {
        private static void handle(UpdateDimensionsPacket packet)
        {
            @SuppressWarnings("resource")
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player == null)
                return;

            final Set<ResourceKey<Level>> dimensionList = player.connection.levels();
            if (dimensionList == null)
                return;

            Consumer<ResourceKey<Level>> keyConsumer = packet.add()
                    ? dimensionList::add
                    : dimensionList::remove;

            packet.keys().forEach(keyConsumer);
        }
    }
}