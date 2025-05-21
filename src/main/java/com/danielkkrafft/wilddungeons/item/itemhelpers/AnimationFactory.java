package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;

public class AnimationFactory {

    public static AnimationController<GeoAnimatable> create(String name, GeoAnimatable owner, ArrayList<Pair<RawAnimation, Float>> animations) {

        AnimationController<GeoAnimatable> controller = new AnimationController<>(owner, getControllerId(name).toString(), 1, state -> PlayState.CONTINUE);

        for (Pair<RawAnimation, Float> anim : animations) {
            String key = anim.getFirst().getAnimationStages().getFirst().animationName();
            controller.triggerableAnim(key, anim.getFirst());
        }
        return controller;
    }

    // unsafe, need to make sure you've initialized a controller.
    public static ResourceLocation getControllerId(String name) {
        return ResourceLocation.fromNamespaceAndPath("wilddungeons", name);
    }
}