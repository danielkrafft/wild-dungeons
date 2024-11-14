package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.Alignments;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
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
        int[] offsetColor = applyHueOffset(red, green, blue, hueOffset);
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(offsetColor[0], offsetColor[1], offsetColor[2], 128)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static int[] applyHueOffset(int r, int g, int b, int hueOffset) {
        float[] hsl = rgbToHsl(r, g, b);
        hsl[0] = (hsl[0] + hueOffset) % 360;
        if (hsl[0] < 0) hsl[0] += 360;
        return hslToRgb(hsl[0], hsl[1], hsl[2]);
    }

    private static float[] rgbToHsl(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h = 0f;
        if (delta != 0) {
            if (max == rf) {
                h = ((gf - bf) / delta) % 6;
            } else if (max == gf) {
                h = ((bf - rf) / delta) + 2;
            } else {
                h = ((rf - gf) / delta) + 4;
            }
            h *= 60;
            if (h < 0) h += 360;
        }

        float l = (max + min) / 2;
        float s = delta == 0 ? 0 : delta / (1 - Math.abs(2 * l - 1));

        return new float[]{h, s, l}; // Hue, Saturation, Lightness
    }

    private static int[] hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;

        float rf = 0, gf = 0, bf = 0;
        if (h < 60) { rf = c; gf = x; bf = 0; }
        else if (h < 120) { rf = x; gf = c; bf = 0; }
        else if (h < 180) { rf = 0; gf = c; bf = x; }
        else if (h < 240) { rf = 0; gf = x; bf = c; }
        else if (h < 300) { rf = x; gf = 0; bf = c; }
        else { rf = c; gf = 0; bf = x; }

        int r = Math.round((rf + m) * 255);
        int g = Math.round((gf + m) * 255);
        int b = Math.round((bf + m) * 255);

        return new int[]{r, g, b}; // Red, Green, Blue
    }

}
