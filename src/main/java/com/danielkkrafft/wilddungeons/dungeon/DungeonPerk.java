package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class DungeonPerk {

    public DungeonPerks.Perks perk;
    public int count = 1;

    public DungeonPerk(DungeonPerks.Perks perk) {
        this.perk = perk;
    }

    public static void addPerk(DungeonSession session, DungeonPerks.Perks perk) {

        WildDungeons.getLogger().info("ADDING PERK: {}", perk);

        if (session.perks.containsKey(perk)) {
            session.perks.get(perk).count += 1;
        } else {
            session.perks.put(perk, new DungeonPerk(perk));
        }
        DungeonPerks.onPerk(perk, session);

    }

}
