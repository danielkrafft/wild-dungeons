package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateRegistry.*;

public class OfferingTemplatePoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<OfferingTemplate>> OFFERING_TEMPLATE_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedPool<OfferingTemplate> FREE_PERK_POOL = new WeightedPool<OfferingTemplate>().setName("FREE_PERK_POOL");
    public static final WeightedPool<OfferingTemplate> CHEAP_BASIC_POOL = new WeightedPool<OfferingTemplate>().setName("CHEAP_BASIC_POOL");
    public static final WeightedPool<OfferingTemplate> MEDIUM_BASIC_POOL = new WeightedPool<OfferingTemplate>().setName("MEDIUM_BASIC_POOL");
    public static final WeightedPool<OfferingTemplate> EXPENSIVE_BASIC_POOL = new WeightedPool<OfferingTemplate>().setName("EXPENSIVE_BASIC_POOL");


    public static void setupOfferingPools(){
        FREE_PERK_POOL
                .add(FREE_SWORD_DAMAGE,1)
                .add(FREE_AXE_DAMAGE,1)
                .add(FREE_BOW_DAMAGE,1)
                .add(FREE_EXTRA_LIFE,1);
        OFFERING_TEMPLATE_POOL_REGISTRY.add(FREE_PERK_POOL);

        CHEAP_BASIC_POOL
                .add(ARROWS,2)
                .add(STEAKS,1)
                .add(BAKED_POTATOES,2)
                .add(IRON_INGOTS,1)
                .add(LEATHER,2)
        ;
        OFFERING_TEMPLATE_POOL_REGISTRY.add(CHEAP_BASIC_POOL);

        MEDIUM_BASIC_POOL
                .add(EMERALDS,1)
                .add(BLAZE_RODS,1)
                .add(ENDER_PEARLS,1)
                .add(EXTRA_LIFE_NORMAL,1)
                .add(COAL,1);
        OFFERING_TEMPLATE_POOL_REGISTRY.add(MEDIUM_BASIC_POOL);

        EXPENSIVE_BASIC_POOL
                .add(SWORD_DAMAGE_NORMAL,1)
                .add(SWORD_DAMAGE_NETHER,1)
                .add(SWORD_DAMAGE_END,1)
                .add(AXE_DAMAGE_NORMAL,1)
                .add(AXE_DAMAGE_NETHER,1)
                .add(AXE_DAMAGE_END,1)
                .add(BOW_DAMAGE_NORMAL,1)
                .add(BOW_DAMAGE_NETHER,1)
                .add(BOW_DAMAGE_END,1)
                .add(EXTRA_LIFE_NETHER,1)
                .add(EXTRA_LIFE_END,1);
        OFFERING_TEMPLATE_POOL_REGISTRY.add(EXPENSIVE_BASIC_POOL);
    }
}
