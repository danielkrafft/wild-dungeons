package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.ItemTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;

public class ItemTemplateRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<ItemTemplate> LOOT_ENTRY_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static ItemTemplate ARROWS = new ItemTemplate("arrows_8", Items.ARROW, 8).setDeviance(1.5f);
    public static ItemTemplate COOKED_BEEF = new ItemTemplate("beef_8", Items.COOKED_BEEF, 8).setDeviance(1.5f);
    public static ItemTemplate BAKED_POTATOES = new ItemTemplate("baked_potato_24", Items.BAKED_POTATO, 24).setDeviance(1.5f);
    public static ItemTemplate SEEDS = new ItemTemplate("seeds_8", Items.WHEAT_SEEDS, 8).setDeviance(1.5f);
    public static ItemTemplate MELONS = new ItemTemplate("melon_slice_8", Items.MELON_SLICE, 8).setDeviance(1.5f);
    public static ItemTemplate LEATHER = new ItemTemplate("leather_6", Items.LEATHER, 6).setDeviance(1.5f);
    public static ItemTemplate COAL = new ItemTemplate("coal_8", Items.COAL, 8).setDeviance(1.5f);
    public static ItemTemplate CHARCOAL = new ItemTemplate("charcoal_8", Items.CHARCOAL, 8).setDeviance(1.5f);
    public static ItemTemplate OAK_LOGS = new ItemTemplate("oak_logs_10", Items.OAK_LOG, 10).setDeviance(1.5f);
    public static ItemTemplate GUNPOWDER = new ItemTemplate("gunpowder_8", Items.GUNPOWDER, 8).setDeviance(1.5f);
    public static ItemTemplate STONE_SHOVEL = new ItemTemplate("stone_shovel", Items.STONE_SHOVEL, 1);
    public static ItemTemplate STONE_AXE = new ItemTemplate("stone_axe", Items.STONE_AXE, 1);
    public static ItemTemplate STONE_PICKAXE = new ItemTemplate("stone_pickaxe", Items.STONE_PICKAXE, 1);
    public static ItemTemplate STONE_SWORD = new ItemTemplate("stone_sword", Items.STONE_SWORD, 1);
    public static ItemTemplate INK_SACS = new ItemTemplate("ink_sacs_8", Items.INK_SAC, 8).setDeviance(1.5f);
    public static ItemTemplate IRON_INGOTS = new ItemTemplate("iron_ingots_8", Items.IRON_INGOT, 8).setDeviance(1.5f);
    public static ItemTemplate GOLD_INGOTS = new ItemTemplate("gold_ingots_6", Items.GOLD_INGOT, 6).setDeviance(1.5f);
    public static ItemTemplate LAPIS_LAZULI = new ItemTemplate("lapis_lazuli_10", Items.LAPIS_LAZULI, 10).setDeviance(1.5f);
    public static ItemTemplate BOTTLES_O_ENCHANTING = new ItemTemplate("experience_bottles_5", Items.EXPERIENCE_BOTTLE, 5).setDeviance(1.5f);
    public static ItemTemplate DIAMOND = new ItemTemplate("diamonds_1", Items.DIAMOND, 1);
    public static ItemTemplate EMERALD = new ItemTemplate("emeralds_24", Items.EMERALD, 24);
    public static ItemTemplate BLAZE_ROD = new ItemTemplate("blaze_rod_8", Items.BLAZE_ROD, 8);
    public static ItemTemplate ENDER_PEARL = new ItemTemplate("ender_pearl_8", Items.ENDER_PEARL, 8);
    public static ItemTemplate HEALTH_POTION = new ItemTemplate("health_potion", Potions.HEALING);
    public static ItemTemplate INVISIBILITY_POTION = new ItemTemplate("invisibility_potion", Potions.INVISIBILITY);
    public static ItemTemplate NIGHT_VISION_POTION = new ItemTemplate("night_vision_potion", Potions.NIGHT_VISION);
    public static ItemTemplate SWIFTNESS_POTION = new ItemTemplate("swiftness_potion", Potions.SWIFTNESS);
    public static ItemTemplate WATER_BREATHING_POTION = new ItemTemplate("water_breathing_potion", Potions.WATER_BREATHING);
    public static ItemTemplate FIRE_RESISTANCE_POTION = new ItemTemplate("fire_resistance_potion", Potions.FIRE_RESISTANCE);
    public static ItemTemplate LEAPING_POTION = new ItemTemplate("leaping_potion", Potions.LEAPING);
    public static ItemTemplate STRENGTH_POTION = new ItemTemplate("strength_potion", Potions.STRENGTH);
    public static ItemTemplate HARMING_POTION_SPLASH = new ItemTemplate("harming_potion_splash", Potions.HARMING).setSplashPotion();
    public static ItemTemplate POISON_POTION_SPLASH = new ItemTemplate("poison_potion_splash", Potions.POISON).setSplashPotion();
    public static ItemTemplate REGENERATION_POTION_SPLASH = new ItemTemplate("regeneration_potion_splash", Potions.REGENERATION).setSplashPotion();

    public static ItemTemplate IRON_RAW = new ItemTemplate("iron_ore_8", Items.RAW_IRON, 8).setDeviance(1.5f);
    public static ItemTemplate GOLD_RAW = new ItemTemplate("gold_ore_8", Items.RAW_GOLD, 8).setDeviance(1.5f);
    public static ItemTemplate REDSTONE = new ItemTemplate("redstone_8", Items.REDSTONE, 8).setDeviance(1.5f);
    public static ItemTemplate GLOWSTONE_DUST = new ItemTemplate("glowstone_dust_8", Items.GLOWSTONE_DUST, 8).setDeviance(1.5f);
    public static ItemTemplate QUARTZ = new ItemTemplate("quartz_8", Items.QUARTZ, 8).setDeviance(1.5f);
    public static ItemTemplate NETHER_WART = new ItemTemplate("nether_wart_8", Items.NETHER_WART, 8).setDeviance(1.5f);
    public static ItemTemplate GHAST_TEAR = new ItemTemplate("ghast_tear_3", Items.GHAST_TEAR, 3);
    public static ItemTemplate BLAZE_POWDER = new ItemTemplate("blaze_powder_8", Items.BLAZE_POWDER, 8).setDeviance(1.5f);
    public static ItemTemplate MAGMA_CREAM = new ItemTemplate("magma_cream_8", Items.MAGMA_CREAM, 8).setDeviance(1.5f);
    public static ItemTemplate SLIME_BALL = new ItemTemplate("slime_ball_8", Items.SLIME_BALL, 8).setDeviance(1.5f);
    public static ItemTemplate SPIDER_EYE = new ItemTemplate("spider_eye_8", Items.SPIDER_EYE, 8).setDeviance(1.5f);
    public static ItemTemplate RABBIT_FOOT = new ItemTemplate("rabbit_foot_2", Items.RABBIT_FOOT, 2);




    public static void setupLootEntries(){
        add(ARROWS);
        add(COOKED_BEEF);
        add(SEEDS);
        add(MELONS);
        add(LEATHER);
        add(COAL);
        add(CHARCOAL);
        add(OAK_LOGS);
        add(GUNPOWDER);
        add(STONE_SHOVEL);
        add(STONE_AXE);
        add(STONE_PICKAXE);
        add(STONE_SWORD);
        add(INK_SACS);
        add(IRON_INGOTS);
        add(GOLD_INGOTS);
        add(LAPIS_LAZULI);
        add(BOTTLES_O_ENCHANTING);
        add(DIAMOND);
        add(EMERALD);
        add(BLAZE_ROD);
        add(ENDER_PEARL);
        add(IRON_RAW);
        add(GOLD_RAW);
        add(REDSTONE);
        add(GLOWSTONE_DUST);
        add(QUARTZ);
        add(NETHER_WART);
        add(GHAST_TEAR);
        add(BLAZE_POWDER);
        add(MAGMA_CREAM);
        add(SLIME_BALL);
        add(SPIDER_EYE);
        add(RABBIT_FOOT);
        add(HEALTH_POTION);
        add(INVISIBILITY_POTION);
        add(NIGHT_VISION_POTION);
        add(SWIFTNESS_POTION);
        add(WATER_BREATHING_POTION);
        add(FIRE_RESISTANCE_POTION);
        add(LEAPING_POTION);
        add(STRENGTH_POTION);
        add(HARMING_POTION_SPLASH);
        add(POISON_POTION_SPLASH);
        add(REGENERATION_POTION_SPLASH);

    }


    public static void add(ItemTemplate ItemTemplate) {
        LOOT_ENTRY_REGISTRY.add(ItemTemplate);
    }

    //probably never needed, because now we declare the loot entries as static fields
    public static ItemTemplate get(String name) {
        return LOOT_ENTRY_REGISTRY.get(name);
    }
}
