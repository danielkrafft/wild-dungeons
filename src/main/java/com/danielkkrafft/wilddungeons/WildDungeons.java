package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundOpenConnectionBlockUIPacket;
import com.danielkkrafft.wilddungeons.network.serverbound.ServerboundUpdateConnectionBlockPacket;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.Alignments;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import com.danielkkrafft.wilddungeons.entity.renderer.EssenceOrbRenderer;
import com.danielkkrafft.wilddungeons.entity.renderer.RiftRenderer;
import com.danielkkrafft.wilddungeons.registry.WDEvents;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.ui.CustomHUDHandler;
import com.danielkkrafft.wilddungeons.ui.EssenceBar;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import com.danielkkrafft.wilddungeons.world.dimension.tools.UpdateDimensionsPacket;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(WildDungeons.MODID)
public class WildDungeons {
    public static final String MODID = "wilddungeons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public WildDungeons(IEventBus modEventBus, ModContainer modContainer) {
        WDEntities.ENTITIES.register(modEventBus);
        WDBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        WDItems.ITEMS.register(modEventBus);
        WDBlocks.BLOCKS.register(modEventBus);

        modEventBus.register(WildDungeons.class);

        NeoForge.EVENT_BUS.register(CustomHUDHandler.class);
        NeoForge.EVENT_BUS.register(WDEvents.class);

    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        FileUtil.setGamePath(FMLPaths.GAMEDIR.get());
        FileUtil.setDungeonsPath(FileUtil.getGamePath().resolve(MODID));
        Alignments.setupAlignments();
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(ClientboundUpdateWDPlayerPacket.TYPE, ClientboundUpdateWDPlayerPacket.STREAM_CODEC, ClientboundUpdateWDPlayerPacket::handle);
        registrar.playToClient(UpdateDimensionsPacket.TYPE, UpdateDimensionsPacket.STREAM_CODEC, UpdateDimensionsPacket::handle);
        registrar.playToClient(ClientboundOpenConnectionBlockUIPacket.TYPE, ClientboundOpenConnectionBlockUIPacket.STREAM_CODEC, ClientboundOpenConnectionBlockUIPacket::handle);

        registrar.playToServer(ServerboundUpdateConnectionBlockPacket.TYPE, ServerboundUpdateConnectionBlockPacket.STREAM_CODEC, ServerboundUpdateConnectionBlockPacket::handle);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR, rl("essence_bar"), EssenceBar.INSTANCE);
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
        event.registerBlockEntityRenderer(WDBlockEntities.RIFT_BLOCK_ENTITY.get(), RiftRenderer::new);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public static Logger getLogger() {
        return LOGGER;
    }
}
