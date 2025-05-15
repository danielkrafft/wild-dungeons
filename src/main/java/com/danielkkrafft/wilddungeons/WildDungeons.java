package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.datagen.WDItemModelProvider;
import com.danielkkrafft.wilddungeons.entity.*;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import com.danielkkrafft.wilddungeons.entity.boss.BusinessCEO;
import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;
import com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.registry.*;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.danielkkrafft.wilddungeons.world.dimension.tools.UpdateDimensionsPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(WildDungeons.MODID)
public class WildDungeons {
    public static final String MODID = "wilddungeons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WildDungeons(IEventBus modEventBus, ModContainer modContainer) {
        Serializer.setup();
//        BedrockBlockstateHelper.generateBedrockBlockstate();//this is only used when we need to make the gigantic blockstate list for wdbedrock

        WDEntities.ENTITIES.register(modEventBus);
        WDBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        WDItems.ITEMS.register(modEventBus);
        WDFluids.FLUID_TYPES.register(modEventBus);
        WDFluids.FLUIDS.register(modEventBus);
        WDBlocks.BLOCKS.register(modEventBus);
        WDCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        WDStructureTypes.STRUCTURE_TYPES.register(modEventBus);
        WDStructurePieceTypes.STRUCTURE_PIECE_TYPES.register(modEventBus);
        WDSoundEvents.SOUND_EVENTS.register(modEventBus);
        WDDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

        modEventBus.register(WildDungeons.class);
        NeoForge.EVENT_BUS.register(WDEvents.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        FileUtil.setGamePath(FMLPaths.GAMEDIR.get());
        FileUtil.setDungeonsPath(FileUtil.getGamePath().resolve(MODID));
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        SimplePacketManager.setup(registrar);
        registrar.playToClient(UpdateDimensionsPacket.TYPE, UpdateDimensionsPacket.STREAM_CODEC, UpdateDimensionsPacket::handle);
    }

    @SubscribeEvent
    public static void registerExtra(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.CHUNK_GENERATOR)) {
            Registry.register(BuiltInRegistries.CHUNK_GENERATOR, "wilddungeons:empty_generator", EmptyGenerator.CODEC);
        }
    }


    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent e)
    {
        e.put(WDEntities.BREEZE_GOLEM.get(), BreezeGolem.setAttributes());
        e.put(WDEntities.MUTANT_BOGGED.get(), MutantBogged.setAttributes());
        e.put(WDEntities.AMOGUS.get(), AmogusEntity.setAttributes());
        e.put(WDEntities.NETHER_DRAGON.get(), NetherDragonEntity.setAttributes());
        e.put(WDEntities.BUSINESS_GOLEM.get(), BusinessGolem.createAttributes().build());
        e.put(WDEntities.BUSINESS_VINDICATOR.get(), BusinessVindicator.createAttributes().build());
        e.put(WDEntities.BUSINESS_EVOKER.get(), BusinessEvoker.createAttributes().build());
        e.put(WDEntities.SMALL_EMERALD_WISP.get(), EmeraldWisp.createAttributes().build());
        e.put(WDEntities.LARGE_EMERALD_WISP.get(), LargeEmeraldWisp.createAttributes().build());
        e.put(WDEntities.FRIENDLY_EMERALD_WISP.get(), FriendlyEmeraldWisp.createAttributes().build());
        e.put(WDEntities.FRIENDLY_LARGE_EMERALD_WISP.get(), FriendlyLargeEmeraldWisp.createAttributes().build());
        e.put(WDEntities.BUSINESS_CEO.get(), BusinessCEO.setAttributes());
    }



    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public static Logger getLogger() {
        return LOGGER;
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new WDItemModelProvider(output, helper));
    }
}
