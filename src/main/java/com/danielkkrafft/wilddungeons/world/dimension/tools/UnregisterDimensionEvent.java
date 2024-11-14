package com.danielkkrafft.wilddungeons.world.dimension.tools;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fires when a dimension/level is about to be unregistered by Infiniverse.<br>
 * This event fires on {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS} and is not cancellable.<br>
 *
 * Credit:Commoble
 */
public class UnregisterDimensionEvent extends Event implements ICancellableEvent
{
    private final ServerLevel level;

    public UnregisterDimensionEvent(ServerLevel level)
    {
        this.level = level;
    }

    /**
     * @return The level that is about to be unregistered by Infiniverse.
     */
    public ServerLevel getLevel()
    {
        return this.level;
    }
}