package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.client.DeltaTracker;

public class CameraShakeUtil {

    private static float shakeStrength = 0f;
    private static final float DECAY_RATE = 0.05f;

    // cooldown
    private static final double SHAKE_COOLDOWN_DURATION = 0.2f;
    private static double shakeCooldownAlpha = 0.3f;

    public static void trigger(float strength) {
        shakeStrength = Math.min(shakeStrength + strength, 1.0f);
        shakeCooldownAlpha = SHAKE_COOLDOWN_DURATION;
    }

    public static void tick(float partialTick) {
        if (shakeStrength > 0f) {

        }

        // when trigger is called the alpha is refreshed.
        shakeCooldownAlpha -= partialTick;
        if (shakeCooldownAlpha <= 0.f && shakeStrength > 0.f) {
            shakeStrength -= DECAY_RATE;
        }

        if (shakeStrength < 0f) shakeStrength = 0f;
    }

    public static float getShakeStrength() {
        return shakeStrength;
    }
}