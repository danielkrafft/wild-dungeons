package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.item.*;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.BowDataRegistry;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.GunDataRegistry;
import com.danielkkrafft.wilddungeons.util.debug.DebugItem;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

public class WDItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WildDungeons.MODID);

    public static final DeferredItem<Item> DEBUG_ITEM = ITEMS.register("debug_item", () -> new DebugItem(new Item.Properties()));
    public static final DeferredItem<Item> OFFERING_ITEM = ITEMS.register("offering_item", () -> new OfferingItem(new Item.Properties()));
    public static final DeferredItem<Item> RIFT_ITEM = ITEMS.register("rift_item", () -> new RiftItem(new Item.Properties()));
    public static final DeferredItem<Item> PERK_TESTER = ITEMS.register("perk_tester", () -> new PerkTesterItem(new Item.Properties()));

    public static final DeferredItem<Item> MEATHOOK_ITEM = ITEMS.register("meathook", Meathook::new);
    public static final DeferredItem<Item> ESSENCE_BOTTLE = ITEMS.register("essence_bottle", EssenceBottleItem::new);
    public static final DeferredItem<Item> AMOGUS_STAFF = ITEMS.register("amogus_staff", AmogusStaff::new);
    public static final DeferredItem<Item> LASER_SWORD_ITEM = ITEMS.register("laser_sword", LaserSword::new);
    public static final DeferredItem<Item> WIND_MACE_ITEM = ITEMS.register("wind_mace", WindMace::new);
    public static final DeferredItem<Item> WIND_CANNON_ITEM = ITEMS.register("wind_cannon", WindCannon::new);
    public static final DeferredItem<Item> WIND_HAMMER_ITEM = ITEMS.register("wind_hammer", WindHammer::new);
    public static final DeferredItem<Item> FIREWORK_GUN_ITEM = ITEMS.register("firework_gun", FireworkGun::new);
    public static final DeferredItem<Item> LIFE_LIQUID_BUCKET = ITEMS.register("life_liquid_bucket", () -> new BucketItem(WDFluids.LIFE_LIQUID.get(), (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> BREEZE_GOLEM_SPAWN_EGG = ITEMS.register("breeze_golem_spawn_egg", () -> new SpawnEggItem(WDEntities.BREEZE_GOLEM.get(), new Color(115, 124, 255).getRGB(), new Color(0, 255, 233).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> MUTANT_BOGGED_SPAWN_EGG = ITEMS.register("mutant_bogged_spawn_egg", () -> new SpawnEggItem(WDEntities.MUTANT_BOGGED.get(), new Color(62, 162, 0).getRGB(), new Color(110, 71, 56).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> NETHER_DRAGON_SPAWN_EGG = ITEMS.register("nether_dragon_spawn_egg", () -> new SpawnEggItem(WDEntities.NETHER_DRAGON.get(), new Color(134, 0, 19).getRGB(), new Color(255, 136, 0).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> WD_DUNGEON_KEY = ITEMS.register("wd_dungeon_key", () -> new Item(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> ROOM_EXPORT_WAND = ITEMS.register("room_export_wand", () -> new RoomExportWand(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).component(WDDataComponents.WAND_MODE.get(),0).component(WDDataComponents.WAND_ROOM_NAME.get(),"room")));
    public static final DeferredItem<Item> INSTANT_LOADOUT_LEATHER = ITEMS.register("instant_loadout_leather", () -> new InstantLoadout(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), InstantLoadout.Type.Leather));
    public static final DeferredItem<Item> INSTANT_LOADOUT_IRON = ITEMS.register("instant_loadout_iron", () -> new InstantLoadout(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), InstantLoadout.Type.Iron));
    public static final DeferredItem<Item> INSTANT_LOADOUT_DIAMOND = ITEMS.register("instant_loadout_diamond", () -> new InstantLoadout(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), InstantLoadout.Type.Diamond));
    public static final DeferredItem<Item> INSTANT_LOADOUT_NETHERITE = ITEMS.register("instant_loadout_netherite", () -> new InstantLoadout(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), InstantLoadout.Type.Netherite));
    public static final DeferredItem<Item> INSTANT_LOADOUT_GOLD = ITEMS.register("instant_loadout_gold", () -> new InstantLoadout(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), InstantLoadout.Type.Gold));
    public static final DeferredItem<Item> BOSS_KEY = ITEMS.register("boss_key", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    //------- MUSIC DISCS -------//
    public static final DeferredItem<Item> OVERFLOW_MUSIC_DISC = ITEMS.register("overflow_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.OVERFLOW_KEY).stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> OVERFLOW_SAFE_MUSIC_DISC = ITEMS.register("overflow_safe_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.OVERFLOW_SAFE_KEY).stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> OVERFLOW_UNDERWATER_MUSIC_DISC = ITEMS.register("overflow_underwater_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.OVERFLOW_UNDERWATER_KEY).stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> OVERFLOW_UNDERWATER_SAFE_MUSIC_DISC = ITEMS.register("overflow_underwater_safe_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.OVERFLOW_UNDERWATER_SAFE_KEY).stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> ANGEL_INVESTOR_MUSIC_DISC = ITEMS.register("angel_investor_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.ANGEL_INVESTOR_KEY).stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> ANGEL_INVESTOR_SAFE_MUSIC_DISC = ITEMS.register("angel_investor_safe_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.ANGEL_INVESTOR_SAFE_KEY).stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> THE_CAPITAL_MUSIC_DISC = ITEMS.register("the_capital_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.THE_CAPITAL_KEY).stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> THE_CAPITAL_SAFE_MUSIC_DISC = ITEMS.register("the_capital_safe_music_disc", () -> new Item( new Item.Properties().jukeboxPlayable(WDSoundEvents.THE_CAPITAL_SAFE_KEY).stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> WIND_BOW_ITEM = ITEMS.register("wind_bow", () -> new WindBow(BowDataRegistry.find("wind_bow")));
    public static final DeferredItem<Item> EMERALD_STAFF = ITEMS.register("emerald_staff", () -> new EmeraldStaff(GunDataRegistry.find("emerald_staff")));
    public static final DeferredItem<Item> STAR_CANNON = ITEMS.register("star_cannon", () -> new StarCannon(GunDataRegistry.find("star_cannon")));
}

