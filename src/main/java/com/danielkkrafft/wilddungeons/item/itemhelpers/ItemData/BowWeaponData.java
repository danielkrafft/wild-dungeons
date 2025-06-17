package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BowWeaponData extends BaseProjectileData {

    public String bowModelCharged;
    public String bowTextureCharged;
    public Holder<SoundEvent> drawSound;

    public BowWeaponData(
            String name, int stacksTo, int durability, int useDuration,
            Item ammoType, int projectileRange, UseAnim useAnim,
            DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>> arrowClass,
            Rarity bowRarity, String bowAnimations, String bowModelStill, String bowModelCharged,
            String bowTextureStill, String bowTextureCharged, Holder<SoundEvent> bowDrawSound,
            boolean hasIdle, String ammoName
    ) {
        this.name = name;
        this.stacksTo = stacksTo;
        this.durability = durability;
        this.useDuration = useDuration;
        this.ammoType = ammoType;
        this.projectileRange = projectileRange;
        this.useAnim = useAnim;
        this.projectileClass = arrowClass;
        this.rarity = bowRarity;
        this.animations = bowAnimations;
        this.baseModel = bowModelStill;
        this.bowModelCharged = bowModelCharged;
        this.baseTexture = bowTextureStill;
        this.bowTextureCharged = bowTextureCharged;
        this.drawSound = bowDrawSound;
        this.hasIdle = hasIdle;
    }
}

