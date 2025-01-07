package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.entity.model.WindChargeProjectileModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WindChargeProjectileRenderer extends GeoEntityRenderer<WindChargeProjectile>
{
    public WindChargeProjectileRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager,new WindChargeProjectileModel());
    }
    @Override
    public boolean shouldRender(@NotNull WindChargeProjectile laser, @NotNull Frustum frustum, double p_114493_, double p_114494_, double p_114495_)
    {
        return true;
    }
    @Override
    public RenderType getRenderType(WindChargeProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucentCull(texture);
    }
    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, WindChargeProjectile proj, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay)
    {
        poseStack.scale(proj.getSize()*2,proj.getSize()*2,proj.getSize()*2);
    }
}
