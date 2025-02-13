package com.danielkkrafft.wilddungeons.sound;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class BasicSoundLoop extends SimpleSoundInstance {
    public BasicSoundLoop(SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        super(soundEvent, source, volume, pitch, RandomSource.create(), new BlockPos(0,0,0));
        this.looping = true;
        this.attenuation = Attenuation.NONE;
    }
}
