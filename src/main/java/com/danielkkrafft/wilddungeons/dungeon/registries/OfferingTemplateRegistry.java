package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.OfferingTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.world.item.Items;

import static com.danielkkrafft.wilddungeons.entity.Offering.CostType.*;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.PERK;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.RIFT;

public class OfferingTemplateRegistry {
    public static final DungeonComponentRegistry<OfferingTemplate> OFFERING_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();

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
    public static final OfferingTemplate IRON_PICKAXE = createItem("IRON_PICKAXE", ItemTemplateRegistry.IRON_PICKAXE, OVERWORLD, 6, 1.5f);
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

    //region Village
    public static final OfferingTemplate VILLAGE_DUNGEON_KEY = createItem("VILLAGE_DUNGEON_KEY", ItemTemplateRegistry.DUNGEON_KEY, OVERWORLD, 0, 1.0f).setShowRing(true).setCostItem(Items.EMERALD,777);
    public static final OfferingTemplate VILLAGE_BLAZE_RODS = createItem("VILLAGE_BLAZE_RODS", ItemTemplateRegistry.BLAZE_ROD, NETHER, 4, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_ENDER_PEARLS = createItem("VILLAGE_ENDER_PEARLS", ItemTemplateRegistry.ENDER_PEARL, END, 4, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_COAL = createItem("VILLAGE_COAL",ItemTemplateRegistry.COAL, OVERWORLD, 2, 1.5f).setCostItem(Items.EMERALD,4);
    public static final OfferingTemplate VILLAGE_CHARCOAL = createItem("VILLAGE_CHARCOAL", ItemTemplateRegistry.CHARCOAL, OVERWORLD, 2, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_OAK_LOGS = createItem("VILLAGE_OAK_LOGS", ItemTemplateRegistry.OAK_LOGS, OVERWORLD, 2, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_STONE_PICKAXE = createItem("VILLAGE_STONE_PICKAXE", ItemTemplateRegistry.STONE_PICKAXE, OVERWORLD, 1, 1.0f).setCostItem(Items.EMERALD,4);
    public static final OfferingTemplate VILLAGE_IRON_PICKAXE = createItem("VILLAGE_IRON_PICKAXE", ItemTemplateRegistry.IRON_PICKAXE, OVERWORLD, 6, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_STONE_SHOVEL = createItem("VILLAGE_STONE_SHOVEL", ItemTemplateRegistry.STONE_SHOVEL, OVERWORLD, 1, 1.0f).setCostItem(Items.EMERALD,2);
    public static final OfferingTemplate VILLAGE_GOLD_INGOTS = createItem("VILLAGE_GOLD_INGOTS", ItemTemplateRegistry.GOLD_INGOTS, OVERWORLD, 5, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_DIAMOND = createItem("VILLAGE_DIAMOND", ItemTemplateRegistry.DIAMOND, OVERWORLD, 8, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_REDSTONE = createItem("VILLAGE_REDSTONE", ItemTemplateRegistry.REDSTONE, OVERWORLD, 3, 1.5f).setCostItem(Items.EMERALD,8);
    public static final OfferingTemplate VILLAGE_ELYTRA = createItem("VILLAGE_ELYTRA", ItemTemplateRegistry.ELYTRA, END, 16, 1.5f).setCostItem(Items.EMERALD,24);
    public static final OfferingTemplate VILLAGE_DIAMOND_AXE = createItem("VILLAGE_DIAMOND_AXE", ItemTemplateRegistry.DIAMOND_AXE, OVERWORLD, 16, 1.5f).setCostItem(Items.EMERALD,16);
    public static final OfferingTemplate VILLAGE_RAW_IRON = createItem("VILLAGE_RAW_IRON", ItemTemplateRegistry.IRON_RAW, OVERWORLD, 5, 1.5f).setCostItem(Items.EMERALD,5);
    public static final OfferingTemplate VILLAGE_ARROWS = createItem("VILLAGE_ARROWS", ItemTemplateRegistry.ARROWS, OVERWORLD, 4, 1.5f).setCostItem(Items.EMERALD,4);
    public static final OfferingTemplate VILLAGE_STEAKS = createItem("VILLAGE_STEAKS", ItemTemplateRegistry.COOKED_BEEF, OVERWORLD, 4, 1.5f).setCostItem(Items.EMERALD,4);
    public static final OfferingTemplate VILLAGE_BAKED_POTATOES = createItem("VILLAGE_BAKED_POTATOES", ItemTemplateRegistry.BAKED_POTATOES, OVERWORLD, 4, 1.5f).setCostItem(Items.EMERALD,4);
    public static final OfferingTemplate VILLAGE_IRON_INGOTS = createItem("VILLAGE_IRON_INGOTS", ItemTemplateRegistry.IRON_INGOTS, OVERWORLD, 6, 1.5f).setCostItem(Items.EMERALD,6);
    public static final OfferingTemplate VILLAGE_LEATHER = createItem("VILLAGE_LEATHER", ItemTemplateRegistry.LEATHER, OVERWORLD, 4, 1.5f).setCostItem(Items.EMERALD,4);
    public static final OfferingTemplate VILLAGE_SWORD_DAMAGE = createPerk("VILLAGE_SWORD_DAMAGE", PerkRegistry.SWORD_DAMAGE, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_AXE_DAMAGE = createPerk("VILLAGE_AXE_DAMAGE", PerkRegistry.AXE_DAMAGE, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_BOW_DAMAGE = createPerk("VILLAGE_BOW_DAMAGE", PerkRegistry.BOW_DAMAGE, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_EXTRA_LIFE = createPerk("VILLAGE_EXTRA_LIFE", PerkRegistry.EXTRA_LIFE, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_FIRE_RESIST = createPerk("VILLAGE_FIRE_RESIST", PerkRegistry.FIRE_RESIST, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_STRENGTH = createPerk("VILLAGE_STRENGTH", PerkRegistry.STRENGTH, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_NIGHT_VISION = createPerk("VILLAGE_NIGHT_VISION", PerkRegistry.NIGHT_VISION, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_HEALTH_BOOST = createPerk("VILLAGE_HEALTH_BOOST", PerkRegistry.HEALTH_BOOST, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_MOVEMENT_SPEED = createPerk("VILLAGE_MOVEMENT_SPEED", PerkRegistry.MOVEMENT_SPEED, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_DIG_SPEED = createPerk("VILLAGE_DIG_SPEED", PerkRegistry.HASTE, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_BIG_ABSORPTION = createPerk("VILLAGE_BIG_ABSORPTION", PerkRegistry.BIG_ABSORPTION, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_POISON_IMMUNITY = createPerk("VILLAGE_POISON_IMMUNITY", PerkRegistry.POISON_IMMUNITY, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_STEP_HEIGHT = createPerk("VILLAGE_STEP_HEIGHT", PerkRegistry.STEP_HEIGHT, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_DODGE = createPerk("VILLAGE_DODGE", PerkRegistry.DODGE, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_ONE_PUNCH_MAN = createPerk("VILLAGE_ONE_PUNCH_MAN", PerkRegistry.ONE_PUNCH_MAN, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_EXPLOSION_IMMUNITY = createPerk("VILLAGE_EXPLOSION_IMMUNITY", PerkRegistry.EXPLOSION_IMMUNITY, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_BIG_RED_BUTTON = createPerk("VILLAGE_BIG_RED_BUTTON", PerkRegistry.BIG_RED_BUTTON, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    public static final OfferingTemplate VILLAGE_CRITICAL_HIT = createPerk("VILLAGE_CRITICAL_HIT", PerkRegistry.CRITICAL_HIT, OVERWORLD, 20, 1.5f).setCostItem(Items.EMERALD,150);
    //endregion

    // ----- WIND WEAPONS FOR TRIAL GAUNTLET
    public static final OfferingTemplate FREE_WIND_BOW = createItem("FREE_WIND_BOW", ItemTemplateRegistry.WIND_BOW, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_WIND_MACE = createItem("FREE_WIND_MACE", ItemTemplateRegistry.WIND_MACE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_WIND_HAMMER = createItem("FREE_WIND_HAMMER", ItemTemplateRegistry.WIND_HAMMER, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_WIND_CANNON = createItem("FREE_WIND_CANNON", ItemTemplateRegistry.WIND_CANNON, OVERWORLD, 0, 1);

    // ----- STAR WEAPONS FOR SCIFI GAUNTLET
    public static final OfferingTemplate FREE_STAR_CANNON = createItem("FREE_STAR_CANNON", ItemTemplateRegistry.STAR_CANNON, OVERWORLD, 0, 1);


    public static final OfferingTemplate FREE_SWORD_DAMAGE = createPerk("FREE_SWORD_DAMAGE", PerkRegistry.SWORD_DAMAGE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_AXE_DAMAGE = createPerk("FREE_AXE_DAMAGE", PerkRegistry.AXE_DAMAGE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_BOW_DAMAGE = createPerk("FREE_BOW_DAMAGE", PerkRegistry.BOW_DAMAGE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_EXTRA_LIFE = createPerk("FREE_EXTRA_LIFE", PerkRegistry.EXTRA_LIFE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_FIRE_RESIST = createPerk("FREE_FIRE_RESIST", PerkRegistry.FIRE_RESIST, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_STRENGTH = createPerk("FREE_STRENGTH", PerkRegistry.STRENGTH, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_NIGHT_VISION = createPerk("NIGHT_VISION", PerkRegistry.NIGHT_VISION, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_HEALTH_BOOST = createPerk("FREE_HEALTH_BOOST", PerkRegistry.HEALTH_BOOST, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_MOVEMENT_SPEED = createPerk("FREE_MOVEMENT_SPEED", PerkRegistry.MOVEMENT_SPEED, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_DIG_SPEED = createPerk("FREE_DIG_SPEED", PerkRegistry.HASTE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_BIG_ABSORPTION = createPerk("FREE_BIG_ABSORPTION", PerkRegistry.BIG_ABSORPTION, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_POISON_IMMUNITY = createPerk("FREE_POISON_IMMUNITY", PerkRegistry.POISON_IMMUNITY, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_STEP_HEIGHT = createPerk("FREE_STEP_HEIGHT", PerkRegistry.STEP_HEIGHT, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_DODGE = createPerk("FREE_DODGE", PerkRegistry.DODGE, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_ONE_PUNCH_MAN = createPerk("FREE_ONE_PUNCH", PerkRegistry.ONE_PUNCH_MAN, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_EXPLOSION_IMMUNITY = createPerk("FREE_EXPLOSION_IMMUNITY", PerkRegistry.EXPLOSION_IMMUNITY, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_BIG_RED_BUTTON = createPerk("FREE_BIG_RED_BUTTON", PerkRegistry.BIG_RED_BUTTON, OVERWORLD, 0, 1);
    public static final OfferingTemplate FREE_CRITICAL_HIT = createPerk("FREE_CRITICAL_HIT", PerkRegistry.CRITICAL_HIT, OVERWORLD, 0, 1);

    public static final OfferingTemplate STRENGTH_NORMAL = createPerk("STRENGTH_NORMAL", PerkRegistry.STRENGTH, OVERWORLD, 20, 1.5f);
    public static final OfferingTemplate STRENGTH_NETHER = createPerk("STRENGTH_NETHER", PerkRegistry.STRENGTH, NETHER, 12, 1.5f);
    public static final OfferingTemplate STRENGTH_END = createPerk("STRENGTH_END", PerkRegistry.STRENGTH, END, 12, 1.5f);

    public static final OfferingTemplate HEALTH_BOOST_NORMAL = createPerk("HEALTH_BOOST_NORMAL", PerkRegistry.HEALTH_BOOST, OVERWORLD, 20, 1.5f);
    public static final OfferingTemplate HEALTH_BOOST_NETHER = createPerk("HEALTH_BOOST_NETHER", PerkRegistry.HEALTH_BOOST, NETHER, 12, 1.5f);
    public static final OfferingTemplate HEALTH_BOOST_END = createPerk("HEALTH_BOOST_END", PerkRegistry.HEALTH_BOOST, END, 12, 1.5f);

    public static final OfferingTemplate MOVEMENT_SPEED_NORMAL = createPerk("MOVEMENT_SPEED_NORMAL", PerkRegistry.MOVEMENT_SPEED, OVERWORLD, 20, 1.5f);
    public static final OfferingTemplate MOVEMENT_SPEED_NETHER = createPerk("MOVEMENT_SPEED_NETHER", PerkRegistry.MOVEMENT_SPEED, NETHER, 12, 1.5f);
    public static final OfferingTemplate MOVEMENT_SPEED_END = createPerk("MOVEMENT_SPEED_END", PerkRegistry.MOVEMENT_SPEED, END, 12, 1.5f);

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
    public static final OfferingTemplate VILLAGE_HEADQUARTERS_RIFT = createRift("VILLAGE_HEADQUARTERS_RIFT", "wd-village_dungeon", OVERWORLD, 30, 1.5f).setCostItem(Items.EMERALD,128).setSoundLoop(WDSoundEvents.RIFT_AURA.value());
    public static final OfferingTemplate VILLAGE_HEADQUARTERS_GAUNTLET_RIFT = createRift("VILLAGE_HEADQUARTERS_GAUNTLET_RIFT", "wd-village_dungeon_gauntlet", OVERWORLD, 30, 1.5f).setCostItem(Items.EMERALD,256).setSoundLoop(WDSoundEvents.RIFT_AURA.value());
    public static final OfferingTemplate MEGA_DUNGEON_GAUNTLET_RIFT = createRift("MEGA_DUNGEON_GAUNTLET_RIFT", "wd-mega_dungeon_gauntlet", OVERWORLD, 0, 1.0f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate NETHER_TEST_RIFT = createRift("NETHER_TEST_RIFT", "wd-piglin_factory", NETHER, 30, 1.5f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate REACTION_TEST_RIFT = createRift("REACTION_TEST_RIFT", "wd-village_dungeon", OVERWORLD, 30, 1.5f).setSoundLoop(WDSoundEvents.WHISPERS.value());
    public static final OfferingTemplate END_TEST_RIFT = createRift("END_TEST_RIFT", "wd-dungeon_1", END, 30, 1.5f);
    public static final OfferingTemplate GAUNTLET_RIFT = createRift("GAUNTLET_RIFT", "wd-gauntlet_general", GAUNTLET, 0, 1.0f).setSoundLoop(WDSoundEvents.WHISPERS.value()).setCostItem(WDItems.BOSS_KEY.get(), 1);
    public static final OfferingTemplate EXIT_RIFT = createRift("EXIT_RIFT", "win", OVERWORLD, 0, 1.5f).setSoundLoop(WDSoundEvents.RIFT_AURA.value()).setRenderScale(0.5f);

    public static OfferingTemplate create(String name, Offering.Type type, int amount, String offeringID, Offering.CostType costType, int xpLevel, float rarity){
        OfferingTemplate offering = new OfferingTemplate(name, type, amount, offeringID, costType, xpLevel, rarity);
        OFFERING_TEMPLATE_REGISTRY.add(offering);
        return offering;
    }

    public static OfferingTemplate createRift(String name, String riftID, Offering.CostType costType, int xpLevel, float deviance){
        OfferingTemplate offering = new OfferingTemplate(name, RIFT, 1, riftID, costType, xpLevel, deviance);
        OFFERING_TEMPLATE_REGISTRY.add(offering);
        return offering;
    }

    public static OfferingTemplate createPerk(String name, DungeonPerkTemplate perkTemplate, Offering.CostType costType, int xpLevel, float deviance){
        OfferingTemplate offering = new OfferingTemplate(name, PERK, 1, perkTemplate.name(), costType, xpLevel, deviance);
        OFFERING_TEMPLATE_REGISTRY.add(offering);
        return offering;
    }

    public static OfferingTemplate createItem(String name, DungeonRegistration.ItemTemplate itemTemplate, Offering.CostType costType, int costAmount, float costDeviance) {
        OfferingTemplate offering = new OfferingTemplate(name, itemTemplate, costType, costAmount, costDeviance);
        OFFERING_TEMPLATE_REGISTRY.add(offering);
        return offering;
    }

}


