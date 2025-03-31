package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.AxeItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class BowDamagePerk extends DungeonPerk {

    public BowDamagePerk(String sessionKey) {
        super(sessionKey);
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);

            if (wdPlayer.getCurrentDungeon() == null) return;
            if (event.getSource().is(DamageTypes.ARROW)) {
                DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerkByClass(BowDamagePerk.class);
                if (perk != null) {
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, perk.count)));
                }
            }
        }
    }
}
