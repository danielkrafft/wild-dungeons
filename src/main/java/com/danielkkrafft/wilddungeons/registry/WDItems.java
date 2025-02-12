package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDFluids;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.item.*;
import com.danielkkrafft.wilddungeons.util.debug.DebugItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

public class WDItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WildDungeons.MODID);

    public static final DeferredItem<Item> DEBUG_ITEM = ITEMS.register("debug_item", () -> new DebugItem(new Item.Properties()));
    public static final DeferredItem<Item> OFFERING_ITEM = ITEMS.register("offering_item", () -> new OfferingItem(new Item.Properties()));
    public static final DeferredItem<Item> RIFT_ITEM = ITEMS.register("rift_item", () -> new RiftItem(new Item.Properties()));
    public static final DeferredItem<Item> MEATHOOK_ITEM = ITEMS.register("meathook", Meathook::new);
    public static final DeferredItem<Item> LASER_SWORD_ITEM = ITEMS.register("laser_sword", LaserSword::new);
    public static final DeferredItem<Item> FIREWORK_GUN_ITEM = ITEMS.register("firework_gun", FireworkGun::new);
    public static final DeferredItem<Item> LIFE_LIQUID_BUCKET = ITEMS.register("life_liquid_bucket", () -> new BucketItem(WDFluids.LIFE_LIQUID.get(), (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> BREEZE_GOLEM_SPAWN_EGG = ITEMS.register("breeze_golem_spawn_egg", () -> new SpawnEggItem(WDEntities.BREEZE_GOLEM.get(), new Color(233, 230, 212).getRGB(), new Color(96, 13, 13).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> MUTANT_BOGGED_SPAWN_EGG = ITEMS.register("mutant_bogged_spawn_egg", () -> new SpawnEggItem(WDEntities.MUTANT_BOGGED.get(), new Color(233, 230, 212).getRGB(), new Color(96, 13, 13).getRGB(), new Item.Properties()));
    public static final DeferredItem<Item> WD_DUNGEON_KEY = ITEMS.register("wd_dungeon_key", () -> new DungeonKeyItem(new Item.Properties()));
}

