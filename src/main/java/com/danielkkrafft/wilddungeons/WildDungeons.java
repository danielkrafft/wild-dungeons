package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.datagen.WDItemModelProvider;
import com.danielkkrafft.wilddungeons.entity.*;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import com.danielkkrafft.wilddungeons.entity.boss.BusinessCEO;
import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;
import com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity;
import com.danielkkrafft.wilddungeons.entity.model.AmogusModel;
import com.danielkkrafft.wilddungeons.entity.model.BusinessGolemModel;
import com.danielkkrafft.wilddungeons.entity.model.BusinessIllagerModel;
import com.danielkkrafft.wilddungeons.entity.model.EmeraldWispModel;
import com.danielkkrafft.wilddungeons.entity.renderer.*;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.registry.*;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.danielkkrafft.wilddungeons.world.dimension.tools.UpdateDimensionsPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import static com.danielkkrafft.KeyBindings.TOGGLE_ESSENCE_TYPE;

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
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WDEntities.ESSENCE_ORB.get(), EssenceOrbRenderer::new);
        event.registerEntityRenderer(WDEntities.AMOGUS.get(), AmogusRenderer::new);
        event.registerEntityRenderer(WDEntities.OFFERING.get(), OfferingRenderer::new);
        event.registerEntityRenderer(WDEntities.BREEZE_GOLEM.get(), BreezeGolemRenderer::new);
        event.registerEntityRenderer(WDEntities.MUTANT_BOGGED.get(), MutantBoggedRenderer::new);
        event.registerEntityRenderer(WDEntities.NETHER_DRAGON.get(), NetherDragonRenderer::new);
        event.registerEntityRenderer(WDEntities.PIERCING_ARROW.get(), PiercingArrowRenderer::new);
        event.registerEntityRenderer(WDEntities.WIND_CHARGE_PROJECTILE.get(), WindChargeProjectileRenderer::new);
        event.registerEntityRenderer(WDEntities.WIND_CHARGE_PROJECTILE.get(), WindChargeProjectileRenderer::new);//double registered? error?
        event.registerEntityRenderer(WDEntities.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
        event.registerEntityRenderer(WDEntities.LASER_BEAM.get(), LaserbeamRenderer::new);
        event.registerEntityRenderer(WDEntities.ESSENCE_BOTTLE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(WDEntities.BUSINESS_GOLEM.get(), BusinessGolemRenderer::new);
        event.registerEntityRenderer(WDEntities.BUSINESS_VINDICATOR.get(), BusinessVindicatorRenderer::new);
        event.registerEntityRenderer(WDEntities.BUSINESS_EVOKER.get(), BusinessEvokerRenderer::new);
        event.registerEntityRenderer(WDEntities.SMALL_EMERALD_WISP.get(), (EntityRendererProvider.Context context) -> new EmeraldWispRenderer(context, false));
        event.registerEntityRenderer(WDEntities.LARGE_EMERALD_WISP.get(), (EntityRendererProvider.Context context) -> new EmeraldWispRenderer(context, true));
        event.registerEntityRenderer(WDEntities.FRIENDLY_EMERALD_WISP.get(), (EntityRendererProvider.Context context) -> new EmeraldWispRenderer(context, false));
        event.registerEntityRenderer(WDEntities.FRIENDLY_LARGE_EMERALD_WISP.get(), (EntityRendererProvider.Context context) -> new EmeraldWispRenderer(context, true));
        event.registerEntityRenderer(WDEntities.BUSINESS_CEO.get(), BusinessCEORenderer::new);
        event.registerEntityRenderer(WDEntities.EMERALD_PROJECTILE.get(), context -> new ThrownItemRenderer<>( context,2f,false));
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {

        event.registerFluidType(new IClientFluidTypeExtensions() {

            private static final ResourceLocation LIFE_UNDERWATER = rl("textures/block/life_underwater.png");
            private static final ResourceLocation LIFE_STILL = rl("block/life_still");
            private static final ResourceLocation LIFE_FLOW = rl("block/life_flow");

            @Override
            public int getTintColor() {
                return 0x90FF5095;
            }

            @Override
            public ResourceLocation getStillTexture() {
                return LIFE_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return LIFE_FLOW;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return LIFE_UNDERWATER;
            }

        }, WDFluids.LIFE_LIQUID_TYPE);

        ItemBlockRenderTypes.setRenderLayer(WDFluids.LIFE_LIQUID.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(WDFluids.FLOWING_LIFE_LIQUID.get(), RenderType.translucent());

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

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AmogusModel.LAYER_LOCATION, AmogusModel::createBodyLayer);
        event.registerLayerDefinition(BusinessGolemModel.LAYER_LOCATION, BusinessGolemModel::createBodyLayer);
        event.registerLayerDefinition(BusinessIllagerModel.LAYER_LOCATION, BusinessIllagerModel::createBodyLayer);
        event.registerLayerDefinition(EmeraldWispModel.SMALL_LAYER_LOCATION, () -> EmeraldWispModel.createBodyLayer(false));
        event.registerLayerDefinition(EmeraldWispModel.LARGE_LAYER_LOCATION, () -> EmeraldWispModel.createBodyLayer(true));
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        //so... turns out if you register the keybinding in a 'dist = client' class, it doesn't register in the keybinding list. BUT, if you register it in the common class, it does. So we have to do this here, and then filter for the client
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            event.register(TOGGLE_ESSENCE_TYPE);
        }
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
