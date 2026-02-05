//package com.danielkkrafft.wilddungeons.entity.renderer;
//
//import com.danielkkrafft.wilddungeons.WildDungeons;
//import com.danielkkrafft.wilddungeons.entity.AmogusEntity;
//import com.danielkkrafft.wilddungeons.entity.model.AmogusModel;
//import net.minecraft.client.renderer.entity.EntityRendererProvider;
//import net.minecraft.client.renderer.entity.MobRenderer;
//import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//import net.minecraft.resources.Identifier;
//
//public class AmogusRenderer extends MobRenderer<AmogusEntity, LivingEntityRenderState, AmogusModel<AmogusEntity>> {
//
//    public AmogusRenderer(EntityRendererProvider.Context context) {
//        super(context, new AmogusModel<>(context.bakeLayer(AmogusModel.LAYER_LOCATION)), 0.25f);
//    }
//
//    @Override
//    public LivingEntityRenderState createRenderState() {
//        return null;
//    }
//
//    @Override
//    public Identifier getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
//        return WildDungeons.rl("textures/entity/amogus.png");
//    }
//}
