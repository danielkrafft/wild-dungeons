package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.ItemTemplate;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;

public class ItemTemplateRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<ItemTemplate> LOOT_ENTRY_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<ItemTemplate> lootEntries = new ArrayList<>();

    public static ItemTemplate ARROWS = create("arrows_8", Items.ARROW, 8).setDeviance(1.5f);
    public static ItemTemplate COOKED_BEEF = create("beef_8", Items.COOKED_BEEF, 8).setDeviance(1.5f);
    public static ItemTemplate BAKED_POTATOES = create("baked_potato_24", Items.BAKED_POTATO, 24).setDeviance(1.5f);
    public static ItemTemplate SEEDS = create("seeds_8", Items.WHEAT_SEEDS, 8).setDeviance(1.5f);
    public static ItemTemplate MELONS = create("melon_slice_8", Items.MELON_SLICE, 8).setDeviance(1.5f);
    public static ItemTemplate LEATHER = create("leather_6", Items.LEATHER, 6).setDeviance(1.5f);
    public static ItemTemplate COAL = create("coal_8", Items.COAL, 8).setDeviance(1.5f);
    public static ItemTemplate CHARCOAL = create("charcoal_8", Items.CHARCOAL, 8).setDeviance(1.5f);
    public static ItemTemplate OAK_LOGS = create("oak_logs_10", Items.OAK_LOG, 10).setDeviance(1.5f);
    public static ItemTemplate GUNPOWDER = create("gunpowder_8", Items.GUNPOWDER, 8).setDeviance(1.5f);
    public static ItemTemplate STONE_SHOVEL = create("stone_shovel", Items.STONE_SHOVEL, 1);
    public static ItemTemplate STONE_AXE = create("stone_axe", Items.STONE_AXE, 1);
    public static ItemTemplate STONE_PICKAXE = create("stone_pickaxe", Items.STONE_PICKAXE, 1);
    public static ItemTemplate STONE_SWORD = create("stone_sword", Items.STONE_SWORD, 1);
    public static ItemTemplate INK_SACS = create("ink_sacs_8", Items.INK_SAC, 8).setDeviance(1.5f);
    public static ItemTemplate IRON_INGOTS = create("iron_ingots_8", Items.IRON_INGOT, 8).setDeviance(1.5f);
    public static ItemTemplate GOLD_INGOTS = create("gold_ingots_6", Items.GOLD_INGOT, 6).setDeviance(1.5f);
    public static ItemTemplate LAPIS_LAZULI = create("lapis_lazuli_10", Items.LAPIS_LAZULI, 10).setDeviance(1.5f);
    public static ItemTemplate BOTTLES_O_ENCHANTING = create("experience_bottles_5", Items.EXPERIENCE_BOTTLE, 5).setDeviance(1.5f);
    public static ItemTemplate DIAMOND = create("diamonds_1", Items.DIAMOND, 1);
    public static ItemTemplate EMERALD = create("emeralds_24", Items.EMERALD, 24);
    public static ItemTemplate BLAZE_ROD = create("blaze_rod_8", Items.BLAZE_ROD, 8);
    public static ItemTemplate ENDER_PEARL = create("ender_pearl_8", Items.ENDER_PEARL, 8);
    public static ItemTemplate HEALTH_POTION = create("health_potion", Potions.HEALING);
    public static ItemTemplate INVISIBILITY_POTION = create("invisibility_potion", Potions.INVISIBILITY);
    public static ItemTemplate NIGHT_VISION_POTION = create("night_vision_potion", Potions.NIGHT_VISION);
    public static ItemTemplate SWIFTNESS_POTION = create("swiftness_potion", Potions.SWIFTNESS);
    public static ItemTemplate WATER_BREATHING_POTION = create("water_breathing_potion", Potions.WATER_BREATHING);
    public static ItemTemplate FIRE_RESISTANCE_POTION = create("fire_resistance_potion", Potions.FIRE_RESISTANCE);
    public static ItemTemplate LEAPING_POTION = create("leaping_potion", Potions.LEAPING);
    public static ItemTemplate STRENGTH_POTION = create("strength_potion", Potions.STRENGTH);
    public static ItemTemplate HARMING_POTION_SPLASH = create("harming_potion_splash", Potions.HARMING).setSplashPotion();
    public static ItemTemplate POISON_POTION_SPLASH = create("poison_potion_splash", Potions.POISON).setSplashPotion();
    public static ItemTemplate REGENERATION_POTION_SPLASH = create("regeneration_potion_splash", Potions.REGENERATION).setSplashPotion();
    public static ItemTemplate IRON_RAW = create("iron_ore_8", Items.RAW_IRON, 8).setDeviance(1.5f);
    public static ItemTemplate GOLD_RAW = create("gold_ore_8", Items.RAW_GOLD, 8).setDeviance(1.5f);
    public static ItemTemplate REDSTONE = create("redstone_8", Items.REDSTONE, 8).setDeviance(1.5f);
    public static ItemTemplate GLOWSTONE_DUST = create("glowstone_dust_8", Items.GLOWSTONE_DUST, 8).setDeviance(1.5f);
    public static ItemTemplate QUARTZ = create("quartz_8", Items.QUARTZ, 8).setDeviance(1.5f);
    public static ItemTemplate NETHER_WART = create("nether_wart_8", Items.NETHER_WART, 8).setDeviance(1.5f);
    public static ItemTemplate GHAST_TEAR = create("ghast_tear_3", Items.GHAST_TEAR, 3);
    public static ItemTemplate BLAZE_POWDER = create("blaze_powder_8", Items.BLAZE_POWDER, 8).setDeviance(1.5f);
    public static ItemTemplate MAGMA_CREAM = create("magma_cream_8", Items.MAGMA_CREAM, 8).setDeviance(1.5f);
    public static ItemTemplate SLIME_BALL = create("slime_ball_8", Items.SLIME_BALL, 8).setDeviance(1.5f);
    public static ItemTemplate SPIDER_EYE = create("spider_eye_8", Items.SPIDER_EYE, 8).setDeviance(1.5f);
    public static ItemTemplate RABBIT_FOOT = create("rabbit_foot_2", Items.RABBIT_FOOT, 2);

    public static ItemTemplate AMOGUS_STAFF = create("amogus_staff", WDItems.AMOGUS_STAFF.get(), 1);
    public static ItemTemplate MEATHOOK = create("meathook", WDItems.MEATHOOK_ITEM.get(), 1);

    public static ItemTemplate DUNGEON_KEY = create("dungeon_key", WDItems.WD_DUNGEON_KEY.get(), 1);

    public static ItemTemplate create(String name, Item item, int count) {
        ItemTemplate itemTemplate = new ItemTemplate(name, item, count);
        lootEntries.add(itemTemplate);
        return itemTemplate;
    }

    public static ItemTemplate create(String name, Holder<Potion> potion) {
        ItemTemplate itemTemplate = new ItemTemplate(name, potion);
        lootEntries.add(itemTemplate);
        return itemTemplate;
    }

    public static void setupLootEntries() {
        lootEntries.forEach(ItemTemplateRegistry::add);
    }

    public static void add(ItemTemplate ItemTemplate) {
        LOOT_ENTRY_REGISTRY.add(ItemTemplate);
    }

    //probably never needed, because now we declare the loot entries as static fields
    public static ItemTemplate get(String name) {
        return LOOT_ENTRY_REGISTRY.get(name);
    }
}