package com.danielkkrafft.wilddungeons.enchantment.custom;

import com.danielkkrafft.wilddungeons.entity.attachmenttypes.HomingTargetAttachmentType;
import com.danielkkrafft.wilddungeons.registry.WDAttachmentTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record HomingEnchantmentEffect() implements EnchantmentEntityEffect {

    public static final MapCodec<HomingEnchantmentEffect> CODEC = MapCodec.unit(HomingEnchantmentEffect::new);

    @Override
    public void apply(@NotNull ServerLevel serverLevel, int i, @NotNull EnchantedItemInUse enchantedItemInUse, @NotNull Entity entity, @NotNull Vec3 vec3) {
        HomingTargetAttachmentType homing = entity.getData(WDAttachmentTypes.HOMING_ATTACHMENT).copy();
        homing.setLevel(i);
        entity.setData(WDAttachmentTypes.HOMING_ATTACHMENT,homing);
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}