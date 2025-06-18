package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.registries.DeferredHolder;

public class GunWeaponData extends BaseProjectileData {

    public Holder<SoundEvent> fireSound;
    public boolean usesAmmo;
    public int cooldown;
    public float spawnDistanceOffset;
    public float spawnHeightOffset;
    public float projectileSpeed;
    public boolean hasFire;
    public boolean hasReload;

    public GunWeaponData(
            String name, int stacksTo, int durability, int duration,
            Item ammoType, int projectileRange, UseAnim useAnim,
            DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>> projectileClass,
            Rarity rarity, String animations, String model, String texture,
            Holder<SoundEvent> fireSound, Boolean usesAmmo, int cooldown,
            float spawnDistanceOffset, float spawnHeightOffset, float projectileSpeed,
            String projectileName, boolean hasIdle, boolean hasFire, boolean hasReload, boolean hasEmissive
    ) {

        this.name = name;
        this.stacksTo = stacksTo;
        this.durability = durability;
        this.useDuration = duration;
        this.ammoType = ammoType;
        this.projectileRange = projectileRange;
        this.useAnim = useAnim;
        this.projectileClass = projectileClass;
        this.rarity = rarity;
        this.animations = animations;
        this.baseModel = model;
        this.baseTexture = texture;
        this.fireSound = fireSound;
        this.usesAmmo = usesAmmo;
        this.cooldown = cooldown;
        this.spawnDistanceOffset = spawnDistanceOffset;
        this.spawnHeightOffset = spawnHeightOffset;
        this.projectileSpeed = projectileSpeed;
        this.projectileName = projectileName;
        this.hasIdle = hasIdle;
        this.hasFire = hasFire;
        this.hasReload = hasReload;
        this.hasEmissive = hasEmissive;
    }
}
