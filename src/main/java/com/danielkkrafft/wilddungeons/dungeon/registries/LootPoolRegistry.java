package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.LootEntry;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.LootEntryRegistry.*;

public class LootPoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<LootEntry>> LOOT_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static final WeightedPool<LootEntry> COMMON_LOOT_POOL = new WeightedPool<LootEntry>().setName("COMMON_LOOT_POOL");
    public static final WeightedPool<LootEntry> MEDIUM_LOOT_POOL = new WeightedPool<LootEntry>().setName("MEDIUM_LOOT_POOL");
    public static final WeightedPool<LootEntry> RARE_LOOT_POOL = new WeightedPool<LootEntry>().setName("RARE_LOOT_POOL");

    public static void setupLootPools(){
        COMMON_LOOT_POOL
                .add(ARROWS, 1)
                .add(SEEDS, 1)
                .add(MELONS, 1)
                .add(LEATHER, 1)
                .add(INK_SACS, 2)
                .add(COAL, 1);
        LOOT_POOL_REGISTRY.add(COMMON_LOOT_POOL);

        MEDIUM_LOOT_POOL
                .add(IRON_INGOTS, 2)
                .add(CHARCOAL, 1)
                .add(OAK_LOGS, 1)
                .add(GUNPOWDER, 1)
                .add(STONE_SHOVEL, 1)
                .add(STONE_PICKAXE, 1)
                .add(STONE_AXE, 1);
        LOOT_POOL_REGISTRY.add(MEDIUM_LOOT_POOL);

        RARE_LOOT_POOL
                .add(LAPIS_LAZULI, 2)
                .add(BOTTLES_O_ENCHANTING, 2)
                .add(DIAMOND, 1);
        LOOT_POOL_REGISTRY.add(RARE_LOOT_POOL);
    }
}
