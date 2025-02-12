package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.Laserbeam;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LaserSword extends WDWeapon {

    public static final String NAME = "laser_sword";
    public enum AnimationList { idle, gun_transform, charging_up, fully_charged, shoot, sword_transform }

    public LaserSword() {
        super(NAME);
        this.addLoopingAnimation(AnimationList.idle.toString());
        this.addAnimation(AnimationList.gun_transform.toString());
        this.addLoopingAnimation(AnimationList.charging_up.toString());
        this.addLoopingAnimation(AnimationList.fully_charged.toString());
        this.addAnimation(AnimationList.shoot.toString());
        this.addAnimation(AnimationList.sword_transform.toString());
    }

    @Override @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            int charge = getUseDuration(stack, livingEntity) - remainingUseDuration;

            if (charge == 0) setAnimation(AnimationList.gun_transform.toString(), stack, player, player.level());
            if (charge == 100) setAnimation(AnimationList.charging_up.toString(), stack, player, player.level());
            if (charge == (15*20) + 100) setAnimation(AnimationList.fully_charged.toString(), stack, player, player.level());
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int count) {

        if (!(livingEntity instanceof Player player)) return;
        int charge = getUseDuration(stack, player) - count - 100;
        if (charge <= 0) return;

        float ratio = Math.clamp((float) charge / (15*20), 0.0f, 1.0f);

        int blastLevel = Mth.lerpInt(ratio, 1, 5);
        float damage = Mth.lerp(ratio, 0.75f, 20f);
        float laserRadius = Mth.lerp(ratio, 0.1f, 1.0f);
        float range = Mth.lerp(ratio, 15, 160);
        float explosionRadius = Mth.lerpInt(ratio, 1, 30);
        boolean explosion = ratio > 0.4f;
        boolean debris = ratio > 0.6f;

        if (!player.isCreative()) stack.setDamageValue(stack.getDamageValue()+blastLevel);
        player.getCooldowns().addCooldown(this,charge * 3);
        shoot(blastLevel, level, player, damage, laserRadius, range, explosion, explosionRadius, debris);
        setAnimation(AnimationList.shoot.toString(), stack, player, level);
    }

    private void shoot(int blastLevel, Level level, Player player, float damage, float radius, float range, boolean explosion, float explosionradius, boolean debris)
    {
        float yaw = player.getYRot(), pitch = player.getXRot();
        Vec3 vec = MathUtil.displaceVector(0.5f, player.getEyePosition(), yaw, pitch);
        level.addFreshEntity(new Laserbeam(player, vec, yaw, pitch, damage, radius, range, explosion, explosionradius, debris));
        Vec3 oppositeLook = MathUtil.velocity3d(1, yaw + 180, -pitch);

        float ratio = Math.clamp((float) blastLevel / 5, 0.0f, 1.0f);
        float pushFactor = Mth.lerp(ratio, 0.4f, 2.7f);
        int particleCount = Mth.lerpInt(ratio, 5, 20);
        float sound1Pitch = Mth.lerp(ratio, 2f, 0.9f);
        float sound2Pitch = Mth.lerp(ratio, 2f, 0.7f);

        player.setDeltaMovement(player.getDeltaMovement().add(oppositeLook.multiply(pushFactor, pushFactor, pushFactor)));
        for (int i = 0; i < particleCount; i++) level.addAlwaysVisibleParticle(ParticleTypes.POOF, true, vec.x, vec.y, vec.z, 0.005f, 0.005f, 0.005f);
        level.playSound(null, player.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS, 1f, sound1Pitch);
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.5f, sound2Pitch);
        if (blastLevel >= 5) {
            level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION_EMITTER,true,vec.x,vec.y,vec.z,0,0,0);
            level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE,true,vec.x,vec.y,vec.z,0,0,0);
        }
        else if (blastLevel >= 3) {
            level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION,true,vec.x,vec.y,vec.z,0,0,0);
            level.addAlwaysVisibleParticle(ParticleTypes.SMOKE,true,vec.x,vec.y,vec.z,0,0,0);
        }
    }
}