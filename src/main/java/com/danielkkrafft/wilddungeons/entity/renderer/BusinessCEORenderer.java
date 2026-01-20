package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.BusinessCEO;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BusinessCEORenderer extends GeoEntityRenderer<BusinessCEO> {
    public BusinessCEORenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ClientModel<>("business_ceo", "entity"));
    }
}
