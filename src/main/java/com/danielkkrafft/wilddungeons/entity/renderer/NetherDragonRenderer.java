package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class NetherDragonRenderer extends GeoEntityRenderer<NetherDragonEntity> {
    private static final ClientModel<NetherDragonEntity> MODEL = new ClientModel<>("nether_dragon", "entity");

    public NetherDragonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, MODEL);
        shadowRadius = 4;
        shadowStrength = 1;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, NetherDragonEntity entity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BendNeck(entity, model, partialTick);

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    private void VanillaBendHead(NetherDragonEntity entity, BakedGeoModel model, float partialTick) {
        for (String headName : new String[]{"head", "head2", "head3"}) {
            model.getBone(headName).ifPresent(bone -> {
                bone.setRotY(Mth.DEG_TO_RAD * (Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot) - Mth.lerp(partialTick, entity.yHeadRotO, entity.yHeadRot)));
                bone.setRotX(Mth.DEG_TO_RAD * Mth.lerp(partialTick, -entity.xRotO, -entity.getXRot()));
            });
        }
    }

    private final float[][] prevNeckYaw = new float[3][5];
    private final float[][] prevNeckPitch = new float[3][5];
    private final float[] prevHeadYaw = new float[3];
    private final float[] prevHeadPitch = new float[3];
    private static final float LERP_FACTOR = 0.1f; // Adjust this to control smoothness

    private void BendNeck(NetherDragonEntity entity, BakedGeoModel model, float partialTick) {
        NetherDragonEntity.AttackPhase attackPhase = entity.getAttackPhase();
        Vector3f targetPos = entity.getMoveTargetPoint();

        // Calculate direction to target
        double dx = targetPos.x() - entity.getX();
        double dy = targetPos.y() - (entity.getY() + entity.getEyeHeight());
        double dz = targetPos.z() - entity.getZ();

        // Calculate angles
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F - entity.getYRot();
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontalDistance));

        yaw = Mth.wrapDegrees(yaw);
        yaw = Mth.clamp(yaw, -120.0f, 120.0F);
        pitch = Mth.clamp(pitch, -120.0F, 120.0F);

        if (attackPhase == NetherDragonEntity.AttackPhase.FIREBALL) {
            yaw = entity.yHeadRot - entity.getYRot();
            pitch = entity.getXRot();
        }

        // Define neck offsets and fixed base rotations
        float[] neckOffsets = {0.0F, 45.0F, -45.0F};
        String[] neckNames = {"neck", "neckLeft", "neckRight"};

        // Apply to each neck segment
        for (int neckIndex = 0; neckIndex < neckNames.length; neckIndex++) {
            String neckName = neckNames[neckIndex];
            float neckOffset = neckOffsets[neckIndex];

            for (int i = 0; i < 5; i++) {
                final int segmentIndex = i;
                final int finalNeckIndex = neckIndex;
                float finalYaw = yaw + neckOffset;
                float finalPitch = pitch;

                // Special handling for the base segment
                if (i == 0) {

                    model.getBone(neckName).ifPresent(bone -> {
                        float targetYaw = (float) Math.toRadians(neckOffset);
                        // Lerp between previous and target rotation
                        prevNeckYaw[finalNeckIndex][0] = Mth.lerp(LERP_FACTOR, prevNeckYaw[finalNeckIndex][0], targetYaw);
                        bone.setRotY(prevNeckYaw[finalNeckIndex][0]);
                        bone.setRotX((float) Math.toRadians(-finalPitch / 4.0F));
                    });
                    continue;
                }

                // For other segments, gradually follow the target

                model.getBone(neckName + i).ifPresent(bone -> {
                    // Progressive bending factor (0.3 to 1.0)
                    float bendFactor = 0.3f + ((float) segmentIndex / 10.0f);

                    float targetYaw = (float) Math.toRadians(-finalYaw / 5.0F) * bendFactor;
                    float targetPitch = (float) Math.toRadians(-finalPitch / 4.0F) * bendFactor;

                    // Lerp between previous and target rotation
                    prevNeckYaw[finalNeckIndex][segmentIndex] = Mth.lerp(LERP_FACTOR, prevNeckYaw[finalNeckIndex][segmentIndex], targetYaw);
                    prevNeckPitch[finalNeckIndex][segmentIndex] = Mth.lerp(LERP_FACTOR, prevNeckPitch[finalNeckIndex][segmentIndex], targetPitch);

                    bone.setRotY(prevNeckYaw[finalNeckIndex][segmentIndex]);
                    bone.setRotX(prevNeckPitch[finalNeckIndex][segmentIndex]);
                });
            }
        }

        // Also apply offsets to the heads
        String[] headNames = {"head", "head2", "head3"};
        for (int i = 0; i < headNames.length; i++) {
            final float headOffset = neckOffsets[i] * 0.25f;
            final int headIndex = i;

            model.getBone(headNames[i]).ifPresent(bone -> {
                float targetHeadYaw = -(Mth.DEG_TO_RAD * (Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot) -
                        Mth.lerp(partialTick, entity.yHeadRotO, entity.yHeadRot)) +
                        Mth.DEG_TO_RAD * headOffset);
                float targetHeadPitch = Mth.DEG_TO_RAD * Mth.lerp(partialTick, -entity.xRotO, -entity.getXRot());

                // Lerp between previous and target rotation
                prevHeadYaw[headIndex] = Mth.lerp(LERP_FACTOR, prevHeadYaw[headIndex], targetHeadYaw);
                prevHeadPitch[headIndex] = Mth.lerp(LERP_FACTOR, prevHeadPitch[headIndex], targetHeadPitch);

                bone.setRotY(prevHeadYaw[headIndex]);
                bone.setRotX(prevHeadPitch[headIndex]);
            });
        }
    }

    @Override
    public RenderType getRenderType(NetherDragonEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}