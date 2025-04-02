package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class DodgePerk extends DungeonPerk {
    public DodgePerk(String sessionKey) {
        super(sessionKey);
    }

    @SubscribeEvent
    public static void onPlayerHit(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;

            DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerkByClass(DodgePerk.class);
            if (perk != null) {
                if (Math.random() > Math.pow(0.90, perk.count)) {
                    event.setNewDamage(0.0f);
                    wdPlayer.getServerPlayer().sendSystemMessage(Component.literal("Dodged!"), true);
                }
            }
        }
    }
}
