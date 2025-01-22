package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ShopTables {

    public static final Offering.OfferingTemplate ARROWS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 32, "arrow", Offering.CostType.XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate STEAKS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 8, "cooked_beef", Offering.CostType.XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate BAKED_POTATOES = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 24, "baked_potato", Offering.CostType.XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate IRON_INGOTS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 4, "iron_ingot", Offering.CostType.XP_LEVEL, 4, 1.5f);


    public static final Offering.OfferingTemplate EMERALDS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 24, "emerald", Offering.CostType.XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate BLAZE_RODS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 8, "blaze_rod", Offering.CostType.NETHER_XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate ENDER_PEARLS = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 8, "ender_pearl", Offering.CostType.END_XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate COAL = new Offering.OfferingTemplate(
            Offering.Type.ITEM, 48, "coal", Offering.CostType.XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate EXTRA_LIFE_NORMAL = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "EXTRA_LIFE", Offering.CostType.XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate EXTRA_LIFE_NETHER = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "EXTRA_LIFE", Offering.CostType.NETHER_XP_LEVEL, 4, 1.5f);

    public static final Offering.OfferingTemplate EXTRA_LIFE_END = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "EXTRA_LIFE", Offering.CostType.END_XP_LEVEL, 4, 1.5f);


    public static final Offering.OfferingTemplate SWORD_DAMAGE_INCREASE_NORMAL = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "SWORD_DAMAGE_INCREASE", Offering.CostType.XP_LEVEL, 15, 1.5f);

    public static final Offering.OfferingTemplate SWORD_DAMAGE_INCREASE_NETHER = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "SWORD_DAMAGE_INCREASE", Offering.CostType.NETHER_XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate SWORD_DAMAGE_INCREASE_END = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "SWORD_DAMAGE_INCREASE", Offering.CostType.END_XP_LEVEL, 8, 1.5f);


    public static final Offering.OfferingTemplate AXE_DAMAGE_INCREASE_NORMAL = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "AXE_DAMAGE_INCREASE", Offering.CostType.XP_LEVEL, 15, 1.5f);

    public static final Offering.OfferingTemplate AXE_DAMAGE_INCREASE_NETHER = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "AXE_DAMAGE_INCREASE", Offering.CostType.NETHER_XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate AXE_DAMAGE_INCREASE_END = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "AXE_DAMAGE_INCREASE", Offering.CostType.END_XP_LEVEL, 8, 1.5f);


    public static final Offering.OfferingTemplate BOW_DAMAGE_INCREASE_NORMAL = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "BOW_DAMAGE_INCREASE", Offering.CostType.XP_LEVEL, 15, 1.5f);

    public static final Offering.OfferingTemplate BOW_DAMAGE_INCREASE_NETHER = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "BOW_DAMAGE_INCREASE", Offering.CostType.NETHER_XP_LEVEL, 8, 1.5f);

    public static final Offering.OfferingTemplate BOW_DAMAGE_INCREASE_END = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "BOW_DAMAGE_INCREASE", Offering.CostType.END_XP_LEVEL, 8, 1.5f);




    public static final WeightedPool<Offering.OfferingTemplate> CHEAP_BASIC_POOL = new WeightedPool<Offering.OfferingTemplate>()
            .add(ARROWS, 1).add(STEAKS, 1).add(BAKED_POTATOES, 1).add(IRON_INGOTS, 1);

    public static final WeightedPool<Offering.OfferingTemplate> MEDIUM_BASIC_POOL = new WeightedPool<Offering.OfferingTemplate>()
            .add(EMERALDS, 1).add(BLAZE_RODS, 1).add(ENDER_PEARLS, 1).add(COAL, 1)
            .add(EXTRA_LIFE_NORMAL, 1);

    public static final WeightedPool<Offering.OfferingTemplate> EXPENSIVE_BASIC_POOL = new WeightedPool<Offering.OfferingTemplate>()
            .add(SWORD_DAMAGE_INCREASE_NORMAL, 1).add(SWORD_DAMAGE_INCREASE_NETHER, 1).add(SWORD_DAMAGE_INCREASE_END, 1)
            .add(AXE_DAMAGE_INCREASE_NORMAL, 1).add(AXE_DAMAGE_INCREASE_NETHER, 1).add(AXE_DAMAGE_INCREASE_END, 1)
            .add(BOW_DAMAGE_INCREASE_NORMAL, 1).add(BOW_DAMAGE_INCREASE_NETHER, 1).add(BOW_DAMAGE_INCREASE_END, 1)
            .add(EXTRA_LIFE_NETHER, 1).add(EXTRA_LIFE_END, 1);

    public static final WeightedTable<Offering.OfferingTemplate> BASIC_TABLE = new WeightedTable<Offering.OfferingTemplate>()
            .add(CHEAP_BASIC_POOL, 1).add(MEDIUM_BASIC_POOL, 5).add(EXPENSIVE_BASIC_POOL, 10);

}
