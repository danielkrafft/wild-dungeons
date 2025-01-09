package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonSessionManager {

    private static final DungeonSessionManager INSTANCE = new DungeonSessionManager();
    private Map<String, DungeonSession> sessions = new HashMap<>();
    public MinecraftServer server;

    private DungeonSessionManager(){}

    public DungeonSession getDungeonSession(String key) {
        return sessions.getOrDefault(key, null);
    }

    public DungeonSession getOrCreateDungeonSession(BlockPos entrance, ServerLevel entranceLevel, DungeonComponents.DungeonTemplate template) {
        return sessions.computeIfAbsent(buildDungeonSessionKey(entrance), k -> new DungeonSession(entrance, entranceLevel, template));
    }

    public static String buildDungeonSessionKey(BlockPos entrance) {
        return "wd" + entrance.getX() + entrance.getY() + entrance.getZ();
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
        String sessionKey = levelKey.location().getPath().split("_")[0];
        WildDungeons.getLogger().info("TRYING TO GET SESSION KEY: {}", sessionKey);
        return this.getDungeonSession(sessionKey);
    }

    public static void tick() {
        List<String> sessionsToRemove = new ArrayList<>();
        INSTANCE.sessions.forEach((key, session) -> {
            session.tick();
            if (session.markedForShutdown) {
                sessionsToRemove.add(key);
            }
        });
        sessionsToRemove.forEach(s -> {
            INSTANCE.sessions.remove(s);
        });
    }

}
