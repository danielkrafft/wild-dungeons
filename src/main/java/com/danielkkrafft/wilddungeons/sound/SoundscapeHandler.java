package com.danielkkrafft.wilddungeons.sound;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPauseChangeEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class SoundscapeHandler {

    public static HashSet<SynchronizedSoundLoop> currentlyPlayingSounds = new HashSet<>();
    public static HashSet<SynchronizedSoundLoop> currentlyPlayingUnderwaterSounds = new HashSet<>();
    public static DungeonRegistration.SoundscapeTemplate currentTemplate = null;
    public static int currentIntensity = 0;
    public static boolean isUnderwater = false;

    public static void handleSwitchSoundscape(DungeonRegistration.SoundscapeTemplate template, int intensity, boolean forceReset) {

        if (template == null || forceReset) {
            stopAndClearAllPlayingSounds();
            if (template == null) return;
        }

        currentIntensity = intensity;
        HashSet<ResourceLocation> soundRLs = new HashSet<>();
        HashSet<SynchronizedSoundLoop> toPlay = new HashSet<>();

        addSoundsToPlay(template.soundsList, soundRLs, toPlay, currentlyPlayingSounds);
        playSounds(toPlay, currentlyPlayingSounds);

        if (!template.underwaterSoundsList.isEmpty()) {
            toPlay.clear();
            addSoundsToPlay(template.underwaterSoundsList, soundRLs, toPlay, currentlyPlayingUnderwaterSounds);
            playSounds(toPlay, currentlyPlayingUnderwaterSounds);

        }
         refreshCurrentlyPlayingSounds();

        removeSounds(soundRLs, currentlyPlayingSounds);
        removeSounds(soundRLs, currentlyPlayingUnderwaterSounds);
    }

    public static void refreshCurrentlyPlayingSounds() {

        if (isUnderwater && !currentlyPlayingUnderwaterSounds.isEmpty()) {

            currentlyPlayingUnderwaterSounds.forEach(soundInstance -> {
                if (currentIntensity >= soundInstance.layer && !soundInstance.active) soundInstance.rise();
                else if (currentIntensity < soundInstance.layer && soundInstance.active) soundInstance.fade();
            });
            currentlyPlayingSounds.forEach(SynchronizedSoundLoop::fade);
        } else {

            currentlyPlayingSounds.forEach(soundInstance -> {
                if (currentIntensity >= soundInstance.layer && !soundInstance.active) soundInstance.rise();
                else if (currentIntensity < soundInstance.layer && soundInstance.active) soundInstance.fade();
            });
            currentlyPlayingUnderwaterSounds.forEach(SynchronizedSoundLoop::fade);
        }
    }

    public static void stopAndClearSoundSet(HashSet<SynchronizedSoundLoop> soundSetToStopAndClear) {

        soundSetToStopAndClear.forEach(SynchronizedSoundLoop::stopPlaying);
        soundSetToStopAndClear.clear();
    }

    public static void stopAndClearAllPlayingSounds(){

        stopAndClearSoundSet(currentlyPlayingSounds);
        stopAndClearSoundSet(currentlyPlayingUnderwaterSounds);
    }

    public static void addSoundsToPlay(List<List<Holder<SoundEvent>>> rawSoundList, HashSet<ResourceLocation> inSoundRLs, HashSet<SynchronizedSoundLoop> inToPlay, HashSet<SynchronizedSoundLoop> registryCheck) {

        for (int i = 0; i < rawSoundList.size(); i++) {
            for (int j = 0; j < rawSoundList.get(i).size(); j++) {
                SoundEvent soundEvent = rawSoundList.get(i).get(j).value();
                inSoundRLs.add(soundEvent.getLocation());

                if (registryCheck.stream().noneMatch(sound -> sound.getLocation().equals(soundEvent.getLocation()))) {
                    SynchronizedSoundLoop sound = new SynchronizedSoundLoop(soundEvent, SoundSource.MUSIC, i);
                    inToPlay.add(sound);
                }
            }
        }
    }

    public static void playSounds(HashSet<SynchronizedSoundLoop> inToPlay, HashSet<SynchronizedSoundLoop> currentlyPlayingRegistry) {

        inToPlay.forEach(soundLoop -> {
            currentlyPlayingRegistry.add(soundLoop);
            Minecraft.getInstance().getSoundManager().play(soundLoop);
        });
    }

    public static void removeSounds(HashSet<ResourceLocation> soundLocationSet, HashSet<SynchronizedSoundLoop> SyncSoundLoopSet) {

        List<SynchronizedSoundLoop> toRemove = new ArrayList<>();
        SyncSoundLoopSet.forEach(soundLoop -> {
            if (!soundLocationSet.contains(soundLoop.getLocation())) {
                toRemove.add(soundLoop);
                Minecraft.getInstance().getSoundManager().stop(soundLoop);
            }
        });

        toRemove.forEach(SyncSoundLoopSet::remove);
    }

    @SubscribeEvent
    public static void onMusicPlay(PlaySoundEvent event) {
        if (Minecraft.getInstance().player == null) {
//            WildDungeons.getLogger().info("PLAYING SOUND: {}", event.getName());
            return;
        }
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateClientWDPlayer(Minecraft.getInstance().player);
        if (!wdPlayer.isInsideDungeon()) {
//            WildDungeons.getLogger().info("PLAYING SOUND: {}", event.getName());
            return;
        }
        if (event.getSound().getLocation().toString().contains(WildDungeons.MODID)) {
//            WildDungeons.getLogger().info("PLAYING SOUND: {}", event.getName());
            return;
        }
        if (event.getSound().getLocation().toString().contains("music")) {
//            WildDungeons.getLogger().info("CANCELLING SOUND: {}", event.getName());
            event.setSound(null);
        }
    }

    @SubscribeEvent
    public static void onPause(ClientPauseChangeEvent.Post event) {
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) == 0.0f) {
            currentlyPlayingSounds.forEach(SynchronizedSoundLoop::stopPlaying);
            currentlyPlayingSounds.clear();
            return;
        }
    }

    public static void toggleUnderwater(boolean inUnderwaterStatus) {

        isUnderwater = inUnderwaterStatus;
        refreshCurrentlyPlayingSounds();
    }
}
