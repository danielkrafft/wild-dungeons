package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WDItemAnimator {

    private final ArrayList<Pair<RawAnimation, Float>> itemAnimations = new ArrayList<>();
    private AnimationController<GeoAnimatable> animController;
    private String ownerName;
    private GeoAnimatable owner;
    private final AnimatableInstanceCache cache;

    public WDItemAnimator(String name, GeoAnimatable animatable) {

        ownerName = name;
        cache = GeckoLibUtil.createInstanceCache(animatable);
    }

    public AnimatableInstanceCache getCache() { return cache; }

    public void registerControllersFromAnimator(GeoAnimatable item, AnimatableManager.ControllerRegistrar registrar) {

        if (animController == null) {
            animController = AnimationFactory.create(ownerName, item, itemAnimations);
        }
        registrar.add(animController);
    }

    public void setAnimationSpeed(double speed) {
        //if (isClient()) return;

        animController.setAnimationSpeed(speed);
    }

    public void setSoundKeyframeHandler(@Nullable AnimationController.SoundKeyframeHandler<GeoAnimatable> geoAnimatable) {
        //if (isClient()) return;

        animController.setSoundKeyframeHandler(geoAnimatable);
    }

    public void addAnimation(String animName) {
        //if (isClient()) return;

        addAnimation(animName, 1);
    }

    public void addAnimation(String animName, float animSpeed) {
        //if (isClient()) return;

        itemAnimations.add(Pair.of(RawAnimation.begin().thenPlay(generateAnimPathString(animName)), animSpeed));
    }

    public void addLoopingAnimation(String animName) {
        //if (isClient()) return;

        addLoopingAnimation(animName, 1);
    }

    public void addLoopingAnimation(String animName, float animSpeed) {
        //if (isClient()) return;

        itemAnimations.add(Pair.of(RawAnimation.begin().thenLoop(generateAnimPathString(animName)), animSpeed));
    }

    public void addHoldingAnimation(String ownerName, String animName) {
        //if (isClient()) return;

        addHoldingAnimation(animName, 1);
    }

    public void addHoldingAnimation(String animName, float animSpeed) {
        //if (isClient()) return;

        itemAnimations.add(Pair.of(RawAnimation.begin(). thenPlayAndHold(generateAnimPathString(animName)), animSpeed));
    }

    public void addThenPlayAnimation(String animName, String nextAnimName) {
        //if (isClient()) return;

        addThenPlayAnimation(animName, nextAnimName, 1);
    }

    public void addThenPlayAnimation(String animName, String nextAnimName, float animSpeed) {
        //if (isClient()) return;

        itemAnimations.add(Pair.of(RawAnimation.begin().thenPlay(generateAnimPathString(animName)). thenPlay(generateAnimPathString(nextAnimName)), animSpeed));
    }

    // Not safe on client
    public String getAnimationName (int index) {

        return itemAnimations.get(index).getFirst().getAnimationStages().getFirst().animationName();
    }

    public ArrayList<Pair<RawAnimation, Float>>getAnimations() {
        return itemAnimations;
    }

    public void playAnimation(GeoItem owner, String animName, ItemStack stack, Player player, Level level) {
        //if (isClient()) return;

        String finalName = animName.startsWith("animation.") ? animName : generateAnimPathString(animName);
        owner.triggerAnim(
                player,
                GeoItem.getOrAssignId(stack, (ServerLevel) level),
                WildDungeons.rl(ownerName).toString(),
                finalName
        );

        float speed = itemAnimations.stream()
                .filter(pair -> pair.getFirst().getAnimationStages().getFirst().animationName().equals(finalName))
                .findFirst().get().getSecond();

        setAnimationSpeed(speed);  // this is still OK
    }

    private String generateAnimPathString(String animName) {

        return "animation." + ownerName + "." + animName;
    }

    private boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }
}
