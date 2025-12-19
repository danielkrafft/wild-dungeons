package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardian;
import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardianSegment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class CondemnedGuardianSegmentGlowLayer extends GeoRenderLayer<CondemnedGuardianSegment> {


    public CondemnedGuardianSegmentGlowLayer(GeoRenderer<CondemnedGuardianSegment> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, CondemnedGuardianSegment animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.eyes(this.getTextureResource(animatable));

        getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                glowRenderType,
                bufferSource.getBuffer(glowRenderType),
                partialTick,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                Color.WHITE.getRGB()
        );
    }

    @Override
    protected ResourceLocation getTextureResource(CondemnedGuardianSegment animatable) {
        return ResourceLocation.fromNamespaceAndPath(WildDungeons.MODID,"textures/entity/condemned_guardian_glow.png");
    }
}
