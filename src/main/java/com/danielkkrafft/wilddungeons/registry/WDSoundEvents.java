package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, WildDungeons.MODID);

    public static final Holder<SoundEvent> HAMMER_SMASH_LIGHT = SOUND_EVENTS.register("item.hammer.smash.light", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> BREEZE_GOLEM_WALK = SOUND_EVENTS.register("entity.breeze_golem.walk", SoundEvent::createVariableRangeEvent);
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


    public static final Holder<SoundEvent> AMOGUS_AMBIENT = SOUND_EVENTS.register("entity.amogus.ambient", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> AMOGUS_KILL = SOUND_EVENTS.register("entity.amogus.kill", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> AMOGUS_STEP = SOUND_EVENTS.register("entity.amogus.step", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> RIFT_AURA = SOUND_EVENTS.register("entity.rift_aura", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> WHISPERS = SOUND_EVENTS.register("entity.whispers", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> SHIMMER = SOUND_EVENTS.register("entity.shimmer", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> CAVE_01 = SOUND_EVENTS.register("soundscape.ambient.cave_01", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> NETHER_BASS = SOUND_EVENTS.register("soundscape.music.nether_bass", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> NETHER_BACKING = SOUND_EVENTS.register("soundscape.music.nether_backing", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> NETHER_MELODY = SOUND_EVENTS.register("soundscape.music.nether_melody", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> NETHER_BEAT = SOUND_EVENTS.register("soundscape.music.nether_beat", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> CAVE_02 = SOUND_EVENTS.register("soundscape.ambient.cave_02", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEGA_DUNGEON_MELODY = SOUND_EVENTS.register("soundscape.music.mega_dungeon_melody", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEGA_DUNGEON_BEAT = SOUND_EVENTS.register("soundscape.music.mega_dungeon_beat", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> JAZZ = SOUND_EVENTS.register("soundscape.music.jazz", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MOONLIGHT_SONATA_1ST = SOUND_EVENTS.register("soundscape.music.moonlight_sonata_1st", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MOONLIGHT_SONATA_3RD = SOUND_EVENTS.register("soundscape.music.moonlight_sonata_3rd", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> UI_BEEP = SOUND_EVENTS.register("ui.misc.beep", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> MEATHOOK_HIT = SOUND_EVENTS.register("entity.meathook.hit", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_REEL = SOUND_EVENTS.register("entity.meathook.reel", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_CHARGE_1 = SOUND_EVENTS.register("item.meathook.charge1", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_CHARGE_2 = SOUND_EVENTS.register("item.meathook.charge2", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_CHARGE_3 = SOUND_EVENTS.register("item.meathook.charge3", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_CHARGE_4 = SOUND_EVENTS.register("item.meathook.charge4", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_CHARGE_5 = SOUND_EVENTS.register("item.meathook.charge5", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_LOAD = SOUND_EVENTS.register("item.meathook.loaded", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_FIRE = SOUND_EVENTS.register("item.meathook.fire", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> MEATHOOK_RETRACT = SOUND_EVENTS.register("item.meathook.retract", SoundEvent::createVariableRangeEvent);
}
