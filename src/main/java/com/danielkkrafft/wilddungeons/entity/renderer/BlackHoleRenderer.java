package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.BlackHole;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class BlackHoleRenderer extends EntityRenderer<BlackHole> {

    private static final ResourceLocation TEXTURE_CORE = WildDungeons.rl("textures/entity/blackhole/black_hole_inner.png");
    private static final ResourceLocation TEXTURE_OUTER = WildDungeons.rl("textures/entity/blackhole/black_hole_outer.png");
    private static final ResourceLocation TEXTURE_RING = WildDungeons.rl("textures/entity/blackhole/black_hole_ring.png");

    public BlackHoleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull BlackHole blackHole) {
        return null;
    }

    @Override
    public void render(BlackHole entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // Billboarded core + outer glow
        poseStack.mulPose(this.entityRenderDispatcher.camera.rotation());

        float pulse = 1.0f + 0.1f * Mth.sin((entity.tickCount + partialTicks) * 0.3f);
        float size = entity.getSize();

        // Apply size * pulse scale for the glowing outer layer
        poseStack.scale(size * pulse, size * pulse, size * pulse);
        renderQuad(poseStack, bufferSource, TEXTURE_OUTER, packedLight);

        // Slight Z-forward for core, negate pulse so core stays crisp
        poseStack.translate(0.0, 0.0, 0.001f);
        poseStack.scale(1.0f / pulse, 1.0f / pulse, 1.0f / pulse); // Only size scale remains
        renderQuad(poseStack, bufferSource, TEXTURE_CORE, packedLight);

        poseStack.popPose();

        // Rotate and render ring (flat & spinning)
        poseStack.pushPose();

        // Ring scales with size (but not pulse)
        poseStack.scale(size, size, size);
        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 4f));
        poseStack.mulPose(Axis.XP.rotationDegrees(80));
        renderQuad(poseStack, bufferSource, TEXTURE_RING, packedLight);

        poseStack.popPose();
    }


    private static void extracted(PoseStack poseStack, float pulse) {
        poseStack.scale(pulse, pulse, pulse);
    }

    private void renderQuad(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, int light) {
        VertexConsumer builder = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        Matrix4f pose = poseStack.last().pose();
        int color = 0xFFFFFFFF;
        int overlay = OverlayTexture.NO_OVERLAY;

        // Pre-transform vertices
        Vector4f v1 = new Vector4f(-0.5f,  0.5f, 0f, 1.0f); v1.mul(pose);
        Vector4f v2 = new Vector4f(-0.5f, -0.5f, 0f, 1.0f); v2.mul(pose);
        Vector4f v3 = new Vector4f( 0.5f, -0.5f, 0f, 1.0f); v3.mul(pose);
        Vector4f v4 = new Vector4f( 0.5f,  0.5f, 0f, 1.0f); v4.mul(pose);

        builder.addVertex(v1.x(), v1.y(), v1.z(), color, 0f, 0f, overlay, light, 0f, 1f, 0f);
        builder.addVertex(v2.x(), v2.y(), v2.z(), color, 0f, 1f, overlay, light, 0f, 1f, 0f);
        builder.addVertex(v3.x(), v3.y(), v3.z(), color, 1f, 1f, overlay, light, 0f, 1f, 0f);
        builder.addVertex(v4.x(), v4.y(), v4.z(), color, 1f, 0f, overlay, light, 0f, 1f, 0f);
    }
}
