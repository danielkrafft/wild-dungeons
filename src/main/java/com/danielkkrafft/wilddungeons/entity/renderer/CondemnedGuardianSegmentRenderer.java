package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardianSegment;
import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeSegment;
import com.danielkkrafft.wilddungeons.entity.model.CondemnedGuardianSegmentModel;
import com.danielkkrafft.wilddungeons.entity.model.SkelepedeSegmentModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class CondemnedGuardianSegmentRenderer extends GeoEntityRenderer<CondemnedGuardianSegment> {
    public CondemnedGuardianSegmentRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CondemnedGuardianSegmentModel());
        this.addRenderLayer(new CondemnedGuardianSegmentGlowLayer(this));
    }

    @Override
    public void render(CondemnedGuardianSegment entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(partialTick) - 40));
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
