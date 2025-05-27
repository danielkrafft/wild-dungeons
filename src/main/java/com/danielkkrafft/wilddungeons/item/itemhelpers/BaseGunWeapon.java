package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.entity.BaseClasses.SelfGovernedEntity;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.GunWeaponData;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.AbstractProjectileParent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BaseGunWeapon extends AbstractProjectileParent<BaseGunWeapon, GunWeaponData, BaseGunWeapon.GunFactoryModel> {

    public static final String IDLE_ANIM = "idle";
    public static final String FIRE_ANIM = "fire";
    public static final String RELOAD_ANIM = "reload";

    public BaseGunWeapon(GunWeaponData newGunWeaponData) {
        super(
                newGunWeaponData,
                new GunFactoryModel(newGunWeaponData),
                buildProperties(newGunWeaponData)
        );

        if (newGunWeaponData.hasIdle) animator.addLoopingAnimation(IDLE_ANIM);
        if (newGunWeaponData.hasFire) animator.addAnimation(FIRE_ANIM);
        if (newGunWeaponData.hasReload) animator.addAnimation(RELOAD_ANIM);
    }

    public static class GunFactoryModel extends ProjectileRenderModel<BaseGunWeapon> {

        public GunFactoryModel(GunWeaponData data) {
            super(data.animations, data.baseModel, data.baseTexture);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {

        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));
        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(stack).isEmpty();

        InteractionResultHolder<ItemStack> ret = net.neoforged.neoforge.event.EventHooks.onArrowNock(stack, level, player, hand, flag);
        if (ret != null) return ret;

        if (!player.getAbilities().instabuild && !flag) {
            return InteractionResultHolder.fail(stack);
        }
        else {
            ItemStack ammo = player.getProjectile(stack);
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }

            if (itemData.hasFire) {
                animator.playAnimation(this, FIRE_ANIM, stack, player, level);
            }

            ProjectileFactory.spawnProjectile(
                    level,
                    itemData.projectileClass.get(),
                    player,
                    itemData.spawnDistanceOffset,
                    itemData.spawnHeightOffset,
                    itemData.projectileSpeed,
                    projectile -> {
                        projectile.setCustomName(Component.literal(itemData.projectileName));

                        if (projectile instanceof SelfGovernedEntity sG) {
                            sG.setFiredDirectionAndSpeed(player.getLookAngle(), itemData.projectileSpeed);
                        }
                    }
            );

            player.getCooldowns().addCooldown(this, itemData.cooldown);
            level.playSound(null, player.blockPosition(), itemData.fireSound.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }

        return super.use(level, player, hand);
    }
}
