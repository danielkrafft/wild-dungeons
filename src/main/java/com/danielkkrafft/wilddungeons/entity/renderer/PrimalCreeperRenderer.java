package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.PrimalCreeper;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrimalCreeperRenderer extends GeoEntityRenderer<PrimalCreeper> {
    private static final ClientModel<PrimalCreeper> MODEL =
            ClientModel.<PrimalCreeper>ofEntity("primal_creeper")
            .withConditionalTexture(PrimalCreeper::isShiny, "primal_creeper_black");

    public PrimalCreeperRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, MODEL);
    }
}
