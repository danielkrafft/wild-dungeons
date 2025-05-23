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
        withExistingParent(WDItems.OVERFLOW_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));
        withExistingParent(WDItems.OVERFLOW_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));
        withExistingParent(WDItems.OVERFLOW_UNDERWATER_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));
        withExistingParent(WDItems.OVERFLOW_UNDERWATER_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));
        withExistingParent(WDItems.ANGEL_INVESTOR_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));
        withExistingParent(WDItems.ANGEL_INVESTOR_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));
        withExistingParent(WDItems.THE_CAPITAL_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));
        withExistingParent(WDItems.THE_CAPITAL_SAFE_MUSIC_DISC.getId().getPath(), mcLoc("item/generated")).texture("layer0", WildDungeons.rl("item/village_music_disc"));

    }
}
