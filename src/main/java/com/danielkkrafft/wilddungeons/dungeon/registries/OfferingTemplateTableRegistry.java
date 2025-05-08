package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;

import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplatePoolRegistry.*;

public class OfferingTemplateTableRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedTable<OfferingTemplate>> OFFERING_TEMPLATE_TABLE_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedTable<OfferingTemplate> FREE_PERK_OFFERING_TABLE = create("FREE_PERK_OFFERING_TABLE")
            .add(FREE_PERK_POOL,1);
    public static final WeightedTable<OfferingTemplate> BASIC_SHOP_TABLE = create("BASIC_SHOP_TABLE")
            .add(CHEAP_BASIC_POOL,1)
            .add(MEDIUM_BASIC_POOL,5)
            .add(EXPENSIVE_BASIC_POOL,10);
    public static final WeightedTable<OfferingTemplate> FREE_CUSTOM_WEAPON_TABLE = create("FREE_CUSTOM_WEAPON_TABLE")
            .add(new WeightedPool<OfferingTemplate>()
                    .add(OfferingTemplateRegistry.FREE_AMOGUS_STAFF, 1)
                    .add(OfferingTemplateRegistry.FREE_LASER_SWORD, 1)
                    .add(OfferingTemplateRegistry.FREE_FIREWORK_GUN, 1)
                    .add(OfferingTemplateRegistry.FREE_MEATHOOK, 1)
                    , 1);

    public static WeightedTable<OfferingTemplate> create(String name){
        WeightedTable<OfferingTemplate> offeringTable = new WeightedTable<OfferingTemplate>();
        offeringTable.setName(name);
        OFFERING_TEMPLATE_TABLE_REGISTRY.add(offeringTable);
        return offeringTable;
    }
}
