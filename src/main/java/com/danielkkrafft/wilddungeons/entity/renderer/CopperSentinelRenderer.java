package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.CopperSentinel;
import com.danielkkrafft.wilddungeons.entity.model.CopperSentinelModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class CopperSentinelRenderer extends GeoEntityRenderer<CopperSentinel> {
    public CopperSentinelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CopperSentinelModel());
    }
}
