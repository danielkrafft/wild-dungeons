package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmeraldProjectileEntity extends Fireball  {

    public EmeraldProjectileEntity(Level level, double x, double y, double z, Vec3 movement) {
        super(WDEntities.EMERALD_PROJECTILE.get(), x, y, z, movement, level);
    }

    public EmeraldProjectileEntity(Level level, LivingEntity owner, Vec3 movement) {
        super(WDEntities.EMERALD_PROJECTILE.get(), owner, movement, level);
    }

    public EmeraldProjectileEntity(EntityType<? extends EmeraldProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.EMERALD);
    }
    private ItemStack getDefaultItem() {
        return new ItemStack(Items.EMERALD);
    }

    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Level level = this.level();
        if (level instanceof ServerLevel serverlevel) {
            result.getEntity().hurt(this.damageSources().magic(), 5.0F);
            ItemStack itemstack = this.getDefaultItem();
            BlockPos blockpos = result.getEntity().getOnPos();
            ItemEntity itemEntity = new ItemEntity(this.level(), blockpos.getX() + 0.5,
                    blockpos.getY() + 0.5, blockpos.getZ() + 0.5, itemstack);
            this.level().addFreshEntity(itemEntity);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            ItemStack itemstack = this.getDefaultItem();
            BlockPos blockpos = result.getBlockPos();
            ItemEntity itemEntity = new ItemEntity(this.level(), blockpos.getX() + 0.5,
                    blockpos.getY() + 0.5, blockpos.getZ() + 0.5, itemstack);
            this.level().addFreshEntity(itemEntity);
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected @Nullable ParticleOptions getTrailParticle() {
        return ParticleTypes.COMPOSTER;
    }
}