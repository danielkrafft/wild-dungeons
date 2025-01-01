package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;

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

    public DungeonSession getOrCreateDungeonSession(BlockPos entrance, DungeonComponents.DungeonTemplate template) {
        return sessions.computeIfAbsent(buildDungeonSessionKey(entrance), k -> new DungeonSession(entrance, template));
    }

    public static String buildDungeonSessionKey(BlockPos entrance) {
        return WildDungeons.rl("wild_" + entrance.getX() + entrance.getY() + entrance.getZ()).toString();
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
