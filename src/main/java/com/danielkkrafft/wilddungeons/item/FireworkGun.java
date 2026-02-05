package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

public class FireworkGun extends WDWeapon {

    public static final String NAME = "firework_gun";

    //TODO - Fix animations to match 1.21.11
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    public enum AnimationList {idle, rotate}

    public FireworkGun() {
        super(NAME);
//        this.animator.addLoopingAnimation(AnimationList.idle.toString()); TODO - Fix animations to match 1.21.11
//        this.animator.addLoopingAnimation(AnimationList.rotate.toString());
    }

    @Override
    @NotNull
    public InteractionResult use(@NotNull Level level, Player p, @NotNull InteractionHand hand) {
        p.startUsingItem(hand);
        return InteractionResult.PASS;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            Inventory inv = player.getInventory();
            if (player.getCooldowns().isOnCooldown(stack)) return;
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack ammoStack = inv.getItem(i);
                if (ammoStack.getItem().equals(Items.FIREWORK_ROCKET)) {
                    //this.animator.playAnimation(this, AnimationList.rotate.toString(), stack, player, level); TODO - Fix animations to match 1.21.11
                    shoot(level, player, ammoStack, stack, player.getYRot(), player.getXRot());
                    break;
                }
            }
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    public void shoot(Level level, Player player, ItemStack fireworks, ItemStack stack, float yaw, float pitch) {
        level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_LOADING_START.value(), SoundSource.PLAYERS, 0.8f, 0.7f);
        Vec3 vec = MathUtil.displaceVector(0.5f, player.getEyePosition(), yaw, pitch);
        FireworkRocketEntity firework = new FireworkRocketEntity(level, fireworks, player, vec.x, vec.y, vec.z, true);
        firework.setDeltaMovement(MathUtil.velocity3d(2, yaw, pitch));
        level.addFreshEntity(firework);
        level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 0.8f, 0.7f);
        level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, true, vec.x, vec.y, vec.z, 0, 0, 0);
        player.getCooldowns().addCooldown(stack, 4);
        fireworks.setCount(fireworks.getCount() - 1);
    }
}
