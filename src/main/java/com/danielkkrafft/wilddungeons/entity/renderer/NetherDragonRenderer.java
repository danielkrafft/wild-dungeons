package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity;
import com.danielkkrafft.wilddungeons.entity.model.NetherDragonModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NetherDragonRenderer extends GeoEntityRenderer<NetherDragonEntity>
{
    public NetherDragonRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager,new NetherDragonModel());
        shadowRadius = 12f;
        shadowStrength = 1;
        //addRenderLayer(new NetherDragonLayer<>(this));
    }
    @Override
    public void actuallyRender(PoseStack poseStack, NetherDragonEntity entity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        model.getBone("head").ifPresent(bone->{
            bone.setRotY(Mth.DEG_TO_RAD*(Mth.lerp(partialTick,entity.yBodyRotO,entity.yBodyRot)-Mth.lerp(partialTick,entity.yHeadRotO,entity.yHeadRot)));
            bone.setRotX(Mth.DEG_TO_RAD*Mth.lerp(partialTick,-entity.xRotO,-entity.getXRot()));
        });
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
    @Override
    public RenderType getRenderType(NetherDragonEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucent(texture);
    }
}