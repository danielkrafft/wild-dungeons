package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;

import java.util.ArrayList;

import static com.danielkkrafft.wilddungeons.entity.EssenceOrb.Type.*;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.PERK;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.RIFT;

public class OfferingTemplateRegistry {
    public static final DungeonComponentRegistry<OfferingTemplate> OFFERING_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();
    public static ArrayList<OfferingTemplate> offerings = new ArrayList<>();

    public static final OfferingTemplate ARROWS = createItem("ARROWS", ItemTemplateRegistry.ARROWS, OVERWORLD, 4, 1.5f);
    public static final OfferingTemplate STEAKS = createItem("STEAKS", ItemTemplateRegistry.COOKED_BEEF, OVERWORLD, 4, 1.5f);
    public static final OfferingTemplate BAKED_POTATOES = createItem("BAKED_POTATOES", ItemTemplateRegistry.BAKED_POTATOES, OVERWORLD, 4, 1.5f);
    public static final OfferingTemplate IRON_INGOTS = createItem("IRON_INGOTS", ItemTemplateRegistry.IRON_INGOTS, OVERWORLD, 6, 1.5f);
    public static final OfferingTemplate LEATHER = createItem("LEATHER", ItemTemplateRegistry.LEATHER, OVERWORLD, 4, 1.5f);
    //potion effects do not transfer into offerings as neither OfferingTemplates nor Offerings have a way to store potion effects.
    //could be added later, but I am writing this with 3 days left to work on this project so it's just out of scope for now -Skylor
    // daniel - i didn't look but couldn't we just cause offerings to reference itemtemplates rather than items if we want them to retain all the same properties without making duplicate properties in offeringtemplate
//    public static final OfferingTemplate HEALTH_POTION = createItem("HEALTH_POTION", ItemTemplateRegistry.HEALTH_POTION, OVERWORLD, 8, 1.5f);
//    public static final OfferingTemplate REGENERATION_POTION = createItem("REGENERATION_POTION", ItemTemplateRegistry.REGENERATION_POTION_SPLASH, OVERWORLD, 8, 1.5f);

    public static final OfferingTemplate EMERALDS = createItem("EMERALDS",ItemTemplateRegistry.EMERALD, OVERWORLD, 8, 1.5f);
    public static final OfferingTemplate BLAZE_RODS = createItem("BLAZE_RODS", ItemTemplateRegistry.BLAZE_ROD, NETHER, 4, 1.5f);
    public static final OfferingTemplate ENDER_PEARLS = createItem("ENDER_PEARLS", ItemTemplateRegistry.ENDER_PEARL, END, 4, 1.5f);
    public static final OfferingTemplate COAL = createItem("COAL",ItemTemplateRegistry.COAL, OVERWORLD, 2, 1.5f);

    public static final OfferingTemplate CHARCOAL = createItem("CHARCOAL", ItemTemplateRegistry.CHARCOAL, OVERWORLD, 2, 1.5f);
    public static final OfferingTemplate OAK_LOGS = createItem("OAK_LOGS", ItemTemplateRegistry.OAK_LOGS, OVERWORLD, 2, 1.5f);
    public static final OfferingTemplate STONE_PICKAXE = createItem("STONE_PICKAXE", ItemTemplateRegistry.STONE_PICKAXE, OVERWORLD, 1, 1.0f);
    public static final OfferingTemplate STONE_SHOVEL = createItem("STONE_SHOVEL", ItemTemplateRegistry.STONE_SHOVEL, OVERWORLD, 1, 1.0f);
    public static final OfferingTemplate GOLD_INGOTS = createItem("GOLD_INGOTS", ItemTemplateRegistry.GOLD_INGOTS, OVERWORLD, 5, 1.5f);
    public static final OfferingTemplate DIAMOND = createItem("DIAMOND", ItemTemplateRegistry.DIAMOND, OVERWORLD, 8, 1.5f);
    public static final OfferingTemplate REDSTONE = createItem("REDSTONE", ItemTemplateRegistry.REDSTONE, OVERWORLD, 3, 1.5f);
    public static final OfferingTemplate ELYTRA = createItem("ELYTRA", ItemTemplateRegistry.ELYTRA, END, 16, 1.5f);
    public static final OfferingTemplate DIAMOND_AXE = createItem("DIAMOND_AXE", ItemTemplateRegistry.DIAMOND_AXE, OVERWORLD, 16, 1.5f);
    public static final OfferingTemplate RAW_IRON = createItem("RAW_IRON", ItemTemplateRegistry.IRON_RAW, OVERWORLD, 5, 1.5f);

    public static final OfferingTemplate FREE_AMOGUS_STAFF = createItem("FREE_AMOGUS_STAFF", ItemTemplateRegistry.AMOGUS_STAFF, OVERWORLD, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate FREE_LASER_SWORD = createItem("FREE_LASER_SWORD", ItemTemplateRegistry.LASER_SWORD, OVERWORLD, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate FREE_FIREWORK_GUN = createItem("FREE_FIREWORK_GUN", ItemTemplateRegistry.FIREWORK_GUN, OVERWORLD, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate FREE_MEATHOOK = createItem("FREE_MEATHOOK", ItemTemplateRegistry.MEATHOOK, OVERWORLD, 0, 1.0f).setRenderScale(2.0f);
    public static final OfferingTemplate DUNGEON_KEY = createItem("DUNGEON_KEY", ItemTemplateRegistry.DUNGEON_KEY, OVERWORLD, 0, 1.0f).setRenderScale(2.0f).setShowRing(true).setSoundLoop(WDSoundEvents.SHIMMER.value());

    public static final OfferingTemplate FREE_SWORD_DAMAGE = createPerk("FREE_SWORD_DAMAGE", PerkRegistry.SWORD_DAMAGE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_AXE_DAMAGE = createPerk("FREE_AXE_DAMAGE", PerkRegistry.AXE_DAMAGE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_BOW_DAMAGE = createPerk("FREE_BOW_DAMAGE", PerkRegistry.BOW_DAMAGE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_EXTRA_LIFE = createPerk("FREE_EXTRA_LIFE", PerkRegistry.EXTRA_LIFE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_FIRE_RESIST = createPerk("FREE_FIRE_RESIST", PerkRegistry.FIRE_RESIST, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_STRENGTH = createPerk("FREE_STRENGTH", PerkRegistry.STRENGTH, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_NIGHT_VISION = createPerk("NIGHT_VISION", PerkRegistry.NIGHT_VISION, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_HEALTH_BOOST = createPerk("FREE_HEALTH_BOOST", PerkRegistry.HEALTH_BOOST, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_MOVEMENT_SPEED = createPerk("FREE_MOVEMENT_SPEED", PerkRegistry.MOVEMENT_SPEED, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_DIG_SPEED = createPerk("FREE_DIG_SPEED", PerkRegistry.DIG_SPEED, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_BIG_ABSORPTION = createPerk("FREE_BIG_ABSORPTION", PerkRegistry.BIG_ABSORPTION, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_ATTACK_SPEED = createPerk("FREE_ATTACK_SPEED", PerkRegistry.ATTACK_SPEED, OVERWORLD, 0, 1);

    public static final OfferingTemplate EXTRA_LIFE_NORMAL = createPerk("EXTRA_LIFE_NORMAL", PerkRegistry.EXTRA_LIFE, OVERWORLD, 8, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_NETHER = createPerk("EXTRA_LIFE_NETHER", PerkRegistry.EXTRA_LIFE, NETHER, 4, 1.5f);
    public static final OfferingTemplate EXTRA_LIFE_END = createPerk("EXTRA_LIFE_END", PerkRegistry.EXTRA_LIFE, END, 4, 1.5f);
    
    public static final OfferingTemplate SWORD_DAMAGE_NORMAL = createPerk("SWORD_DAMAGE_NORMAL", PerkRegistry.SWORD_DAMAGE, OVERWORLD, 15, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_NETHER = createPerk("SWORD_DAMAGE_NETHER", PerkRegistry.SWORD_DAMAGE, NETHER, 8, 1.5f);
    public static final OfferingTemplate SWORD_DAMAGE_END = createPerk("SWORD_DAMAGE_END", PerkRegistry.SWORD_DAMAGE, END, 8, 1.5f);
    
    public static final OfferingTemplate AXE_DAMAGE_NORMAL = createPerk("AXE_DAMAGE_NORMAL", PerkRegistry.AXE_DAMAGE, OVERWORLD, 15, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_NETHER = createPerk("AXE_DAMAGE_NETHER", PerkRegistry.AXE_DAMAGE, NETHER, 8, 1.5f);
    public static final OfferingTemplate AXE_DAMAGE_END = createPerk("AXE_DAMAGE_END", PerkRegistry.AXE_DAMAGE, END, 8, 1.5f);
    
    public static final OfferingTemplate BOW_DAMAGE_NORMAL = createPerk("BOW_DAMAGE_NORMAL", PerkRegistry.BOW_DAMAGE, OVERWORLD, 15, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_NETHER = createPerk("BOW_DAMAGE_NETHER", PerkRegistry.BOW_DAMAGE, NETHER, 8, 1.5f);
    public static final OfferingTemplate BOW_DAMAGE_END = createPerk("BOW_DAMAGE_END", PerkRegistry.BOW_DAMAGE, END, 8, 1.5f);

    public static final OfferingTemplate OVERWORLD_TEST_RIFT = createRift("OVERWORLD_TEST_RIFT", "wd-mega_dungeon", OVERWORLD, 30, 1.5f).setSoundLoop(WDSoundEvents.RIFT_AURA.value());
    public static final OfferingTemplate MEGA_DUNGEON_GAUNTLET_RIFT = createRift("MEGA_DUNGEON_GAUNTLET_RIFT", "wd-mega_dungeon_gauntlet", OVERWORLD, 0, 1.0f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate NETHER_TEST_RIFT = createRift("NETHER_TEST_RIFT", "wd-piglin_factory", NETHER, 30, 1.5f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate REACTION_TEST_RIFT = createRift("REACTION_TEST_RIFT", "wd-reaction_dungeon", OVERWORLD, 30, 1.5f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate END_TEST_RIFT = createRift("END_TEST_RIFT", "wd-dungeon_1", END, 30, 1.5f);

    public static final OfferingTemplate EXIT_RIFT = createRift("EXIT_RIFT", "win", OVERWORLD, 0, 1.5f).setSoundLoop(WDSoundEvents.RIFT_AURA.value());

    public static OfferingTemplate create(String name, Offering.Type type, int amount, String offeringID, EssenceOrb.Type costType, int xpLevel, float rarity){
        OfferingTemplate offering = new OfferingTemplate(name, type, amount, offeringID, costType, xpLevel, rarity);
        offerings.add(offering);
        return offering;
    }

    public static OfferingTemplate createRift(String name, String riftID, EssenceOrb.Type costType, int xpLevel, float deviance){
        OfferingTemplate offering = new OfferingTemplate(name, RIFT, 1, riftID, costType, xpLevel, deviance);
        offerings.add(offering);
        return offering;
    }

    public static OfferingTemplate createPerk(String name, DungeonPerkTemplate perkTemplate, EssenceOrb.Type costType, int xpLevel, float deviance){
        OfferingTemplate offering = new OfferingTemplate(name, PERK, 1, perkTemplate.name(), costType, xpLevel, deviance);
        offerings.add(offering);
        return offering;
    }

    public static OfferingTemplate createItem(String name, DungeonRegistration.ItemTemplate itemTemplate, EssenceOrb.Type costType, int costAmount, float costDeviance) {
        OfferingTemplate offering = new OfferingTemplate(name, itemTemplate, costType, costAmount, costDeviance);
        offerings.add(offering);
        return offering;
    }

    //it turns out we *never* use this registry. But it's a good idea to have it, just in case we decide to use it later.
    public static void setupOfferings(){
        offerings.forEach(OFFERING_TEMPLATE_REGISTRY::add);
    }
}


