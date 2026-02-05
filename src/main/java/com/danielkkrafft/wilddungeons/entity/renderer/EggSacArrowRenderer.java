package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EggSacArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EggSacArrowRenderer extends ArrowRenderer<EggSacArrow, ArrowRenderState> {
    public static final Identifier RESOURCE_LOCATION = WildDungeons.rl("textures/entity/egg_sac_arrow.png");

    public EggSacArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return null;
    }

    @Override
    protected Identifier getTextureLocation(ArrowRenderState arrowRenderState) {
        return RESOURCE_LOCATION;
    }
}