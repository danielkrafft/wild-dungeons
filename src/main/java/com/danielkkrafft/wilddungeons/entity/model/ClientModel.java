package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ClientModel<T extends GeoAnimatable> extends GeoModel<T> {
    protected Identifier animation;
    protected Identifier model;
    protected Identifier texture;

    protected List<ConditionalResource<T>> conditionalResources = new ArrayList<>();

    public ClientModel(Identifier a, Identifier m, Identifier t) {
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

    //TODO - Figure out how to implement this in 1.21.1 and not just return null. Likely need a deep dive into RenderState, GeoRenderState, and overall new render system. This whole class is COOKED.
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return null;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return null;
    }

    @Override
    public Identifier getAnimationResource(@NotNull T t){return animation;}

    public <R extends GeoRenderState, O> Identifier getModelResource(T animatable, @Nullable GeoRenderer<T, O, R> renderer) {
        Identifier result = model;

        for (ConditionalResource<T> conditional : conditionalResources) {
            if (conditional.condition.test(animatable) && conditional.model != null) {
                result = conditional.model;
                break;
            }
        }
        return result;
    }

    public <R extends GeoRenderState, O> Identifier getTextureResource(T animatable, @Nullable GeoRenderer<T, O, R> renderer) {
        Identifier result = texture;

        for (ConditionalResource<T> conditional : conditionalResources) {
            if (conditional.condition.test(animatable) && conditional.texture != null) {
                result = conditional.texture;
                break;
            }
        }
        return result;
    }

    protected static class ConditionalResource<T extends GeoAnimatable> {
        final Predicate<T> condition;
        final @Nullable Identifier texture;
        final @Nullable Identifier model;

        ConditionalResource(Predicate<T> condition, @Nullable Identifier texture, @Nullable Identifier model) {
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