package com.danielkkrafft.wilddungeons.api.client;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import java.util.ArrayList;
import java.util.List;

public class DelayedRenderTypes {

    public static List<RenderTypes> ALL_ENTITY = new ArrayList<>();
    public static List<RenderTypes> TRANSLUCENT_ENTITY = new ArrayList<>();
    public static List<RenderTypes> ADDITIVE_ENTITY = new ArrayList<>();

    public static List<RenderTypes> ALL_PARTICLES = new ArrayList<>();
    public static List<RenderTypes> TRANSLUCENT_PARTICLES = new ArrayList<>();
    public static List<RenderTypes> ADDITIVE_PARTICLES = new ArrayList<>();


    /**
     * Used to register any renderType that has to be delayed
     * @param renderType The RenderType in question
     * @param target Render Target type : Either a Particle or an Entity , defaulted to Entity
     * @param render The Render Method used : Normal , Additive , Translucent , defaulted to Normal
     */
    public static void registerDelayedRenderType(RenderTypes renderType, TargetType target, RenderMethod render) {
        if (target == TargetType.PARTICLE) {
            switch (render) {
                case NONE -> ALL_PARTICLES.add(renderType);
                case ADDITIVE -> ADDITIVE_PARTICLES.add(renderType);
                case TRANSLUCENT -> TRANSLUCENT_PARTICLES.add(renderType);
                case null, default -> ALL_PARTICLES.add(renderType);
            }
        } else {
            switch (render) {
                case NONE -> ALL_ENTITY.add(renderType);
                case ADDITIVE -> ADDITIVE_ENTITY.add(renderType);
                case TRANSLUCENT -> TRANSLUCENT_ENTITY.add(renderType);
                case null, default -> ALL_ENTITY.add(renderType);
            }
        }
    }

    public static void registerDelayedRenderType(RenderTypes renderType, RenderMethod render) {
        registerDelayedRenderType(renderType,TargetType.ENTITY,render);
    }

    public static void registerDelayedRenderType(RenderTypes renderType, TargetType target) {
        registerDelayedRenderType(renderType,target,RenderMethod.NONE);
    }

    public static void registerDelayedRenderType(RenderTypes renderType) {
        registerDelayedRenderType(renderType,TargetType.ENTITY,RenderMethod.NONE);
    }

    public enum TargetType {
        ENTITY,
        PARTICLE
    }

    public enum RenderMethod {
        NONE,
        TRANSLUCENT,
        ADDITIVE
    }

}
