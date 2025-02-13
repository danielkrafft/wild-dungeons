package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class WDWeapon extends Item implements GeoAnimatable, GeoItem {

    public final String name;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ArrayList<RawAnimation> animations = new ArrayList<>();

    public WDWeapon(String name) {
        super(new Item.Properties()
                .rarity(Rarity.RARE)
                .durability(1000));
        this.name = name;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    /**
     *  The first animation added to the list will be treated as the "idle", default animation
     */
    public void addAnimation(String animName) { animations.add(RawAnimation.begin().thenPlay("animation." + this.name + "." + animName)); }
    public void addLoopingAnimation(String animName) { animations.add(RawAnimation.begin().thenLoop("animation." + this.name + "." + animName)); }
    public String getAnimationName(int index) { return this.animations.get(index).getAnimationStages().getFirst().animationName(); }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new WDWeaponRenderer<>();
            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() { return this.renderer; }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<GeoAnimatable> controller = new AnimationController<>(this, WildDungeons.rl(this.name).toString(), 1, state -> PlayState.CONTINUE);
        this.animations.forEach(rawAnimation -> {
           controller.triggerableAnim(rawAnimation.getAnimationStages().getFirst().animationName(), rawAnimation);
        });
        controllers.add(controller);
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) {return 100000;}
    @Override public @NotNull UseAnim getUseAnimation(@NotNull ItemStack it){return UseAnim.BOW;}

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {
        if(entity instanceof Player player && !player.getCooldowns().isOnCooldown(this) && !player.isUsingItem()) {
            setAnimation(this.getAnimationName(0), itemStack, player, level);
        }
    }

    public void setAnimation(String animName, ItemStack stack, Player player, Level level) {
        if(level instanceof ServerLevel serverLevel) {
            String finalName = Objects.equals(animName.split("\\.")[0], "animation") ? animName : "animation." + this.name + "." + animName;
            this.triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), WildDungeons.rl(this.name).toString(), finalName);
        }
    }

    public static class WDWeaponRenderer<T extends WDWeapon> extends GeoItemRenderer<T> {
        public WDWeaponRenderer() {
            super(new WDWeaponModel<T>());
        }
    }

    public static class WDWeaponModel<T extends WDWeapon> extends GeoModel<T> {

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
