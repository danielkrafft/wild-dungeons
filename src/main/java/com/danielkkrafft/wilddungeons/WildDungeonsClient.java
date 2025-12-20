package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.entity.model.*;
import com.danielkkrafft.wilddungeons.entity.renderer.*;
import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDFluids;
import com.danielkkrafft.wilddungeons.util.CameraShakeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.awt.*;

import static com.danielkkrafft.KeyBindings.TOGGLE_ESSENCE_TYPE;

@EventBusSubscriber(modid = "wilddungeons", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WildDungeonsClient {

    public static final String MODID = "wilddungeons";

    public static void initializeClient() {
        NeoForge.EVENT_BUS.addListener(WildDungeonsClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(WildDungeonsClient::onCameraShake);
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
        event.registerEntityRenderer(WDEntities.EMERALD_PROJECTILE.get(), context -> new ThrownItemRenderer<>(context, 2f, false));
        event.registerEntityRenderer(WDEntities.WIND_ARROW.get(), ctx -> new WDArrowRenderer<>(ctx, new WindArrowModel()));
        event.registerEntityRenderer(WDEntities.BLACK_HOLE.get(), BlackHoleRenderer::new);
        event.registerEntityRenderer(WDEntities.SPIDERLING.get(), SpiderlingRenderer::new);
        event.registerEntityRenderer(WDEntities.FRIENDLY_SPIDERLING.get(), SpiderlingRenderer::new);
        event.registerEntityRenderer(WDEntities.SKELEPEDE.get(), SkelepedeMainRenderer::new);
        event.registerEntityRenderer(WDEntities.SKELEPEDE_SEGMENT.get(), SkelepedeSegmentRenderer::new);
        event.registerEntityRenderer(WDEntities.SMALL_TOXIC_WISP.get(),  (EntityRendererProvider.Context context) -> new ToxicWispRenderer(context, false));
        event.registerEntityRenderer(WDEntities.LARGE_TOXIC_WISP.get(),  (EntityRendererProvider.Context context) -> new ToxicWispRenderer(context, true));
        event.registerEntityRenderer(WDEntities.COPPER_SENTINEL.get(),  CopperSentinelRenderer::new);
        event.registerEntityRenderer(WDEntities.PRIMAL_CREEPER.get(),  PrimalCreeperRenderer::new);
        event.registerEntityRenderer(WDEntities.EGG_SAC_ARROW.get(),  EggSacArrowRenderer::new);
        event.registerEntityRenderer(WDEntities.THROWN_NAUTILUS_SHIELD.get(),  ThrownNautilusShieldRenderer::new);

        event.registerEntityRenderer(WDEntities.PRIMED_DENSE_TNT.get(),  PrimedDenseTntRenderer::new);

        event.registerEntityRenderer(WDEntities.CONDEMNED_GUARDIAN.get(),  CondemnedGuardianRenderer::new);
        event.registerEntityRenderer(WDEntities.CONDEMNED_GUARDIAN_SEGMENT.get(),  CondemnedGuardianSegmentRenderer::new);


        event.registerBlockEntityRenderer(WDBlockEntities.TOXIC_GAS_ENTITY.get(), GasBlockRenderer::new);

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
        ItemBlockRenderTypes.setRenderLayer(WDBlocks.SMALL_DETONITE_BUD.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WDBlocks.MEDIUM_DETONITE_BUD.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WDBlocks.LARGE_DETONITE_BUD.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WDBlocks.DETONITE_CLUSTER.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WDBlocks.PUTRID_EGG.get(), RenderType.cutoutMipped());

        event.registerFluidType(new IClientFluidTypeExtensions() {

            private static final ResourceLocation TOXIC_UNDERWATER = rl("textures/block/toxic_underwater.png");
            private static final ResourceLocation TOXIC_STILL = rl("block/toxic_still");
            private static final ResourceLocation TOXIC_FLOW = rl("block/toxic_flow");

            @Override
            public int getTintColor() {
                return new Color(151, 255, 0).getRGB();
            }

            @Override
            public ResourceLocation getStillTexture() {
                return TOXIC_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return TOXIC_FLOW;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return TOXIC_UNDERWATER;
            }

        }, WDFluids.TOXIC_SLUDGE_TYPE);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AmogusModel.LAYER_LOCATION, AmogusModel::createBodyLayer);
        event.registerLayerDefinition(BusinessGolemModel.LAYER_LOCATION, BusinessGolemModel::createBodyLayer);
        event.registerLayerDefinition(BusinessIllagerModel.LAYER_LOCATION, BusinessIllagerModel::createBodyLayer);
        event.registerLayerDefinition(EmeraldWispModel.SMALL_LAYER_LOCATION, () -> EmeraldWispModel.createBodyLayer(false));
        event.registerLayerDefinition(EmeraldWispModel.LARGE_LAYER_LOCATION, () -> EmeraldWispModel.createBodyLayer(true));
        event.registerLayerDefinition(ToxicWispModel.SMALL_LAYER_LOCATION, () -> ToxicWispModel.createBodyLayer(false));
        event.registerLayerDefinition(ToxicWispModel.LARGE_LAYER_LOCATION, () -> ToxicWispModel.createBodyLayer(true));

    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            event.register(TOGGLE_ESSENCE_TYPE);
        }
    }

    //these are bus.GAME events, not bus.MOD events
    public static void onClientTick(ClientTickEvent.Post event){
        // --- Camera Shake ---
        float delta = Minecraft.getInstance().getFrameTimeNs() / 50_000_000f; // for your camera shake
        CameraShakeUtil.tick(delta);
    }

    public static void onCameraShake(ViewportEvent.ComputeCameraAngles event) {
        float shake = CameraShakeUtil.getShakeStrength();
        if (shake > 0f) {
            double yawOffset = (Math.random() - 0.5) * shake;
            double pitchOffset = (Math.random() - 0.5) * shake;
            event.setYaw(event.getYaw() + (float)yawOffset);
            event.setPitch(event.getPitch() + (float)pitchOffset);
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
