package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.block.WDFluids;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry;
import com.danielkkrafft.wilddungeons.entity.AmogusEntity;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;
import com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity;
import com.danielkkrafft.wilddungeons.entity.model.AmogusModel;
import com.danielkkrafft.wilddungeons.entity.renderer.*;
import com.danielkkrafft.wilddungeons.network.clientbound.*;
import com.danielkkrafft.wilddungeons.network.serverbound.ServerboundRestorePlayerGamemodePacket;
import com.danielkkrafft.wilddungeons.network.serverbound.ServerboundUpdateConnectionBlockPacket;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.*;
import com.danielkkrafft.wilddungeons.sound.SoundscapeHandler;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.danielkkrafft.wilddungeons.world.dimension.tools.UpdateDimensionsPacket;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(WildDungeons.MODID)
public class WildDungeons {
    public static final String MODID = "wilddungeons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WildDungeons(IEventBus modEventBus, ModContainer modContainer) {
        Serializer.setup();
//        BedrockBlockstateHelper.generateBedrockBlockstate();

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

        PerkRegistry.setupPerks();

        modEventBus.register(WildDungeons.class);
        NeoForge.EVENT_BUS.register(WDEvents.class);
        NeoForge.EVENT_BUS.register(WDPlayerManager.class);
        NeoForge.EVENT_BUS.register(DungeonPerk.class);
        NeoForge.EVENT_BUS.register(SoundscapeHandler.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        FileUtil.setGamePath(FMLPaths.GAMEDIR.get());
        FileUtil.setDungeonsPath(FileUtil.getGamePath().resolve(MODID));
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(ClientboundUpdateWDPlayerPacket.TYPE, ClientboundUpdateWDPlayerPacket.STREAM_CODEC, ClientboundUpdateWDPlayerPacket::handle);
        registrar.playToClient(UpdateDimensionsPacket.TYPE, UpdateDimensionsPacket.STREAM_CODEC, UpdateDimensionsPacket::handle);
        registrar.playToClient(ClientboundOpenConnectionBlockUIPacket.TYPE, ClientboundOpenConnectionBlockUIPacket.STREAM_CODEC, ClientboundOpenConnectionBlockUIPacket::handle);
        registrar.playToClient(ClientboundNullScreenPacket.TYPE, ClientboundNullScreenPacket.STREAM_CODEC, ClientboundNullScreenPacket::handle);
        registrar.playToClient(ClientboundLoadingScreenPacket.TYPE, ClientboundLoadingScreenPacket.STREAM_CODEC, ClientboundLoadingScreenPacket::handle);
        registrar.playToClient(ClientboundPostDungeonScreenPacket.TYPE, ClientboundPostDungeonScreenPacket.STREAM_CODEC, ClientboundPostDungeonScreenPacket::handle);
        registrar.playToClient(ClientboundPlayDynamicSoundPacket.TYPE, ClientboundPlayDynamicSoundPacket.STREAM_CODEC, ClientboundPlayDynamicSoundPacket::handle);
        registrar.playToClient(ClientboundSwitchSoundscapePacket.TYPE, ClientboundSwitchSoundscapePacket.STREAM_CODEC, ClientboundSwitchSoundscapePacket::handle);

        registrar.playToServer(ServerboundUpdateConnectionBlockPacket.TYPE, ServerboundUpdateConnectionBlockPacket.STREAM_CODEC, ServerboundUpdateConnectionBlockPacket::handle);
        registrar.playToServer(ServerboundRestorePlayerGamemodePacket.TYPE, ServerboundRestorePlayerGamemodePacket.STREAM_CODEC, ServerboundRestorePlayerGamemodePacket::handle);
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
        event.registerEntityRenderer(WDEntities.WIND_CHARGE_PROJECTILE.get(), WindChargeProjectileRenderer::new);
        event.registerEntityRenderer(WDEntities.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
        event.registerEntityRenderer(WDEntities.LASER_BEAM.get(), LaserbeamRenderer::new);
        event.registerEntityRenderer(WDEntities.ESSENCE_BOTTLE.get(), ThrownItemRenderer::new);
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
    }

    public static ShaderInstance RIFT_SHADER;
    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), rl("rift"), DefaultVertexFormat.POSITION), shader -> RIFT_SHADER = shader);
        } catch (IOException e) {
            WildDungeons.getLogger().info("Failed to load custom shader!", e);
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AmogusModel.LAYER_LOCATION, AmogusModel::createBodyLayer);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public static Logger getLogger() {
        return LOGGER;
    }
}
