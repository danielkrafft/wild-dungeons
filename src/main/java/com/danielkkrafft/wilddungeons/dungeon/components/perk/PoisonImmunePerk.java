package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class PoisonImmunePerk extends DungeonPerk {
    public PoisonImmunePerk(String sessionKey) {
        super(sessionKey);
    }

    @SubscribeEvent
    public static void onPoison(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;

            if (event.getSource().is(Tags.DamageTypes.IS_POISON)) {
                DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerkByClass(PoisonImmunePerk.class);
                if (perk != null) {
                    event.setNewDamage(0.0f);
                    event.getEntity().removeEffect(MobEffects.POISON);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPoisonApply(MobEffectEvent.Added event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;

            if (event.getEffectInstance().is(MobEffects.POISON)) {
                DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerkByClass(PoisonImmunePerk.class);
                if (perk != null) {
                    event.getEntity().removeEffect(MobEffects.POISON);
                }
            }
        }
    }
}
