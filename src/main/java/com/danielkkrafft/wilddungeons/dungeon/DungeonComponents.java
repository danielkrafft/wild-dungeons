package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.registry.WDDimensions;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import com.danielkkrafft.wilddungeons.world.dimension.tools.InfiniverseAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 *  Dungeons contain a set of DungeonFloors, which contain a set of DungeonRooms
 *  Components are layered this way to allow for modular logic
 */
public class DungeonComponents {
    public static final StructurePlaceSettings EMPTY_STRUCTURE_PLACE_SETTINGS = new StructurePlaceSettings();
    public static final BlockPos EMPTY_BLOCK_POS = new BlockPos(0, 0, 0);
    public static MinecraftServer server;

    public static final DungeonComponentRegistry<DungeonRoomTemplate> DUNGEON_ROOM_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonFloorTemplate> DUNGEON_FLOOR_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonTemplate> DUNGEON_REGISTRY = new DungeonComponentRegistry<>();

    public static final DungeonComponentPool<DungeonRoomTemplate> ROOM_POOL = new DungeonComponentPool<>();
    public static final DungeonComponentPool<DungeonFloorTemplate> FLOOR_POOL = new DungeonComponentPool<>();
    public static final DungeonComponentPool<DungeonTemplate> DUNGEON_POOL = new DungeonComponentPool<>();

    public static void setupDungeons() {

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build("stone/small_1"));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build("stone/small_2"));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build("stone/small_3"));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build("stone/medium_1"));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build("stone/medium_2"));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build("stone/large_1"));
        DUNGEON_ROOM_REGISTRY.registry.forEach((key, value) -> ROOM_POOL.add(value));

        DUNGEON_FLOOR_REGISTRY.add(DungeonFloorTemplate.build("empty_floor", ROOM_POOL, DUNGEON_ROOM_REGISTRY.get("stone/small_1"), 100));
        DUNGEON_FLOOR_REGISTRY.registry.forEach((key, value) -> FLOOR_POOL.add(value));

        DUNGEON_REGISTRY.add(DungeonTemplate.build("dungeon_1", DungeonOpenBehavior.NONE, DUNGEON_FLOOR_REGISTRY.get("empty_floor")));
        DUNGEON_REGISTRY.registry.forEach((key, value) -> DUNGEON_POOL.add(value));
    }

    public static class DungeonComponentPool<T extends DungeonComponent> {

        private final List<T> pool;
        public DungeonComponentPool() { pool = new ArrayList<>(); }

        public void add(T item) { pool.add(item); }
        public T getRandom() { return pool.get(server.overworld().getRandom().nextInt(pool.size())); }
    }

    public static class DungeonComponentRegistry<T extends DungeonComponent> {

        private final HashMap<String, T> registry;
        public DungeonComponentRegistry() { registry = new HashMap<>(); }

        public void add(T component) { registry.put(component.name(), component); }
        public T get(String key) { return registry.get(key); }
    }

    public interface DungeonComponent { String name(); }

    public record DungeonRoomTemplate(String name, StructureTemplate template, BoundingBox boundingBox, List<ConnectionPoint> connectionPoints) implements DungeonComponent {

        public static DungeonRoomTemplate build(String name) {

            StructureTemplate template = server.getStructureManager().getOrCreate(WildDungeons.rl(name));
            BoundingBox boundingBox = template.getBoundingBox(EMPTY_STRUCTURE_PLACE_SETTINGS, EMPTY_BLOCK_POS);

            List<ConnectionPoint> connectionPoints = ConnectionPoint.locateConnectionPoints(template, boundingBox);
            return new DungeonRoomTemplate(name, template, boundingBox, connectionPoints);
        }

        public DungeonRoom placeInWorld(ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> connectionPoints) {
            return new DungeonRoom(this, level, position, EMPTY_BLOCK_POS, settings, level.random, 2, connectionPoints);
        }
    }

    public record DungeonFloorTemplate(String name, DungeonComponentPool<DungeonRoomTemplate> roomPool, DungeonRoomTemplate startingRoom, int roomCount) implements DungeonComponent {

        public static DungeonFloorTemplate build(String name, DungeonComponentPool<DungeonRoomTemplate> roomPool, DungeonRoomTemplate startingRoom, int roomCount) {
            return new DungeonFloorTemplate(name, roomPool, startingRoom, roomCount);
        }

        public DungeonFloor placeInWorld(ServerLevel level, BlockPos position) {
            return new DungeonFloor(this, level, position);
        }
    }

    public record DungeonTemplate(String name, String openBehavior, DungeonFloorTemplate floorTemplate) implements DungeonComponent {

        public static DungeonTemplate build(String name, String openBehavior, DungeonFloorTemplate floorTemplate) {
            return new DungeonTemplate(name, openBehavior, floorTemplate);
        }

        public void startDungeonDimension(MinecraftServer server) {

            final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(this.name));
            ServerLevel newLevel = InfiniverseAPI.get().getOrCreateLevel(server, LEVEL_KEY, () -> WDDimensions.createLevel(server));

            this.floorTemplate.placeInWorld(newLevel, EMPTY_BLOCK_POS);
        }

        public void enterDungeon(ServerPlayer player) {

            final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(this.name));
            WDPlayer.SavedTransform newRespawn = new WDPlayer.SavedTransform(new Vec3(0.0, -30.0, 0.0), 0.0, 0.0, LEVEL_KEY);

            player.setRespawnPosition(newRespawn.getDimension(), newRespawn.getBlockPos(), (float) newRespawn.getYaw(), true, false);
            CommandUtil.executeTeleportCommand(player, newRespawn);
        }
    }
}