package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.AmogusEntity;
import com.danielkkrafft.wilddungeons.entity.model.AmogusModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AmogusRenderer extends MobRenderer<AmogusEntity, AmogusModel<AmogusEntity>> {

    public AmogusRenderer(EntityRendererProvider.Context context) {
        super(context, new AmogusModel<>(context.bakeLayer(AmogusModel.LAYER_LOCATION)), 0.25f);
    }
    public AmogusRenderer(EntityRendererProvider.Context context, AmogusModel<AmogusEntity> model, float shadowRadius) {
        super(context, model, 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(AmogusEntity amogusEntity) {
        return WildDungeons.rl("textures/entity/amogus.png");
    }
}
