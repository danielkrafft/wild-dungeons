package com.danielkkrafft.wilddungeons.item.model;

import com.danielkkrafft.wilddungeons.item.Meathook;
import com.danielkkrafft.wilddungeons.item.renderer.MeathookRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MeathookModel extends GeoModel<Meathook>
{
    private ResourceLocation anim= MeathookRenderer.MEATHOOK_ANIM,
            model=MeathookRenderer.MEATHOOK_MODEL,
            tex=MeathookRenderer.MEATHOOK_TEXTURE;
    public void setAnimation(ResourceLocation anim) {this.anim=anim;}
    public void setModel(ResourceLocation model) {this.model=model;}
    public void setTexture(ResourceLocation tex) {this.tex=tex;}

    @Override
    public ResourceLocation getAnimationResource(Meathook hook) {
        return this.anim;
    }

    @Override
    public ResourceLocation getModelResource(Meathook animatable, @Nullable GeoRenderer<Meathook> renderer) {
        return this.model;
    }

    @Override
    public ResourceLocation getModelResource(Meathook animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(Meathook animatable, @Nullable GeoRenderer<Meathook> renderer) {
        return this.tex;
    }

    @Override
    public ResourceLocation getTextureResource(Meathook animatable) {
        return this.tex;
    }
}
