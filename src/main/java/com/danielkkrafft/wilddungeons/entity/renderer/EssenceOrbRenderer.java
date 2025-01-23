package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.dungeon.components.Alignments;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.util.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;

public class EssenceOrbRenderer extends ExperienceOrbRenderer {
    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

    public EssenceOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ExperienceOrb entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        EssenceOrb essenceOrb = (EssenceOrb) entity;
        int hueOffset = Alignments.ALIGNMENTS.get(essenceOrb.essence_type).ORB_HUE_OFFSET();
        poseStack.pushPose();
        int i = entity.getIcon();
        float f = (float)(i % 4 * 16 + 0) / 64.0F;
        float f1 = (float)(i % 4 * 16 + 16) / 64.0F;
        float f2 = (float)(i / 4 * 16 + 0) / 64.0F;
        float f3 = (float)(i / 4 * 16 + 16) / 64.0F;
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        float f7 = 255.0F;
        float f8 = ((float)entity.tickCount + partialTicks) / 2.0F;
        int j = (int)((Mth.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
        int k = 255;
        int l = (int)((Mth.sin(f8 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
        poseStack.translate(0.0F, 0.1F, 0.0F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        float f9 = 0.3F;
        poseStack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose posestack$pose = poseStack.last();
        vertex(vertexconsumer, posestack$pose, -0.5F, -0.25F, j, 255, l, f, f3, packedLight, hueOffset);
        vertex(vertexconsumer, posestack$pose, 0.5F, -0.25F, j, 255, l, f1, f3, packedLight, hueOffset);
        vertex(vertexconsumer, posestack$pose, 0.5F, 0.75F, j, 255, l, f1, f2, packedLight, hueOffset);
        vertex(vertexconsumer, posestack$pose, -0.5F, 0.75F, j, 255, l, f, f2, packedLight, hueOffset);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, int red, int green, int blue, float u, float v, int packedLight, int hueOffset) {
        int[] offsetColor = ColorUtil.applyHueOffset(red, green, blue, hueOffset);
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(offsetColor[0], offsetColor[1], offsetColor[2], 128)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

}
