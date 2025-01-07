package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class BreezeGolemLayer<T extends BreezeGolem> extends GeoRenderLayer<T>
{
    public static final ResourceLocation flameTexture= WildDungeons.rl("textures/entity/breeze_golem_smoke.png");
    public BreezeGolemLayer(GeoRenderer<T> entityRendererIn)
    {
        super(entityRendererIn);
    }
    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
    {
        RenderType type=RenderType.entityTranslucent(flameTexture);
        AnimatableTexture.setAndUpdate(flameTexture);
        renderer.reRender(getDefaultBakedModel(animatable),poseStack,bufferSource,animatable,type,bufferSource.getBuffer(type),partialTick,packedLight,OverlayTexture.NO_OVERLAY,Color.WHITE.argbInt());
    }
}
