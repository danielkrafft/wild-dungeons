package com.danielkkrafft.wilddungeons.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SynchronizedSoundLoop extends AbstractTickableSoundInstance {
    public long currentMS = 0;
    public long elapsedMS = 0;
    public int fadeMS = 1000;
    public int layer = 0;
    public boolean active = false;

    public SynchronizedSoundLoop(SoundEvent soundEvent, SoundSource source, int layer) {
        super(soundEvent, source, RandomSource.create());
        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.looping = true;
        this.attenuation = Attenuation.NONE;
        this.layer = layer;
        this.currentMS = System.currentTimeMillis();
    }

    public void rise() {
        this.active = true;
        this.elapsedMS = 0;
    }

    public void fade() {
        this.active = false;
        this.elapsedMS = 0;
    }

    public void stopPlaying() {
        this.stop();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public float getVolume() {
        this.elapsedMS += System.currentTimeMillis() - this.currentMS;
        this.currentMS = System.currentTimeMillis();
        float ratio = Math.clamp((float) this.elapsedMS / fadeMS, 0.0f, 1.0f);
        if (this.active) this.volume = Mth.lerp(ratio, 0.0f, 1.0f);
        else this.volume = Mth.lerp(ratio, 1.0f, 0.0f);
        return this.volume;
    }

    @Override
    public void tick() {
        if (!Minecraft.getInstance().player.isAlive()) {
            this.stop();
            SoundscapeHandler.currentlyPlayingSounds.remove(this);
        }
    }
}
