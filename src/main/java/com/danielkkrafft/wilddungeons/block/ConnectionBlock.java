package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ConnectionBlock extends Block implements EntityBlock {
    public ConnectionBlock(Properties properties) {super(properties);}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConnectionBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {return InteractionResult.PASS;}

        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ConnectionBlockEntity connectionBlockEntity && player.canUseGameMasterBlocks()) {
            CompoundTag data = new CompoundTag();
            data.putString("packet", ClientPacketHandler.Packets.OPEN_CONNECTION_BLOCK_UI.toString());
            data.putString("unblockedBlockstate", connectionBlockEntity.unblockedBlockstate);
            data.putString("pool", connectionBlockEntity.pool);
            data.putString("type", connectionBlockEntity.type);
            data.putInt("x", connectionBlockEntity.getBlockPos().getX());
            data.putInt("y", connectionBlockEntity.getBlockPos().getY());
            data.putInt("z", connectionBlockEntity.getBlockPos().getZ());

            PacketDistributor.sendToPlayer((ServerPlayer) player, new SimplePacketManager.ClientboundTagPacket(data));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }
}
