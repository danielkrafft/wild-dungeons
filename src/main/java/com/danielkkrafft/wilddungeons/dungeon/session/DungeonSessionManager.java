package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.util.SaveSystem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// for server use only
public class DungeonSessionManager {

    private static final DungeonSessionManager INSTANCE = new DungeonSessionManager();

    private Map<String, DungeonSession> sessions = new HashMap<>();
    public MinecraftServer server;

    private DungeonSessionManager(){}

    public static void ValidateSessions() {
//        WildDungeons.getLogger().info("Validating Sessions");
        INSTANCE.sessions.forEach((key, session) -> {
            try {
                if (session.generationFutures == null) session.generationFutures = new ArrayList<>();
                session.generationFutures.add(CompletableFuture.runAsync(session::validate));
            } catch (Exception e) {
                WildDungeons.getLogger().error("Error validating session: {}", key);
                e.printStackTrace();
            }
        });
    }

    public DungeonSession getDungeonSession(String key) {
        return sessions.getOrDefault(key, null);
    }

    public DungeonSession getOrCreateDungeonSession(String entranceUUID, ResourceKey<Level> entranceLevelKey, String template) {
        return sessions.computeIfAbsent(buildDungeonSessionKey(entranceUUID), k -> new DungeonSession(entranceUUID, entranceLevelKey, template));
    }

    public static String buildDungeonSessionKey(String entranceUUID) {
        return "wd" + entranceUUID;
    }

    public Map<String, DungeonSession> getSessions() {return this.sessions;}
    public void setSessions(Map<String, DungeonSession> map) {this.sessions = map;}
    public static DungeonSessionManager getInstance() {return INSTANCE;}

    public List<String> getSessionNames() {
        List<String> result = new ArrayList<>();
        sessions.forEach((k,v) -> {
            result.add(k);
        });
        return result;
    }

    public DungeonSession getFromKey(ResourceKey<Level> levelKey) {
        String sessionKey = levelKey.location().getPath().split("___")[0];
        WildDungeons.getLogger().info("TRYING TO GET SESSION KEY: {}", sessionKey);
        return this.getDungeonSession(sessionKey);
    }

    public DungeonFloor getFloorFromKey(ResourceKey<Level> levelKey) {
        DungeonSession session = this.getFromKey(levelKey);
        return session.getFloors().get(Integer.parseInt(levelKey.location().getPath().split("___")[2]));
    }

    public static void tick() {
        if (SaveSystem.isLoading() || !SaveSystem.isLoaded()) return;//prevent crash if world boots up with players in the dungeon
        List<String> sessionsToRemove = new ArrayList<>();
        INSTANCE.sessions.forEach((key, session) -> {
            session.tick();
            if (session.isMarkedForShutdown()) {
                sessionsToRemove.add(key);
            }
        });
        sessionsToRemove.forEach(s -> {
            INSTANCE.sessions.remove(s);
        });
    }

    public static void onShutdown(){
        INSTANCE.sessions.forEach((key, session) -> {
            session.cancelGenerations();
        });
    }
}
