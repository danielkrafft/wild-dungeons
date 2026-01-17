package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EmeraldWisp;
import com.danielkkrafft.wilddungeons.entity.model.EmeraldWispModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class EmeraldWispRenderer extends MobRenderer<EmeraldWisp, EmeraldWispModel<EmeraldWisp>> {
    private static final ResourceLocation EMERALD_WISP = WildDungeons.rl("textures/entity/emerald_wisp.png");
    private static final ResourceLocation LARGE_EMERALD_WISP = WildDungeons.rl("textures/entity/large_emerald_wisp.png");
    private static final float BASE_SCALE = 1.0F;

    private final boolean isLarge;

    public EmeraldWispRenderer(EntityRendererProvider.Context context, boolean isLarge) {
        super(context, new EmeraldWispModel<>(context.bakeLayer(
                isLarge ? EmeraldWispModel.LARGE_LAYER_LOCATION : EmeraldWispModel.SMALL_LAYER_LOCATION)), 0.25f);
        this.isLarge = isLarge;
    }

    @Override
    protected float getWhiteOverlayProgress(EmeraldWisp livingEntity, float partialTicks) {
        float swelling = livingEntity.getSwelling(partialTicks);
        return (int) (swelling * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swelling, 0.5F, 1.0F);
    }

    @Override
    protected void scale(EmeraldWisp livingEntity, PoseStack poseStack, float partialTickTime) {
        float swelling = livingEntity.getSwelling(partialTickTime);
        float oscillation = BASE_SCALE + Mth.sin(swelling * 100.0F) * swelling * 0.01F;
        float clampedSwelling = Mth.clamp(swelling, 0.0F, 1.0F);
        float scaledSwelling = clampedSwelling * clampedSwelling * clampedSwelling * clampedSwelling; // Math.pow(clampedSwelling, 4)
        float scaleX = (BASE_SCALE + scaledSwelling * 0.4F) * oscillation;
        float scaleY = (BASE_SCALE + scaledSwelling * 0.1F) / oscillation;
        poseStack.scale(scaleX, scaleY, scaleX);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EmeraldWisp entity) {
        return isLarge ? LARGE_EMERALD_WISP : EMERALD_WISP;
    }
}
