package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.ThrownNautilusShield;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ThrownNautilusShieldRenderer extends EntityRenderer<ThrownNautilusShield> {
    public ThrownNautilusShieldRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ThrownNautilusShield p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        ItemStack stack = p_entity.getStack();
        if (stack != null) {
            Minecraft mc = Minecraft.getInstance();
            float completedTick = p_entity.tickCount + partialTick;
            poseStack.pushPose();
            poseStack.translate(0, -0.1, 0);
            if (p_entity.shouldSpin()) {
                poseStack.mulPose(Axis.YP.rotationDegrees((p_entity.spinTicks + partialTick) * 20));
            }
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.sin(completedTick * 0.5) * 5f)));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (Math.cos(completedTick * 0.5) * 5f)));
            poseStack.translate(0, 0.4, 0);
            poseStack.scale(1.4f, 1.4f, 1.4f);


            mc.getItemRenderer().render(stack, ItemDisplayContext.GROUND,
                    false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY,
                    mc.getItemRenderer().getModel(stack, p_entity.level(), null, 5));

            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownNautilusShield thrownNautilusShield) {
        return null;
    }
}
