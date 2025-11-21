package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class EggSacArrow extends AbstractArrow {

    @Nullable
    private ItemStack firedFromWeapon;

    public EggSacArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        ItemStack pickupItemStack = WDItems.EGG_SAC_ARROWS.toStack();
        pickupItemStack.setCount(1);
        this.setPickupItemStack(pickupItemStack);
        this.firedFromWeapon = null;
    }

    public EggSacArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(WDEntities.EGG_SAC_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
        ItemStack copy = pickupItemStack.copy();
        copy.setCount(1);
        this.setPickupItemStack(copy);
        this.setCustomName((Component)pickupItemStack.get(DataComponents.CUSTOM_NAME));
        Unit unit = (Unit)pickupItemStack.remove(DataComponents.INTANGIBLE_PROJECTILE);
        if (unit != null) {
            this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        this.setPos(x, y, z);
        if (firedFromWeapon != null && level instanceof ServerLevel serverlevel) {
            if (firedFromWeapon.isEmpty()) {
                throw new IllegalArgumentException("Invalid weapon firing an arrow");
            }

            this.firedFromWeapon = firedFromWeapon.copy();
            int i = EnchantmentHelper.getPiercingCount(serverlevel, firedFromWeapon, this.getPickupItem());


            EnchantmentHelper.onProjectileSpawned(serverlevel, firedFromWeapon, this, (p_348347_) -> this.firedFromWeapon = null);
        }
    }

    public EggSacArrow(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(WDEntities.EGG_SAC_ARROW.get(),owner,level, pickupItemStack, firedFromWeapon);

        ItemStack copy = pickupItemStack.copy();
        copy.setCount(1);
        this.setPickupItemStack(copy);
        this.setCustomName((Component)pickupItemStack.get(DataComponents.CUSTOM_NAME));
        Unit unit = (Unit)pickupItemStack.remove(DataComponents.INTANGIBLE_PROJECTILE);
        if (unit != null) {
            this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        if (firedFromWeapon != null && level instanceof ServerLevel serverlevel) {
            if (firedFromWeapon.isEmpty()) {
                throw new IllegalArgumentException("Invalid weapon firing an arrow");
            }

            this.firedFromWeapon = firedFromWeapon.copy();
            int i = EnchantmentHelper.getPiercingCount(serverlevel, firedFromWeapon, this.getPickupItem());


            EnchantmentHelper.onProjectileSpawned(serverlevel, firedFromWeapon, this, (p_348347_) -> this.firedFromWeapon = null);
        }
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
        return WDItems.EGG_SAC_ARROWS.toStack();
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