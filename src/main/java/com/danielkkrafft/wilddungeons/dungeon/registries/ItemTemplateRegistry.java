package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.ItemTemplate;
import net.minecraft.world.item.Items;

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
    public static ItemTemplate INK_SACS = new ItemTemplate("ink_sacs_8", Items.INK_SAC, 8).setDeviance(1.5f);
    public static ItemTemplate IRON_INGOTS = new ItemTemplate("iron_ingots_8", Items.IRON_INGOT, 8).setDeviance(1.5f);
    public static ItemTemplate LAPIS_LAZULI = new ItemTemplate("lapis_lazuli_10", Items.LAPIS_LAZULI, 10).setDeviance(1.5f);
    public static ItemTemplate BOTTLES_O_ENCHANTING = new ItemTemplate("experience_bottles_5", Items.EXPERIENCE_BOTTLE, 5).setDeviance(1.5f);
    public static ItemTemplate DIAMOND = new ItemTemplate("diamonds_1", Items.DIAMOND, 1);
    public static ItemTemplate EMERALD = new ItemTemplate("emeralds_24", Items.EMERALD, 24);
    public static ItemTemplate BLAZE_ROD = new ItemTemplate("blaze_rod_8", Items.BLAZE_ROD, 8);
    public static ItemTemplate ENDER_PEARL = new ItemTemplate("ender_pearl_8", Items.ENDER_PEARL, 8);



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
        add(INK_SACS);
        add(IRON_INGOTS);
        add(LAPIS_LAZULI);
        add(BOTTLES_O_ENCHANTING);
        add(DIAMOND);
        add(EMERALD);
        add(BLAZE_ROD);
        add(ENDER_PEARL);
    }


    public static void add(ItemTemplate ItemTemplate) {
        LOOT_ENTRY_REGISTRY.add(ItemTemplate);
    }

    //probably never needed, because now we declare the loot entries as static fields
    public static ItemTemplate get(String name) {
        return LOOT_ENTRY_REGISTRY.get(name);
    }
}
