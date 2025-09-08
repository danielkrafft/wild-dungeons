package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Spiderling extends Spider implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String spiderling_controller = "spiderling_controller";
    private final AnimationController<Spiderling> mainController = new AnimationController<>(this, spiderling_controller, 5, spiderlingPredicate());

    private AnimationController.AnimationStateHandler<Spiderling> spiderlingPredicate() {
        return (state) -> PlayState.STOP;//todo animation logic
    }

    public Spiderling(EntityType<? extends Spiderling> entityType, Level level) {
        super(entityType, level);
    }
    public static AttributeSupplier.Builder createSpiderling() {
        return Spider.createAttributes().add(Attributes.MAX_HEALTH, (double)8.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
