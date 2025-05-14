package com.danielkkrafft.wilddungeons.datagen;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class WDItemModelProvider extends ItemModelProvider {
    public WDItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super (output, WildDungeons.MODID, helper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(WDItems.OVERFLOW_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_13"));
        withExistingParent(WDItems.OVERFLOW_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_cat"));
        withExistingParent(WDItems.OVERFLOW_UNDERWATER_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_blocks"));
        withExistingParent(WDItems.OVERFLOW_UNDERWATER_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_chirp"));
        withExistingParent(WDItems.ANGEL_INVESTOR_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_far"));
        withExistingParent(WDItems.ANGEL_INVESTOR_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_mall"));
        withExistingParent(WDItems.THE_CAPITAL_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_mellohi"));
        withExistingParent(WDItems.THE_CAPITAL_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", mcLoc("item/music_disc_stal"));

    }
}
