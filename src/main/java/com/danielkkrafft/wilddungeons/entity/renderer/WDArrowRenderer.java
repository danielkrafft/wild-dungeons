package com.danielkkrafft.wilddungeons.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

public class WDArrowRenderer<T extends com.danielkkrafft.wilddungeons.entity.BaseClasses.WDArrow> extends GeoEntityRenderer<T> {
    public WDArrowRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0.25f;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T arrow, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay, int color) {
        super.actuallyRender(poseStack, arrow, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);

        Optional<GeoBone> arrowBone = getGeoModel().getBone("arrow");
        if (arrowBone.isPresent()) {
            GeoBone bone = arrowBone.get();

            float lerpedYaw = Mth.lerp(partialTick, arrow.yRotO, arrow.getYRot());
            float lerpedPitch = Mth.lerp(partialTick, arrow.xRotO, arrow.getXRot());

            // Convert degrees to radians for GeckoLib bone rotation
            bone.setRotY((float) Math.toRadians(-lerpedYaw));
            bone.setRotX((float) Math.toRadians(lerpedPitch));
        }
    }


}