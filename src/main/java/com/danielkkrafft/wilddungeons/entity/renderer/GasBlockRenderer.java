package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.GasBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


public class GasBlockRenderer implements BlockEntityRenderer<GasBlockEntity> {
    private static final ResourceLocation GAS_TEXTURE = WildDungeons.rl("textures/entity/gas.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(GAS_TEXTURE);
    private EntityRenderDispatcher entityRenderDispatcher;

    public GasBlockRenderer(BlockEntityRendererProvider.Context context) {
        entityRenderDispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(GasBlockEntity gasBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0, 0.5F);

        float time = (float)(System.currentTimeMillis() % 10000L) / 1000.0f;
        int layers = 8;
        for (int i = 0; i < layers; i++) {
            poseStack.pushPose();
            // Make layer face the camera
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
            // random slight translation for each layer
            float offsetX = (float)(Math.sin(time + i * 1.5) * .2);
            float offsetY = (float)(Math.cos(time + i * 1.5) * 0.2);
            float offsetZ = (float)(Math.sin(time + i * 1.5 + Math.PI / 2) * 0.2);
            poseStack.translate(offsetX, offsetY, offsetZ);


            // Z offset to prevent z-fighting
            float zOffset = 0.001f * i;
            poseStack.translate(0.0f, 0.0f, zOffset);

            poseStack.scale(2, 2, 2);

            // Rotation
//            float angle = time * 40f + i * 90f;
//            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

            // Color: alternate yellow/green
            float[] color;
            if (i % 2 == 0) {
                color = new float[]{0.7f, 1.0f, 0.4f, 0.3f}; // yellowish
            } else {
                color = new float[]{0.3f, 1.0f, 0.5f, 0.3f}; // greenish
            }

            VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertexColored(vertexconsumer, posestack$pose, -0.5F, -0.25F, 0.0f, 1.0f, packedLight, color);
            vertexColored(vertexconsumer, posestack$pose, 0.5F, -0.25F, 1.0f, 1.0f, packedLight, color);
            vertexColored(vertexconsumer, posestack$pose, 0.5F, 0.75F, 1.0f, 0.0f, packedLight, color);
            vertexColored(vertexconsumer, posestack$pose, -0.5F, 0.75F, 0.0f, 0.0f, packedLight, color);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(GasBlockEntity blockEntity) {
        return BlockEntityRenderer.super.shouldRenderOffScreen(blockEntity);
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }

    @Override
    public boolean shouldRender(GasBlockEntity blockEntity, Vec3 cameraPos) {
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public AABB getRenderBoundingBox(GasBlockEntity blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }


    private static void vertexColored(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int packedLight, float[] color) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(color[0], color[1], color[2], color[3])
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
