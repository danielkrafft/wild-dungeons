package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CopperSentinel extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String COPPER_SENTINEL_CONTROLLER = "copper_sentinel_controller";
    private final AnimationController<CopperSentinel> mainController = new AnimationController<>(this, COPPER_SENTINEL_CONTROLLER, 5, animationPredicate());

    public CopperSentinel(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    private AnimationController.AnimationStateHandler<CopperSentinel> animationPredicate() {
        return (state) -> PlayState.STOP;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(mainController);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
