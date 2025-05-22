package com.danielkkrafft.wilddungeons.entity.BaseClasses;

import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemAnimator;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemBase;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.function.Consumer;

public abstract class WDArrow extends Arrow implements GeoItem {

    public String name = "default_arrow";

    protected WDItemAnimator animator;
    protected int ticksTillDed = 60;

    public WDArrow(EntityType<? extends WDArrow> entityType, Level level, String name) {
        super(entityType, level);

        animator = new WDItemAnimator(name, this);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {

        animator.registerControllersFromAnimator(this, registrar);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {

        return animator.getCache();
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {

        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new WDItemBase.WDWeaponRenderer<>();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        // Arrow is in ground (or stuck in water and not moving much)
        if (this.inGround || this.isInWater()) {
            inGroundTime++;
        } else {
            inGroundTime = 0;
        }

        // Despawn after 60 ticks (3 seconds) of being stuck
        if (inGroundTime > ticksTillDed) {
            this.discard(); // Remove the entity
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
    }
}
