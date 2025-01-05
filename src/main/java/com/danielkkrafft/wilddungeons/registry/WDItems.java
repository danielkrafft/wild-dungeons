package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDFluids;
import com.danielkkrafft.wilddungeons.util.debug.DebugItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WildDungeons.MODID);

    public static final DeferredItem<Item> DEBUG_ITEM = ITEMS.register("debug_item", () -> new DebugItem(new Item.Properties()));
    public static final DeferredItem<Item> LIFE_LIQUID_BUCKET = ITEMS.register("life_liquid_bucket", () -> new BucketItem(WDFluids.LIFE_LIQUID.get(), (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
}

