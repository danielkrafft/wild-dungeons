package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.CopperSentinel;
import com.danielkkrafft.wilddungeons.entity.PrimalCreeper;
import com.danielkkrafft.wilddungeons.entity.model.CopperSentinelModel;
import com.danielkkrafft.wilddungeons.entity.model.PrimalCreeperModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrimalCreeperRenderer extends GeoEntityRenderer<PrimalCreeper> {
    public PrimalCreeperRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PrimalCreeperModel());
    }
}
