package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.EggSacArrow;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EggSacArrowItem extends ArrowItem {
    public EggSacArrowItem(Properties properties) {
        super(properties);
    }

    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        EggSacArrow arrow = new EggSacArrow(level,shooter, ammo, weapon);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ());
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        return arrow;
    }

    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        EggSacArrow arrow = new EggSacArrow(WDEntities.EGG_SAC_ARROW.get(), level);
        arrow.setPos(pos.x(), pos.y(), pos.z());
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        return arrow;
    }

    public boolean isInfinite(ItemStack ammo, ItemStack bow, LivingEntity livingEntity) {
        return false;
    }

}
