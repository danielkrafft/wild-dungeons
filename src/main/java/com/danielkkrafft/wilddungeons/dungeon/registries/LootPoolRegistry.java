package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;

import static com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.ItemTemplate;
import static com.danielkkrafft.wilddungeons.dungeon.registries.ItemTemplateRegistry.*;

public class LootPoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<ItemTemplate>> LOOT_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedPool<ItemTemplate> COMMON_LOOT_POOL = create("COMMON_LOOT_POOL")
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
            .add(CYAN_WOOL, 3)
            .add(PAPER, 3)
            .add(PUFFERFISH, 3)
            .add(MUSHROOM_STEW, 3)
            .add(WOODEN_AXE, 3)
            .add(PRISMARINE_BRICKS, 3)
            .add(LEATHER_BOOTS, 3)
            .add(EGG, 3)
            .add(GREEN_DYE, 3);
    public static final WeightedPool<ItemTemplate> MEDIUM_LOOT_POOL = create("MEDIUM_LOOT_POOL")
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
            .add(END_STONE, 3)
            .add(IRON_BOOTS, 3)
            .add(IRON_PICKAXE, 3)
            .add(SMOOTH_QUARTZ, 3)
            .add(BLAZE_ROD, 1)
            .add(ENDER_PEARL, 1)
            .add(EMERALD, 1)
            .add(LEATHER_HORSE_ARMOR, 3)
            .add(BLANK_MAP, 1)
            .add(SHROOMLIGHT, 3);
    public static final WeightedPool<ItemTemplate> RARE_LOOT_POOL = create("RARE_LOOT_POOL")
            .add(LAPIS_LAZULI, 8)
            .add(BOTTLES_O_ENCHANTING, 8)
            .add(DIAMOND, 4)
            .add(EMERALD, 4)
            .add(BLAZE_ROD, 4)
            .add(ENDER_PEARL, 4)
            .add(OVERFLOW_MUSIC_DISC, 1)
            .add(OVERFLOW_SAFE_MUSIC_DISC, 1)
            .add(OVERFLOW_UNDERWATER_MUSIC_DISC, 1)
            .add(OVERFLOW_UNDERWATER_SAFE_MUSIC_DISC, 1)
            .add(ANGEL_INVESTOR_MUSIC_DISC, 1)
            .add(ANGEL_INVESTOR_SAFE_MUSIC_DISC, 1)
            .add(THE_CAPITAL_MUSIC_DISC, 1)
            .add(THE_CAPITAL_SAFE_MUSIC_DISC, 1);

    public static final WeightedPool<ItemTemplate> EPIC_LOOT_POOL = create("EPIC_LOOT_POOL")
            .add(DIAMOND_AXE, 1)
            .add(NETHER_STAR, 1)
            .add(PINK_SHULKER_BOX, 1)
            .add(ELYTRA, 1);

    public static final WeightedPool<ItemTemplate> METRO_COMMON_POOL = copyOf(COMMON_LOOT_POOL,"METRO_COMMON_LOOT_POOL")
            .add(EMERALD,45);
    public static final WeightedPool<ItemTemplate> METRO_MEDIUM_POOL = copyOf(MEDIUM_LOOT_POOL,"METRO_MEDIUM_LOOT_POOL")
            .add(EMERALD,30);
    public static final WeightedPool<ItemTemplate> METRO_RARE_POOL = copyOf(RARE_LOOT_POOL,"METRO_RARE_LOOT_POOL")
            .add(EMERALD,20);
    public static final WeightedPool<ItemTemplate> METRO_EPIC_POOL = copyOf(EPIC_LOOT_POOL,"METRO_EPIC_LOOT_POOL")
            .add(EMERALD,1);

    public static final WeightedPool<ItemTemplate> VILLAGE_SEWER_COMMON_POOL = copyOf(COMMON_LOOT_POOL,"VILLAGE_SEWER_COMMON_POOL")
            .add(WATER_BREATHING_POTION, 15);
    public static final WeightedPool<ItemTemplate> VILLAGE_SEWER_MEDIUM_POOL = copyOf(MEDIUM_LOOT_POOL,"VILLAGE_SEWER_MEDIUM_POOL")
            .add(WATER_BREATHING_POTION, 5);
    public static final WeightedPool<ItemTemplate> VILLAGE_SEWER_RARE_POOL = copyOf(RARE_LOOT_POOL,"VILLAGE_SEWER_RARE_POOL")
            .add(WATER_BREATHING_POTION, 5);
    public static final WeightedPool<ItemTemplate> VILLAGE_SEWER_EPIC_POOL = copyOf(EPIC_LOOT_POOL,"VILLAGE_SEWER_EPIC_POOL")
            .add(WATER_BREATHING_POTION, 5);

    public static WeightedPool<ItemTemplate> create(String name) {
        WeightedPool<ItemTemplate> pool = new WeightedPool<ItemTemplate>().setName(name);
        LOOT_POOL_REGISTRY.add(pool);
        return pool;
    }

    public static WeightedPool<ItemTemplate> copyOf(WeightedPool<ItemTemplate> pool, String name) {
        WeightedPool<ItemTemplate> newPool = new WeightedPool<ItemTemplate>().setName(name);
        for (Pair<ItemTemplate, Integer> entry : pool.getAllWithWeights()) {
            newPool.add(entry.getFirst(), entry.getSecond());
        }
        return newPool;
    }
}
