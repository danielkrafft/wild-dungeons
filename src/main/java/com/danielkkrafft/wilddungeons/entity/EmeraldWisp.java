package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmeraldWisp extends PathfinderMob implements TraceableEntity {
    @javax.annotation.Nullable
    Entity owner;

    boolean isLarge;

    public EmeraldWisp(EntityType<? extends PathfinderMob> entityType, Level level, boolean isLarge) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.isLarge = isLarge;
    }

    @Override
    public @Nullable Entity getOwner() {
        return owner;
    }

    public void setOwner(@Nullable Entity owner) {
        this.owner = owner;
    }

    public static AttributeSupplier.Builder createAttributes(boolean isLarge) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0F)
                .add(Attributes.FLYING_SPEED, 0.1F)
                .add(Attributes.MOVEMENT_SPEED, 0.1F)
                .add(Attributes.ATTACK_DAMAGE, 2.0F)
                .add(Attributes.FOLLOW_RANGE, 48.0F);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.5F));
            } else {
                this.moveRelative(this.getSpeed(), travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }


    public boolean isLarge() {
        return isLarge;
    }
}