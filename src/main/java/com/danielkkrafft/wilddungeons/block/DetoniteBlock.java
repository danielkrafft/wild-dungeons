package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import java.util.Random;

public class DetoniteBlock extends Block {
    public static final BooleanProperty COOLDOWN = BooleanProperty.create("cooldown");

    public DetoniteBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(COOLDOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COOLDOWN);
    }


    @Override
    public float getExplosionResistance() {
        return 3_600_000.0F;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) return;
        if (state.getValue(COOLDOWN)) return;
        if (entity instanceof Player player && player.isCrouching()) return;
        triggerExplosion(level, pos, state, null);
    }


    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {

        if (player.isCreative()) return super.playerWillDestroy(level, pos, state, player);


            if (!level.isClientSide) {
                boolean silkTouch = EnchantmentHelper.getItemEnchantmentLevel(
                        WildDungeons.getEnchantment(Enchantments.SILK_TOUCH), player.getMainHandItem()) > 0;

                if (!silkTouch) {
                    level.explode(
                            null,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            3.0F,
                            Level.ExplosionInteraction.MOB
                    );
                }
            }

        return super.playerWillDestroy(level, pos, state, player);
    }


    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(COOLDOWN)) {
            level.setBlock(pos, state.setValue(COOLDOWN, false), Block.UPDATE_ALL);
        }
    }


    private void triggerExplosion(Level level, BlockPos pos, BlockState state, @Nullable Entity source) {

        level.explode(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                3.0F,
                Level.ExplosionInteraction.MOB
        );

        level.setBlock(pos, state.setValue(COOLDOWN, true), Block.UPDATE_ALL);

        level.scheduleTick(pos, this, 10);
    }
}
