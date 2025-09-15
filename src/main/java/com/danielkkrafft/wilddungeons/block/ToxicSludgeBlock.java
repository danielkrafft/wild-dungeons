package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.entity.ToxicWisp;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.AABB;

public class ToxicSludgeBlock extends LiquidBlock {
    public ToxicSludgeBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    private static final int TICKS_BETWEEN_WISP_SPAWNS = 400;
    private static final int TICKS_RANDOM_OFFSET = 200;
    private static final float SPAWN_CHANCE = 0.05f;

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this,  level.random.nextInt(TICKS_BETWEEN_WISP_SPAWNS) + level.random.nextInt(TICKS_RANDOM_OFFSET));
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
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        spawnWispIfPlayerNearby(level, pos);
        level.scheduleTick(pos, this, TICKS_BETWEEN_WISP_SPAWNS + level.random.nextInt(TICKS_RANDOM_OFFSET));

    }

    private void spawnWispIfPlayerNearby(ServerLevel level, BlockPos pos) {
//        WildDungeons.getLogger().debug("Toxic Sludge at " + pos + " is attempting to spawn a wisp");
        BlockPos above = pos.above();
        boolean playerNearby = !level.getEntitiesOfClass(Player.class, AABB.ofSize(pos.getCenter(), 20, 20, 20)).isEmpty();
        if (playerNearby) {
            if (level.random.nextFloat() > SPAWN_CHANCE) {
                return; // did not pass the random chance check
            }
            //spawn a wisp
            ToxicWisp wisp = WDEntities.SMALL_TOXIC_WISP.get().create(level);
            wisp.setPos(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5);
            level.addFreshEntity(wisp);
        }
    }
}
