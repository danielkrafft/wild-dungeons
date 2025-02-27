package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.GrapplingHook;
import com.danielkkrafft.wilddungeons.registry.WDDataComponents;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Meathook extends WDWeapon {

    public static final String NAME = "meathook";

    public enum AnimationList {idle, charge, hold, fire}

    private static final int chargeSeconds = 1;

    public Meathook() {
        super(NAME);
        this.addLoopingAnimation(AnimationList.idle.toString());//0s long
        this.addAnimation(AnimationList.charge.toString(), 2.13f / chargeSeconds);//2.13s long
        this.addLoopingAnimation(AnimationList.hold.toString());//0s long
        this.addAnimation(AnimationList.fire.toString());//0.25s long
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level world, @NotNull Entity en, int slot, boolean inMainHand) {
        if (en instanceof Player p) {
            if (Meathook.getHookUUID(itemStack) == null) {
                if (!inMainHand && !p.getItemInHand(InteractionHand.OFF_HAND).equals(itemStack)) {
                    if (Meathook.isCharged(itemStack)) {
                        Meathook.setCharged(itemStack, false);
                    }
                    if (Meathook.isCharging(itemStack)) {
                        Meathook.setCharging(itemStack, false);
                    }
                    setAnimation(AnimationList.idle.toString(), itemStack, p, p.level());
                } else if (!Meathook.isCharging(itemStack) && !Meathook.isCharged(itemStack))
                    setAnimation(AnimationList.idle.toString(), itemStack, p, p.level());
            } else {
                if (!world.isClientSide) {
                    MinecraftServer server = en.getServer();
                    if (server != null) {
                        for (ServerLevel l : server.getAllLevels()) {
                            Entity enn = l.getEntity(Meathook.getHookUUID(itemStack));
                            if (enn != null && !enn.level().equals(world)) Meathook.resetHook(p, itemStack);
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack it) {
        return UseAnim.NONE;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack it, Player p) {
        if (!p.level().isClientSide) {
            resetHook(p, it);
            setAnimation(AnimationList.idle.toString(), it, p, p.level());
            return super.onDroppedByPlayer(it, p);
        }
        return false;
    }

    public static void resetHook(Player p, ItemStack it) {
        if (it != null && !p.level().isClientSide) {
            setCharged(it, false);
            setCharging(it, false);
            if (getHookUUID(it) != null) {
                p.level().playSound(null, p.blockPosition(), retractMeathook(), SoundSource.PLAYERS, 1f, 1f);
                setHook(it, null);
                if (!p.isCreative()) it.setDamageValue(it.getDamageValue() + 1);
            }
            p.getCooldowns().addCooldown(it.getItem(), 20);
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, Player p, @NotNull InteractionHand hand) {
        ItemStack it = p.getItemInHand(hand);
        if (getHookUUID(it) == null) {
            p.startUsingItem(hand);
            return InteractionResultHolder.consume(it);
        }
        return InteractionResultHolder.fail(it);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int remainingUseDuration) {
        int i = getUseDuration(itemStack, livingEntity) - remainingUseDuration;

        if (i >= chargeSeconds * 20) {
            if (i == chargeSeconds * 20) {
                level.playSound(null, livingEntity.blockPosition(), loadMeathook(), SoundSource.PLAYERS, 1f, 1f);
                if (livingEntity instanceof Player)
                    setAnimation(AnimationList.charge.toString(), itemStack, (Player) livingEntity, level);
            }
            setCharged(itemStack, true);
        } else {
            setCharging(itemStack, true);
            if (i % 8 == 0)
                level.playSound(null, livingEntity.blockPosition(), chargeMeathook(i), SoundSource.PLAYERS, 1f, 1f);
            if (i == 0) {
                if (livingEntity instanceof Player)
                    setAnimation(AnimationList.charge.toString(), itemStack, (Player) livingEntity, level);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack it, Level world, LivingEntity p, int count) {
        if (p instanceof Player p2) {
            if (isCharged(it)) {
                setCharging(it, false);
                setCharged(it, false);
                shoot(world, p2, it, p.getYRot(), p.getXRot());
                setAnimation(AnimationList.fire.toString(), it, p2, world);
            } else setAnimation(AnimationList.idle.toString(), it, p2, world);
        }
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
}