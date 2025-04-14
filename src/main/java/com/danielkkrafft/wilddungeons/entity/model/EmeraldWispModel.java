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
            body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(12, 15).addBox(-3.0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(7.5f, 1, 0));
            body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(6, 15).addBox(-3.0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(-7.5f, 1, 0));
        } else {
            body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0, -2.5f, 5, 4, 5, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20, 0.0F));
            body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(12, 10).addBox(-3.0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(5.5f, 1, 0));
            body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(6, 10).addBox(-3.0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(-5.5f, 1, 0));
        }
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull EmeraldWisp emeraldWisp, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int i, int i1, int i2) {
        body.render(poseStack, vertexConsumer, i, i1, i2);
    }
}
