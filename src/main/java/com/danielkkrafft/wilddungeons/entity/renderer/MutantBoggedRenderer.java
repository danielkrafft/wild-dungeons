package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer;

public class MutantBoggedRenderer extends DynamicGeoEntityRenderer<MutantBogged>
{
    public MutantBoggedRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, ClientModel.ofEntity("mutant_bogged", "entity"));
    }
    @Override
    public void actuallyRender(PoseStack poseStack, MutantBogged entity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour)
    {
        var head=model.getBone("head");
        if(head.isPresent())
        {
            GeoBone bone=head.get();
            bone.setRotY(Mth.DEG_TO_RAD*(Mth.lerp(partialTick,entity.yBodyRotO,entity.yBodyRot)-Mth.lerp(partialTick,entity.yHeadRotO,entity.yHeadRot)));
            bone.setRotX(Mth.DEG_TO_RAD*Mth.lerp(partialTick,-entity.xRotO,-entity.getXRot()));
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}