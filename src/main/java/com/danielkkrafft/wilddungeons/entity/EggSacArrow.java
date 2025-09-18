package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class EggSacArrow extends Arrow {
    public EggSacArrow(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
        ItemStack pickupItemStack = WDItems.EGG_SAC_ARROWS.toStack();
        pickupItemStack.setCount(1);
        this.setPickupItemStack(pickupItemStack);
    }

    public EggSacArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(level, x, y, z, pickupItemStack, firedFromWeapon);
    }

    public EggSacArrow(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(level, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        //summon 1-3 spiderlings that wander around
        int count = 1 + this.random.nextInt(3);
        for (int i = 0; i < count; i++) {
            FriendlySpiderling spiderling = WDEntities.FRIENDLY_SPIDERLING.get().create(this.level());
            if (spiderling != null) {
                spiderling.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0);
                spiderling.setOwner(this.getOwner());
            }
            this.level().addFreshEntity(spiderling);
        }
        super.onHitBlock(result);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        //summon 1-3 spiderlings that attack the hit entity
        int count = 1 + this.random.nextInt(3);
        for (int i = 0; i < count; i++) {
            FriendlySpiderling spiderling = WDEntities.FRIENDLY_SPIDERLING.get().create(this.level());
            if (spiderling != null) {
                spiderling.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0);
                spiderling.setTarget((LivingEntity) result.getEntity());
                spiderling.setOwner(this.getOwner());
            }
            this.level().addFreshEntity(spiderling);
        }
        super.onHitEntity(result);
    }


}