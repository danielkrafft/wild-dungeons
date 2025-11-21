package com.danielkkrafft.wilddungeons.enchantment;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.enchantment.custom.DensityEnchantmentEffect;
import com.danielkkrafft.wilddungeons.enchantment.custom.HomingEnchantmentEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;

public class WDEnchantments {

    public static final ResourceKey<Enchantment> DENSITY =
            ResourceKey.create(Registries.ENCHANTMENT, WildDungeons.rl("density_enchantment"));

    public static final ResourceKey<Enchantment> LUNGE =
            ResourceKey.create(Registries.ENCHANTMENT, WildDungeons.rl("lunge_enchantment"));

    public static final ResourceKey<Enchantment> HOMING =
            ResourceKey.create(Registries.ENCHANTMENT, WildDungeons.rl("homing_enchantment"));

    public static final ResourceKey<Enchantment> RANGE =
            ResourceKey.create(Registries.ENCHANTMENT, WildDungeons.rl("range_enchantment"));

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        register(context, DENSITY, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                items.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                1,
                5,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(25, 8),
                2,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM, new DensityEnchantmentEffect()));

        register(context, LUNGE, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(25, 8),
                        2,
                        EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE)));

        register(context, RANGE, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(25, 8),
                        2,
                        EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE)));

        register(context, HOMING, Enchantment.enchantment(
                        Enchantment.definition(
                                items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                2,
                                3,
                                Enchantment.dynamicCost(5, 8),
                                Enchantment.dynamicCost(25, 8),
                                4,
                                EquipmentSlotGroup.MAINHAND
                        )).withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED, new HomingEnchantmentEffect()));

    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
}