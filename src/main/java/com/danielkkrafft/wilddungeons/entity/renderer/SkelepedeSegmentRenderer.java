package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeSegment;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class SkelepedeSegmentRenderer extends GeoEntityRenderer<SkelepedeSegment> {
    public SkelepedeSegmentRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, ClientModel.ofEntity("skelepede_segment", "entity"));
    }
}
