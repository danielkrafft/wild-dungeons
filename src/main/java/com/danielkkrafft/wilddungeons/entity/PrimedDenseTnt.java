package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.block.state.BlockState;

public class PrimedDenseTnt extends PrimedTnt {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.BLOCK_STATE);

    public PrimedDenseTnt(EntityType<? extends PrimedDenseTnt> entityType, Level level) {
        super(entityType, level);
    }

    public PrimedDenseTnt(Level level, double x, double y, double z, @Nullable LivingEntity owner) {
        super(WDEntities.PRIMED_DENSE_TNT.get(), level);
        this.setPos(x, y, z);
        double d0 = level.random.nextDouble() * (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(d0) * 0.02, 0.2, -Math.cos(d0) * 0.02);
        this.setFuse(140);
        this.setBlockState(WDBlocks.DENSE_TNT.get().defaultBlockState());
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FUSE_ID, 140);
        builder.define(DATA_BLOCK_STATE_ID, WDBlocks.DENSE_TNT.get().defaultBlockState());
    }
    @Override
    protected void explode() {
        this.level().explode(
                this,
                Explosion.getDefaultDamageSource(this.level(), this),
                null,
                this.getX(),
                this.getY(0.0625),
                this.getZ(),
                8.0F,
                false,
                Level.ExplosionInteraction.TNT
        );
    }
}