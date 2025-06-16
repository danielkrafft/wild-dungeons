package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;

public class WDItemAnimator {

    private String ownerName;

    private final AnimatableInstanceCache cache;
    private final ArrayList<Pair<RawAnimation, Float>> animations = new ArrayList<>();
    private AnimationController<GeoAnimatable> animController;


    public WDItemAnimator(String name, GeoAnimatable animatable) {

        ownerName = name;
        cache = GeckoLibUtil.createInstanceCache(animatable);
    }

    public AnimatableInstanceCache getCache() { return cache; }

    public void registerControllersFromAnimator(GeoAnimatable item, AnimatableManager.ControllerRegistrar controllers) {

        if (animController == null) {
            animController = new AnimationController<>(item, WildDungeons.rl(ownerName).toString(), 1, state -> PlayState.CONTINUE);
            this.animations.forEach(rawAnimation -> {
                RawAnimation animation = rawAnimation.getFirst();
                animController.triggerableAnim(animation.getAnimationStages().getFirst().animationName(), animation);
            });
        }
        controllers.add(animController);
    }

    public void setAnimationSpeed(float inSpeed, Level level) {

        // make sure we have a valid controller
        if (animController == null) return;

        // ensure we are on the server
        if (level.isClientSide()) return;

        animController.setAnimationSpeed(inSpeed);
    }

    public void setSoundKeyframeHandler(@Nullable AnimationController.SoundKeyframeHandler<GeoAnimatable> geoAnimatable) {

        // controller check
        if (animController == null) return;

        animController.setSoundKeyframeHandler(geoAnimatable);
    }

    /**
     * The first animation added to the list will be treated as the "idle", default animation
     */
    public void addAnimation(String animName) {
        addAnimation(animName, 1);
    }

    public void addAnimation(String animName, float animationSpeed) {
        animations.add(Pair.of(RawAnimation.begin().thenPlay(generateAnimPathString(animName)), animationSpeed));
    }

    public void addLoopingAnimation(String animName) {
        addLoopingAnimation(animName, 1);
    }

    public void addLoopingAnimation(String animName, float animationSpeed) {
        animations.add(Pair.of(RawAnimation.begin().thenLoop(generateAnimPathString(animName)), animationSpeed));
    }

    public void addHoldingAnimation(String animName) {
        addHoldingAnimation(animName, 1);
    }

    public void addHoldingAnimation(String animName, float animationSpeed) {
        animations.add(Pair.of(RawAnimation.begin().thenPlayAndHold(generateAnimPathString(animName)), animationSpeed));
    }

    public void addThenPlayAnimation(String animName, String nextAnimName) {
        addThenPlayAnimation(animName, nextAnimName, 1);
    }

    public void addThenPlayAnimation(String animName, String nextAnimName, float animationSpeed) {
        animations.add(Pair.of(RawAnimation.begin().thenPlay(generateAnimPathString(animName)).thenPlay(generateAnimPathString(nextAnimName)), animationSpeed));
    }

    public ArrayList<Pair<RawAnimation, Float>>getAnimations() {
        return animations;
    }

    public String getAnimationName(int index) {
        if (index < 0 || index >= animations.size()) return null;
        return animations.get(index).getFirst().getAnimationStages().getFirst().animationName();

    }

    public void playAnimation(GeoItem owner, String animName, ItemStack stack, Player player, Level level) {
        if (level.isClientSide) return;
        if (animName == null || animName.isEmpty()) {
//            WildDungeons.getLogger().warn("Tried to play an empty animation for item: " + ownerName);
            return;
        }

        String finalName = animName.startsWith("animation.") ? animName : generateAnimPathString(animName);
        owner.triggerAnim(
                player,
                GeoItem.getOrAssignId(stack, (ServerLevel) level),
                WildDungeons.rl(ownerName).toString(),
                finalName
        );

        float speed = animations.stream()
                .filter(pair -> pair.getFirst().getAnimationStages().getFirst().animationName().equals(finalName))
                .findFirst().get().getSecond();

        setAnimationSpeed(speed, level);
    }

    private String generateAnimPathString(String animName) {
        return "animation." + ownerName + "." + animName;
    }
}
