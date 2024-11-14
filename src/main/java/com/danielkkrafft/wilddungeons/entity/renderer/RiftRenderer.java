package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.RiftBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RiftRenderer implements BlockEntityRenderer<RiftBlockEntity> {

    private static final ResourceLocation RIFT_TEXTURE = WildDungeons.rl("textures/entity/rift.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(RIFT_TEXTURE);

    private EntityRenderDispatcher entityRenderDispatcher;

    public RiftRenderer(BlockEntityRendererProvider.Context context) {
        entityRenderDispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(RiftBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(2.0F, 2.0F, 2.0F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        PoseStack.Pose posestack$pose = poseStack.last();
        vertex(vertexconsumer, posestack$pose, -0.5F, -0.25F, 0.0f, 1.0f, packedLight);
        vertex(vertexconsumer, posestack$pose, 0.5F, -0.25F, 1.0f, 1.0f, packedLight);
        vertex(vertexconsumer, posestack$pose, 0.5F, 0.75F, 1.0f, 0.0f, packedLight);
        vertex(vertexconsumer, posestack$pose, -0.5F, 0.75F, 0.0f, 0.0f, packedLight);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int packedLight) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
