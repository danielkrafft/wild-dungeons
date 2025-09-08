package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.Spiderling;
import com.danielkkrafft.wilddungeons.entity.model.SpiderlingModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class SpiderlingRenderer extends GeoEntityRenderer<Spiderling> {
    public SpiderlingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpiderlingModel());
    }
}
