package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.entity.renderer.NautilusShieldRenderer;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemAnimator;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public class NautilusShieldItem extends ShieldItem implements GeoAnimatable, GeoItem {
    protected String name = "nautilus_shield";
    protected WDItemAnimator animator;
    protected boolean hasIdle = true;
    // Model
    protected ClientModel<WDWeapon> model;
    protected boolean hasEmissive = false;


    public NautilusShieldItem(Properties properties) {
        super(properties);
        this.model = new ClientModel<>(this.name, "item");
        this.animator = new WDItemAnimator(this.name, this);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final GeoItemRenderer<?> renderer = new NautilusShieldRenderer();

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
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 100000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack it) {
        return UseAnim.BLOCK;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {
        if (entity instanceof Player player && !player.getCooldowns().isOnCooldown(this) && !player.isUsingItem() && hasIdle) {
            animator.playAnimation(this,this.animator.getAnimationName(0), itemStack, player, level);
        }
    }
}
