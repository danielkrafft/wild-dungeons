package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.SoundscapeTemplate;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;

import java.util.ArrayList;

public class SoundscapeTemplateRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<SoundscapeTemplate> SOUNDSCAPE_TEMPLATE_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<SoundscapeTemplate> soundscapes = new ArrayList<>();

    public static final SoundscapeTemplate NONE = create("NONE");

    public static final SoundscapeTemplate NETHER_CAVES = create("NETHER_CAVES")
            .addSound(WDSoundEvents.CAVE_01, 0)
            .addSound(WDSoundEvents.NETHER_MELODY, 1)
            .addSound(WDSoundEvents.NETHER_BASS, 2)
            .addSound(WDSoundEvents.NETHER_BEAT, 3);

    public static final SoundscapeTemplate MEGA_DUNGEON = create("MEGA_DUNGEON")
            .addSound(WDSoundEvents.CAVE_02, 0)
            .addSound(WDSoundEvents.MEGA_DUNGEON_MELODY, 1)
            .addSound(WDSoundEvents.MEGA_DUNGEON_BEAT, 2);

    public static final SoundscapeTemplate PIGLIN_FACTORY = create("PIGLIN_FACTORY")
            .addSound(WDSoundEvents.CAVE_02, 0)
            .addSound(WDSoundEvents.FACTORY_MELODY, 1)
            .addSound(WDSoundEvents.FACTORY_BASS, 2)
            .addSound(WDSoundEvents.FACTORY_BEAT, 3);

    public static final SoundscapeTemplate NETHER_DRAGON_LEADUP = create("NETHER_DRAGON_LEADUP")
            .addSound(WDSoundEvents.HORRIFIC_SCREAMING, 0)
            .addSound(WDSoundEvents.NETHER_DRAGON_BASS, 1);

    public static final SoundscapeTemplate NETHER_DRAGON = create("NETHER_DRAGON")
            .addSound(WDSoundEvents.NETHER_DRAGON, 0);

    public static final SoundscapeTemplate PEACEFUL = create("PEACEFUL")
            .addSound(WDSoundEvents.CAVE_02, 0)
            .addSound(WDSoundEvents.JAZZ, 1);

    public static final SoundscapeTemplate MOONLIGHT_SONATA_1ST = create("MOONLIGHT_SONATA_1ST")
            .addSound(WDSoundEvents.CAVE_02, 0)
            .addSound(WDSoundEvents.MOONLIGHT_SONATA_1ST, 1);

    public static final SoundscapeTemplate MOONLIGHT_SONATA_3RD = create("MOONLIGHT_SONATA_3RD")
            .addSound(WDSoundEvents.CAVE_02, 0)
            .addSound(WDSoundEvents.MOONLIGHT_SONATA_3RD, 1);

    public static SoundscapeTemplate create(String name){
        SoundscapeTemplate soundscape = new SoundscapeTemplate(name);
        soundscapes.add(soundscape);
        return soundscape;
    }

    public static void setupSoundscapes(){
        soundscapes.forEach(SOUNDSCAPE_TEMPLATE_REGISTRY::add);
    }
}
