package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDFluids;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import com.danielkkrafft.wilddungeons.registry.WDTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class ToxicSludge extends FlowingFluid {



    @Override
    public FluidType getFluidType() {
        return WDFluids.TOXIC_SLUDGE_TYPE.value();
    }

    @Override
    public Fluid getFlowing() {return WDFluids.FLOWING_TOXIC_SLUDGE.get();}

    @Override
    public Fluid getSource() {return WDFluids.TOXIC_SLUDGE.get();}

    @Override
    public Item getBucket() {return WDItems.TOXIC_SLUDGE_BUCKET.get();}

    @Nullable
    @Override
    public ParticleOptions getDripParticle() {return ParticleTypes.SCULK_SOUL;}

    @Override
    public boolean canConvertToSource(FluidState state, Level level, BlockPos pos) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockentity = blockState.hasBlockEntity() ? levelAccessor.getBlockEntity(blockPos) : null;
        Block.dropResources(blockState, levelAccessor, blockPos, blockentity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return 4;
    }

    @Override
    public BlockState createLegacyBlock(FluidState p_76466_) {
        return WDBlocks.TOXIC_SLUDGE.get().defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(p_76466_)));
    }

    @Override
    public boolean isSame(Fluid p_76456_) {
        return p_76456_ == WDFluids.TOXIC_SLUDGE.get() || p_76456_ == WDFluids.FLOWING_TOXIC_SLUDGE.get();
    }

    @Override
    public int getDropOff(LevelReader p_76469_) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader p_76454_) {
        return 4;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(WDTags.Fluids.TOXIC_SLUDGE);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing extends ToxicSludge
    {
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76476_)
        {
            super.createFluidStateDefinition(p_76476_);
            p_76476_.add(LEVEL);
        }

        @Override
        protected boolean canConvertToSource(Level level) {
            return false;
        }

        public int getAmount(FluidState p_76480_) {
            return p_76480_.getValue(LEVEL);
        }

        public boolean isSource(FluidState p_76478_) {
            return false;
        }
    }

    public static class Source extends ToxicSludge
    {
        @Override
        protected boolean canConvertToSource(Level level) {
            return false;
        }

        public int getAmount(FluidState p_76485_) {
            return 8;
        }

        public boolean isSource(FluidState p_76483_) {
            return true;
        }
    }
}
