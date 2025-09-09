package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.ToxicWisp;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class ToxicWispModel<T extends ToxicWisp> extends EntityModel<ToxicWisp> {
    public static final ModelLayerLocation SMALL_LAYER_LOCATION = new ModelLayerLocation(WildDungeons.rl("toxic_wisp"), "small");
    public static final ModelLayerLocation LARGE_LAYER_LOCATION = new ModelLayerLocation(WildDungeons.rl("toxic_wisp"), "large");
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    public ToxicWispModel(ModelPart root) {
        this.body = root.getChild("body");
        this.rightWing = body.getChild("rightWing");
        this.leftWing = body.getChild("leftWing");
    }

    public static LayerDefinition createBodyLayer(Boolean large){
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body;
        if (large){
            //todo
            body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, 0, -2.5f, 5, 4, 5, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20, 0.0F));
            body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(12, 15).addBox(0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(4.5f, 1, 0));
            body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(6, 15).addBox(-6.0F, -2.5f, 0, 6, 5F, 0, new CubeDeformation(0.0F)), PartPose.offset(-4.5f, 1, 0));
        } else {
            body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 8.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 9).addBox(-1.5F, 2.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -2.0F, 0.5F));

            PartDefinition leftWing = body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(12, 0).addBox(0.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 5.5F, 0.5F));

            PartDefinition rightWing = body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(12, 0).mirror().addBox(-6.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.5F, 5.5F, 0.5F));

            PartDefinition hair1 = body.addOrReplaceChild("hair1", CubeListBuilder.create().texOffs(12, 5).addBox(0.0F, -2.0F, -0.5F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.5672F, 0.9599F, -0.6981F));

            PartDefinition hair2 = hair1.addOrReplaceChild("hair2", CubeListBuilder.create().texOffs(12, 8).addBox(0.0F, -2.0F, -0.5F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

            return LayerDefinition.create(meshdefinition, 32, 32);
        }
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull ToxicWisp toxicWisp, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
