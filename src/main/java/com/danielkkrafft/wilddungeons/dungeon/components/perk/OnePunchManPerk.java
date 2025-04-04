package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class OnePunchManPerk extends DungeonPerk {
    public int kills = 0;


    public OnePunchManPerk(String sessionKey) {
        super(sessionKey);
    }

    @SubscribeEvent
    public static void onMobKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            if (serverPlayer.getWeaponItem().isEmpty()) {
                OnePunchManPerk perk = (OnePunchManPerk) wdPlayer.getCurrentDungeon().getPerkByClass(OnePunchManPerk.class);

                if (perk != null) {
                    wdPlayer.getServerPlayer().sendSystemMessage(Component.literal("Punch Damage Upgraded!"), true);
                    perk.kills += 1;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMobHurt(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            if (serverPlayer.getWeaponItem().isEmpty()) {
                OnePunchManPerk perk = (OnePunchManPerk) wdPlayer.getCurrentDungeon().getPerkByClass(OnePunchManPerk.class);

                if (perk != null) event.setNewDamage(event.getOriginalDamage() + perk.kills * 0.1f);
            }
        }
    }
}
