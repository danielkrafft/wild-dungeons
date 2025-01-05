package com.danielkkrafft.wilddungeons.util;

public class ColorUtil {

    public static int[] applyHueOffset(int r, int g, int b, int hueOffset) {
        float[] hsl = rgbToHsl(r, g, b);
        hsl[0] = (hsl[0] + hueOffset) % 360;
        if (hsl[0] < 0) hsl[0] += 360;
        return hslToRgb(hsl[0], hsl[1], hsl[2]);
    }

    public static float[] rgbToHsl(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h = 0f;
        if (delta != 0) {
            if (max == rf) {
                h = ((gf - bf) / delta) % 6;
            } else if (max == gf) {
                h = ((bf - rf) / delta) + 2;
            } else {
                h = ((rf - gf) / delta) + 4;
            }
            h *= 60;
            if (h < 0) h += 360;
        }

        float l = (max + min) / 2;
        float s = delta == 0 ? 0 : delta / (1 - Math.abs(2 * l - 1));

        return new float[]{h, s, l}; // Hue, Saturation, Lightness
    }

    public static int[] hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;

        float rf = 0, gf = 0, bf = 0;
        if (h < 60) { rf = c; gf = x; bf = 0; }
        else if (h < 120) { rf = x; gf = c; bf = 0; }
        else if (h < 180) { rf = 0; gf = c; bf = x; }
        else if (h < 240) { rf = 0; gf = x; bf = c; }
        else if (h < 300) { rf = x; gf = 0; bf = c; }
        else { rf = c; gf = 0; bf = x; }

        int r = Math.round((rf + m) * 255);
        int g = Math.round((gf + m) * 255);
        int b = Math.round((bf + m) * 255);

        return new int[]{r, g, b}; // Red, Green, Blue
    }

}
