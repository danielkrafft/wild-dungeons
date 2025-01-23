package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;

public class OfferingTables {

    public static Offering.OfferingTemplate SWORD_DAMAGE_INCREASE = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "SWORD_DAMAGE_INCREASE", Offering.CostType.XP_LEVEL, 0, 1);

    public static Offering.OfferingTemplate AXE_DAMAGE_INCREASE = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "AXE_DAMAGE_INCREASE", Offering.CostType.XP_LEVEL, 0, 1);

    public static Offering.OfferingTemplate BOW_DAMAGE_INCREASE = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "BOW_DAMAGE_INCREASE", Offering.CostType.XP_LEVEL, 0, 1);

    public static Offering.OfferingTemplate EXTRA_LIFE = new Offering.OfferingTemplate(
            Offering.Type.PERK, 1, "EXTRA_LIFE", Offering.CostType.XP_LEVEL, 0, 1);

    public static final WeightedPool<Offering.OfferingTemplate> PERK_OFFERING_POOL = new WeightedPool<Offering.OfferingTemplate>()
            .add(SWORD_DAMAGE_INCREASE, 1).add(AXE_DAMAGE_INCREASE, 1).add(BOW_DAMAGE_INCREASE, 1).add(EXTRA_LIFE, 1);

    public static final WeightedTable<Offering.OfferingTemplate> PERK_OFFERING_TABLE = new WeightedTable<Offering.OfferingTemplate>()
            .add(PERK_OFFERING_POOL, 1);

}
