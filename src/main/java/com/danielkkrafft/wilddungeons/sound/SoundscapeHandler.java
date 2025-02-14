package com.danielkkrafft.wilddungeons.sound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class SoundscapeHandler {

    public static HashSet<SynchronizedSoundLoop> currentlyPlayingSounds = new HashSet<>();

    public static void handleSwitchSoundscape(DungeonRegistration.SoundscapeTemplate template, int intensity) {
        if (template == null) {
            currentlyPlayingSounds.forEach(soundLoop -> {soundLoop.stopPlaying();});
            currentlyPlayingSounds.clear();
            return;
        }
        HashSet<ResourceLocation> soundRLs = new HashSet<>();
        HashSet<SynchronizedSoundLoop> toPlay = new HashSet<>();


        for (int i = 0; i < template.soundsList.size(); i++) {
            for (int j = 0; j < template.soundsList.get(i).size(); j++) {
                SoundEvent soundEvent = template.soundsList.get(i).get(j);
                soundRLs.add(soundEvent.getLocation());

                if (currentlyPlayingSounds.stream().noneMatch(sound -> sound.getLocation().equals(soundEvent.getLocation()))) {
                    SynchronizedSoundLoop sound = new SynchronizedSoundLoop(soundEvent, SoundSource.MUSIC, i);
                    toPlay.add(sound);
                }
            }
        }

        toPlay.forEach(soundLoop -> {
            currentlyPlayingSounds.add(soundLoop);
            Minecraft.getInstance().getSoundManager().play(soundLoop);
        });

        currentlyPlayingSounds.forEach(soundInstance -> {
            if (intensity >= soundInstance.layer && !soundInstance.active) soundInstance.rise();
            else if (intensity < soundInstance.layer && soundInstance.active) soundInstance.fade();
        });

        List<SynchronizedSoundLoop> toRemove = new ArrayList<>();
        currentlyPlayingSounds.forEach(soundLoop -> {
           if (!soundRLs.contains(soundLoop.getLocation())) {
               toRemove.add(soundLoop);
               Minecraft.getInstance().getSoundManager().stop(soundLoop);
           }
        });

        toRemove.forEach(soundLoop -> {
            currentlyPlayingSounds.remove(soundLoop);
        });
    }

    @SubscribeEvent
    public static void onMusicPlay(PlaySoundEvent event) {
        WildDungeons.getLogger().info("PLAYING SOUND: {}", event.getSound().getLocation());
        if (Minecraft.getInstance().player == null) return;
        if (WDPlayerManager.getInstance().getOrCreateClientWDPlayer(Minecraft.getInstance().player).getCurrentDungeon() == null) return;
        if (event.getSound().getLocation().toString().contains(WildDungeons.MODID)) return;
        if (event.getSound().getLocation().toString().contains("music")) {
            event.setSound(null);
        }
    }
}
