package com.danielkkrafft.wilddungeons.network.serverbound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundUpdateConnectionBlockPacket(CompoundTag data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundUpdateConnectionBlockPacket> TYPE = new CustomPacketPayload.Type<>(WildDungeons.rl("serverbound_update_connection_block"));

    public static final StreamCodec<ByteBuf, ServerboundUpdateConnectionBlockPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, ServerboundUpdateConnectionBlockPacket::data,
            ServerboundUpdateConnectionBlockPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerLevel level = (ServerLevel) context.player().level();
            BlockEntity blockEntity = level.getBlockEntity(new BlockPos(data.getInt("x"), data.getInt("y"), data.getInt("z")));
            if (blockEntity instanceof ConnectionBlockEntity connectionBlockEntity) {
                connectionBlockEntity.unblockedBlockstate = data.getString("unblockedBlockstate");
                connectionBlockEntity.pool = data.getString("pool");
            }
        });
    }
}
