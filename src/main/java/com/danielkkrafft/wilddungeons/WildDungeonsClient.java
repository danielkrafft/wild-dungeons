package com.danielkkrafft.wilddungeons;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = WildDungeons.MODID, dist = Dist.CLIENT)
public class WildDungeonsClient {
    public WildDungeonsClient(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
    }

}
