package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity;
import com.danielkkrafft.wilddungeons.entity.model.NetherDragonModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.UUID;
import java.util.function.Consumer;

public class NetherDragonRenderer extends GeoEntityRenderer<NetherDragonEntity>
{
    public NetherDragonRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager,new NetherDragonModel());
        shadowRadius = 4;
        shadowStrength = 1;
    }
    @Override
    public void actuallyRender(PoseStack poseStack, NetherDragonEntity entity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        VanillaBendHead(entity, model, partialTick);

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

    private void BendNeck(NetherDragonEntity entity, BakedGeoModel model, float partialTick) {
        Vector3f targetPos = entity.getMoveTargetPoint();

        // Calculate direction to target
        double dx = targetPos.x() - entity.getX();
        double dy = targetPos.y() - (entity.getY() + entity.getEyeHeight());
        double dz = targetPos.z() - entity.getZ();

        // Calculate angles
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F - entity.getYRot();
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontalDistance));

        // Normalize and clamp angles
        yaw = Mth.wrapDegrees(yaw);
        pitch = Mth.clamp(pitch, -40.0F, 60.0F);

        // Apply to each neck segment with progressive bending
        for (String neckName : new String[]{"neck", "neckLeft", "neckRight"}) {
            for (int i = 0; i < 5; i++) {
                final int segmentIndex = i;
                float finalYaw = yaw;
                float finalPitch = pitch;
                model.getBone(i > 0 ? neckName + i : neckName).ifPresent(bone -> {
                    // Progressive bending factor (0.3 to 1.0)
                    float bendFactor = 0.3f + ((float)segmentIndex / 10.0f);

                    float segmentYaw = (float) Math.toRadians(-finalYaw / 5.0F) * bendFactor;
                    float segmentPitch = (float) Math.toRadians(-finalPitch / 5.0F) * bendFactor;

                    bone.setRotY(segmentYaw);
                    bone.setRotX(segmentPitch);
                });
            }
        }

        VanillaBendHead(entity, model, partialTick);
    }

    @Override
    public RenderType getRenderType(NetherDragonEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucent(texture);
    }

}