package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;

import java.util.function.Predicate;

public class BowWeaponData {
    public final String name;
    public final int stacksTo;
    public final int durability;
    public final int useDuration;
    public final Predicate<ItemStack> ammoType;
    public final int projectileRange;
    public final UseAnim useAnim;
    public final String arrowClass;
    public final Rarity bowRarity;
    public final String rendererClass;
    public final String bowAnimations;
    public final String bowModelStill;
    public final String bowModelCharged;
    public final String bowTextureStill;
    public final String bowTextureCharged;
    public final Holder<SoundEvent> drawSound;

    public BowWeaponData(
            String name, int stacksTo, int durability, int useDuration,
            Predicate<ItemStack> ammoType, int projectileRange, UseAnim useAnim,
            String arrowClass, Rarity bowRarity, String rendererClass,
            String bowAnimations, String bowModelStill, String bowModelCharged,
            String bowTextureStill, String bowTextureCharged, Holder<SoundEvent> bowDrawSound
    ) {
        this.name = name;
        this.stacksTo = stacksTo;
        this.durability = durability;
        this.useDuration = useDuration;
        this.ammoType = ammoType;
        this.projectileRange = projectileRange;
        this.useAnim = useAnim;
        this.arrowClass = arrowClass;
        this.bowRarity = bowRarity;
        this.rendererClass = rendererClass;
        this.bowAnimations = bowAnimations;
        this.bowModelStill = bowModelStill;
        this.bowModelCharged = bowModelCharged;
        this.bowTextureStill = bowTextureStill;
        this.bowTextureCharged = bowTextureCharged;
        this.drawSound = bowDrawSound;
    }
}

