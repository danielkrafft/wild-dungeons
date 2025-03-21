package com.danielkkrafft;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final Lazy<KeyMapping> TOGGLE_ESSENCE_TYPE = Lazy.of(() -> new KeyMapping(
            "key.wilddungeons.toggle_essence_type",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.wilddungeons"
    ));
}
