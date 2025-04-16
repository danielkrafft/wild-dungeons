package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class FriendlyLargeEmeraldWisp extends LargeEmeraldWisp{
    public FriendlyLargeEmeraldWisp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerSpecificGoals() {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 16f, 0.6, 1.0F));
        this.goalSelector.addGoal(2, new SummonMoreGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true, (target) -> target != this.getOwner()));
    }

    @Override
    public EntityType<?> getSummonType() {
        return WDEntities.FRIENDLY_EMERALD_WISP.get();
    }
}
