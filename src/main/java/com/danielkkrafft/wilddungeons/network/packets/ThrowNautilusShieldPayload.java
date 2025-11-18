package com.danielkkrafft.wilddungeons.network.packets;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import static com.danielkkrafft.wilddungeons.entity.attachmenttypes.HomingTargetAttachmentType.UUID_CODEC;

//I CREATED THIS CLASS CAUSE I SAW THE PAYLOAD SYSTEM WAS DIFFERENT AND I WILL RE-IMPLEMENT IT USING THE OTHER SYSTEM AFTERWARD
//TODO: IMPLEMENT Payload in the style of the other packets
public record ThrowNautilusShieldPayload(boolean isLoyal, boolean canTrack, boolean isOffHand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ThrowNautilusShieldPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(WildDungeons.MODID,"throw_nautilus_shield"));

    public static final StreamCodec<ByteBuf, ThrowNautilusShieldPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.BOOL),
            ThrowNautilusShieldPayload::isLoyal,
            ByteBufCodecs.fromCodec(Codec.BOOL),
            ThrowNautilusShieldPayload::canTrack,
            ByteBufCodecs.fromCodec(Codec.BOOL),
            ThrowNautilusShieldPayload::isOffHand,
            ThrowNautilusShieldPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}