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
                output.accept(EMERALD_STAFF.get());
                output.accept(LASER_SWORD_ITEM.get());
                output.accept(FIREWORK_GUN_ITEM.get());
                output.accept(WIND_MACE_ITEM.get());
                output.accept(WIND_BOW_ITEM.get());
                output.accept(WIND_CANNON_ITEM.get());
                output.accept(WIND_HAMMER_ITEM.get());
                output.accept(STAR_CANNON.get());

                output.accept(WATCHFUL_EYE.get());
                output.accept(PUTRID_EGG.get());
                output.accept(DENSE_TNT.get());

                output.accept(DETONITE_ORE.get());
                output.accept(DETONITE_BLOCK.get());
                output.accept(DETONITE_CRYSTAL.get());
                output.accept(DETONITE_CRYSTAL_BLOCK.get());
                output.accept(SMALL_DETONITE_BUD.get());
                output.accept(MEDIUM_DETONITE_BUD.get());
                output.accept(LARGE_DETONITE_BUD.get());
                output.accept(DETONITE_CLUSTER.get());

                output.accept(EGG_SAC_ARROWS.get());

                output.accept(WOODEN_WAR_SPEAR.get());
                output.accept(STONE_WAR_SPEAR.get());
                output.accept(IRON_WAR_SPEAR.get());
                output.accept(GOLD_WAR_SPEAR.get());
                output.accept(DIAMOND_WAR_SPEAR.get());
                output.accept(NETHERITE_WAR_SPEAR.get());
                output.accept(HEAVY_WAR_SPEAR.get());

                output.accept(NAUTILUS_SHIELD.get());

                //output.accept(WOODEN_WAR_SPEAR.get());

                output.accept(ROTTEN_MOSS.get());
                output.accept(TOXIC_GAS.get());
                output.accept(SPIDER_EGG.get());
                output.accept(HEAVY_RUNE.get());

                output.accept(EssenceBottleItem.setEssenceType(new ItemStack(ESSENCE_BOTTLE.get()), EssenceOrb.Type.NETHER));
                output.accept(EssenceBottleItem.setEssenceType(new ItemStack(ESSENCE_BOTTLE.get()), EssenceOrb.Type.END));
                output.accept(WD_DUNGEON_KEY.get());
                output.accept(LIFE_LIQUID_BUCKET.get());
                output.accept(TOXIC_SLUDGE_BUCKET.get());

                output.accept(BREEZE_GOLEM_SPAWN_EGG.get());
                output.accept(MUTANT_BOGGED_SPAWN_EGG.get());
                output.accept(NETHER_DRAGON_SPAWN_EGG.get());
                output.accept(SKELEPEDE_SPAWN_EGG.get());
                output.accept(SPIDERLING_SPAWN_EGG.get());
                output.accept(COPPER_SENTINEL_SPAWN_EGG.get());
                output.accept(TOXIC_WISP_SPAWN_EGG.get());
                output.accept(PRIMAL_CREEPER_SPAWN_EGG.get());
                output.accept(CONDEMNED_GUARDIAN_SPAWN_EGG.get());

                output.accept(CONNECTION_BLOCK.get());
                output.accept(SPAWN_BLOCK.get());
                output.accept(OFFERING_ITEM.get());
                output.accept(RIFT_ITEM.get());
                output.accept(ROOM_EXPORT_WAND.get());
                output.accept(DEBUG_ITEM.get());
                output.accept(PERK_TESTER.get());

                output.accept(INSTANT_LOADOUT_LEATHER.get());
                output.accept(INSTANT_LOADOUT_IRON.get());
                output.accept(INSTANT_LOADOUT_DIAMOND.get());
                output.accept(INSTANT_LOADOUT_NETHERITE.get());
                output.accept(INSTANT_LOADOUT_GOLD.get());

                output.accept(OVERFLOW_MUSIC_DISC.get());
                output.accept(OVERFLOW_SAFE_MUSIC_DISC.get());
                output.accept(OVERFLOW_UNDERWATER_MUSIC_DISC.get());
                output.accept(OVERFLOW_UNDERWATER_SAFE_MUSIC_DISC.get());
                output.accept(ANGEL_INVESTOR_MUSIC_DISC.get());
                output.accept(ANGEL_INVESTOR_SAFE_MUSIC_DISC.get());
                output.accept(THE_CAPITAL_MUSIC_DISC.get());
                output.accept(THE_CAPITAL_SAFE_MUSIC_DISC.get());

                output.accept(IRON_GRATE.get());

                output.accept(BOSS_KEY.get());

                output.accept(PRISMARINE_TILE.get());
                output.accept(PRISMARINE_TILE_SLAB.get());
                output.accept(PRISMARINE_TILE_STAIRS.get());

                output.accept(PRISMARINE_SMALL_TILE.get());
                output.accept(PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(BROWN_PRISMARINE_TILE.get());
                output.accept(BROWN_PRISMARINE_TILE_SLAB.get());
                output.accept(BROWN_PRISMARINE_TILE_STAIRS.get());

                output.accept(BROWN_PRISMARINE_SMALL_TILE.get());
                output.accept(BROWN_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(BROWN_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(GREEN_PRISMARINE_TILE.get());
                output.accept(GREEN_PRISMARINE_TILE_SLAB.get());
                output.accept(GREEN_PRISMARINE_TILE_STAIRS.get());

                output.accept(GREEN_PRISMARINE_SMALL_TILE.get());
                output.accept(GREEN_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(GREEN_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(LIME_PRISMARINE_TILE.get());
                output.accept(LIME_PRISMARINE_TILE_SLAB.get());
                output.accept(LIME_PRISMARINE_TILE_STAIRS.get());

                output.accept(LIME_PRISMARINE_SMALL_TILE.get());
                output.accept(LIME_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(LIME_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(YELLOW_PRISMARINE_TILE.get());
                output.accept(YELLOW_PRISMARINE_TILE_SLAB.get());
                output.accept(YELLOW_PRISMARINE_TILE_STAIRS.get());

                output.accept(YELLOW_PRISMARINE_SMALL_TILE.get());
                output.accept(YELLOW_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(YELLOW_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(ORANGE_PRISMARINE_TILE.get());
                output.accept(ORANGE_PRISMARINE_TILE_SLAB.get());
                output.accept(ORANGE_PRISMARINE_TILE_STAIRS.get());

                output.accept(ORANGE_PRISMARINE_SMALL_TILE.get());
                output.accept(ORANGE_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(ORANGE_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(RED_PRISMARINE_TILE.get());
                output.accept(RED_PRISMARINE_TILE_SLAB.get());
                output.accept(RED_PRISMARINE_TILE_STAIRS.get());

                output.accept(RED_PRISMARINE_SMALL_TILE.get());
                output.accept(RED_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(RED_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(PINK_PRISMARINE_TILE.get());
                output.accept(PINK_PRISMARINE_TILE_SLAB.get());
                output.accept(PINK_PRISMARINE_TILE_STAIRS.get());

                output.accept(PINK_PRISMARINE_SMALL_TILE.get());
                output.accept(PINK_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(PINK_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(MAGENTA_PRISMARINE_TILE.get());
                output.accept(MAGENTA_PRISMARINE_TILE_SLAB.get());
                output.accept(MAGENTA_PRISMARINE_TILE_STAIRS.get());

                output.accept(MAGENTA_PRISMARINE_SMALL_TILE.get());
                output.accept(MAGENTA_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(MAGENTA_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(PURPLE_PRISMARINE_TILE.get());
                output.accept(PURPLE_PRISMARINE_TILE_SLAB.get());
                output.accept(PURPLE_PRISMARINE_TILE_STAIRS.get());

                output.accept(PURPLE_PRISMARINE_SMALL_TILE.get());
                output.accept(PURPLE_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(PURPLE_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(BLUE_PRISMARINE_TILE.get());
                output.accept(BLUE_PRISMARINE_TILE_SLAB.get());
                output.accept(BLUE_PRISMARINE_TILE_STAIRS.get());

                output.accept(BLUE_PRISMARINE_SMALL_TILE.get());
                output.accept(BLUE_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(BLUE_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(WHITE_PRISMARINE_TILE.get());
                output.accept(WHITE_PRISMARINE_TILE_SLAB.get());
                output.accept(WHITE_PRISMARINE_TILE_STAIRS.get());

                output.accept(WHITE_PRISMARINE_SMALL_TILE.get());
                output.accept(WHITE_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(WHITE_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(LIGHT_GRAY_PRISMARINE_TILE.get());
                output.accept(LIGHT_GRAY_PRISMARINE_TILE_SLAB.get());
                output.accept(LIGHT_GRAY_PRISMARINE_TILE_STAIRS.get());

                output.accept(LIGHT_GRAY_PRISMARINE_SMALL_TILE.get());
                output.accept(LIGHT_GRAY_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(LIGHT_GRAY_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(GRAY_PRISMARINE_TILE.get());
                output.accept(GRAY_PRISMARINE_TILE_SLAB.get());
                output.accept(GRAY_PRISMARINE_TILE_STAIRS.get());

                output.accept(GRAY_PRISMARINE_SMALL_TILE.get());
                output.accept(GRAY_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(GRAY_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(DARK_GRAY_PRISMARINE_TILE.get());
                output.accept(DARK_GRAY_PRISMARINE_TILE_SLAB.get());
                output.accept(DARK_GRAY_PRISMARINE_TILE_STAIRS.get());

                output.accept(DARK_GRAY_PRISMARINE_SMALL_TILE.get());
                output.accept(DARK_GRAY_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(DARK_GRAY_PRISMARINE_SMALL_TILE_STAIRS.get());

                output.accept(BLACK_PRISMARINE_TILE.get());
                output.accept(BLACK_PRISMARINE_TILE_SLAB.get());
                output.accept(BLACK_PRISMARINE_TILE_STAIRS.get());

                output.accept(BLACK_PRISMARINE_SMALL_TILE.get());
                output.accept(BLACK_PRISMARINE_SMALL_TILE_SLAB.get());
                output.accept(BLACK_PRISMARINE_SMALL_TILE_STAIRS.get());


            })
            .build()
    );
}
