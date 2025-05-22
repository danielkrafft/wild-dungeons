package com.danielkkrafft.wilddungeons.entity.BaseClasses;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;

public interface ArrowFactory {
    AbstractArrow create(Level level, LivingEntity shooter);
}
