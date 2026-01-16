package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ClientModel<T extends GeoAnimatable> extends GeoModel<T>
{
    protected ResourceLocation animation;
    protected ResourceLocation model;
    protected ResourceLocation texture;
    protected ResourceLocation baseModel;
    protected ResourceLocation baseTexture;
    protected ResourceLocation altModel;
    protected ResourceLocation altTexture;

    protected List<ConditionalResource<T>> conditionalResources = new ArrayList<>();

    public ClientModel(ResourceLocation a, ResourceLocation m, ResourceLocation t) {
        animation = a;
        model = m;
        texture = t;
        setBaseModel(m, t);
        setAltModel(m, t);
    }

    public ClientModel(String modelName, String modelType) {
        this(
                WildDungeons.rl("animations/" + modelType + "/" + modelName + ".animation.json"),
                WildDungeons.rl("geo/" + modelType + "/" + modelName + ".geo.json"),
                WildDungeons.rl("textures/" + modelType + "/" + modelName + ".png")
        );
    }

    public static <T extends GeoAnimatable> ClientModel<T> ofEntity(String modelName, String modelType) {
        return new ClientModel<>(modelName, modelType);
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
        for (ConditionalResource<T> conditional : conditionalResources) {
            if (conditional.condition.test(animatable)) {
                return conditional.model;
            }
        }
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        for (ConditionalResource<T> conditional : conditionalResources) {
            if (conditional.condition.test(animatable)) {
                return conditional.texture;
            }
        }
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

    protected static class ConditionalResource<T extends GeoAnimatable> {
        final Predicate<T> condition;
        final ResourceLocation texture;
        final ResourceLocation model;

        ConditionalResource(Predicate<T> condition, ResourceLocation texture, ResourceLocation model) {
            this.condition = condition;
            this.texture = texture;
            this.model = model;
        }
    }

    public ClientModel<T> withConditionalTexture(Predicate<T> condition, String texturePath, String modelType) {
        conditionalResources.add(new ConditionalResource<>(
                condition,
                WildDungeons.rl("textures/" + modelType + "/" + texturePath + ".png"),
                this.model
        ));
        return this;
    }

    public ClientModel<T> withConditionalModel(Predicate<T> condition, String modelPath, String modelType) {
        conditionalResources.add(new ConditionalResource<>(
                condition,
                this.texture,
                WildDungeons.rl("geo/" + modelType + "/" + modelPath + ".geo.json")
        ));
        return this;
    }

    public ClientModel<T> withConditionalResources(Predicate<T> condition, String texturePath, String modelPath, String modelType) {
        conditionalResources.add(new ConditionalResource<>(
                condition,
                WildDungeons.rl("textures/" + modelType + "/" + texturePath + ".png"),
                WildDungeons.rl("geo/" + modelType + "/" + modelPath + ".geo.json")
        ));
        return this;
    }
}