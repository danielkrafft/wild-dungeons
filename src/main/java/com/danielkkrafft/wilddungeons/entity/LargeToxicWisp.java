package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class LargeToxicWisp extends ToxicWisp{
    public LargeToxicWisp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }
}
