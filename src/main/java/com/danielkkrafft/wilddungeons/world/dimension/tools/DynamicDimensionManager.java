package com.danielkkrafft.wilddungeons.world.dimension.tools;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.google.common.collect.Lists;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.RegistryAccess.ImmutableRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelData.RespawnData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.commoble.infiniverse.internal.ReflectionBuddy;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * InfiniverseAPI internal implementation
 *
 * Credit:Commoble
 */
public final class DynamicDimensionManager implements InfiniverseAPI
{
    private static final RegistrationInfo DIMENSION_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.stable());
    private DynamicDimensionManager() {}

    /**
     * singleton impl instance -- prefer calling {@link InfiniverseAPI#get}
     */
    public static final DynamicDimensionManager INSTANCE = new DynamicDimensionManager();

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<ResourceKey<Level>> VANILLA_LEVELS = Set.of(Level.OVERWORLD, Level.NETHER, Level.END);

    private Set<ResourceKey<Level>> levelsPendingUnregistration = new HashSet<>();

    /**
     * Gets a level, dynamically creating and registering one if it doesn't exist.<br>
     * The dimension registry is stored in the server's level file, all previously registered dimensions are loaded
     * and recreated and reregistered whenever the server starts.<br>
     * This can be used for making dynamic dimensions at runtime; static dimensions should be defined in json instead.<br>
     * @param server a MinecraftServer instance (you can get this from a ServerPlayerEntity or ServerWorld)
     * @param levelKey A ResourceKey for your level
     * @param dimensionFactory A function that produces a new LevelStem (dimension) instance if necessary<br>
     * If this factory is used, it should be assumed that intended dimension has not been created or registered yet,
     * so making the factory attempt to get this dimension from the server's dimension registry will fail
     * @return Returns a ServerLevel, creating and registering a world and dimension for it if the world does not already exist
     */
    public ServerLevel getOrCreateLevel(final MinecraftServer server, final ResourceKey<Level> levelKey, final Supplier<LevelStem> dimensionFactory)
    {
        // this is marked as deprecated but it's not called from anywhere and I'm not sure how old it is,
        // it's probably left over from forge's previous dimension api
        // in any case we need to get at the server's world field, and if we didn't use this getter,
        // then we'd just end up making a private-field-getter for it ourselves anyway
        @SuppressWarnings("deprecation")
        Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
        @Nullable ServerLevel existingLevel = map.get(levelKey);

        // if the world already exists, return it
        return existingLevel == null
                ? createAndRegisterLevel(server, map, levelKey, dimensionFactory)
                : existingLevel;
    }

    /**
     * Saves a non-vanilla level and Schedules it to be unregistered and removed at the end of the current server tick.<br>
     * Unregistering the level will have the following effects:<br>
     * <ul>
     * <li>Unregistered levels will stop ticking.
     * <li>Unregistered dimensions will not be loaded on server startup unless and until they are registered again (via {@link DynamicDimensionManager#getOrCreateLevel}.
     * <li>Players still present in the given level will, when the level is removed, be ejected to their spawn points.
     * <li>Players who have respawn points in levels being unloaded will have their spawn points reset to the overworld and respawned there.
     * </ul>
     * Unregistering a level does not delete the region files or other persistant data associated with the level.<br>
     * If a level is reregistered after unregistering it, the level will retain all prior data (unless manually deleted by a server admin.)<br>
     * This has no effect on the vanilla dimensions (The Overworld, The Nether, and The End);
     * this is because vanilla will automatically reconstitute these anyway if we try to remove them,
     * so we disallow their removal to avoid strangeness.<br>
     * @param server The server to remove the dimension from
     * @param levelToRemove The resource key for the level to be unregistered
     */
    public void markDimensionForUnregistration(final MinecraftServer server, final ResourceKey<Level> levelToRemove)
    {
        if (!VANILLA_LEVELS.contains(levelToRemove))
        {
            ServerLevel level = server.getLevel(levelToRemove);
            if (level != null)
            {
                level.save(null, true, false);
                levelsPendingUnregistration.add(levelToRemove);
            }
        }
    }

    /**
     * @return An immutable copy of the dimensions that will be unregistered at the end of the current server tick.
     * (returns an empty set if called while no server is running)
     */
    public Set<ResourceKey<Level>> getLevelsPendingUnregistration()
    {
        return ImmutableSet.copyOf(levelsPendingUnregistration);
    }

    @SuppressWarnings("deprecation") // markWorldsDirty is deprecated, see below
    private static ServerLevel createAndRegisterLevel(final MinecraftServer server, final Map<ResourceKey<Level>, ServerLevel> map, final ResourceKey<Level> levelKey, Supplier<LevelStem> dimensionFactory)
    {
        // get everything we need to create the dimension and the level
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        // dimension keys have a 1:1 relationship with level keys, they have the same IDs as well
        final ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, levelKey.identifier());
        final LevelStem dimension = dimensionFactory.get();

        final Executor executor = ReflectionBuddy.MinecraftServerAccess.executor.apply(server);
        final LevelStorageAccess anvilConverter = ReflectionBuddy.MinecraftServerAccess.storageSource.apply(server);
        final WorldData worldData = server.getWorldData();
        final DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());

        // now we have everything we need to create the dimension and the level
        // this is the same order server init creates levels:
        // the dimensions are already registered when levels are created, we'll do that first
        // then instantiate level, add border listener, add to map, fire world load event

        // register the actual dimension
        Registry<LevelStem> dimensionRegistry = server.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
        if (dimensionRegistry instanceof MappedRegistry<LevelStem> writableRegistry)
        {
            writableRegistry.unfreeze(false);
            writableRegistry.register(dimensionKey, dimension, DIMENSION_REGISTRATION_INFO);
        }
        else
        {
            throw new IllegalStateException(String.format("Unable to register dimension %s -- dimension registry not writable", dimensionKey.identifier()));
        }

        // create the level instance
        final ServerLevel newLevel = new ServerLevel(
                server,
                executor,
                anvilConverter,
                derivedLevelData,
                levelKey,
                dimension,
                worldData.isDebugWorld(),
                overworld.getSeed(), // don't need to call BiomeManager#obfuscateSeed, overworld seed is already obfuscated
                List.of(), // "special spawn list"
                // phantoms, travelling traders, patrolling/sieging raiders, and cats are overworld special spawns
                // this is always empty for non-overworld dimensions (including json dimensions)
                // these spawners are ticked when the world ticks to do their spawning logic,
                // mods that need "special spawns" for their own dimensions should implement them via tick events or other systems
                false, // "tick time", true for overworld, always false for nether, end, and json dimensions
                overworld.getRandomSequences() // as of 1.21.9 non-overworld levels share the overworld's randomSequences
        );

        newLevel.getWorldBorder().setAbsoluteMaxSize(server.getAbsoluteMaxWorldSize());
        // no, we don't need to remember the worldborder listener to remove it later
        // worldborder listeners are stored in the specific level's savedata
        // so if the level unloads it'll get gc'd with everything else
        server.getPlayerList().addWorldborderListener(newLevel);

        // register level
        map.put(levelKey, newLevel);

        // update forge's world cache so the new level can be ticked
        server.markWorldsDirty();

        // fire world load event
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(newLevel));

        // update clients' dimension lists
        QuietPacketDistributors.sendToAll(server, new UpdateDimensionsPacket(Set.of(levelKey), true));

        return newLevel;
    }

    @SuppressWarnings("deprecation")
    private void unregisterScheduledDimensions(final MinecraftServer server)
    {
        if (this.levelsPendingUnregistration.isEmpty())
            return;

        // flush the buffer
        final Set<ResourceKey<Level>> keysToRemove = this.levelsPendingUnregistration;
        this.levelsPendingUnregistration = new HashSet<>();

        // we need to remove the dimension/level from three places:
        // the server's dimension/levelstem registry, the server's level registry, and
        // the overworld's border listener
        // the level registry is just a simple map and the border listener has a remove() method
        // the dimension registry has five sub-collections that need to be cleaned up
        // we should also eject players from removed worlds so they don't get stuck there

        final Registry<LevelStem> oldRegistry = server.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
        if (!(oldRegistry instanceof MappedRegistry<LevelStem> oldMappedRegistry))
        {
            LOGGER.warn("Cannot unload dimensions: dimension registry not an instance of MappedRegistry. There may be another mod causing incompatibility with Infiniverse, or Infiniverse may need to be updated for your version of forge/minecraft.");
            return;
        }
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess = ReflectionBuddy.MinecraftServerAccess.registries.apply(server);
        RegistryAccess.Frozen composite = ReflectionBuddy.LayeredRegistryAccessAccess.composite.apply(layeredRegistryAccess);
        if (!(composite instanceof ImmutableRegistryAccess immutableRegistryAccess))
        {
            LOGGER.warn("Cannot unload dimensions: composite registry not an instance of ImmutableRegistryAccess. There may be another mod causing incompatibility with Infiniverse, or Infiniverse may be updated for your version of forge/minecraft.");
            return;
        }

        final Set<ResourceKey<Level>> removedLevelKeys = new HashSet<>();
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        for (final ResourceKey<Level> levelKeyToRemove : keysToRemove)
        {
            final @Nullable ServerLevel levelToRemove = server.getLevel(levelKeyToRemove);
            if (levelToRemove == null)
                continue;

            UnregisterDimensionEvent unregisterDimensionEvent = new UnregisterDimensionEvent(levelToRemove);
            NeoForge.EVENT_BUS.post(unregisterDimensionEvent);
            if (unregisterDimensionEvent.isCanceled())
                continue;

            // null if specified level not present
            final @Nullable ServerLevel removedLevel = server.forgeGetWorldMap().remove(levelKeyToRemove);

            if (removedLevel != null) // if we removed the key from the map
            {
                // eject players from dead world
                // iterate over a copy as the world will remove players from the original list
                for (final ServerPlayer player : Lists.newArrayList(removedLevel.players()))
                {
                    // send players to their respawn point
                    @Nullable RespawnConfig respawnConfig = player.getRespawnConfig();
                    RespawnData respawnData = respawnConfig == null
                            ? server.getRespawnData()
                            : respawnConfig.respawnData();
                    ResourceKey<Level> respawnKey = respawnData.dimension();
                    BlockPos destinationPos = respawnData.pos();

                    // if we're removing their respawn world then just send them to the overworld
                    if (keysToRemove.contains(respawnKey))
                    {
                        respawnKey = Level.OVERWORLD;
                        // make sure to wipe the player's respawn point if it was set here
                        if (respawnConfig != null && respawnConfig.respawnData().dimension() == respawnKey)
                        {
                            player.setRespawnPosition(null, false);
                        }
                    }
                    if (respawnKey == null)
                    {
                        respawnKey = Level.OVERWORLD;
                    }
                    @Nullable ServerLevel destinationLevel = server.getLevel(respawnKey);
                    if (destinationLevel == null)
                    {
                        destinationLevel = overworld;
                    }

                    // "respawning" the player via the player list schedules a task in the server to
                    // run after the post-server tick
                    // that causes some minor logspam due to the player's world no longer being
                    // loaded
                    // teleporting the player via a teleport avoids this
                    player.teleportTo(destinationLevel, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), Set.of(), respawnData.pitch(), respawnData.yaw(), true);
                }
                // save the world now or it won't be saved later and data that may be wanted to
                // be kept may be lost
                removedLevel.save(null, false, removedLevel.noSave());

                // fire world unload event -- when the server stops, this would fire after
                // worlds get saved, we'll do that here too
                NeoForge.EVENT_BUS.post(new LevelEvent.Unload(removedLevel));

                // track the removed level
                removedLevelKeys.add(levelKeyToRemove);
            }
        }

        if (!removedLevelKeys.isEmpty())
        {
            // replace the old dimension registry with a new one containing the dimensions
            // that weren't removed, in the same order
            final MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, oldMappedRegistry.registryLifecycle());

            for (final var entry : oldRegistry.entrySet())
            {
                final ResourceKey<LevelStem> oldKey = entry.getKey();
                final ResourceKey<Level> oldLevelKey = ResourceKey.create(Registries.DIMENSION, oldKey.identifier());
                final LevelStem dimension = entry.getValue();
                if (oldKey != null && dimension != null && !removedLevelKeys.contains(oldLevelKey))
                {
                    newRegistry.register(oldKey, dimension, oldRegistry.registrationInfo(oldKey).orElse(DIMENSION_REGISTRATION_INFO));
                }
            }

            // then replace the old registry with the new registry
            // as of 1.20.1 the dimension registry is stored in the server's layered registryaccess
            // this has several immutable collections of sub-registryaccesses,
            // so we'll need to recreate each of them.

            // Each ServerLevel has a reference to the layered registry access's *composite* registry access
            // so we should edit the internal fields where possible (instead of reconstructing the registry accesses)

            List<RegistryAccess.Frozen> newRegistryAccessList = new ArrayList<>();
            for (RegistryLayer layer : RegistryLayer.values())
            {
                if (layer == RegistryLayer.DIMENSIONS)
                {
                    newRegistryAccessList.add(new RegistryAccess.ImmutableRegistryAccess(List.of(newRegistry)).freeze());
                }
                else
                {
                    newRegistryAccessList.add(layeredRegistryAccess.getLayer(layer));
                }
            }
            Map<ResourceKey<? extends Registry<?>>, Registry<?>> newRegistryMap = new HashMap<>();
            for (var registryAccess : newRegistryAccessList)
            {
                var registries = registryAccess.registries().toList();
                for (var registryEntry : registries)
                {
                    newRegistryMap.put(registryEntry.key(), registryEntry.value());
                }
            }
            ReflectionBuddy.LayeredRegistryAccessAccess.values.set(layeredRegistryAccess, List.copyOf(newRegistryAccessList));
            ReflectionBuddy.ImmutableRegistryAccessAccess.registries.set(immutableRegistryAccess, newRegistryMap);

            // update the server's levels so dead levels don't get ticked
            server.markWorldsDirty();

            // notify client of the removed levels
            QuietPacketDistributors.sendToAll(server, new UpdateDimensionsPacket(removedLevelKeys, false));
        }
    }

    @EventBusSubscriber(modid = WildDungeons.MODID)
    private static class ForgeEventHandler
    {
        @SubscribeEvent(priority=EventPriority.LOWEST)
        public static void onServerTick(final ServerTickEvent.Post event)
        {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null)
            {
                DynamicDimensionManager.INSTANCE.unregisterScheduledDimensions(server);
            }
        }

        @SubscribeEvent
        public static void onServerStopped(final ServerStoppedEvent event)
        {
            // clear state on server exit (important for singleplayer worlds)
            DynamicDimensionManager.INSTANCE.levelsPendingUnregistration = new HashSet<>();
        }
    }
}