package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;

import static com.danielkkrafft.wilddungeons.entity.Offering.CostType.*;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.*;

public class OfferingTemplateRegistry {
    public static final DungeonComponentRegistry<OfferingTemplate> OFFERING_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();

    public static final OfferingTemplate ARROWS = new OfferingTemplate("ARROWS", ItemTemplateRegistry.ARROWS, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate STEAKS = new OfferingTemplate("STEAKS", ItemTemplateRegistry.COOKED_BEEF, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate BAKED_POTATOES =new OfferingTemplate("BAKED_POTATOES", ItemTemplateRegistry.BAKED_POTATOES, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate IRON_INGOTS = new OfferingTemplate("IRON_INGOTS", ItemTemplateRegistry.IRON_INGOTS, XP_LEVEL, 6, 1.5f);
    public static final OfferingTemplate LEATHER = new OfferingTemplate("LEATHER", ItemTemplateRegistry.LEATHER, XP_LEVEL, 4, 1.5f);



    public static final OfferingTemplate EMERALDS = new OfferingTemplate("EMERALDS",ItemTemplateRegistry.EMERALD, XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate BLAZE_RODS = new OfferingTemplate("BLAZE_RODS", ItemTemplateRegistry.BLAZE_ROD, NETHER_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate ENDER_PEARLS = new OfferingTemplate("ENDER_PEARLS", ItemTemplateRegistry.ENDER_PEARL, END_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate COAL = new OfferingTemplate("COAL",ItemTemplateRegistry.COAL, XP_LEVEL, 2, 1.5f);

    public static final OfferingTemplate FREE_SWORD_DAMAGE = new OfferingTemplate("FREE_SWORD_DAMAGE", PERK, 1, "SWORD_DAMAGE", XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_AXE_DAMAGE = new OfferingTemplate("FREE_AXE_DAMAGE", PERK, 1, "AXE_DAMAGE", XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_BOW_DAMAGE = new OfferingTemplate("FREE_BOW_DAMAGE", PERK, 1, "BOW_DAMAGE", XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_EXTRA_LIFE = new OfferingTemplate("FREE_EXTRA_LIFE", PERK, 1, "EXTRA_LIFE", XP_LEVEL, 0, 1);

    public static final OfferingTemplate EXTRA_LIFE_NORMAL = new OfferingTemplate("EXTRA_LIFE_NORMAL", PERK, 1, "EXTRA_LIFE", XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_NETHER = new OfferingTemplate("EXTRA_LIFE_NETHER", PERK, 1, "EXTRA_LIFE", NETHER_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_END = new OfferingTemplate("EXTRA_LIFE_END", PERK, 1, "EXTRA_LIFE", END_XP_LEVEL, 4, 1.5f);
    
    public static final OfferingTemplate SWORD_DAMAGE_NORMAL = new OfferingTemplate("SWORD_DAMAGE_NORMAL", PERK, 1, "SWORD_DAMAGE", XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_NETHER = new OfferingTemplate("SWORD_DAMAGE_NETHER", PERK, 1, "SWORD_DAMAGE", NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_END = new OfferingTemplate("SWORD_DAMAGE_END", PERK, 1, "SWORD_DAMAGE", END_XP_LEVEL, 8, 1.5f);
    
    public static final OfferingTemplate AXE_DAMAGE_NORMAL = new OfferingTemplate("AXE_DAMAGE_NORMAL", PERK, 1, "AXE_DAMAGE", XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_NETHER = new OfferingTemplate("AXE_DAMAGE_NETHER", PERK, 1, "AXE_DAMAGE", NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_END = new OfferingTemplate("AXE_DAMAGE_END", PERK, 1, "AXE_DAMAGE", END_XP_LEVEL, 8, 1.5f);
    
    public static final OfferingTemplate BOW_DAMAGE_NORMAL = new OfferingTemplate("BOW_DAMAGE_NORMAL", PERK, 1, "BOW_DAMAGE", XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_NETHER = new OfferingTemplate("BOW_DAMAGE_NETHER", PERK, 1, "BOW_DAMAGE", NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_END = new OfferingTemplate("BOW_DAMAGE_END", PERK, 1, "BOW_DAMAGE", END_XP_LEVEL, 8, 1.5f);

    public static final OfferingTemplate OVERWORLD_TEST_RIFT = new DungeonRegistration.OfferingTemplate("OVERWORLD_TEST_RIFT", RIFT, 1, "wd-mega_dungeon", XP_LEVEL, 30, 1.5f).setRenderScale(1.0f).setSoundLoop(WDSoundEvents.RIFT_AURA.value());
    public static final OfferingTemplate NETHER_TEST_RIFT = new OfferingTemplate("NETHER_TEST_RIFT", RIFT, 1, "wd-dungeon_1", NETHER_XP_LEVEL, 30, 1.5f);
    public static final OfferingTemplate END_TEST_RIFT = new OfferingTemplate("END_TEST_RIFT", RIFT, 1, "wd-dungeon_1", END_XP_LEVEL, 30, 1.5f);


    public static void setupOfferings(){
        OFFERING_TEMPLATE_REGISTRY.add(ARROWS);
        OFFERING_TEMPLATE_REGISTRY.add(STEAKS);
        OFFERING_TEMPLATE_REGISTRY.add(BAKED_POTATOES);
        OFFERING_TEMPLATE_REGISTRY.add(IRON_INGOTS);

        OFFERING_TEMPLATE_REGISTRY.add(EMERALDS);
        OFFERING_TEMPLATE_REGISTRY.add(BLAZE_RODS);
        OFFERING_TEMPLATE_REGISTRY.add(ENDER_PEARLS);
        OFFERING_TEMPLATE_REGISTRY.add(COAL);

        OFFERING_TEMPLATE_REGISTRY.add(FREE_SWORD_DAMAGE);
        OFFERING_TEMPLATE_REGISTRY.add(FREE_AXE_DAMAGE);
        OFFERING_TEMPLATE_REGISTRY.add(FREE_BOW_DAMAGE);
        OFFERING_TEMPLATE_REGISTRY.add(FREE_EXTRA_LIFE);

        OFFERING_TEMPLATE_REGISTRY.add(EXTRA_LIFE_NORMAL);
        OFFERING_TEMPLATE_REGISTRY.add(EXTRA_LIFE_NETHER);
        OFFERING_TEMPLATE_REGISTRY.add(EXTRA_LIFE_END);

        OFFERING_TEMPLATE_REGISTRY.add(SWORD_DAMAGE_NORMAL);
        OFFERING_TEMPLATE_REGISTRY.add(SWORD_DAMAGE_NETHER);
        OFFERING_TEMPLATE_REGISTRY.add(SWORD_DAMAGE_END);

        OFFERING_TEMPLATE_REGISTRY.add(AXE_DAMAGE_NORMAL);
        OFFERING_TEMPLATE_REGISTRY.add(AXE_DAMAGE_NETHER);
        OFFERING_TEMPLATE_REGISTRY.add(AXE_DAMAGE_END);

        OFFERING_TEMPLATE_REGISTRY.add(BOW_DAMAGE_NORMAL);
        OFFERING_TEMPLATE_REGISTRY.add(BOW_DAMAGE_NETHER);
        OFFERING_TEMPLATE_REGISTRY.add(BOW_DAMAGE_END);

        OFFERING_TEMPLATE_REGISTRY.add(NETHER_TEST_RIFT);
        OFFERING_TEMPLATE_REGISTRY.add(END_TEST_RIFT);
    }
}
