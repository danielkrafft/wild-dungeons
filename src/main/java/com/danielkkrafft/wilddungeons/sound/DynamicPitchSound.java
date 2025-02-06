package com.danielkkrafft.wilddungeons.sound;

import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class DynamicPitchSound extends AbstractTickableSoundInstance {
    private final Entity entity;
    private float distance = 10.0f;

    public DynamicPitchSound(SoundEvent sound, SoundSource category, float volume, float pitch, Entity entity, boolean loop) {
        super(sound, category, RandomSource.create());
        this.volume = volume;
        this.pitch = pitch;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.entity = entity;
        attenuation = Attenuation.LINEAR;
        looping = loop;
    }

    @Override
    public float getVolume() {
        this.distance = (float) Math.sqrt(Minecraft.getInstance().player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5));
        float ratio =  Math.min(this.distance / 25.0f, 1.0f);
        return super.getVolume() * Mth.lerp(ratio, 3.0f, 0.0f);
    }

    @Override
    public float getPitch() {
        float ratio = Math.min(this.distance / 10.0f, 1.0f);
        return super.getPitch() * Mth.lerp(ratio, 1.0f, 0.5f);
    }

    @Override public void tick() {
        if (!entity.isAlive() || this.distance > 80.0f) {
            this.stop();
            ClientPacketHandler.loopingSounds.remove(this.entity.getId());
        }
    }
}
