package com.danielkkrafft.wilddungeons.dungeon.mob_effects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import static net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable.Result.DO_NOT_APPLY;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class WDMobEffect extends MobEffect {
    private boolean shouldApplyEffectTickThisTick = false;//this is what MobEffect.class says
    private boolean isInstantaneous = false;


    protected WDMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    protected WDMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    public WDMobEffect setShouldApplyEffectTickThisTick(boolean shouldApplyEffectTickThisTick) {
        this.shouldApplyEffectTickThisTick = shouldApplyEffectTickThisTick;
        return this;
    }

    public WDMobEffect setInstantaneous(boolean isInstantenous) {
        this.isInstantaneous = isInstantenous;
        return this;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return shouldApplyEffectTickThisTick;
    }

    @Override
    public boolean isInstantenous() {
        return isInstantaneous;
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event){
        if (event.getSource().getEntity() instanceof LivingEntity sourceEntity) {
            if (sourceEntity.getWeaponItem().isEmpty()) {
                MobEffectInstance onePunchManPerk = sourceEntity.getEffect(WDMobEffects.ONE_PUNCH_MAN);
                if (onePunchManPerk != null) {
                    int newAmplifier = onePunchManPerk.getAmplifier() + 1;
                    sourceEntity.removeEffect(WDMobEffects.ONE_PUNCH_MAN);
                    sourceEntity.addEffect(new MobEffectInstance(WDMobEffects.ONE_PUNCH_MAN, -1, newAmplifier));
                    if (sourceEntity instanceof ServerPlayer serverPlayer)
                        serverPlayer.sendSystemMessage(Component.literal("Punch Damage Upgraded!"), true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Pre event) {
        processExplosionResistance(event);
        processPoisonResistance(event);
        processKeenEdge(event);
        processEvasionPerk(event);
        processBigRedButton(event);

        if (event.getSource().getEntity() instanceof LivingEntity sourceEntity) {
            processWeaponItemDamageBonus(event, sourceEntity);
            processBowDamageBonus(event, sourceEntity);
        }
    }
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Applicable event) {
        MobEffectInstance perk = event.getEntity().getEffect(WDMobEffects.POISON_RESISTANCE);
        if (perk != null) {
            event.setResult(DO_NOT_APPLY);
        }
    }

    private static void processBowDamageBonus(LivingDamageEvent.Pre event, LivingEntity sourceEntity) {
        if (event.getSource().is(DamageTypes.ARROW)){
            MobEffectInstance bowDamagePerk = sourceEntity.getEffect(WDMobEffects.BOW_DAMAGE);
            if (bowDamagePerk != null) {
                event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, bowDamagePerk.getAmplifier())));
            }
        }
    }

    private static void processWeaponItemDamageBonus(LivingDamageEvent.Pre event, LivingEntity sourceEntity) {
        switch (sourceEntity.getWeaponItem().getItem()) {
            case SwordItem ignored -> {
                MobEffectInstance swordDamagePerk = sourceEntity.getEffect(WDMobEffects.SWORD_DAMAGE);
                if (swordDamagePerk != null) {
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, swordDamagePerk.getAmplifier())));
                }
            }
            case AxeItem ignored -> {
                MobEffectInstance axeDamagePerk = sourceEntity.getEffect(WDMobEffects.AXE_DAMAGE);
                if (axeDamagePerk != null) {
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, axeDamagePerk.getAmplifier())));
                }
            }
            default -> {
                if (sourceEntity.getWeaponItem().isEmpty()) {
                    MobEffectInstance onePunchManPerk = sourceEntity.getEffect(WDMobEffects.ONE_PUNCH_MAN);
                    if (onePunchManPerk != null) event.setNewDamage(event.getOriginalDamage() + (event.getOriginalDamage() * onePunchManPerk.getAmplifier() * 0.1f));
//                    WildDungeons.getLogger().info("One Punch " + event.getEntity().getName().getString() + " for " + event.getNewDamage() + " damage with no weapon");
                }
            }
        }
    }

    private static void processEvasionPerk(LivingDamageEvent.Pre event) {
        MobEffectInstance evasionPerk = event.getEntity().getEffect(WDMobEffects.EVASION);
        if (evasionPerk != null) {
            event.setNewDamage(0.0f);
            if (event.getEntity() instanceof ServerPlayer player){
                player.sendSystemMessage(Component.literal("Dodged!"), true);
            }
        }
    }

    public static void processKeenEdge(LivingDamageEvent.Pre event) {
        MobEffectInstance keenPerk = event.getEntity().getEffect(WDMobEffects.KEEN_EDGE);
        if (keenPerk == null) return;
        float multiplier = 1.0f;
        int stacks = keenPerk.getAmplifier();

        while (stacks > 10) {
            stacks -= 10;
            multiplier *= 3;
        }

        if (Math.random() < (double) stacks /10) {
            multiplier *= 3;
        }

        //todo special effect like particles or sound
        event.setNewDamage(event.getOriginalDamage() * multiplier);
    }

    private static void processBigRedButton(LivingDamageEvent.Pre event) {
        if (event.getNewDamage() < 0.5f) return;
        LivingEntity entity = event.getEntity();
        MobEffectInstance brbPerk = entity.getEffect(WDMobEffects.BIG_RED_BUTTON);
        if (brbPerk == null) return;
        int tntToSpawn = brbPerk.getAmplifier() + 1; // Amplifier starts at 0, so we add 1 to get the count
        if (tntToSpawn <= 0) return; // No TNT to spawn (e.g., if amplifier is negative)
        // search around the entity for a valid position to spawn TNT
        BlockPos entityPos = entity.blockPosition();
        for (int i = 0; i < tntToSpawn; i++) {
            BlockPos pos = entityPos;
            for (int j = 0; j < 20; j++) { // Try up to 20 times
                int offsetX = entity.level().random.nextIntBetweenInclusive(-10, 10); // Random offset in x direction
                int offsetY = entity.level().random.nextIntBetweenInclusive(1, 2); // Random offset in y direction
                int offsetZ = entity.level().random.nextIntBetweenInclusive(-10, 10); // Random offset in z direction
                BlockPos testPos = pos.offset(offsetX, offsetY, offsetZ);

                // Check for a valid spawn location
                if (entity.level().getBlockState(testPos).isAir() &&
                        entity.level().getBlockState(testPos.above()).isAir()) {
                    pos = testPos;
                    break;
                }
            }
            PrimedTnt primedTnt = new PrimedTnt(entity.level(), pos.getX(), pos.getY(), pos.getZ(), entity);
            entity.level().addFreshEntity(primedTnt);
        }
    }

    public static void processExplosionResistance(LivingDamageEvent.Pre event) {
        if (event.getSource().is(DamageTypes.EXPLOSION) || event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) {
            MobEffectInstance perk = event.getEntity().getEffect(WDMobEffects.EXPLOSION_RESISTANCE);
            if (perk != null) {
                event.setNewDamage(0.0f);
            }
        }
    }

    public static void processPoisonResistance(LivingDamageEvent.Pre event) {
        if (event.getSource().is(Tags.DamageTypes.IS_POISON)) {
            MobEffectInstance perk = event.getEntity().getEffect(WDMobEffects.POISON_RESISTANCE);
            if (perk != null) {
                event.setNewDamage(0.0f);
                event.getEntity().removeEffect(MobEffects.POISON);
            }
        }
    }
}
