package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public abstract class WDItemBase extends Item implements GeoAnimatable, GeoItem {

    public String name = "default";

    protected WDItemAnimator animator;
    protected boolean hasIdle = false;
    private final GeoItemRenderer<?> itemRenderer;

    public WDItemBase(String name, GeoItemRenderer<?> renderer) {
        this(
                name,
                renderer,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .durability(1000)
        );
    }

    public WDItemBase(String name, GeoItemRenderer<?> renderer, Properties properties) {
        super(properties);
        this.name = name;
        itemRenderer = renderer;
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
            private final GeoItemRenderer<?> renderer = itemRenderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 100000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {
        if (entity instanceof Player player && !player.getCooldowns().isOnCooldown(this) && !player.isUsingItem() && hasIdle) {
            animator.playAnimation(this, animator.getAnimationName(0), itemStack, player, level);
        }
    }

    public static class WDWeaponRenderer<T extends WDItemBase> extends GeoItemRenderer<T> {
        public WDWeaponRenderer() {
            super(new WDItemBase.WDWeaponModel<T>());
        }
    }

    public static class WDWeaponModel<T extends WDItemBase> extends GeoModel<T> {

        @Override @SuppressWarnings("removal") // Must be overridden, warning is unavoidable
        public ResourceLocation getModelResource(T animatable) {
            return WildDungeons.rl("geo/" + animatable.name + ".geo.json");
        }

        @Override @SuppressWarnings("removal") // Must be overridden, warning is unavoidable
        public ResourceLocation getTextureResource(T animatable) {
            return WildDungeons.rl("textures/item/" + animatable.name + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(T animatable) {
            return WildDungeons.rl("animations/" + animatable.name + ".animation.json");
        }
    }
}
