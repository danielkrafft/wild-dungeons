package com.danielkkrafft.wilddungeons;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import static com.danielkkrafft.KeyBindings.TOGGLE_ESSENCE_TYPE;

@Mod(value = WildDungeons.MODID, dist = Dist.CLIENT)
public class WildDungeonsClient {
    public WildDungeonsClient(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_ESSENCE_TYPE.get());
    }
}
