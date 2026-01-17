package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardian;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class CondemnedGuardianRenderer extends GeoEntityRenderer<CondemnedGuardian> {
    private static final ClientModel<CondemnedGuardian> MODEL =
            ClientModel.<CondemnedGuardian>ofEntity("condemned_guardian_head", "entity")
            .withConditionalTexture(CondemnedGuardian::isShiny, "condemned_guardian_shiny", "entity");

    public CondemnedGuardianRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, MODEL);
        this.addRenderLayer(new CondemnedGuardianGlowLayer(this));
    }



    @Override
    public void render(CondemnedGuardian entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(partialTick)));
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
        poseStack.translate(0,0,-0.42f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

    }
}
