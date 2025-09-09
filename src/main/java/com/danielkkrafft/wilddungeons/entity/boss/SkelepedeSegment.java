package com.danielkkrafft.wilddungeons.entity.boss;

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

public class SkelepedeSegment extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String SKELEPEDE_SEGMENT_CONTROLLER = "skelepede_segment_controller";
    private final AnimationController<SkelepedeSegment> mainController = new AnimationController<>(this, SKELEPEDE_SEGMENT_CONTROLLER, 5, animationPredicate());

    public SkelepedeSegment(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    private AnimationController.AnimationStateHandler<SkelepedeSegment> animationPredicate() {
        return (state) -> PlayState.STOP;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // animation logic here
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
