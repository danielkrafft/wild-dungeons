package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.entity.blockentity.GasBlockEntity;
import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ToxicGasBlock extends BaseEntityBlock {
    public ToxicGasBlock(Properties properties) {
        super(properties);
    }
    public static final MapCodec<ToxicGasBlock> CODEC = simpleCodec(ToxicGasBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    private static final int TICKS_PER_SPREAD = 20; // Spread every 20 ticks (1 second)

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        Explode(level, pos);
    }

    private void Explode(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
        level.explode(null,pos.getX(), pos.getY(), pos.getZ(), 1.1f, Level.ExplosionInteraction.MOB);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        //igniting with flint and steel or fire charge should cause an explosion
        if (stack.is(Items.FLINT_AND_STEEL)){
            Explode(level, pos);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        Explode(level, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        //schedule the first tick
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, TICKS_PER_SPREAD);
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        //spawn green particles
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5);
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5);
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5);
            level.addParticle(ParticleTypes.DUST_PLUME, x, y, z, 0.0, 0.1, 0.0);
        super.animateTick(state, level, pos, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        HandleSpread(level, pos, random);
        CheckForChangeOverTimeBlocks(level, pos, random);
        //schedule the next tick
        level.scheduleTick(pos, this, TICKS_PER_SPREAD + random.nextInt(TICKS_PER_SPREAD/2)-TICKS_PER_SPREAD );
    }

    private void CheckForChangeOverTimeBlocks(ServerLevel level, BlockPos pos, RandomSource random) {
        //check the surrounding blocks for ChangeOverTimeBlocks, if any are found, 10% chance to advance that blocks age
        ArrayList<BlockPos> surrounding = new ArrayList<>();
        surrounding.add(pos.north());
        surrounding.add(pos.south());
        surrounding.add(pos.east());
        surrounding.add(pos.west());
        surrounding.add(pos.above());
        surrounding.add(pos.below());
        for (BlockPos checkPos : surrounding) {
            BlockState checkState = level.getBlockState(checkPos);
            if (checkState.getBlock() instanceof ChangeOverTimeBlock changeBlock) {
//                WildDungeons.getLogger().debug("Toxic Gas at "+pos+" is affecting ChangeOverTimeBlock at "+checkPos);
                changeBlock.changeOverTime(checkState, level, checkPos, random);
            }
        }
    }

    public void HandleSpread(Level level, BlockPos pos, RandomSource random) {
        //check the block above to see if its air, if it is, spread upwards
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.isAir()) {
            level.removeBlock(pos, false);
            level.setBlockAndUpdate(above, this.defaultBlockState());
        } else {
            if (pos.getY()>= level.getMaxBuildHeight()-1){
                //if we are at the top of the world, just remove the block
                level.removeBlock(pos, false);
                return;
            }
            //check the blocks to the sides, if any are air, spread to them
            ArrayList<BlockPos> sides = new ArrayList<>();
            sides.add(pos.north());
            sides.add(pos.south());
            sides.add(pos.east());
            sides.add(pos.west());
            //shuffle the list
            java.util.Collections.shuffle(sides);

            for (BlockPos side : sides) {
                BlockState sideState = level.getBlockState(side);
                if (sideState.isAir()) {
                    level.setBlockAndUpdate(side, this.defaultBlockState());
                    //remove the current block
                    level.removeBlock(pos, false);
                    break;
                }
            }
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.hasEffect(MobEffects.POISON)) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 4));
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context.isHoldingItem(Items.FLINT_AND_STEEL)) return Shapes.block();
        return Shapes.empty();
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GasBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, WDBlockEntities.TOXIC_GAS_ENTITY.get(), GasBlockEntity::tick);
    }
}
