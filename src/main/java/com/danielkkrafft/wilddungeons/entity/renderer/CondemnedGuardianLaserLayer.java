package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardian;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class CondemnedGuardianLaserLayer extends GeoRenderLayer<CondemnedGuardian> {
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian_beam.png");
    private static final RenderType LASER_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    public CondemnedGuardianLaserLayer(GeoEntityRenderer<CondemnedGuardian> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, CondemnedGuardian animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.getTarget() == null) return;

        LivingEntity target = animatable.getTarget();

        double shooterX = Mth.lerp(partialTick, animatable.xOld, animatable.getX());
        double shooterY = Mth.lerp(partialTick, animatable.yOld, animatable.getY()) + animatable.getEyeHeight();
        double shooterZ = Mth.lerp(partialTick, animatable.zOld, animatable.getZ());

        double targetX = Mth.lerp(partialTick, target.xOld, target.getX());
        double targetY = Mth.lerp(partialTick, target.yOld, target.getY()) + target.getBbHeight() * 0.5;
        double targetZ = Mth.lerp(partialTick, target.zOld, target.getZ());

        Vec3 diff = new Vec3(targetX - shooterX, targetY - shooterY, targetZ - shooterZ);
        float length = (float) diff.length();
        Vec3 dir = diff.normalize();

        float pitch = (float) Math.acos(dir.y);
        float yaw = (float) Math.atan2(dir.z, dir.x);

        poseStack.pushPose();
        poseStack.translate(shooterX, shooterY, shooterZ);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(-yaw + Math.PI/2)));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(pitch)));

        VertexConsumer consumer = bufferSource.getBuffer(LASER_RENDER_TYPE);

        // simple vertical quad for beam
        float halfWidth = 0.2f;
        vertex(consumer, poseStack.last(), -halfWidth, 0, 0, 255, 255, 255, 0f, 0f);
        vertex(consumer, poseStack.last(), halfWidth, 0, 0, 255, 255, 255, 1f, 0f);
        vertex(consumer, poseStack.last(), halfWidth, 0, length, 255, 255, 255, 1f, length);
        vertex(consumer, poseStack.last(), -halfWidth, 0, length, 255, 255, 255, 0f, length);

        poseStack.popPose();
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int r, int g, int b, float u, float v) {
        consumer.addVertex(pose, x, y, z).setColor(r, g, b, 255)
                .setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880).setNormal(pose, 0, 1, 0);
    }
}