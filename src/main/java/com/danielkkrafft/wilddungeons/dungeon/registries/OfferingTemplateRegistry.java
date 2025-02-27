package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;

import java.util.ArrayList;

import static com.danielkkrafft.wilddungeons.entity.Offering.CostType.*;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.PERK;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.RIFT;

public class OfferingTemplateRegistry {
    public static final DungeonComponentRegistry<OfferingTemplate> OFFERING_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();
    public static ArrayList<OfferingTemplate> offerings = new ArrayList<>();

    public static final OfferingTemplate ARROWS = createItem("ARROWS", ItemTemplateRegistry.ARROWS, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate STEAKS = createItem("STEAKS", ItemTemplateRegistry.COOKED_BEEF, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate BAKED_POTATOES = createItem("BAKED_POTATOES", ItemTemplateRegistry.BAKED_POTATOES, XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate IRON_INGOTS = createItem("IRON_INGOTS", ItemTemplateRegistry.IRON_INGOTS, XP_LEVEL, 6, 1.5f);
    public static final OfferingTemplate LEATHER = createItem("LEATHER", ItemTemplateRegistry.LEATHER, XP_LEVEL, 4, 1.5f);
    //potion effects do not transfer into offerings as neither OfferingTemplates nor Offerings have a way to store potion effects.
    //could be added later, but I am writing this with 3 days left to work on this project so it's just out of scope for now -Skylor
//    public static final OfferingTemplate HEALTH_POTION = createItem("HEALTH_POTION", ItemTemplateRegistry.HEALTH_POTION, XP_LEVEL, 8, 1.5f);
//    public static final OfferingTemplate REGENERATION_POTION = createItem("REGENERATION_POTION", ItemTemplateRegistry.REGENERATION_POTION_SPLASH, XP_LEVEL, 8, 1.5f);

    public static final OfferingTemplate EMERALDS = createItem("EMERALDS",ItemTemplateRegistry.EMERALD, XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate BLAZE_RODS = createItem("BLAZE_RODS", ItemTemplateRegistry.BLAZE_ROD, NETHER_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate ENDER_PEARLS = createItem("ENDER_PEARLS", ItemTemplateRegistry.ENDER_PEARL, END_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate COAL = createItem("COAL",ItemTemplateRegistry.COAL, XP_LEVEL, 2, 1.5f);

    public static final OfferingTemplate FREE_AMOGUS_STAFF = createItem("FREE_AMOGUS_STAFF", ItemTemplateRegistry.AMOGUS_STAFF, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate FREE_LASER_SWORD = createItem("FREE_LASER_SWORD", ItemTemplateRegistry.LASER_SWORD, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate FREE_FIREWORK_GUN = createItem("FREE_FIREWORK_GUN", ItemTemplateRegistry.FIREWORK_GUN, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate FREE_MEATHOOK = createItem("FREE_MEATHOOK", ItemTemplateRegistry.MEATHOOK, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate DUNGEON_KEY = createItem("DUNGEON_KEY", ItemTemplateRegistry.DUNGEON_KEY, XP_LEVEL, 0, 1.0f).setRenderScale(2.0f).setShowRing(true).setSoundLoop(WDSoundEvents.SHIMMER.value());

    public static final OfferingTemplate FREE_SWORD_DAMAGE = createPerk("FREE_SWORD_DAMAGE", PerkRegistry.SWORD_DAMAGE, XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_AXE_DAMAGE = createPerk("FREE_AXE_DAMAGE", PerkRegistry.AXE_DAMAGE, XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_BOW_DAMAGE = createPerk("FREE_BOW_DAMAGE", PerkRegistry.BOW_DAMAGE, XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_EXTRA_LIFE = createPerk("FREE_EXTRA_LIFE", PerkRegistry.EXTRA_LIFE, XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_FIRE_RESIST = createPerk("FREE_FIRE_RESIST", PerkRegistry.FIRE_RESIST, XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_STRENGTH = createPerk("FREE_STRENGTH", PerkRegistry.STRENGTH, XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_NIGHT_VISION = createPerk("NIGHT_VISION", PerkRegistry.NIGHT_VISION, XP_LEVEL, 0, 1);
    public static final OfferingTemplate FREE_HEALTH_BOOST = createPerk("FREE_HEALTH_BOOST", PerkRegistry.HEALTH_BOOST, XP_LEVEL, 0, 1);

    public static final OfferingTemplate EXTRA_LIFE_NORMAL = createPerk("EXTRA_LIFE_NORMAL", PerkRegistry.EXTRA_LIFE, XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_NETHER = createPerk("EXTRA_LIFE_NETHER", PerkRegistry.EXTRA_LIFE, NETHER_XP_LEVEL, 4, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_END = createPerk("EXTRA_LIFE_END", PerkRegistry.EXTRA_LIFE, END_XP_LEVEL, 4, 1.5f);
    
    public static final OfferingTemplate SWORD_DAMAGE_NORMAL = createPerk("SWORD_DAMAGE_NORMAL", PerkRegistry.SWORD_DAMAGE, XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_NETHER = createPerk("SWORD_DAMAGE_NETHER", PerkRegistry.SWORD_DAMAGE, NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_END = createPerk("SWORD_DAMAGE_END", PerkRegistry.SWORD_DAMAGE, END_XP_LEVEL, 8, 1.5f);
    
    public static final OfferingTemplate AXE_DAMAGE_NORMAL = createPerk("AXE_DAMAGE_NORMAL", PerkRegistry.AXE_DAMAGE, XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_NETHER = createPerk("AXE_DAMAGE_NETHER", PerkRegistry.AXE_DAMAGE, NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_END = createPerk("AXE_DAMAGE_END", PerkRegistry.AXE_DAMAGE, END_XP_LEVEL, 8, 1.5f);
    
    public static final OfferingTemplate BOW_DAMAGE_NORMAL = createPerk("BOW_DAMAGE_NORMAL", PerkRegistry.BOW_DAMAGE, XP_LEVEL, 15, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_NETHER = createPerk("BOW_DAMAGE_NETHER", PerkRegistry.BOW_DAMAGE, NETHER_XP_LEVEL, 8, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_END = createPerk("BOW_DAMAGE_END", PerkRegistry.BOW_DAMAGE, END_XP_LEVEL, 8, 1.5f);

    public static final OfferingTemplate OVERWORLD_TEST_RIFT = createRift("OVERWORLD_TEST_RIFT", "wd-mega_dungeon", XP_LEVEL, 30, 1.5f).setSoundLoop(WDSoundEvents.RIFT_AURA.value());
    public static final OfferingTemplate NETHER_TEST_RIFT = createRift("NETHER_TEST_RIFT", "wd-piglin_factory", NETHER_XP_LEVEL, 30, 1.5f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate REACTION_TEST_RIFT = createRift("REACTION_TEST_RIFT", "wd-reaction_dungeon", XP_LEVEL, 30, 1.5f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate END_TEST_RIFT = createRift("END_TEST_RIFT", "wd-dungeon_1", END_XP_LEVEL, 30, 1.5f);

    public static final OfferingTemplate EXIT_RIFT = createRift("EXIT_RIFT", "win", XP_LEVEL, 0, 1.5f).setSoundLoop(WDSoundEvents.RIFT_AURA.value());

    public static OfferingTemplate create(String name, Offering.Type type, int amount, String offeringID, Offering.CostType costType, int xpLevel, float rarity){
        OfferingTemplate offering = new OfferingTemplate(name, type, amount, offeringID, costType, xpLevel, rarity);
        offerings.add(offering);
        return offering;
    }

    public static OfferingTemplate createRift(String name, String riftID, Offering.CostType costType, int xpLevel, float rarity){
        OfferingTemplate offering = new OfferingTemplate(name, RIFT, 1, riftID, costType, xpLevel, rarity);
        offerings.add(offering);
        return offering;
    }

    public static OfferingTemplate createPerk(String name, DungeonPerkTemplate perkTemplate, Offering.CostType costType, int xpLevel, float rarity){
        OfferingTemplate offering = new OfferingTemplate(name, PERK, 1, perkTemplate.name(), costType, xpLevel, rarity);
        offerings.add(offering);
        return offering;
    }

    public static OfferingTemplate createItem(String name, DungeonRegistration.ItemTemplate itemTemplate, Offering.CostType costType, int costAmount, float costDeviance) {
        OfferingTemplate offering = new OfferingTemplate(name, itemTemplate, costType, costAmount, costDeviance);
        offerings.add(offering);
        return offering;
    }

    //it turns out we *never* use this registry. But it's a good idea to have it, just in case we decide to use it later.
    public static void setupOfferings(){
        offerings.forEach(OFFERING_TEMPLATE_REGISTRY::add);
    }
}


