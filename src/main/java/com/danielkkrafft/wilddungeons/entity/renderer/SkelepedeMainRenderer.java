package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeMain;
import com.danielkkrafft.wilddungeons.entity.model.SkelepedeMainModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class SkelepedeMainRenderer extends GeoEntityRenderer<SkelepedeMain> {
    public SkelepedeMainRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkelepedeMainModel());
    }
}
