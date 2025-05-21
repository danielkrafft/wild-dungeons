package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.item.itemhelpers.AnimationFactory;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDProjectileItemBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.function.Predicate;

public class WindBow extends WDProjectileItemBase {

    private static final String NAME = "wind_bow";
    private enum AnimationList {
        draw,
        release
    }

    public WindBow(){
        super(NAME);
        animator.addAnimation(AnimationList.draw.toString());
        animator.addAnimation(AnimationList.release.toString());
        hasIdle = false;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        animator.playAnimation(this, AnimationList.draw.toString(), player.getItemInHand(hand), player, level);
        player.startUsingItem(hand);
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return null;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 0;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

    }
}
