package com.danielkkrafft.wilddungeons.util;

import java.util.List;

public class RandomUtil {

    public static int randIntBetween(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static float randFloatBetween(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static <T> T randomFromList(List<T> values) {
        return values.get(randIntBetween(0, values.size()-1));
    }

    public static boolean sample(double threshold) {
        double f = Math.random();
        return f > threshold;
    }

}
