package com.danielkkrafft.wilddungeons.entity.BaseClasses;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class SelfGovernedEntity extends Entity {

    protected Vec3 firedDirection = Vec3.ZERO;
    protected float initialSpeed = 0.0f;
    protected boolean wasFired;
    private boolean directionWarningLogged = false;

    protected SelfGovernedEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    public void setFiredDirectionAndSpeed(Vec3 newDirection, float newSpeed) {
        wasFired = true;
        firedDirection = newDirection;
        initialSpeed = newSpeed;
    }

    public Vec3 getFiredDirection() {
        return firedDirection;
    }

    public float getInitialSpeed() {
        return initialSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        if (!wasFired && !directionWarningLogged && !level().isClientSide) {
            WildDungeons.getLogger().warn("{} was spawned without direction!", this.getClass().getSimpleName());
            directionWarningLogged = true;
            discard();
        }
    }
}