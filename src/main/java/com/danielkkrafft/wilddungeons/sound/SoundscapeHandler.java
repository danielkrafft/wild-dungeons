package com.danielkkrafft.wilddungeons.sound;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.HashSet;

public class SoundscapeHandler {

    public static HashSet<ResourceLocation> currentlyPlayingSounds = new HashSet<>();

    public static void handleSwitchSoundscape(DungeonRegistration.SoundscapeTemplate template, int intensity) {
        HashSet<SoundEvent> soundsToPlay = new HashSet<>();
        HashSet<ResourceLocation> soundRLs = new HashSet<>();

        for (int i = 0; i <= intensity; i++) {
            soundsToPlay.addAll(template.soundsList.get(i));
        }

        soundsToPlay.forEach(soundEvent -> {
            soundRLs.add(soundEvent.getLocation());
            if (!currentlyPlayingSounds.contains(soundEvent.getLocation())) {
                BasicSoundLoop sound = new BasicSoundLoop(soundEvent, SoundSource.MUSIC, 1.0f, 1.0f);
                currentlyPlayingSounds.add(sound.getLocation());
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        });


        currentlyPlayingSounds.forEach(resourceLocation -> {
           if (!soundRLs.contains(resourceLocation)) {
               currentlyPlayingSounds.remove(resourceLocation);
               Minecraft.getInstance().getSoundManager().stop(resourceLocation, SoundSource.MUSIC);
           }
        });
    }
}
