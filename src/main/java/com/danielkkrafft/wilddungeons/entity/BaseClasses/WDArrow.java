package com.danielkkrafft.wilddungeons.entity.BaseClasses;

import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public abstract class WDArrow extends Arrow implements GeoItem {

    private static String NAME = "default_arrow";

    protected WDItemAnimator animator = new WDItemAnimator(NAME, this);
    protected ClientModel<WDArrow> model = new ClientModel<>((ResourceLocation) null, null, null);

    protected int ticksTillDed = 60;

    public WDArrow(EntityType<? extends WDArrow> entityType, Level level, String name) {
        super(entityType, level);
        NAME = name;

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        animator.registerControllersFromAnimator(this, controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return animator.getCache();}

//    @Override
//    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
//        consumer.accept(new GeoRenderProvider() {
//            private final BlockEntityWithoutLevelRenderer renderer = new WDWeapon.WDWeaponRenderer<>(model, false);
//
//            @Override
//            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
//                return this.renderer;
//            }
//        });
//    }

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
