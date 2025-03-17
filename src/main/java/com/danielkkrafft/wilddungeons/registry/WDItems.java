package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDFluids;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.item.*;
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
    public static final DeferredItem<Item> MEATHOOK_ITEM = ITEMS.register("meathook", Meathook::new);
    public static final DeferredItem<Item> ESSENCE_BOTTLE = ITEMS.register("essence_bottle", EssenceBottleItem::new);
    public static final DeferredItem<Item> AMOGUS_STAFF = ITEMS.register("amogus_staff", AmogusStaff::new);
    public static final DeferredItem<Item> LASER_SWORD_ITEM = ITEMS.register("laser_sword", LaserSword::new);
    public static final DeferredItem<Item> FIREWORK_GUN_ITEM = ITEMS.register("firework_gun", FireworkGun::new);
    public static final DeferredItem<Item> LIFE_LIQUID_BUCKET = ITEMS.register("life_liquid_bucket", () -> new BucketItem(WDFluids.LIFE_LIQUID.get(), (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> BREEZE_GOLEM_SPAWN_EGG = ITEMS.register("breeze_golem_spawn_egg", () -> new SpawnEggItem(WDEntities.BREEZE_GOLEM.get(), new Color(115, 124, 255).getRGB(), new Color(0, 255, 233).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> MUTANT_BOGGED_SPAWN_EGG = ITEMS.register("mutant_bogged_spawn_egg", () -> new SpawnEggItem(WDEntities.MUTANT_BOGGED.get(), new Color(62, 162, 0).getRGB(), new Color(110, 71, 56).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> NETHER_DRAGON_SPAWN_EGG = ITEMS.register("nether_dragon_spawn_egg", () -> new SpawnEggItem(WDEntities.NETHER_DRAGON.get(), new Color(134, 0, 19).getRGB(), new Color(255, 136, 0).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> WD_DUNGEON_KEY = ITEMS.register("wd_dungeon_key", () -> new Item(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> ROOM_EXPORT_WAND = ITEMS.register("room_export_wand", () -> new RoomExportWand(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
}

