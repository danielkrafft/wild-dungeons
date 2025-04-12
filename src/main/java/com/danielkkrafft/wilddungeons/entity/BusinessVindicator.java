package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;

public class BusinessVindicator extends Vindicator {
    public BusinessVindicator(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
    }
}
