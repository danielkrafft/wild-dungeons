package com.danielkkrafft.wilddungeons.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import static net.minecraft.world.effect.MobEffects.POISON;

public class SkelepedeSegment extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String SKELEPEDE_SEGMENT_CONTROLLER = "skelepede_segment_controller";
    private final AnimationController<SkelepedeSegment> mainController = new AnimationController<>(this, SKELEPEDE_SEGMENT_CONTROLLER, 5, animationPredicate());


    public SkelepedeSegment(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    private AnimationController.AnimationStateHandler<SkelepedeSegment> animationPredicate() {
        return (state) -> {
            mainController.setAnimation(RawAnimation.begin().thenWait(level().getRandom().nextInt(20)).thenLoop("animation.skelepede.walk"));
            return PlayState.CONTINUE;
        };
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,20);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(mainController);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // Set the segment's position and rotation to follow the main entity
    public void setSegmentPosition(Vec3 pos, float yRot) {
        this.moveTo(pos.x, pos.y, pos.z);
        this.setYRot(yRot);
        this.yBodyRot = yRot;
        this.yHeadRot = yRot;
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, .25f, 1.0F);
    }


    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.is(POISON)) return false;
        return super.addEffect(effectInstance, entity);
    }
}
