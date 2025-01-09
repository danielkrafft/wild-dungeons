package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, WildDungeons.MODID);

    public static final Holder<SoundEvent> BREEZE_GOLEM_WALK = SOUND_EVENTS.register("entity.breeze_golem.walk", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> HAMMER_SMASH_LIGHT = SOUND_EVENTS.register("item.hammer.smash.light", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BREEZE_GOLEM_CANNON_SHOOT = SOUND_EVENTS.register("entity.breeze_golem.cannon.shoot", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BREEZE_GOLEM_CANNON_CHARGE = SOUND_EVENTS.register("entity.breeze_golem.cannon.charge", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BREEZE_GOLEM_CORE = SOUND_EVENTS.register("entity.breeze_golem.core", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BREEZE_GOLEM_CANNON_START = SOUND_EVENTS.register("entity.breeze_golem.cannon.start", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BREEZE_GOLEM_AMBIENT = SOUND_EVENTS.register("entity.breeze_golem.ambient", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BREEZE_GOLEM_DEATH = SOUND_EVENTS.register("entity.breeze_golem.death", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> WIND_CHARGE_IMPACT = SOUND_EVENTS.register("entity.wind_charge.impact", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_DIG = SOUND_EVENTS.register("entity.mutant_bogged.dig", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_AMBIENT = SOUND_EVENTS.register("entity.mutant_bogged.ambient", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_HIT = SOUND_EVENTS.register("entity.mutant_bogged.hit", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_DEATH = SOUND_EVENTS.register("entity.mutant_bogged.death", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_WALK = SOUND_EVENTS.register("entity.mutant_bogged.walk", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_ARROW_VOLLEY = SOUND_EVENTS.register("entity.mutant_bogged.arrow_volley", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_CHARGED_ARROW = SOUND_EVENTS.register("entity.mutant_bogged.charged_arrow", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MUTANT_BOGGED_GROWL = SOUND_EVENTS.register("entity.mutant_bogged.growl", SoundEvent::createVariableRangeEvent);
}
