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
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Spiderling extends Spider implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String spiderling_controller = "spiderling_controller";
    private static final String
            idle = "animation.blackstone_spiderling.idle",
            walk = "animation.blackstone_spiderling.walk";
    private static final RawAnimation
            idleAnim = RawAnimation.begin().thenLoop(idle),
            walkAnim = RawAnimation.begin().thenLoop(walk);
    private final AnimationController<Spiderling> mainController = new AnimationController<>(this, spiderling_controller, 0, spiderlingPredicate())
            .triggerableAnim(idle, idleAnim)
            .triggerableAnim(walk, walkAnim);
    private AnimationController.AnimationStateHandler<Spiderling> spiderlingPredicate() {
        return state -> {
            if (this.getDeltaMovement().lengthSqr() > 0.01) {
                state.setAndContinue(walkAnim);
            }
            else {
                state.setAndContinue(idleAnim);
            }
            return PlayState.CONTINUE;
        };
    }


    public Spiderling(EntityType<? extends Spiderling> entityType, Level level) {
        super(entityType, level);
    }
    public static AttributeSupplier.Builder createSpiderling() {
        return Spider.createAttributes().add(Attributes.MAX_HEALTH, (double)8.0F);
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
