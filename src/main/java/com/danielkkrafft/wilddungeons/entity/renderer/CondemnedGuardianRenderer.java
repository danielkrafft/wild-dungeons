package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardian;
import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardianSegment;
import com.danielkkrafft.wilddungeons.entity.model.CondemnedGuardianHeadModel;
import com.danielkkrafft.wilddungeons.entity.model.CondemnedGuardianSegmentModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class CondemnedGuardianRenderer extends GeoEntityRenderer<CondemnedGuardian> {

    public CondemnedGuardianRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CondemnedGuardianHeadModel());
        this.addRenderLayer(new CondemnedGuardianGlowLayer(this));
    }



    @Override
    public void render(CondemnedGuardian entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(partialTick)));
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

    }


}
