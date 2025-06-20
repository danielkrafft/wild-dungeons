package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateRegistry.*;

public class OfferingTemplatePoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<OfferingTemplate>> OFFERING_TEMPLATE_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedPool<OfferingTemplate> FREE_PERK_POOL = create("FREE_PERK_POOL")
            .add(FREE_SWORD_DAMAGE,1)
            .add(FREE_AXE_DAMAGE,1)
            .add(FREE_BOW_DAMAGE,1)
            .add(FREE_EXTRA_LIFE,1)
            .add(FREE_FIRE_RESIST,1)
            .add(FREE_STRENGTH,1)
            .add(FREE_NIGHT_VISION,1)
            .add(FREE_HEALTH_BOOST,1)
            .add(FREE_MOVEMENT_SPEED, 1)
            .add(FREE_DIG_SPEED, 1)
            .add(FREE_BIG_ABSORPTION, 1)
            .add(FREE_POISON_IMMUNITY, 1)
            .add(FREE_STEP_HEIGHT, 1)
            .add(FREE_DODGE, 1)
            .add(FREE_ONE_PUNCH_MAN, 1)
            .add(FREE_EXPLOSION_IMMUNITY, 1)
            .add(FREE_BIG_RED_BUTTON, 1)
            .add(FREE_CRITICAL_HIT, 1)
            ;
    public static final WeightedPool<OfferingTemplate> CHEAP_BASIC_POOL = create("CHEAP_BASIC_POOL")
            .add(ARROWS,1)
            .add(STEAKS,1)
            .add(BAKED_POTATOES,1)
            .add(IRON_INGOTS,1)
            .add(LEATHER,1)
//                .add(HEALTH_POTION,1)
//                .add(REGENERATION_POTION,1)
            .add(COAL,1)
            .add(CHARCOAL, 1)
            .add(OAK_LOGS, 1)
            .add(STONE_PICKAXE, 1)
            .add(STONE_SHOVEL, 1)
            .add(GOLD_INGOTS, 1)
            .add(RAW_IRON, 1)
            .add(IRON_PICKAXE, 1);
    public static final WeightedPool<OfferingTemplate> MEDIUM_BASIC_POOL = create("MEDIUM_BASIC_POOL")
            .add(EMERALDS,1)
            .add(BLAZE_RODS,1)
            .add(ENDER_PEARLS,1)
            .add(DIAMOND, 1)
            .add(REDSTONE, 1)
            .add(EXTRA_LIFE_NORMAL,1);
    //                .add(HEALTH_POTION,5)
//                .add(REGENERATION_POTION,5);;
    public static final WeightedPool<OfferingTemplate> EXPENSIVE_BASIC_POOL = create("EXPENSIVE_BASIC_POOL")
            .add(ELYTRA, 3)
            .add(DIAMOND_AXE, 3)
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
            .add(EXTRA_LIFE_END,1)
            .add(STRENGTH_NORMAL, 1)
            .add(STRENGTH_NETHER, 1)
            .add(STRENGTH_END, 1)
            .add(HEALTH_BOOST_NORMAL, 1)
            .add(HEALTH_BOOST_NETHER, 1)
            .add(HEALTH_BOOST_END, 1)
            .add(MOVEMENT_SPEED_NORMAL, 1)
            .add(MOVEMENT_SPEED_NETHER, 1)
            .add(MOVEMENT_SPEED_END, 1);
    public static final WeightedPool<OfferingTemplate> PRICELESS_BASIC_POOL = create("PRICELESS_BASIC_POOL");

    public static final WeightedPool<OfferingTemplate> VILLAGE_STORE_POOL = create("VILLAGE_STORE_POOL")
            .add(VILLAGE_BLAZE_RODS,3)
            .add(VILLAGE_ENDER_PEARLS,3)
            .add(VILLAGE_COAL,3)
            .add(VILLAGE_CHARCOAL,3)
            .add(VILLAGE_OAK_LOGS,3)
            .add(VILLAGE_STONE_PICKAXE,3)
            .add(VILLAGE_IRON_PICKAXE,3)
            .add(VILLAGE_STONE_SHOVEL,3)
            .add(VILLAGE_GOLD_INGOTS,3)
            .add(VILLAGE_DIAMOND,3)
            .add(VILLAGE_REDSTONE,3)
            .add(VILLAGE_ELYTRA,3)
            .add(VILLAGE_DIAMOND_AXE,3)
            .add(VILLAGE_RAW_IRON,3)
            .add(VILLAGE_ARROWS,3)
            .add(VILLAGE_STEAKS,3)
            .add(VILLAGE_BAKED_POTATOES,3)
            .add(VILLAGE_IRON_INGOTS,3)
            .add(VILLAGE_LEATHER,3)
            .add(VILLAGE_SWORD_DAMAGE,1)
            .add(VILLAGE_AXE_DAMAGE,1)
            .add(VILLAGE_BOW_DAMAGE,1)
            .add(VILLAGE_EXTRA_LIFE,1)
            .add(VILLAGE_FIRE_RESIST,1)
            .add(VILLAGE_STRENGTH,1)
            .add(VILLAGE_NIGHT_VISION,1)
            .add(VILLAGE_HEALTH_BOOST,1)
            .add(VILLAGE_MOVEMENT_SPEED,1)
            .add(VILLAGE_DIG_SPEED,1)
            .add(VILLAGE_BIG_ABSORPTION,1)
            .add(VILLAGE_POISON_IMMUNITY,1)
            .add(VILLAGE_STEP_HEIGHT,1)
            .add(VILLAGE_DODGE,1)
            .add(VILLAGE_ONE_PUNCH_MAN,1)
            .add(VILLAGE_EXPLOSION_IMMUNITY,1)
            .add(VILLAGE_BIG_RED_BUTTON,1)
            .add(VILLAGE_CRITICAL_HIT,1)
            ;

    public static final WeightedPool<OfferingTemplate> WIND_WEAPONS_POOL = create("WIND_WEAPONS_POOL")
            .add(FREE_WIND_BOW, 1)
            .add(FREE_WIND_MACE, 1)
            .add(FREE_WIND_HAMMER, 1)
            .add(FREE_WIND_CANNON, 1);

    public static final WeightedPool<OfferingTemplate> SCIFI_WEAPONS_POOL = create("STAR_WEAPONS_POOL")
            .add(FREE_STAR_CANNON, 1);

    public static final WeightedPool<OfferingTemplate> GENERAL_WEAPONS_POOL = create("GENERAL_WEAPONS_POOL")
            .add(FREE_AMOGUS_STAFF, 1)
            .add(FREE_LASER_SWORD, 1)
            .add(FREE_FIREWORK_GUN, 1)
            .add(FREE_MEATHOOK, 1);

    public static WeightedPool<OfferingTemplate> create(String name){
        WeightedPool<OfferingTemplate> offeringPool = new WeightedPool<OfferingTemplate>().setName(name);
        OFFERING_TEMPLATE_POOL_REGISTRY.add(offeringPool);
        return offeringPool;
    }

    public static WeightedPool<OfferingTemplate> copyOf(WeightedPool<OfferingTemplate> pool, String name){
        WeightedPool<OfferingTemplate> offeringPool = new WeightedPool<OfferingTemplate>(pool).setName(name);
        OFFERING_TEMPLATE_POOL_REGISTRY.add(offeringPool);
        return offeringPool;
    }

}
