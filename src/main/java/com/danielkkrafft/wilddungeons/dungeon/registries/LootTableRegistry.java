package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.ItemTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedTable;

import static com.danielkkrafft.wilddungeons.dungeon.registries.LootPoolRegistry.*;

public class LootTableRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedTable<ItemTemplate>> LOOT_TABLE_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedTable<ItemTemplate> BASIC_LOOT_TABLE = create("BASIC_LOOT_TABLE")
            .add(COMMON_LOOT_POOL, 1)
            .add(MEDIUM_LOOT_POOL, 5)
            .add(RARE_LOOT_POOL, 10)
            .add(EPIC_LOOT_POOL, 50);

    public static final WeightedTable<ItemTemplate> METRO_LOOT_TABLE = create("METRO_LOOT_TABLE")
            .add(METRO_COMMON_POOL, 1)
            .add(METRO_MEDIUM_POOL, 5)
            .add(METRO_RARE_POOL, 10)
            .add(METRO_EPIC_POOL, 50);

    public static final WeightedTable<ItemTemplate> SEWER_LOOT_TABLE = create("SEWER_LOOT_TABLE")
            .add(VILLAGE_SEWER_COMMON_POOL, 1)
            .add(VILLAGE_SEWER_MEDIUM_POOL, 5)
            .add(VILLAGE_SEWER_RARE_POOL, 10)
            .add(VILLAGE_SEWER_EPIC_POOL, 50);

    public static  WeightedTable<ItemTemplate> create(String name){
        WeightedTable<ItemTemplate> lootTable = new WeightedTable<ItemTemplate>().setName(name);
        LOOT_TABLE_REGISTRY.add(lootTable);
        return lootTable;
    }
}
