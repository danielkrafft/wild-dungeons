package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.GrapplingHook;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.registry.WDDataComponents;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

import java.util.UUID;

public class Meathook extends WDWeapon {

    public static final String NAME = "meathook";

    //TODO - Fix animations to match 1.21.11
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    public enum AnimationList {idle, charge, hold, fire}

    private static final int chargeSeconds = 1;

    public Meathook() {
        super(NAME, new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .durability(1000)
        );
//        this.animator.addLoopingAnimation(AnimationList.idle.toString());//0s long TODO - Fix animations to match 1.21.11
//        this.animator.addAnimation(AnimationList.charge.toString(), 2.13f / chargeSeconds);//2.13s long
//        this.animator.addLoopingAnimation(AnimationList.hold.toString());//0s long
//        this.animator.addAnimation(AnimationList.fire.toString());//0.25s long
    }

    public Meathook(Properties properties) {
        super(NAME, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player) {
            if (Meathook.getHookUUID(stack) == null) {
                if (!player.getItemInHand(InteractionHand.OFF_HAND).equals(stack)) {
                    if (Meathook.isCharged(stack)) {
                        Meathook.setCharged(stack, false);
                    }
                    if (Meathook.isCharging(stack)) {
                        Meathook.setCharging(stack, false);
                    }
                    //this.animator.playAnimation(this, AnimationList.idle.toString(), itemStack, player, player.level()); TODO - Fix animations to match 1.21.11
                } //else if (!Meathook.isCharging(itemStack) && !Meathook.isCharged(itemStack))
                    //this.animator.playAnimation(this, AnimationList.idle.toString(), itemStack, player, player.level());
            } else {
                if (!level.isClientSide()) {
                    MinecraftServer server = entity.level().getServer();
                    if (server != null) {
                        for (ServerLevel l : server.getAllLevels()) {
                            Entity enn = l.getEntity(Meathook.getHookUUID(stack));
                            if (enn != null && !enn.level().equals(level)) Meathook.resetHook(player, stack);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onDroppedByPlayer(@NotNull ItemStack it, Player p) {
        if (!p.level().isClientSide()) {
            resetHook(p, it);
            //this.animator.playAnimation(this, AnimationList.idle.toString(), it, p, p.level()); TODO - Fix animations to match 1.21.11
            return super.onDroppedByPlayer(it, p);
        }
        return false;
    }

    public static void resetHook(Player player, ItemStack stack) {
        if (stack != null && !player.level().isClientSide()) {
            setCharged(stack, false);
            setCharging(stack, false);
            if (getHookUUID(stack) != null) {
                player.level().playSound(null, player.blockPosition(), retractMeathook(), SoundSource.PLAYERS, 1f, 1f);
                setHook(stack, null);
                if (!player.isCreative()) stack.setDamageValue(stack.getDamageValue() + 1);
            }
            player.getCooldowns().addCooldown(stack, 20);
        }
    }

    @Override
    @NotNull
    public InteractionResult use(@NotNull Level world, Player p, @NotNull InteractionHand hand) {
        ItemStack it = p.getItemInHand(hand);
        if (getHookUUID(it) == null) {
            p.startUsingItem(hand);
            return InteractionResult.CONSUME;
        } else {
            resetHook(p, it);
            return InteractionResult.FAIL;
        }
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, int remainingUseDuration) {
        int i = getUseDuration(itemStack, livingEntity) - remainingUseDuration;

        if (i >= chargeSeconds * 20) {
            if (i == chargeSeconds * 20) {
                level.playSound(null, livingEntity.blockPosition(), loadMeathook(), SoundSource.PLAYERS, 1f, 1f);
                //if (livingEntity instanceof Player)
                    //this.animator.playAnimation(this, AnimationList.charge.toString(), itemStack, (Player) livingEntity, level); TODO - Fix animations to match 1.21.11
            }
            setCharged(itemStack, true);
        } else {
            setCharging(itemStack, true);
            if (i % 8 == 0)
                level.playSound(null, livingEntity.blockPosition(), chargeMeathook(i), SoundSource.PLAYERS, 1f, 1f);
            if (i == 0) {
                //if (livingEntity instanceof Player)
                    //this.animator.playAnimation(this, AnimationList.charge.toString(), itemStack, (Player) livingEntity, level); TODO - Fix animations to match 1.21.11
            }
        }
    }

    @Override
    public boolean releaseUsing(@NotNull ItemStack it, @NotNull Level world, @NotNull LivingEntity p, int count) {
        if (p instanceof Player p2) {
            if (isCharged(it)) {
                setCharging(it, false);
                setCharged(it, false);
                shoot(world, p2, it, p.getYRot(), p.getXRot());
                //this.animator.playAnimation(this, AnimationList.fire.toString(), it, p2, world); TODO - Fix animations to match 1.21.11
            } // else this.animator.playAnimation(this, AnimationList.idle.toString(), it, p2, world);
        }
        return false; // TODO - Implement actual return logic for 1.21.11
    }

    public void shoot(Level level, Player p, ItemStack it, float yaw, float pitch) {
        Vec3 vec = MathUtil.displaceVector(0.5f, p.getEyePosition(), yaw, pitch);
        GrapplingHook hook = new GrapplingHook(p, vec);
        Vec3 vel = MathUtil.velocity3d(4, yaw, pitch);
        hook.setDeltaMovement(vel);
        level.addFreshEntity(hook);
        level.playSound(null, p.blockPosition(), fireMeathook(), SoundSource.PLAYERS, 1f, 1f);
        level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, true, vec.x, vec.y, vec.z, 0, 0, 0);
        setHook(it, hook);
    }


    public static SoundEvent chargeMeathook(int i) {
        return switch (i) {
            case 0 -> WDSoundEvents.MEATHOOK_CHARGE_1.value();
            case 8 -> WDSoundEvents.MEATHOOK_CHARGE_2.value();
            case 16 -> WDSoundEvents.MEATHOOK_CHARGE_3.value();
            case 24 -> WDSoundEvents.MEATHOOK_CHARGE_4.value();
            case 32 -> WDSoundEvents.MEATHOOK_CHARGE_5.value();
            default -> null;
        };
    }

    public static SoundEvent loadMeathook() {
        return WDSoundEvents.MEATHOOK_LOAD.value();
    }

    public static SoundEvent fireMeathook() {
        return WDSoundEvents.MEATHOOK_FIRE.value();
    }

    public static SoundEvent retractMeathook() {
        return WDSoundEvents.MEATHOOK_RETRACT.value();
    }

    public static boolean isCharged(ItemStack it) {
        return it != null && it.getComponents().has(WDDataComponents.CHARGED.get()) && it.getComponents().get(WDDataComponents.CHARGED.get());
    }

    public static void setCharged(ItemStack it, boolean b) {
        if (it != null) {
            it.set(WDDataComponents.CHARGED.get(), b);
        }
    }

    public static boolean isCharging(ItemStack it) {
        return it != null && it.getComponents().has(WDDataComponents.CHARGING.get()) && it.getComponents().get(WDDataComponents.CHARGING.get());
    }

    public static void setCharging(ItemStack it, boolean b) {
        if (it != null) {
            it.set(WDDataComponents.CHARGING.get(), b);
        }
    }

    public static UUID getHookUUID(ItemStack itemStack) {
        return itemStack != null && itemStack.getComponents().has(WDDataComponents.HOOK_UUID.get()) ? itemStack.getComponents().get(WDDataComponents.HOOK_UUID.get()) : null;
    }

    public static void setHook(ItemStack it, GrapplingHook hook) {
        if (it == null) return;
        if (hook != null) {
            it.set(WDDataComponents.HOOK_UUID.get(), hook.getUUID());
        } else if (it.getComponents().has(WDDataComponents.HOOK_UUID.get())) {
            it.remove(WDDataComponents.HOOK_UUID.get());
        }
    }

//    @Override TODO - Fix for 1.21.11
//    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
//        tooltipComponents.add(Component.translatable("tooltip.wilddungeons.meathook_1"));
//        tooltipComponents.add(Component.translatable("tooltip.wilddungeons.meathook_2"));
//        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
//    }
}