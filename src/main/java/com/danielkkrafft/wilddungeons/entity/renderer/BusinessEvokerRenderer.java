package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.BusinessIllagerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BusinessEvokerRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T> {
    private static final ResourceLocation EVOKER_ILLAGER = WildDungeons.rl("textures/entity/village/business_evoker.png");

    public BusinessEvokerRenderer(EntityRendererProvider.Context p_174108_) {
        super(p_174108_, new IllagerModel(p_174108_.bakeLayer(BusinessIllagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<T, IllagerModel<T>>(this, p_174108_.getItemInHandRenderer()) {
            public void render(PoseStack p_114569_, MultiBufferSource p_114570_, int p_114571_, T p_114572_, float p_114573_, float p_114574_, float p_114575_, float p_114576_, float p_114577_, float p_114578_) {
                if (p_114572_.isCastingSpell()) {
                    super.render(p_114569_, p_114570_, p_114571_, p_114572_, p_114573_, p_114574_, p_114575_, p_114576_, p_114577_, p_114578_);
                }

            }
        });
    }

    public ResourceLocation getTextureLocation(T entity) {
        return EVOKER_ILLAGER;
    }
}
