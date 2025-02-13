package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.SoundscapeTemplate;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;

import java.util.ArrayList;

public class SoundscapeTemplateRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<SoundscapeTemplate> SOUNDSCAPE_TEMPLATE_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<SoundscapeTemplate> soundscapes = new ArrayList<>();

    public static final SoundscapeTemplate NETHER_CAVES = create("NETHER_CAVES")
            .addSound(WDSoundEvents.CAVE_01.value(), 0)
            .addSound(WDSoundEvents.NETHER_MELODY.value(), 1)
            .addSound(WDSoundEvents.NETHER_BACKING.value(), 2)
            .addSound(WDSoundEvents.NETHER_BASS.value(), 3)
            .addSound(WDSoundEvents.NETHER_BEAT.value(), 4);

    public static SoundscapeTemplate create(String name){
        SoundscapeTemplate soundscape = new SoundscapeTemplate(name);
        soundscapes.add(soundscape);
        return soundscape;
    }

    public static void setupSoundscapes(){
        soundscapes.forEach(SOUNDSCAPE_TEMPLATE_REGISTRY::add);
    }
}
