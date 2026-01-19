package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.entity.PiercingArrow;
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
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import java.util.Optional;

public class PiercingArrowRenderer extends GeoEntityRenderer<PiercingArrow> {

    public PiercingArrowRenderer(EntityRendererProvider.Context context) {
        super(context, new ClientModel<>("piercing_arrow", "entity"));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, PiercingArrow entity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        Optional<GeoBone> boneGeo=getGeoModel().getBone("arrow");

        if(boneGeo.isPresent()) {
            GeoBone bone=boneGeo.get();
            bone.setRotY(-Mth.lerp(partialTick,Mth.DEG_TO_RAD*entity.yRotO,Mth.DEG_TO_RAD*entity.getYRot()));
            bone.setRotX(Mth.lerp(partialTick,Mth.DEG_TO_RAD*entity.xRotO,Mth.DEG_TO_RAD*entity.getXRot()));
        }
    }
}