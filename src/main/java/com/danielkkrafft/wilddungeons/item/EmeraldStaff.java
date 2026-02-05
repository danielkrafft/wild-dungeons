package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.FriendlyEmeraldWisp;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

public class EmeraldStaff extends WDWeapon {
    private static final String NAME = "emerald_staff";

    private static final int DURABILITY = 2000;
    private static final int RANGE = 50;
    private static final int COOLDOWN_TICKS = 20;

    private static final float SPAWN_DISTANCE = 2.0f;
    private static final float PROJECTILE_SPEED = 1.5f;

    public EmeraldStaff() {
        super(NAME, new Properties()
                .stacksTo(1)
                .durability(DURABILITY)
                .rarity(Rarity.EPIC)
        );

        this.hasIdle = true;
        this.hasEmissive = false;

        this.ammoPredicate = stack -> stack.is(Items.EMERALD);
        this.projectileRange = RANGE;
    }

//    @Override TODO - Fix for 1.21.11
//    protected void configureAnimator(WDItemAnimator animator) {
//        animator.addLoopingAnimation("idle");
//        animator.addAnimation("fire");
//    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        ItemStack ammo = findAmmo(player);
        boolean hasAmmo = !ammo.isEmpty();

        if (!player.getAbilities().instabuild && !hasAmmo) {
            player.displayClientMessage(
                    Component.translatable("wilddungeons.missing_ammo", Items.EMERALD.getName()),
                    true
            );
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        if (!player.getAbilities().instabuild && hasAmmo) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        }

        var look = player.getLookAngle();
        FriendlyEmeraldWisp few = summonEntity((ServerLevel) level, WDEntities.FRIENDLY_EMERALD_WISP.get(), player.getEyePosition().add(look.scale(SPAWN_DISTANCE)));
        few.setYRot(player.getYRot());
        few.setXRot(player.getXRot());
        few.setYHeadRot(player.getYRot());
        few.setYHeadRot(player.getYRot());
        few.setDeltaMovement(look.normalize().scale(PROJECTILE_SPEED));

        // animator.playAnimation(this, "fire", stack, player, level); TODO - Fix for 1.21.11
        level.playSound(null, player.blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f);

        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResult.CONSUME;
    }
}
