package com.danielkkrafft.wilddungeons.datagen;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = WildDungeons.MODID, bus = EventBusSubscriber.Bus.MOD)
public class WDDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // --- CLIENT ---
        generator.addProvider(event.includeClient(), new WDItemModelProvider(packOutput, existingFileHelper));

        // --- SERVER ---
        generator.addProvider(event.includeServer(), new WDDataPackProvider(packOutput, lookupProvider));
    }
}
