package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class CondemnedGuardian extends Monster {
    public CondemnedGuardian(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }


    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 50)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.ATTACK_KNOCKBACK, 2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2);
    }

}
