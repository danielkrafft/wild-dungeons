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
                .add(ARROWS, 25)
                .add(SEEDS, 15)
                .add(MELONS, 15)
                .add(LEATHER, 15)
                .add(INK_SACS, 15)
                .add(COAL, 15)
                .add(CHARCOAL, 15)
                .add(OAK_LOGS, 15)
                .add(GUNPOWDER, 10)
                .add(STONE_SHOVEL, 5)
                .add(STONE_PICKAXE, 5)
                .add(STONE_AXE, 5)
                .add(STONE_SWORD, 5)
                .add(HEALTH_POTION,1)
                .add(IRON_RAW,2)
                .add(GOLD_RAW,2)
                .add(REDSTONE,2)
                .add(GLOWSTONE_DUST,2)
                .add(QUARTZ,2)
                .add(NETHER_WART,2)
                .add(SLIME_BALL,1)
                .add(SPIDER_EYE,5)
                .add(RABBIT_FOOT,2)
                .add(BLAZE_POWDER,1)
                .add(MAGMA_CREAM,1)
                .add(LEAPING_POTION,1)
                .add(STRENGTH_POTION,1)
        ;
        LOOT_POOL_REGISTRY.add(COMMON_LOOT_POOL);

        MEDIUM_LOOT_POOL
                .add(HEALTH_POTION,15)
                .add(INVISIBILITY_POTION, 5)
                .add(NIGHT_VISION_POTION, 5)
                .add(SWIFTNESS_POTION, 5)
                .add(WATER_BREATHING_POTION, 5)
                .add(FIRE_RESISTANCE_POTION, 5)
                .add(LEAPING_POTION, 5)
                .add(STRENGTH_POTION, 5)
                .add(HARMING_POTION_SPLASH, 5)
                .add(POISON_POTION_SPLASH, 5)
                .add(REGENERATION_POTION_SPLASH, 5)
                .add(IRON_RAW, 3)
                .add(GOLD_RAW, 3)
                .add(REDSTONE, 3)
                .add(GLOWSTONE_DUST, 3)
                .add(QUARTZ, 3)
                .add(GHAST_TEAR, 3)
                .add(BLAZE_POWDER, 1)
                .add(MAGMA_CREAM, 1)
                .add(SLIME_BALL, 1)
                .add(IRON_INGOTS, 15)
                .add(GOLD_INGOTS, 15)
                .add(LAPIS_LAZULI, 2)
                .add(DIAMOND, 1)
                .add(BLAZE_ROD, 1)
                .add(ENDER_PEARL, 1)
                .add(EMERALD, 1)


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
