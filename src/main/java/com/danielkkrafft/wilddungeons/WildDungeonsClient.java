package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.entity.model.AmogusModel;
import com.danielkkrafft.wilddungeons.entity.model.BusinessGolemModel;
import com.danielkkrafft.wilddungeons.entity.model.BusinessIllagerModel;
import com.danielkkrafft.wilddungeons.entity.model.EmeraldWispModel;
import com.danielkkrafft.wilddungeons.entity.renderer.*;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import static com.danielkkrafft.KeyBindings.TOGGLE_ESSENCE_TYPE;

@EventBusSubscriber(modid = "wilddungeons", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WildDungeonsClient {

    public static final String MODID = "wilddungeons";

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

}
