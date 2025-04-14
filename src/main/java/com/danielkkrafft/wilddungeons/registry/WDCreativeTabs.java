package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.item.EssenceBottleItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.danielkkrafft.wilddungeons.registry.WDBlocks.*;
import static com.danielkkrafft.wilddungeons.registry.WDItems.*;

public class WDCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, WildDungeons.MODID);
    public static final Supplier<CreativeModeTab> WD_TAB = CREATIVE_MODE_TABS.register("wilddungeons_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + WildDungeons.MODID))
            .icon(() -> new ItemStack(CONNECTION_BLOCK.get()))
            .displayItems((params, output) -> {
                output.accept(MEATHOOK_ITEM.get());
                output.accept(AMOGUS_STAFF.get());
                output.accept(LASER_SWORD_ITEM.get());
                output.accept(FIREWORK_GUN_ITEM.get());

                output.accept(ROTTEN_MOSS.get());
                output.accept(HEAVY_RUNE.get());

                output.accept(EssenceBottleItem.setEssenceType(new ItemStack(ESSENCE_BOTTLE.get()), EssenceOrb.Type.NETHER));
                output.accept(EssenceBottleItem.setEssenceType(new ItemStack(ESSENCE_BOTTLE.get()), EssenceOrb.Type.END));
                output.accept(WD_DUNGEON_KEY.get());
                output.accept(LIFE_LIQUID_BUCKET.get());

                output.accept(BREEZE_GOLEM_SPAWN_EGG.get());
                output.accept(MUTANT_BOGGED_SPAWN_EGG.get());
                output.accept(NETHER_DRAGON_SPAWN_EGG.get());

                output.accept(CONNECTION_BLOCK.get());
                output.accept(SPAWN_BLOCK.get());
                output.accept(OFFERING_ITEM.get());
                output.accept(RIFT_ITEM.get());
                output.accept(ROOM_EXPORT_WAND.get());
                output.accept(DEBUG_ITEM.get());
            })
            .build()
    );
}
