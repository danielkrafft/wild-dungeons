package com.danielkkrafft.wilddungeons;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@Mod(value = WildDungeons.MODID, dist = Dist.CLIENT)
public class WildDungeonsClient {
    public WildDungeonsClient(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
    }
    public static final Lazy<KeyMapping> TOGGLE_ESSENCE_TYPE = Lazy.of(() -> new KeyMapping(//todo should be moved to it's own keymapping class if we add more keybindings
            "key.wilddungeons.toggle_essence_type",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.wilddungeons"
    ));

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_ESSENCE_TYPE.get());
    }
}
