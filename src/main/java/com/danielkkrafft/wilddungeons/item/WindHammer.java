package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.registry.WDDataComponents;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WindHammer extends WDWeapon {

    public static final String NAME = "wind_hammer";
    public static final float MIN_SMASH_DISTANCE=1.5f;

    public WindHammer() {
        super(NAME, new Properties().rarity(Rarity.EPIC).durability(1000).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 8.0f, -3.5f)));

    }

    private enum SmashType {
        light,
        medium,
        heavy
    }

    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, Player player) {

        return !player.isCreative();
    }

    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState blockState) {

        return 5;
    }

    private float calculateAdditionalSmashDamage(float fallDistance) {
        return fallDistance > WindHammer.MIN_SMASH_DISTANCE ? fallDistance - WindHammer.MIN_SMASH_DISTANCE + 1 : 0;
    }

    private SoundEvent getImpactSound(SmashType smashType) {

        return switch (smashType) {
            case SmashType.light -> WDSoundEvents.WIND_HAMMER_SMASH_LIGHT.value();
            case SmashType.medium -> WDSoundEvents.WIND_HAMMER_SMASH_MEDIUM.value();
            case SmashType.heavy -> WDSoundEvents.WIND_HAMMER_SMASH_HEAVY.value();
        };
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull Player attacker, @NotNull Entity hurt) {

        final float fallDist = attacker.fallDistance;
        setFallDist(stack, fallDist);

        if(fallDist >= MIN_SMASH_DISTANCE) {

            Level level = attacker.level();
            Vec3 strikePosition = hurt.position();
            BlockState strikeState = level.getBlockState(new BlockPos ((int)strikePosition.x, (int)strikePosition.y - 1, (int)strikePosition.z));

            if (!level.isClientSide()) {

                Holder<Enchantment> densityHolder = WildDungeons.getEnchantment(Enchantments.DENSITY);
                if (densityHolder == null) {
                    WildDungeons.getLogger().warn("Failed to find DENSITY!");
                }

                // I don't see where the original code was checking if DENSITY was
                // applied. Here, I'm checking if DENSITY is on the hammer, if so, we do
                // the calculation to for additional damage, otherwise we damage nothing.
                // -Lawrence

                float d = 1.4f *  stack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.DENSITY)) > 0 ? calculateAdditionalSmashDamage(fallDist) : 0;
                final float shockwaveRadius = d > 5 ? 5 : d;

                WindChargeProjectile.radiusHit(level,strikePosition,shockwaveRadius,1.5f,shockwaveRadius,null).forEach(
                        l->{
                            if(!l.equals(attacker))
                            {
                                l.hurt(l.damageSources().mobAttack(attacker),5);
                                float dist=Math.max(Mth.sqrt((float)l.distanceToSqr(strikePosition)),1),kb=shockwaveRadius/dist;
                                l.setDeltaMovement(l.getDeltaMovement().add(l.position().subtract(strikePosition).normalize().multiply(kb/5.,0,kb/5.).add(0,kb/10.,0)));
                            }
                        });
            }

            SmashType smashType = fallDist < 8 ? SmashType.light : fallDist < 40 ? SmashType.medium : SmashType.heavy;

            SoundEvent impactSound = getImpactSound(smashType);
            SoundEvent strike = WDSoundEvents.WIND_HAMMER_SMASH.value();
            SoundEvent wind = WDSoundEvents.WIND_HAMMER_SMASH_WIND.value();

            level.playSound(null, attacker.getX(),attacker.getY(),attacker.getZ(), impactSound, attacker.getSoundSource(), 5, 1);
            level.playSound(null, attacker.getX(),attacker.getY(),attacker.getZ(), strike, attacker.getSoundSource(), 5, 1);

            if(!strikeState.isAir())
            {
                if(level instanceof ServerLevel server)
                {
                    UtilityMethods.sendParticles(server,new BlockParticleOption(ParticleTypes.BLOCK,strikeState),true,150,strikePosition.x,strikePosition.y,strikePosition.z,0.5f,0.2f,0.5f,0.6f);

                    // ----------------- SWITCH -----------------
                    // This was an if/then setup in the original code. Changed this flow to an enum to be more safe.
                    // It was also blank, not sure what the intent was, but I filled in the shell so we stayed consistent.

//                    switch (smashType) {
//                        case SmashType.light: {
//                            break;
//                        }
//                        case SmashType.medium: {
//                            break;
//                        }
//                        case SmashType.heavy: {
//                            break;
//                        }
//                    }
                    // ----------------- END SWITCH -----------------

                }
            }

            int windCharge = stack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.WIND_BURST));
            if (windCharge > 0)
            {
                level.playSound(null, attacker, wind, attacker.getSoundSource(), 5, 1);
                attacker.setDeltaMovement(new Vec3(attacker.getDeltaMovement().x,Mth.clamp(fallDist/25.,0.75,5),
                        attacker.getDeltaMovement().z));
            }
            attacker.resetFallDistance();
        }
        return false;
    }

    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity hurt, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return super.hurtEnemy(stack, hurt, attacker);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        return InteractionResult.FAIL;
    }

    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity livingEntity)
    {
        if (state.getDestroySpeed(level, pos) != 0) {
            stack.hurtAndBreak(2, livingEntity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    public static void setFallDist(@NotNull ItemStack stack, float fallDist)
    {
        if(!(stack.getItem() instanceof WindHammer)) {
            return;
        }

        stack.set(WDDataComponents.FALL_DIST.get(), fallDist);
    }

    public static float getFallDist(@NotNull ItemStack stack)
    {
        if(!(stack.getItem()instanceof WindHammer)) {
            return 0;
        }

        return stack.getOrDefault(WDDataComponents.FALL_DIST.get(), 0.f);
    }
}
