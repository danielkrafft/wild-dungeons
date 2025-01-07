package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public abstract class ClientModel<T extends GeoAnimatable> extends GeoModel<T>
{
    private ResourceLocation animation, model, texture;

    protected ClientModel(ResourceLocation a, ResourceLocation m, ResourceLocation t)
    {
        animation=a;model=m;texture=t==null?ResourceLocation.fromNamespaceAndPath(WildDungeons.MODID,"textures/misc/enchanted_glint_item.png"):t;
    }
    public void setAnim(ResourceLocation a){animation=a;}
    public void setModel(ResourceLocation m){model=m;}
    public void setTex(ResourceLocation t){texture=t;}
    @Override@Nullable
    public ResourceLocation getAnimationResource(@NotNull T t){return animation;}

    @Override
    public ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer) {return model;}

    @Override
    public ResourceLocation getTextureResource(T animatable, @Nullable GeoRenderer<T> renderer) {return texture;}

    @Override @Deprecated
    public ResourceLocation getModelResource(T animatable) {
        return null;
    }

    @Override @Deprecated
    public ResourceLocation getTextureResource(T animatable) {
        return null;
    }
}