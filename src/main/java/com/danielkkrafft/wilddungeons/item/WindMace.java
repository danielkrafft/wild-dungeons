package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.CameraShakeUtil;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


public class WindMace extends WDWeapon {

    public static final String NAME = "wind_mace";

    private enum AnimationList {
        idle,
        swing,
        slam
    }

    private static final int SHAKE_TICK=30, JITTER_TICK=60;

    public WindMace() {
        super(NAME, new Properties().rarity(Rarity.EPIC).durability(2000).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 0.f, 0.f)));

        animator.addAnimation(AnimationList.idle.toString());
        animator.addLoopingAnimation(AnimationList.swing.toString());
        animator.addAnimation(AnimationList.slam.toString());
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level,@NotNull Player player, @NotNull InteractionHand hand) {

        if (!level.isClientSide()) {
            animator.setSoundKeyframeHandler(state -> {
                if(state.getKeyframeData().getSound().equals("mace_swing"))
                    level.playSound(null,player,WDSoundEvents.WIND_MACE_SWING.value(),SoundSource.PLAYERS, 1.f, 1.f);
            });
            animator.playAnimation(this, AnimationList.swing.toString(), player.getItemInHand(hand), player, level);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack)
    {
        return UseAnim.NONE;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count)
    {
        if(entity instanceof Player p && p.level()instanceof ServerLevel server)
        {
            animator.playAnimation(this, AnimationList.slam.toString(),stack, p, p.level());
        }
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int tick) {

        int diff = getUseDuration(stack, livingEntity) - tick;
        float r = diff / 150f + 2;

        if (!level.isClientSide()) {
            if (livingEntity instanceof Player player) {
                animator.setAnimationSpeed(r, level);

                // Swing Volume
                final float BASE_VOLUME = 0.25f;
                final float VOLUME_SCALING = 1f / 300f; // rate per tick
                final float MAX_VOLUME = 1.0f;

                // Swing Pitch
                final float BASE_PITCH = 1.0f;
                final float PITCH_SCALING = 1f / 300f; // rate per tick;
                final float MAX_PITCH = 1.65f;

                float volume = Math.min(BASE_VOLUME + (diff * VOLUME_SCALING), MAX_VOLUME);
                float pitch = Math.min(BASE_PITCH + (diff * PITCH_SCALING), MAX_PITCH);
                animator.setSoundKeyframeHandler(state -> {
                    if(state.getKeyframeData().getSound().equals("mace_swing"))
                        level.playSound(null,player,WDSoundEvents.WIND_MACE_SWING.value(),SoundSource.PLAYERS, volume, pitch);
                });
            }
        }
        else {
            if (diff > SHAKE_TICK){
                CameraShakeUtil.trigger(Math.min((diff - SHAKE_TICK) / 100f, 1.0f));
            }
            if (diff > JITTER_TICK){

                // original calculation: Math.sin(Math.min((diff-JITTERTICK)/20.,10)
                // Because it is doing the move off a sin wave, it goes small then big then small
                // if we want to cap it to a certain part of the wave, we need to manage the value
                // thresholds. The below does that. It starts at 10 then goes down to the value
                // of b. This is not the best method to use since its calculation is determined by
                // the duration of the equipment and the JITTER_TICK value. If you adjust either of those
                // everything goes out the window and you have to play around with it to find out what
                // threshold you want. We should refactor this calculation in the future. - Lawrence

                double a =  10 - (diff - JITTER_TICK) / 20.0;
                double b = 9;
                double progress = Math.max(a, b);
//                WildDungeons.getLogger().info("a: {}, b: {}", a, b);
                double pullDir = 0.4 * Math.sin(progress * Math.toRadians(diff * 9));

                if(livingEntity instanceof Player player)
                {
                    Vec3 forward = Vec3.directionFromRotation(0,player.getYHeadRot());
                    player.setDeltaMovement(player.getDeltaMovement().add(pullDir*forward.x,0,pullDir*forward.z));
                }
            }
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack,@NotNull Level level,@NotNull LivingEntity livingEntity, int tick)
    {
        Vec3 strikePosition = livingEntity.position().add(livingEntity.getForward().multiply(2,0,2));

        float power = (getUseDuration(stack, livingEntity) - tick) / 200f;  // power of smash determined by how long it was held
        float radius = power * 4 + 2;                                       // radius of the smash
        float dmg = 20 * power + 5;                                         // damage of the smash
        float kb = 2.5f * power + 0.5f;                                     // knockback of the smash to surrounding entities
        float playerKb = 0.8f * kb;                                         // knockback for the player smashing

        if(level instanceof ServerLevel server)
        {
            if(livingEntity instanceof Player player)
            {
                //stack.hurtAndBreak(5, player, ignored -> player.broadcastBreakEvent(player.getUsedItemHand()));

                animator.setSoundKeyframeHandler(state -> {
                    if(state.getKeyframeData().getSound().equals("mace_smash"))
                    {
//                        WildDungeons.getLogger().info("Strike? DMG: {} ", dmg);
                        level.playSound(null,strikePosition.x,strikePosition.y,strikePosition.z, WDSoundEvents.WIND_MACE_SMASH.value(),SoundSource.PLAYERS,3,0.7f);
                        WindChargeProjectile.radiusHit(level,strikePosition,radius,2,radius,null).forEach(
                                l->{
                                    if(!l.equals(livingEntity))l.hurt(l.damageSources().mobAttack(livingEntity),dmg);
                                    l.setDeltaMovement(l.getDeltaMovement().add(l.position().subtract(strikePosition).normalize().multiply(kb,0,kb).add(0,kb,0)));
                                });
                    }
                });
            }
            BlockState strikeState=level.getBlockState(new BlockPos((int)strikePosition.x,(int)strikePosition.y-1,(int)strikePosition.z));
            if(!strikeState.isAir())
            {
                UtilityMethods.sendParticles(server,new BlockParticleOption(ParticleTypes.BLOCK,strikeState),true,150,strikePosition.x,strikePosition.y+0.5,strikePosition.z,0.5f,0.2f,0.5f,0.6f);
            }
        }
        else
        {
            //deal 3/4 kb to player
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(livingEntity.position().subtract(strikePosition).normalize().multiply(playerKb,0,playerKb).add(0,playerKb,0)));
        }
        if(livingEntity instanceof Player p)
        {
            p.getCooldowns().addCooldown(this,60);
        }
    }
}
