package com.danielkkrafft.wilddungeons.api.client;

import net.irisshaders.iris.Iris;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

public class ShadersIntegration {
    public static boolean LOADED;

    public static class LoadedOnly {
        public static boolean isShadersEnabled() {
            return Iris.getIrisConfig().areShadersEnabled();
        }
    }

    public static void init() {
        LOADED = ModList.get().isLoaded("oculus") || ModList.get().isLoaded("iris");
    }

    public static boolean isLoaded() {
        return LOADED;
    }

    public static boolean isShadersEnabled() {
        if (isLoaded()) {
            return LoadedOnly.isShadersEnabled();
        }
        return false;
    }

    public static boolean shouldApply() {
        return isShadersEnabled() || Minecraft.useShaderTransparency();
    }

}