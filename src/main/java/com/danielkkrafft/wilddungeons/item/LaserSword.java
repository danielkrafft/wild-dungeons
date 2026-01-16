package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.Laserbeam;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class LaserSword extends WDWeapon {

    public static final String NAME = "laser_sword";

    public enum AnimationList {idle, gun_transform, charging_up, fully_charged, shoot, sword_transform}

    private static final int warmUpSeconds = 5;//old 5
    private static final int maxChargeSeconds = 15;//old 15
    private static final int cooldownSeconds = 3;//used to be 3x the total charge time
    private static final float cooldownTransitionRatio = 0.25f;
    private static final int laserEntityChargeSeconds = 0;
    private static final Vector2i blastLevelRange = new Vector2i(1, 5);
    private static final Vector2f damageRange = new Vector2f(0.75f, 20f);
    private static final Vector2f laserRadiusRange = new Vector2f(0.1f, 1.0f);
    private static final Vector2f laserDistanceRange = new Vector2f(15, 160);
    private static final Vector2i explosionRadiusRange = new Vector2i(1, 30);


    public LaserSword() {
        super(NAME, new Properties().rarity(Rarity.RARE).durability(200).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F)));
        hasEmissive = true;

        ClientModel<WDWeapon> model = new ClientModel<>(NAME, "item");
        model.setAltModel("item/laser_sword_working", "laser_sword_working");
        this.model = model;
        this.model.activateAltModel();

        this.animator.addLoopingAnimation(AnimationList.idle.toString());//default animation
        this.animator.addAnimation(AnimationList.gun_transform.toString(), (float) 2 / warmUpSeconds);//2 seconds long
        this.animator.addLoopingAnimation(AnimationList.charging_up.toString(), (float) 20 / (maxChargeSeconds + warmUpSeconds));//20 seconds long
        this.animator.addLoopingAnimation(AnimationList.fully_charged.toString());//20 seconds long
        this.animator.addAnimation(AnimationList.shoot.toString(), 1.5f / (cooldownSeconds * cooldownTransitionRatio));//1.5 seconds long
        this.animator.addAnimation(AnimationList.sword_transform.toString(), (float) 2 / (cooldownSeconds * (1 - cooldownTransitionRatio)));//2 seconds long
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        model.activateAltModel();
        player.startUsingItem(hand);
        this.animator.playAnimation(this, AnimationList.gun_transform.toString(), player.getItemInHand(hand), player, player.level());
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            int charge = getUseDuration(stack, livingEntity) - remainingUseDuration;
            if (charge == 0) this.animator.playAnimation(this, AnimationList.gun_transform.toString(), stack, player, player.level());
            if (charge == warmUpSeconds * 20)
                this.animator.playAnimation(this, AnimationList.charging_up.toString(), stack, player, player.level());
            if (charge == (maxChargeSeconds * 20) + (warmUpSeconds * 20))
                this.animator.playAnimation(this, AnimationList.fully_charged.toString(), stack, player, player.level());
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack it) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int remainingUseDuration) {

        model.activateBaseModel();
        if (!(livingEntity instanceof Player player)) return;
        int charge = getUseDuration(stack, player) - remainingUseDuration - warmUpSeconds * 20;
        if (charge <= 0) return;

        float ratio = Math.clamp((float) charge / (maxChargeSeconds * 20), 0.0f, 1.0f);

        int blastLevel = Mth.lerpInt(ratio, blastLevelRange.x, blastLevelRange.y);
        float damage = Mth.lerp(ratio, damageRange.x, damageRange.y);
        float laserRadius = Mth.lerp(ratio, laserRadiusRange.x, laserRadiusRange.y);
        float range = Mth.lerp(ratio, laserDistanceRange.x, laserDistanceRange.y);
        float explosionRadius = Mth.lerpInt(ratio, explosionRadiusRange.x, explosionRadiusRange.y);
        boolean explosion = ratio > 0.4f;
        boolean debris = ratio > 0.6f;

        if (!player.isCreative()) stack.setDamageValue(stack.getDamageValue() + blastLevel * 10);//should this be hurtAndBreak?
        player.getCooldowns().addCooldown(this, cooldownSeconds * 20);
        shoot(blastLevel, level, player, damage, laserRadius, range, explosion, explosionRadius, debris);
        this.animator.playAnimation(this, AnimationList.shoot.toString(), stack, player, level);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {

        if (entity instanceof Player player && player.getCooldowns().isOnCooldown(this) && !player.isUsingItem()) {
            if (player.getCooldowns().getCooldownPercent(this, 0) <= 1 - cooldownTransitionRatio) {
                this.animator.playAnimation(this, AnimationList.sword_transform.toString(), itemStack, player, level);
            }
        } else if (entity instanceof Player player && !player.getCooldowns().isOnCooldown(this) && !player.isUsingItem()) {
            this.animator.playAnimation(this, AnimationList.idle.toString(), itemStack, player, level);
        }
    }

    private void shoot(int blastLevel, Level level, Player player, float damage, float radius, float range, boolean explosion, float explosionradius, boolean debris) {
        float yaw = player.getYRot(), pitch = player.getXRot();
        Vec3 vec = MathUtil.displaceVector(0.5f, player.getEyePosition(), yaw, pitch);
        level.addFreshEntity(new Laserbeam(player, vec, yaw, pitch, damage, radius, range, explosion, explosionradius, debris, laserEntityChargeSeconds));
        Vec3 oppositeLook = MathUtil.velocity3d(1, yaw + 180, -pitch);

        float ratio = Math.clamp((float) blastLevel / 5, 0.0f, 1.0f);
        float pushFactor = Mth.lerp(ratio, 0.4f, 2.7f);
        int particleCount = Mth.lerpInt(ratio, 5, 20);
        float sound1Pitch = Mth.lerp(ratio, 2f, 0.9f);
        float sound2Pitch = Mth.lerp(ratio, 2f, 0.7f);

        player.setDeltaMovement(player.getDeltaMovement().add(oppositeLook.multiply(pushFactor, pushFactor, pushFactor)));
        for (int i = 0; i < particleCount; i++)
            level.addAlwaysVisibleParticle(ParticleTypes.POOF, true, vec.x, vec.y, vec.z, 0.005f, 0.005f, 0.005f);
        level.playSound(null, player.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS, 1f, sound1Pitch);
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.5f, sound2Pitch);
        if (blastLevel >= 5) {
            level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION_EMITTER, true, vec.x, vec.y, vec.z, 0, 0, 0);
            level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, true, vec.x, vec.y, vec.z, 0, 0, 0);
        } else if (blastLevel >= 3) {
            level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION, true, vec.x, vec.y, vec.z, 0, 0, 0);
            level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, true, vec.x, vec.y, vec.z, 0, 0, 0);
        }
    }
}