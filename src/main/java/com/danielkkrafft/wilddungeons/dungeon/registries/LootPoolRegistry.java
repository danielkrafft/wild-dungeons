package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.ItemTemplateRegistry.*;

public class LootPoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<ItemTemplate>> LOOT_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static final WeightedPool<ItemTemplate> COMMON_LOOT_POOL = new WeightedPool<ItemTemplate>().setName("COMMON_LOOT_POOL");
    public static final WeightedPool<ItemTemplate> MEDIUM_LOOT_POOL = new WeightedPool<ItemTemplate>().setName("MEDIUM_LOOT_POOL");
    public static final WeightedPool<ItemTemplate> RARE_LOOT_POOL = new WeightedPool<ItemTemplate>().setName("RARE_LOOT_POOL");

    public static void setupLootPools(){
        COMMON_LOOT_POOL
                .add(ARROWS, 15)
                .add(SEEDS, 10)
                .add(MELONS, 10)
                .add(LEATHER, 10)
                .add(INK_SACS, 10)
                .add(COAL, 10)
                .add(CHARCOAL, 10)
                .add(OAK_LOGS, 10)
                .add(GUNPOWDER, 5)
                .add(STONE_SHOVEL, 2)
                .add(STONE_PICKAXE, 2)
                .add(STONE_AXE, 2)
                .add(STONE_SWORD, 2)
                .add(HEALTH_POTION,1)
                .add(IRON_INGOTS, 1)
        ;
        LOOT_POOL_REGISTRY.add(COMMON_LOOT_POOL);

        MEDIUM_LOOT_POOL
                .add(HEALTH_POTION,15)
                .add(IRON_INGOTS, 12)
                .add(LAPIS_LAZULI, 2)
                .add(DIAMOND, 1)
                .add(BLAZE_ROD, 1)
                .add(ENDER_PEARL, 1)

        ;
        LOOT_POOL_REGISTRY.add(MEDIUM_LOOT_POOL);

        RARE_LOOT_POOL
                .add(LAPIS_LAZULI, 2)
                .add(BOTTLES_O_ENCHANTING, 2)
                .add(DIAMOND, 1)
                .add(EMERALD, 1)
                .add(BLAZE_ROD, 1)
                .add(ENDER_PEARL, 1)

        ;
        LOOT_POOL_REGISTRY.add(RARE_LOOT_POOL);
    }
}
