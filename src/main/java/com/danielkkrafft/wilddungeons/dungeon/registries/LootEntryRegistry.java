package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.LootEntry;
import net.minecraft.world.item.Items;

public class LootEntryRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<LootEntry> LOOT_ENTRY_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static LootEntry ARROWS = new LootEntry("ARROWS", Items.ARROW, 8, 1.5f);
    public static LootEntry SEEDS = new LootEntry("SEEDS", Items.WHEAT_SEEDS, 8, 1.5f);
    public static LootEntry MELONS = new LootEntry("MELONS", Items.MELON_SLICE, 8, 1.5f);
    public static LootEntry LEATHER = new LootEntry("LEATHER", Items.LEATHER, 6, 1.5f);
    public static LootEntry COAL = new LootEntry("COAL", Items.COAL, 8, 1.5f);
    public static LootEntry CHARCOAL = new LootEntry("CHARCOAL", Items.CHARCOAL, 8, 1.5f);
    public static LootEntry OAK_LOGS = new LootEntry("OAK_LOGS", Items.OAK_LOG, 10, 1.5f);
    public static LootEntry GUNPOWDER = new LootEntry("GUNPOWDER", Items.GUNPOWDER, 8, 1.5f);
    public static LootEntry STONE_SHOVEL = new LootEntry("STONE_SHOVEL", Items.STONE_SHOVEL, 1, 1f);
    public static LootEntry STONE_AXE = new LootEntry("STONE_AXE", Items.STONE_AXE, 1, 1f);
    public static LootEntry STONE_PICKAXE = new LootEntry("STONE_PICKAXE", Items.STONE_PICKAXE, 1, 1f);
    public static LootEntry INK_SACS = new LootEntry("INK_SACS", Items.INK_SAC, 8, 1f);
    public static LootEntry IRON_INGOTS = new LootEntry("IRON_INGOTS", Items.IRON_INGOT, 8, 1.5f);
    public static LootEntry LAPIS_LAZULI = new LootEntry("LAPIS_LAZULI", Items.LAPIS_LAZULI, 10, 1.5f);
    public static LootEntry BOTTLES_O_ENCHANTING = new LootEntry("BOTTLES_O_ENCHANTING", Items.EXPERIENCE_BOTTLE, 5, 1.5f);
    public static LootEntry DIAMOND = new LootEntry("DIAMOND", Items.DIAMOND, 1, 1f);



    public static void setupLootEntries(){
        add(ARROWS);
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
    }


    public static void add(LootEntry lootEntry) {
        LOOT_ENTRY_REGISTRY.add(lootEntry);
    }

    //probably never needed, because now we declare the loot entries as static fields
    public static LootEntry get(String name) {
        return LOOT_ENTRY_REGISTRY.get(name);
    }
}
