package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class CriticalHitPerk extends DungeonPerk {
    public CriticalHitPerk(String sessionKey) {
        super(sessionKey);
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerkByClass(CriticalHitPerk.class);
            if (perk == null) return;
            float multiplier = 1.0f;
            int stacks = perk.count;

            while (stacks > 10) {
                stacks -= 10;
                multiplier *= 3;
            }

            if (Math.random() < (double) stacks /10) {
                multiplier *= 3;
            }

            event.setNewDamage(event.getOriginalDamage() * multiplier);
        }
    }
}
