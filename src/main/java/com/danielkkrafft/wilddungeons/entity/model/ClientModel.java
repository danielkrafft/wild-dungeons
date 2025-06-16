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
    protected ResourceLocation animation, model, texture, inventoryModel, inventoryTexture, workingModel, workingTexture;

    public ClientModel(ResourceLocation a, ResourceLocation m, ResourceLocation t) {
        animation = a;
        model = m;
        texture = t;
        setInventoryModel(m, t);
        setWorkingModel(m, t);
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

    public void activateWorkingModel() {
        this.setModel(workingModel);
        this.setTex(workingTexture);
    }

    public void activateInventoryModel() {
        this.setModel(inventoryModel);
        this.setTex(inventoryTexture);
    }

    public void setInventoryModel(ResourceLocation model, ResourceLocation texture) {
        this.inventoryModel = model;
        this.inventoryTexture = texture;
    }

    public void setWorkingModel(ResourceLocation model, ResourceLocation texture) {
        this.workingModel = model;
        this.workingTexture = texture;
    }

    public void setInventoryModel(String model, String texture) {
        this.inventoryModel = WildDungeons.makeGeoModelRL(model);
        this.inventoryTexture = WildDungeons.makeItemTextureRL(texture);
    }

    public void setWorkingModel(String model, String texture) {
        this.workingModel = WildDungeons.makeGeoModelRL(model);
        this.workingTexture = WildDungeons.makeItemTextureRL(texture);
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