package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.CopperSentinel;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class CopperSentinelRenderer extends GeoEntityRenderer<CopperSentinel> {
    private static final ClientModel<CopperSentinel> MODEL =
            ClientModel.<CopperSentinel>ofEntity("copper_sentinel")
                    .withConditionalTexture(sentinel -> (sentinel.getHealth() / sentinel.getMaxHealth()) < 0.25f, "copper_sentinel_oxidized_3")
                    .withConditionalTexture(sentinel -> (sentinel.getHealth() / sentinel.getMaxHealth()) < 0.5f, "copper_sentinel_oxidized_2")
                    .withConditionalTexture(sentinel -> (sentinel.getHealth() / sentinel.getMaxHealth()) < 0.75f, "copper_sentinel_oxidized_1");

    public CopperSentinelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, MODEL);
    }
}
