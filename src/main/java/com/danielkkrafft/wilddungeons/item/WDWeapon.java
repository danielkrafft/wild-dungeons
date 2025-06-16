package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemAnimator;
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
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.function.Consumer;

public abstract class WDWeapon extends Item implements GeoAnimatable, GeoItem {

    public String name = "default";

    // Animations
    protected WDItemAnimator animator;
    protected boolean hasIdle = true;

    // Model
    protected ClientModel<WDWeapon> model = new ClientModel<>((ResourceLocation) null, null, null);
    protected boolean hasEmissive = false;

    public WDWeapon(String name) {
        this(name, new Item.Properties()
                .rarity(Rarity.RARE)
                .durability(1000));
    }

    public WDWeapon(String name, Properties properties) {
        super(properties);
        this.name = name;
        this.model = new ClientModel<>(name,name,name);
        this.animator = new WDItemAnimator(name, this);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    /**
     * The first animation added to the list will be treated as the "idle", default animation
     */
    public void addAnimation(String animName) {addAnimation(animName, 1);}

    public void addAnimation(String animName, float animationSpeed) {animator.addAnimation(animName,animationSpeed);}

    public void addLoopingAnimation(String animName) {addLoopingAnimation(animName, 1);}

    public void addLoopingAnimation(String animName, float animationSpeed) {animator.addLoopingAnimation(animName, animationSpeed);}

    public void addHoldingAnimation(String animName) {
        addHoldingAnimation(animName, 1);
    }

    public void addHoldingAnimation(String animName, float animationSpeed) {animator.addHoldingAnimation(animName, animationSpeed);}

    public void addThenPlayAnimation(String animName, String nextAnimName) {
        addThenPlayAnimation(animName, nextAnimName, 1);
    }

    public void addThenPlayAnimation(String animName, String nextAnimName, float animationSpeed) {animator.addThenPlayAnimation(animName, nextAnimName, animationSpeed);}

    public String getAnimationName(int index) {
        return animator.getAnimationName(index);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new WDWeaponRenderer<>(model, hasEmissive);

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        animator.registerControllersFromAnimator(this,controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animator.getCache();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 100000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack it) {
        return UseAnim.BOW;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {
        if (entity instanceof Player player && !player.getCooldowns().isOnCooldown(this) && !player.isUsingItem() && hasIdle) {
            playAnimation(this.getAnimationName(0), itemStack, player, level);
        }
    }

    public void playAnimation(String animName, ItemStack stack, Player player, Level level) {
        animator.playAnimation(this, animName, stack, player, level);
    }

    protected void setAnimationSpeed(float inSpeed, Level level) {
        animator.setAnimationSpeed(inSpeed, level);
    }

    protected void setSoundKeyframeHandler(@Nullable AnimationController.SoundKeyframeHandler<GeoAnimatable> geoAnimatable) {
        animator.setSoundKeyframeHandler(geoAnimatable);
    }

    public static class WDWeaponRenderer<T extends WDWeapon> extends GeoItemRenderer<T> {
        public WDWeaponRenderer(ClientModel<T> model, boolean hasEmissive) {
            super(model);

            if (hasEmissive) this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
    }

}
