package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;

public class BusinessGolem extends IronGolem {

    public BusinessGolem(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
    }
}
