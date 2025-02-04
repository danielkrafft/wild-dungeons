package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry.*;

public class PerkPoolRegistry {
    public static final WeightedPool<DungeonPerkTemplate> ALL_PERKS_POOL = new WeightedPool<>();

    public static void setupPerkPools(){
        ALL_PERKS_POOL
                .add(SWORD_DAMAGE,1)
                .add(AXE_DAMAGE,1)
                .add(BOW_DAMAGE,1)
                .add(EXTRA_LIFE,1);
    }
}
