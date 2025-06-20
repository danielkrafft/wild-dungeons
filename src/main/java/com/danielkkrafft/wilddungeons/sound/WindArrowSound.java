package com.danielkkrafft.wilddungeons.sound;

import com.danielkkrafft.wilddungeons.entity.WindArrow;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WindArrowSound extends AbstractTickableSoundInstance {
    private final WindArrow arrow;

    public WindArrowSound(@NotNull WindArrow arrow) {
        super(WDSoundEvents.WIND_ARROW_FLYBY.value(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.arrow = arrow;
        this.x = (float)arrow.getX();
        this.y = (float)arrow.getY();
        this.z = (float)arrow.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !this.arrow.isSilent();
    }

    private float prevPitch;

    @Override
    public void tick() {
        if (arrow.isRemoved()) stop();
        else {
            x = arrow.getX();
            y = arrow.getY();
            z = arrow.getZ();
            double dist = arrow.getDeltaMovement().horizontalDistance();
            if (dist >= 0.01F) {
                this.pitch = (float) Mth.lerp(Mth.clamp(dist, 0.7f, 1.1f),0.7f, 1.1f);
                this.volume = (float) Mth.lerp(Mth.clamp(dist, 0.0F, 0.5F), 0.0F, 1.2F);
            } else {
                this.pitch = 0.0F;
                this.volume = 0.0F;
            }
        }
    }
}