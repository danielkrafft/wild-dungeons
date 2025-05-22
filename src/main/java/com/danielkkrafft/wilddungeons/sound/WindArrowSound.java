package com.danielkkrafft.wilddungeons.sound;

import com.danielkkrafft.wilddungeons.entity.WindArrow;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WindArrowSound extends AbstractTickableSoundInstance {
    private final WindArrow arrow;
    private final LocalPlayer player;

    public WindArrowSound(@NotNull WindArrow arrow, @NotNull LocalPlayer p) {
        super(WDSoundEvents.WIND_ARROW_FLYBY.value(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.arrow = arrow;
        player = p;
        looping = true;
        delay = 0;
        volume = 0;
        x = player.getX();
        y = player.getY();
        z = player.getZ();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return true;
    }

    private float prevPitch;

    @Override
    public void tick() {
        if (arrow.isRemoved() || player.isRemoved()) stop();
        else {
            x = arrow.getX();
            y = arrow.getY();
            z = arrow.getZ();
            double dist = arrow.position().subtract(player.position()).length();
            Vec3 directionTo = arrow.position().subtract(player.position()), velDir = arrow.getDeltaMovement();
            //-1<d<0 -> approaching user
            //0<d<1 -> flyby
            float direction = (float) ((directionTo.x * velDir.x + directionTo.y * velDir.y + directionTo.z * velDir.z) / (directionTo.length() * velDir.length()));
            float vol = 2 / (float) ((dist < 2 ? 2 : dist) * (direction > 0 ? 0.5f : 1f));
            if (vol > 0.05f) {
                volume = vol;
                float pi = direction < 0 ? 1 : (1 - Math.abs(direction));
                float goToPitch = pi * 0.5f + 0.5f;
                float diff = goToPitch - pitch;
                float dt = direction < 0 ? 0.05f : 0.01f;
                if (Mth.abs(diff) < dt) pitch = goToPitch;
                else pitch += (diff > 0 ? 1 : -1) * dt;
            } else volume = 0;
        }
    }
}