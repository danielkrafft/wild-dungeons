package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EmeraldWisp;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class EmeraldWispModel<T extends EmeraldWisp> extends EntityModel<EmeraldWisp> {
    public static final ModelLayerLocation SMALL_LAYER_LOCATION = new ModelLayerLocation(WildDungeons.rl("emerald_wisp"), "small");
    public static final ModelLayerLocation LARGE_LAYER_LOCATION = new ModelLayerLocation(WildDungeons.rl("emerald_wisp"), "large");
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    public EmeraldWispModel(ModelPart root) {
        this.body = root.getChild("body");
        this.rightWing = body.getChild("rightWing");
        this.leftWing = body.getChild("leftWing");
    }

    public static LayerDefinition createBodyLayer(Boolean large){
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body;
        if (large){
            body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, 0, 0-4.5f, 9, 6, 9, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18, 0.0F));
            body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(12, 15).addBox(0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(4.5f, 1, 0));
            body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(6, 15).addBox(-6.0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(-4.5f, 1, 0));
        } else {
            body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0, -2.5f, 5, 4, 5, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20, 0.0F));
            body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(12, 10).addBox(0, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(2.5f, 1, 0));
            body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(6, 10).addBox(-6.0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(-2.5f, 1, 0));
        }
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull EmeraldWisp emeraldWisp, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.rightWing.yRot = (float) ((float) Math.toRadians(-30) - (float) Math.toRadians(30) * Math.sin(ageInTicks * 0.2f));
        this.leftWing.yRot = (float) ((float) Math.toRadians(30) + (float) Math.toRadians(30) * Math.sin(ageInTicks * 0.2f));
        this.body.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.body.xRot = headPitch * ((float) Math.PI / 180F);
        this.rightWing.zRot = (float) (Math.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount);
        this.leftWing.zRot = (float) (Math.cos(limbSwing * 0.6662F) * limbSwingAmount);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int i, int i1, int i2) {
        body.render(poseStack, vertexConsumer, i, i1, i2);
    }
}
