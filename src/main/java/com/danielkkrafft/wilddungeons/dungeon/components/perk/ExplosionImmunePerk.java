package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ExplosionImmunePerk extends DungeonPerk {
    public ExplosionImmunePerk(String sessionKey) {
        super(sessionKey);
    }

    @SubscribeEvent
    public static void onExplosionHurt(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;

            if (event.getSource().is(DamageTypes.EXPLOSION) || event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) {
                DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerkByClass(ExplosionImmunePerk.class);
                if (perk != null) {
                    event.setNewDamage(0.0f);
                }
            }
        }
    }
}
