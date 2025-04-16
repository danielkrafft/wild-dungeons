package com.danielkkrafft.wilddungeons.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BusinessCEO extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_6);
    private static final String
            idle = "idle";
    private static final RawAnimation
            idleAnim = RawAnimation.begin().thenLoop(idle);

    public BusinessCEO(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    private final AnimationController<BusinessCEO> mainController = new AnimationController<>(this, "ceo_controller", 5,
            state -> state.setAndContinue(idleAnim))
            .triggerableAnim(idle, idleAnim);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(mainController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 225)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 50)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.ATTACK_KNOCKBACK, 2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2)
                .build();
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getCommandSenderWorld();
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        if (!level.isClientSide && !isDeadOrDying()) {
            //logic
        }

    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
    }

    @Override
    public boolean mayBeLeashed() {
        return false;
    }

    @Override
    public boolean canHaveALeashAttachedToIt() {
        return false;
    }
}