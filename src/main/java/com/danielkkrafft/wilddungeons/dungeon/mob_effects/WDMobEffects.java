package com.danielkkrafft.wilddungeons.dungeon.mob_effects;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

public class WDMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, WildDungeons.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> STEP_HEIGHT =
            MOB_EFFECTS.register(
                    "step_height",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(250, 154, 237).getRGB())
                            .addAttributeModifier(
                                    Attributes.STEP_HEIGHT,
                                    WildDungeons.rl("effect.step_height"),
                                    0.5f,
                                    AttributeModifier.Operation.ADD_VALUE
                            )
            );
    public static final DeferredHolder<MobEffect, MobEffect> AXE_DAMAGE =
            MOB_EFFECTS.register(
                    "axe_damage",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(0, 247, 255).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> SWORD_DAMAGE =
            MOB_EFFECTS.register(
                    "sword_damage",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(0, 140, 255).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> BOW_DAMAGE =
            MOB_EFFECTS.register(
                    "bow_damage",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(0, 255, 178).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> POISON_RESISTANCE =
            MOB_EFFECTS.register(
                    "poison_resistance",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(151, 255, 0).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> EXPLOSION_RESISTANCE =
            MOB_EFFECTS.register(
                    "explosion_resistance",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(194, 0, 0).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> EVASION =
            MOB_EFFECTS.register(
                    "evasion",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(255, 229, 171).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> ONE_PUNCH_MAN =
            MOB_EFFECTS.register(
                    "one_punch_man",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(217, 90, 0).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> BIG_RED_BUTTON =
            MOB_EFFECTS.register(
                    "big_red_button",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(255, 0, 0).getRGB())
            );
    public static final DeferredHolder<MobEffect, MobEffect> KEEN_EDGE =
            MOB_EFFECTS.register(
                    "keen_edge",
                    () -> new WDMobEffect(MobEffectCategory.BENEFICIAL, new Color(181, 208, 218).getRGB())
            );
}
