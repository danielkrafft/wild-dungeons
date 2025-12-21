package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.GuardianLaserBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GuardianLaserBeamEntityRenderer extends EntityRenderer<GuardianLaserBeamEntity> {
    private static final ResourceLocation GUARDIAN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE;

    public GuardianLaserBeamEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    public boolean shouldRender(GuardianLaserBeamEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntity.getTarget() != null) {
                LivingEntity livingentity = livingEntity.getTarget();
                if (livingentity != null) {
                    Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * (double)0.5F, 1.0F);
                    Vec3 vec31 = this.getPosition(livingEntity, (double)livingEntity.getEyeHeight(), 1.0F);
                    return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
                }
            }

            return false;
        }
    }

    private Vec3 getPosition(Entity livingEntity, double yOffset, float partialTick) {
        double d0 = Mth.lerp((double)partialTick, livingEntity.xOld, livingEntity.getX());
        double d1 = Mth.lerp((double)partialTick, livingEntity.yOld, livingEntity.getY()) + yOffset;
        double d2 = Mth.lerp((double)partialTick, livingEntity.zOld, livingEntity.getZ());
        return new Vec3(d0, d1, d2);
    }

    public void render(GuardianLaserBeamEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        LivingEntity livingentity = entity.getTarget();
        if (livingentity != null) {
            float f = entity.getAttackAnimationScale(partialTicks);
            float f1 = (entity.getClientSideAttackTime()*4f) + partialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = entity.getEyeHeight();
            poseStack.pushPose();
            poseStack.translate(0.0F, f3, 0.0F);
            Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * (double)0.5F, partialTicks);
            Vec3 vec31 = this.getPosition(entity, (double)f3, partialTicks);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float)(vec32.length() + (double)1.0F);
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            poseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0F);
            int k = 32 + (int)(f8 * 191.0F);
            int l = 128 - (int)(f8 * 64.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
            float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
            float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = Mth.cos(f7 + (float)Math.PI) * 0.2F;
            float f20 = Mth.sin(f7 + (float)Math.PI) * 0.2F;
            float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
            float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
            float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            VertexConsumer vertexconsumer = buffer.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertex(vertexconsumer, posestack$pose, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, posestack$pose, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, posestack$pose, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, posestack$pose, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(vertexconsumer, posestack$pose, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, posestack$pose, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, posestack$pose, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, posestack$pose, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (entity.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, posestack$pose, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, posestack$pose, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, posestack$pose, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(vertexconsumer, posestack$pose, f15, f4, f16, j, k, l, 0.5F, f31);
            poseStack.popPose();
        }

    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int red, int green, int blue, float u, float v) {
        consumer.addVertex(pose, x, y, z).setColor(red, green, blue, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public ResourceLocation getTextureLocation(GuardianLaserBeamEntity entity) {
        return GUARDIAN_LOCATION;
    }

    static {
        BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);
    }
}