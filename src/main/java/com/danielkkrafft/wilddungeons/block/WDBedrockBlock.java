package com.danielkkrafft.wilddungeons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class WDBedrockBlock extends Block {
    public static final IntegerProperty MIMIC = IntegerProperty.create("mimic", 0, 2000);//currently there are 1020 blocks in the game

    public WDBedrockBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MIMIC, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MIMIC);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem blockItem) || !(player instanceof ServerPlayer serverPlayer) || !serverPlayer.gameMode.getGameModeForPlayer().equals(GameType.CREATIVE)) {
            return ItemInteractionResult.FAIL;
        }
        Block blockToMimic = blockItem.getBlock();
        BlockState neighborBlockstate = blockToMimic.defaultBlockState();
        setBlockToMimic(state, level, pos, neighborBlockstate);
        return ItemInteractionResult.SUCCESS;
    }

    public static void setBlockToMimic(BlockState state, Level level, BlockPos pos, BlockState newBlockstate) {
        int index = BuiltInRegistries.BLOCK.getId(newBlockstate.getBlock());
        level.setBlock(pos, state.trySetValue(MIMIC, index), 2);
    }

    public static BlockState of(Block mimicBlock) {
        return WDBlocks.WD_BEDROCK.get().getStateDefinition().any().trySetValue(MIMIC, BuiltInRegistries.BLOCK.getId(mimicBlock));
    }
}
