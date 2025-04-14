package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.BusinessGolem;
import com.danielkkrafft.wilddungeons.entity.model.BusinessGolemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BusinessGolemRenderer extends MobRenderer<BusinessGolem, BusinessGolemModel<BusinessGolem>> {
    private static final ResourceLocation GOLEM_LOCATION = WildDungeons.rl("textures/entity/village/business_golem.png");

    public BusinessGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new BusinessGolemModel<>(context.bakeLayer(BusinessGolemModel.LAYER_LOCATION)), 0.7F);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull BusinessGolem entity) {
        return GOLEM_LOCATION;
    }

    protected void setupRotations(@NotNull BusinessGolem entity, @NotNull PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        if (!((double)entity.walkAnimation.speed() < 0.01)) {
            float f1 = entity.walkAnimation.position(partialTick) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
        }
    }
}
