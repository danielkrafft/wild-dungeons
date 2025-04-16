package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.BusinessCEO;
import com.danielkkrafft.wilddungeons.entity.model.BusinessCEOModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BusinessCEORenderer extends GeoEntityRenderer<BusinessCEO> {
    public BusinessCEORenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BusinessCEOModel());
    }
}
