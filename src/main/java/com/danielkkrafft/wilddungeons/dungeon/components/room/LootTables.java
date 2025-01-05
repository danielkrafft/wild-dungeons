package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LootTables {

    public record LootEntry(Item item, int count, float deviance) {
        public ItemStack asItemStack() {
            return new ItemStack(item, RandomUtil.randIntBetween((int) (count / deviance), (int) (count * deviance)));
        }
    }

    public static final LootEntry ARROWS = new LootEntry(Items.ARROW, 8, 1.5f);
    public static final LootEntry SEEDS = new LootEntry(Items.WHEAT_SEEDS, 8, 1.5f);
    public static final LootEntry MELONS = new LootEntry(Items.MELON_SLICE, 8, 1.5f);
    public static final LootEntry LEATHER = new LootEntry(Items.LEATHER, 6, 1.5f);
    public static final LootEntry COAL = new LootEntry(Items.COAL, 3, 1.5f);
    public static final LootEntry CHARCOAL = new LootEntry(Items.CHARCOAL, 5, 1.5f);
    public static final LootEntry OAK_LOGS = new LootEntry(Items.OAK_LOG, 10, 1.5f);
    public static final LootEntry GUNPOWDER = new LootEntry(Items.GUNPOWDER, 8, 1.5f);
    public static final LootEntry STONE_SHOVEL = new LootEntry(Items.STONE_SHOVEL, 1, 1f);
    public static final LootEntry STONE_AXE = new LootEntry(Items.STONE_AXE, 1, 1f);
    public static final LootEntry STONE_PICKAXE = new LootEntry(Items.STONE_PICKAXE, 1, 1f);
    public static final LootEntry INK_SACS = new LootEntry(Items.INK_SAC, 8, 1f);
    public static final LootEntry IRON_INGOTS = new LootEntry(Items.IRON_INGOT, 6, 1.5f);
    public static final LootEntry LAPIS_LAZULI = new LootEntry(Items.LAPIS_LAZULI, 10, 1.5f);
    public static final LootEntry BOTTLES_O_ENCHANTING = new LootEntry(Items.EXPERIENCE_BOTTLE, 5, 1.5f);
    public static final LootEntry DIAMOND = new LootEntry(Items.DIAMOND, 1, 1f);

    public static final WeightedPool<LootEntry> COMMON_LOOT_POOL = new WeightedPool<LootEntry>()
            .add(ARROWS, 1).add(SEEDS, 1).add(MELONS, 1).add(LEATHER, 1).add(COAL, 1);

    public static final WeightedPool<LootEntry> MEDIUM_LOOT_POOL = new WeightedPool<LootEntry>()
            .add(CHARCOAL, 1).add(OAK_LOGS, 1).add(GUNPOWDER, 1).add(STONE_SHOVEL, 1).add(STONE_AXE, 1).add(STONE_PICKAXE, 1);

    public static final WeightedPool<LootEntry> RARE_LOOT_POOL = new WeightedPool<LootEntry>()
            .add(INK_SACS, 2).add(IRON_INGOTS, 2).add(LAPIS_LAZULI, 2).add(BOTTLES_O_ENCHANTING, 2).add(DIAMOND, 1);

    public static final WeightedTable<LootEntry> BASIC_LOOT_TABLE = new WeightedTable<LootEntry>()
            .add(COMMON_LOOT_POOL, 1).add(MEDIUM_LOOT_POOL, 5).add(RARE_LOOT_POOL, 10);


}
