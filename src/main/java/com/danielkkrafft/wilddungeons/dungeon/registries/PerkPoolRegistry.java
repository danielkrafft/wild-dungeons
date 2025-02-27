package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry.*;

public class PerkPoolRegistry {
    public static final WeightedPool<DungeonPerkTemplate> ALL_PERKS_POOL = new WeightedPool<>();

    public static void setupPerkPools(){
        ALL_PERKS_POOL
                .add(SWORD_DAMAGE,3)
                .add(AXE_DAMAGE,3)
                .add(BOW_DAMAGE,3)
                .add(EXTRA_LIFE,2)
                .add(FIRE_RESIST,1)
                .add(STRENGTH,1)
                .add(NIGHT_VISION,1)
                .add(HEALTH_BOOST,1);
    }
}
