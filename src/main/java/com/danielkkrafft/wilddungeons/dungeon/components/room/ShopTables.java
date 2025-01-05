package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.DungeonPerks;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ShopTables {

    public static final Offering.OfferingTemplate ARROWS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 32, Item.getId(Items.ARROW), Offering.CostType.XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate STEAKS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 8, Item.getId(Items.COOKED_BEEF), Offering.CostType.XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate BAKED_POTATOES = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 24, Item.getId(Items.BAKED_POTATO), Offering.CostType.XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate IRON_INGOTS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 4, Item.getId(Items.IRON_INGOT), Offering.CostType.XP_LEVEL, 4, 1.5f);


    public static final Offering.OfferingTemplate EMERALDS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 24, Item.getId(Items.EMERALD), Offering.CostType.XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate BLAZE_RODS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 8, Item.getId(Items.BLAZE_ROD), Offering.CostType.NETHER_XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate ENDER_PEARLS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 8, Item.getId(Items.ENDER_PEARL), Offering.CostType.END_XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate COAL = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 48, Item.getId(Items.COAL), Offering.CostType.XP_LEVEL, 4, 1.5f);


    public static final Offering.OfferingTemplate SWORD_DAMAGE_INCREASE_NORMAL = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.SWORD_DAMAGE_INCREASE.getIndex(), Offering.CostType.XP_LEVEL, 15, 1.5f);

    public static final Offering.OfferingTemplate SWORD_DAMAGE_INCREASE_NETHER = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.SWORD_DAMAGE_INCREASE.getIndex(), Offering.CostType.NETHER_XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate SWORD_DAMAGE_INCREASE_END = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.SWORD_DAMAGE_INCREASE.getIndex(), Offering.CostType.END_XP_LEVEL, 8, 1.5f);


    public static final Offering.OfferingTemplate AXE_DAMAGE_INCREASE_NORMAL = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.AXE_DAMAGE_INCREASE.getIndex(), Offering.CostType.XP_LEVEL, 15, 1.5f);

    public static final Offering.OfferingTemplate AXE_DAMAGE_INCREASE_NETHER = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.AXE_DAMAGE_INCREASE.getIndex(), Offering.CostType.NETHER_XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate AXE_DAMAGE_INCREASE_END = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.AXE_DAMAGE_INCREASE.getIndex(), Offering.CostType.END_XP_LEVEL, 8, 1.5f);


    public static final Offering.OfferingTemplate BOW_DAMAGE_INCREASE_NORMAL = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.BOW_DAMAGE_INCREASE.getIndex(), Offering.CostType.XP_LEVEL, 15, 1.5f);

    public static final Offering.OfferingTemplate BOW_DAMAGE_INCREASE_NETHER = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.BOW_DAMAGE_INCREASE.getIndex(), Offering.CostType.NETHER_XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate BOW_DAMAGE_INCREASE_END = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, DungeonPerks.Perks.BOW_DAMAGE_INCREASE.getIndex(), Offering.CostType.END_XP_LEVEL, 8, 1.5f);




    public static final WeightedPool<Offering.OfferingTemplate> CHEAP_BASIC_POOL = new WeightedPool<Offering.OfferingTemplate>()
            .add(ARROWS, 1).add(STEAKS, 1).add(BAKED_POTATOES, 1).add(IRON_INGOTS, 1);

    public static final WeightedPool<Offering.OfferingTemplate> MEDIUM_BASIC_POOL = new WeightedPool<Offering.OfferingTemplate>()
            .add(EMERALDS, 1).add(BLAZE_RODS, 1).add(ENDER_PEARLS, 1).add(COAL, 1);

    public static final WeightedPool<Offering.OfferingTemplate> EXPENSIVE_BASIC_POOL = new WeightedPool<Offering.OfferingTemplate>()
            .add(SWORD_DAMAGE_INCREASE_NORMAL, 1).add(SWORD_DAMAGE_INCREASE_NETHER, 1).add(SWORD_DAMAGE_INCREASE_END, 1)
            .add(AXE_DAMAGE_INCREASE_NORMAL, 1).add(AXE_DAMAGE_INCREASE_NETHER, 1).add(AXE_DAMAGE_INCREASE_END, 1)
            .add(BOW_DAMAGE_INCREASE_NORMAL, 1).add(BOW_DAMAGE_INCREASE_NETHER, 1).add(BOW_DAMAGE_INCREASE_END, 1);

    public static final WeightedTable<Offering.OfferingTemplate> BASIC_TABLE = new WeightedTable<Offering.OfferingTemplate>()
            .add(CHEAP_BASIC_POOL, 1).add(MEDIUM_BASIC_POOL, 5).add(EXPENSIVE_BASIC_POOL, 10);

}
