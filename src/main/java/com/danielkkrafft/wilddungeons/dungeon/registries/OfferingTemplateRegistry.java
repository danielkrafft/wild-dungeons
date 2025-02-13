package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;

import java.util.ArrayList;

import static com.danielkkrafft.wilddungeons.entity.Offering.CostType.*;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.PERK;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.RIFT;

public class OfferingTemplateRegistry {
    public static final DungeonComponentRegistry<OfferingTemplate> OFFERING_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();
    public static ArrayList<OfferingTemplate> offerings = new ArrayList<>();

    public static final OfferingTemplate ARROWS = create("ARROWS", ItemTemplateRegistry.ARROWS, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate STEAKS = create("STEAKS", ItemTemplateRegistry.COOKED_BEEF, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate BAKED_POTATOES = create("BAKED_POTATOES", ItemTemplateRegistry.BAKED_POTATOES, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate IRON_INGOTS = create("IRON_INGOTS", ItemTemplateRegistry.IRON_INGOTS, XP_LEVEL, 6, 1.5f);
    public static final OfferingTemplate LEATHER = create("LEATHER", ItemTemplateRegistry.LEATHER, XP_LEVEL, 4, 1.5f);
    //potion effects do not transfer into offerings as neither OfferingTemplates nor Offerings have a way to store potion effects.
    //could be added later, but I am writing this with 3 days left to work on this project so it's just out of scope for now -Skylor
//    public static final OfferingTemplate HEALTH_POTION = create("HEALTH_POTION", ItemTemplateRegistry.HEALTH_POTION, XP_LEVEL, 8, 1.5f);
//    public static final OfferingTemplate REGENERATION_POTION = create("REGENERATION_POTION", ItemTemplateRegistry.REGENERATION_POTION_SPLASH, XP_LEVEL, 8, 1.5f);

    public static final OfferingTemplate EMERALDS = create("EMERALDS",ItemTemplateRegistry.EMERALD, XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate BLAZE_RODS = create("BLAZE_RODS", ItemTemplateRegistry.BLAZE_ROD, NETHER_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate ENDER_PEARLS = create("ENDER_PEARLS", ItemTemplateRegistry.ENDER_PEARL, END_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate COAL = create("COAL",ItemTemplateRegistry.COAL, XP_LEVEL, 2, 1.5f);

    public static final OfferingTemplate FREE_AMOGUS_STAFF = create("FREE_AMOGUS_STAFF", ItemTemplateRegistry.AMOGUS_STAFF, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate FREE_MEATHOOK = create("FREE_MEATHOOK", ItemTemplateRegistry.MEATHOOK, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate DUNGEON_KEY = create("DUNGEON_KEY", ItemTemplateRegistry.DUNGEON_KEY, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f);

    public static final OfferingTemplate FREE_SWORD_DAMAGE = create("FREE_SWORD_DAMAGE", PERK, 1, "SWORD_DAMAGE", XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_AXE_DAMAGE = create("FREE_AXE_DAMAGE", PERK, 1, "AXE_DAMAGE", XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_BOW_DAMAGE = create("FREE_BOW_DAMAGE", PERK, 1, "BOW_DAMAGE", XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_EXTRA_LIFE = create("FREE_EXTRA_LIFE", PERK, 1, "EXTRA_LIFE", XP_LEVEL, 0, 1);

    public static final OfferingTemplate EXTRA_LIFE_NORMAL = create("EXTRA_LIFE_NORMAL", PERK, 1, "EXTRA_LIFE", XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_NETHER = create("EXTRA_LIFE_NETHER", PERK, 1, "EXTRA_LIFE", NETHER_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_END = create("EXTRA_LIFE_END", PERK, 1, "EXTRA_LIFE", END_XP_LEVEL, 4, 1.5f);
    
    public static final OfferingTemplate SWORD_DAMAGE_NORMAL = create("SWORD_DAMAGE_NORMAL", PERK, 1, "SWORD_DAMAGE", XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_NETHER = create("SWORD_DAMAGE_NETHER", PERK, 1, "SWORD_DAMAGE", NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_END = create("SWORD_DAMAGE_END", PERK, 1, "SWORD_DAMAGE", END_XP_LEVEL, 8, 1.5f);
    
    public static final OfferingTemplate AXE_DAMAGE_NORMAL = create("AXE_DAMAGE_NORMAL", PERK, 1, "AXE_DAMAGE", XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_NETHER = create("AXE_DAMAGE_NETHER", PERK, 1, "AXE_DAMAGE", NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_END = create("AXE_DAMAGE_END", PERK, 1, "AXE_DAMAGE", END_XP_LEVEL, 8, 1.5f);
    
    public static final OfferingTemplate BOW_DAMAGE_NORMAL = create("BOW_DAMAGE_NORMAL", PERK, 1, "BOW_DAMAGE", XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_NETHER = create("BOW_DAMAGE_NETHER", PERK, 1, "BOW_DAMAGE", NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_END = create("BOW_DAMAGE_END", PERK, 1, "BOW_DAMAGE", END_XP_LEVEL, 8, 1.5f);

    public static final OfferingTemplate OVERWORLD_TEST_RIFT = create("OVERWORLD_TEST_RIFT", RIFT, 1, "wd-mega_dungeon", XP_LEVEL, 30, 1.5f).setSoundLoop(WDSoundEvents.RIFT_AURA.value());
    public static final OfferingTemplate NETHER_TEST_RIFT = create("NETHER_TEST_RIFT", RIFT, 1, "wd-piglin_factory", NETHER_XP_LEVEL, 30, 1.5f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate END_TEST_RIFT = create("END_TEST_RIFT", RIFT, 1, "wd-dungeon_1", END_XP_LEVEL, 30, 1.5f);


    public static OfferingTemplate create(String name, Offering.Type type, int cost, String perk, Offering.CostType costType, int xpLevel, float rarity){
        OfferingTemplate offering = new OfferingTemplate(name, type, cost, perk, costType, xpLevel, rarity);
        offerings.add(offering);
        return offering;
    }
    public static OfferingTemplate create(String name, DungeonRegistration.ItemTemplate itemTemplate, Offering.CostType costType, int costAmount, float costDeviance) {
        OfferingTemplate offering = new OfferingTemplate(name, itemTemplate, costType, costAmount, costDeviance);
        offerings.add(offering);
        return offering;
    }

    //it turns out we *never* use this registry. But it's a good idea to have it, just in case we decide to use it later.
    public static void setupOfferings(){
        offerings.forEach(OFFERING_TEMPLATE_REGISTRY::add);
    }
}


