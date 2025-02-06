package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.ItemTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedTable;

import static com.danielkkrafft.wilddungeons.dungeon.registries.LootPoolRegistry.*;

public class LootTableRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedTable<ItemTemplate>> LOOT_TABLE_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedTable<ItemTemplate> BASIC_LOOT_TABLE = new WeightedTable<ItemTemplate>().setName("BASIC_LOOT_TABLE");

    public static void setupLootTables(){
        BASIC_LOOT_TABLE
                .add(COMMON_LOOT_POOL, 1)
                .add(MEDIUM_LOOT_POOL, 5)
                .add(RARE_LOOT_POOL, 10);
        LOOT_TABLE_REGISTRY.add(BASIC_LOOT_TABLE);
    }
}
