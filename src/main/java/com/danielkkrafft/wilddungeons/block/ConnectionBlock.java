package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConnectionBlock extends Block implements EntityBlock {
    public ConnectionBlock(Properties properties) {super(properties);}
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ConnectionBlockEntity(pos, state);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @javax.annotation.Nullable
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
}
