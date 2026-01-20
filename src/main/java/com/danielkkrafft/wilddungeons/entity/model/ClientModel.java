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

public class ClientModel<T extends GeoAnimatable> extends GeoModel<T> {
    protected ResourceLocation animation;
    protected ResourceLocation model;
    protected ResourceLocation texture;

    protected List<ConditionalResource<T>> conditionalResources = new ArrayList<>();

    public ClientModel(ResourceLocation a, ResourceLocation m, ResourceLocation t) {
        animation = a;
        model = m;
        texture = t;
    }

    public ClientModel(String modelName, String modelType) {
        this(
                WildDungeons.rl("animations/" + modelType + "/" + modelName + ".animation.json"),
                WildDungeons.rl("geo/" + modelType + "/" + modelName + ".geo.json"),
                WildDungeons.rl("textures/" + modelType + "/" + modelName + ".png")
        );
    }

    @Override
    public ResourceLocation getAnimationResource(@NotNull T t){return animation;}

    public ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        ResourceLocation result = model;

        for (ConditionalResource<T> conditional : conditionalResources) {
            if (conditional.condition.test(animatable) && conditional.model != null) {
                result = conditional.model;
                break;
            }
        }
        return result;
    }

    public ResourceLocation getTextureResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        ResourceLocation result = texture;

        for (ConditionalResource<T> conditional : conditionalResources) {
            if (conditional.condition.test(animatable) && conditional.texture != null) {
                result = conditional.texture;
                break;
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getModelResource(T animatable) {
        return getModelResource(animatable, null);
    }

    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getTextureResource(T animatable) {
        return getTextureResource(animatable, null);
    }

    protected static class ConditionalResource<T extends GeoAnimatable> {
        final Predicate<T> condition;
        final @Nullable ResourceLocation texture;
        final @Nullable ResourceLocation model;

        ConditionalResource(Predicate<T> condition, @Nullable ResourceLocation texture, @Nullable ResourceLocation model) {
            this.condition = condition;
            this.texture = texture;
            this.model = model;
        }
    }

    public ClientModel<T> withConditionalTexture(Predicate<T> condition, String texturePath, String modelType) {
        conditionalResources.add(new ConditionalResource<>(
                condition,
                WildDungeons.rl("textures/" + modelType + "/" + texturePath + ".png"),
                null
        ));
        return this;
    }

    public ClientModel<T> withConditionalModel(Predicate<T> condition, String modelPath, String modelType) {
        conditionalResources.add(new ConditionalResource<>(
                condition,
                null,
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