package com.danielkkrafft.wilddungeons.datagen;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Collections;

import static com.danielkkrafft.wilddungeons.registry.WDBlocks.PRISMARINE_TILES;

public class WDModelProvider extends ModelProvider {
    public WDModelProvider(PackOutput output) {
        super (output, WildDungeons.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_BASIC.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_STAIRS.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_SLAB.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_WALL.get());

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_BASIC_2.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_STAIRS_2.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_SLAB_2.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_WALL_2.get());

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_BASIC_3.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_STAIRS_3.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_SLAB_3.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_WALL_3.get());

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_BASIC_4.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_STAIRS_4.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_SLAB_4.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_WALL_4.get());

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_LIGHT.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_LIGHT_2.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_LIGHT_3.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_LIGHT_4.get());

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_HANGING_LIGHT.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_HANGING_LIGHT_2.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_HANGING_LIGHT_3.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_HANGING_LIGHT_4.get());

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.PRISMARINE_TILE.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.PRISMARINE_TILE_STAIRS.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.PRISMARINE_TILE_SLAB.get());

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.PRISMARINE_SMALL_TILE.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.PRISMARINE_SMALL_TILE_STAIRS.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.PRISMARINE_SMALL_TILE_SLAB.get());

        for (WDBlocks.PrismarineSet set : WDBlocks.PRISMARINE_TILES.values()) {
            simpleBlockWithItem(itemModels, blockModels, set.tile().get());
            simpleBlockWithItem(itemModels, blockModels, set.tileStairs().get());
            simpleBlockWithItem(itemModels, blockModels, set.tileSlab().get());
        }

        simpleBlockWithItem(itemModels, blockModels, WDBlocks.CONNECTION_BLOCK.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.ROTTEN_MOSS.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.LIFE_LIQUID.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.HEAVY_RUNE.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.WD_SECRET.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.EMERALD_PILE.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.IRON_GRATE.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.DETONITE_ORE.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.DETONITE_BLOCK.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.DETONITE_CRYSTAL_BLOCK.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.DETONITE_CLUSTER.get());
        simpleBlockWithItem(itemModels, blockModels, WDBlocks.DENSE_TNT.get());

        blockModels.createAmethystCluster(WDBlocks.SMALL_DETONITE_BUD.get());
        blockModels.createAmethystCluster(WDBlocks.MEDIUM_DETONITE_BUD.get());
        blockModels.createAmethystCluster(WDBlocks.LARGE_DETONITE_BUD.get());

        simpleItem(itemModels, WDItems.OVERFLOW_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.OVERFLOW_SAFE_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.OVERFLOW_UNDERWATER_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.OVERFLOW_UNDERWATER_SAFE_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.ANGEL_INVESTOR_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.ANGEL_INVESTOR_SAFE_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.THE_CAPITAL_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.THE_CAPITAL_SAFE_MUSIC_DISC.get());
        simpleItem(itemModels, WDItems.DEBUG_ITEM.get());
        simpleItem(itemModels, WDItems.OFFERING_ITEM.get());
        simpleItem(itemModels, WDItems.RIFT_ITEM.get());
        simpleItem(itemModels, WDItems.PERK_TESTER.get());
        simpleItem(itemModels, WDItems.MEATHOOK_ITEM.get());
        simpleItem(itemModels, WDItems.AMOGUS_STAFF.get());
        simpleItem(itemModels, WDItems.ESSENCE_BOTTLE.get());
        simpleItem(itemModels, WDItems.WIND_HAMMER_ITEM.get());
        simpleItem(itemModels, WDItems.LIFE_LIQUID_BUCKET.get());
        simpleItem(itemModels, WDItems.WD_DUNGEON_KEY.get());
        simpleItem(itemModels, WDItems.ROOM_EXPORT_WAND.get());
        simpleItem(itemModels, WDItems.INSTANT_LOADOUT_DIAMOND.get());
        simpleItem(itemModels, WDItems.INSTANT_LOADOUT_GOLD.get());
        simpleItem(itemModels, WDItems.INSTANT_LOADOUT_IRON.get());
        simpleItem(itemModels, WDItems.INSTANT_LOADOUT_LEATHER.get());
        simpleItem(itemModels, WDItems.INSTANT_LOADOUT_NETHERITE.get());
        simpleItem(itemModels, WDItems.BOSS_KEY.get());
        simpleItem(itemModels, WDItems.DETONITE_CRYSTAL.get());
        simpleItem(itemModels, WDItems.WATCHFUL_EYE.get());
        simpleItem(itemModels, WDItems.EGG_SAC_ARROWS.get());
        simpleItem(itemModels, WDItems.NAUTILUS_SHIELD.get());
    }

    private static void simpleItem(ItemModelGenerators itemModels, Item item) {
        itemModels.itemModelOutput.accept(item, new BlockModelWrapper.Unbaked(ModelLocationUtils.getModelLocation(item), Collections.emptyList()));
    }

    private static void simpleBlockItem(ItemModelGenerators itemModels, Block block) {
        itemModels.itemModelOutput.accept(block.asItem(), new BlockModelWrapper.Unbaked(ModelLocationUtils.getModelLocation(block), Collections.emptyList()));
    }

    private static void simpleBlockWithItem(ItemModelGenerators itemModels, BlockModelGenerators blockModels, Block block) {
        blockModels.createTrivialCube(block);
        itemModels.itemModelOutput.accept(block.asItem(), new BlockModelWrapper.Unbaked(ModelLocationUtils.getModelLocation(block), Collections.emptyList()));
    }
}
