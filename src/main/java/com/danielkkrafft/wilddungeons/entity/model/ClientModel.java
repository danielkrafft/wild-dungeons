package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class ClientModel<T extends GeoAnimatable> extends GeoModel<T>
{
    protected ResourceLocation animation;
    protected ResourceLocation model;
    protected ResourceLocation texture;
    protected ResourceLocation baseModel;
    protected ResourceLocation baseTexture;
    protected ResourceLocation altModel;
    protected ResourceLocation altTexture;

    public ClientModel(ResourceLocation a, ResourceLocation m, ResourceLocation t) {
        animation = a;
        model = m;
        texture = t;
        setBaseModel(m, t);
        setAltModel(m, t);
    }

    public ClientModel(String a, String m, String t) {
        this(WildDungeons.makeAnimationRL(a), WildDungeons.makeGeoModelRL(m), WildDungeons.makeItemTextureRL(t));
    }

    public void setAnim(ResourceLocation a) {
        animation = a;
    }

    public void setModel(ResourceLocation m) {
        model = m;
    }

    public void setTex(ResourceLocation t) {
        texture = t;
    }

    public void activateAltModel() {
        this.setModel(altModel);
        this.setTex(altTexture);
    }

    public void activateBaseModel() {
        this.setModel(baseModel);
        this.setTex(baseTexture);
    }

    public void setBaseModel(ResourceLocation model, ResourceLocation texture) {
        this.baseModel = model;
        this.baseTexture = texture;
    }

    public void setAltModel(ResourceLocation model, ResourceLocation texture) {
        this.altModel = model;
        this.altTexture = texture;
    }

    public void setBaseModel(String model, String texture) {
        this.baseModel = WildDungeons.makeGeoModelRL(model);
        this.baseTexture = WildDungeons.makeItemTextureRL(texture);
    }

    public void setAltModel(String model, String texture) {
        this.altModel = WildDungeons.makeGeoModelRL(model);
        this.altTexture = WildDungeons.makeItemTextureRL(texture);
    }

    @Override
    public ResourceLocation getAnimationResource(@NotNull T t){return animation;}

    @Override
    public ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        return texture;
    }

    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getModelResource(T animatable) {
        return null;
    }

    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getTextureResource(T animatable) {
        return null;
    }
}