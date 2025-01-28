package com.danielkkrafft.wilddungeons.util;

import net.minecraft.util.RandomSource;

public class RandomUtil {

    public static int randIntBetween(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static float randFloatBetween(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static boolean sample(RandomSource random, float threshold) {
        float f = random.nextFloat();
        return f > threshold;
    }

}
