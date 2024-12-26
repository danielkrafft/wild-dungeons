package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonSession {
    public static final int SHUTDOWN_TIME = 300;

    public BlockPos entrance;
    private List<WDPlayer> players = new ArrayList<>();
    private HashMap<String, DungeonFloor> floors = new HashMap<>();
    public MinecraftServer server;
    public DungeonComponents.DungeonTemplate template;
    public boolean active = false;
    public int shutdownTimer = SHUTDOWN_TIME;
    public boolean markedForShutdown = false;

    private DungeonSession(BlockPos entrance, DungeonComponents.DungeonTemplate template, MinecraftServer server) {
        this.server = server;
        this.entrance = entrance;
        this.template = template;
        //floors.put(""+1, template.floorTemplates().getFirst().placeInWorld(this, new BlockPos(0,0,0), 1, List.of("2")));
        this.active = true;
    }

    public void enterDungeon(ServerPlayer player) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());
        players.add(wdPlayer);
        wdPlayer.setCurrentDungeon(DungeonSessionManager.buildDungeonSessionKey(this.entrance));
        enterFloor(player, "1");
    }

    public void exitDungeon(ServerPlayer player) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());
        players.remove(wdPlayer);
        wdPlayer.setCurrentDungeon("none");
        wdPlayer.setCurrentFloor("none");
        wdPlayer.rootRespawn(player.getServer());
    }

    public DungeonFloor getFloor(WDPlayer wdPlayer) {
        WildDungeons.getLogger().info("TRYING TO GET FLOOR: {}", wdPlayer.getCurrentFloor());
        WildDungeons.getLogger().info("OUT OF EXISTING FLOORS: {}", floors.keySet());
        return floors.get(wdPlayer.getCurrentFloor());
    }

    public DungeonFloor getFloor(String destination) {
        return floors.get(destination);
    }

    public DungeonFloor getFloor(ResourceKey<Level> levelKey) {
        List<DungeonFloor> matches = floors.values().stream().filter(dungeonFloor -> dungeonFloor.LEVEL_KEY == levelKey).toList();
        return matches.isEmpty() ? null : matches.getFirst();
    }

    public void enterFloor(ServerPlayer player, String destination) {
        if (floors.get(destination) == null) {
            List<String> destinations = floors.size() == template.floorTemplates().size()-1 ? List.of("win") : List.of(""+floors.size()+2);
            floors.put(destination, template.floorTemplates().get(floors.size()).placeInWorld(this, new BlockPos(0,0,0), floors.size()+1, destinations));
        }

        DungeonFloor floor = floors.get(destination);

        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());
        shutdownTimer = SHUTDOWN_TIME;

        WDPlayer.SavedTransform oldRespawn = WDPlayer.SavedTransform.fromRespawn(player);
        WDPlayer.SavedTransform oldPosition = new WDPlayer.SavedTransform(player);
        WDPlayer.SavedTransform newPosition = new WDPlayer.SavedTransform(new Vec3(floor.spawnPoint.getX(), floor.spawnPoint.getY(), floor.spawnPoint.getZ()), 0.0, 0.0, floor.LEVEL_KEY);
        WDPlayer.setRespawnPosition(newPosition, player);
        wdPlayer.storeRespawn(oldRespawn);
        wdPlayer.storePosition(oldPosition);
        wdPlayer.setCurrentFloor(""+floor.id);

        CommandUtil.executeTeleportCommand(player, newPosition);
    }

    public void exitFloor(ServerPlayer player) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());

        WDPlayer.SavedTransform newPosition = wdPlayer.removeLastPosition();
        WDPlayer.setRespawnPosition(wdPlayer.removeLastRespawn(), player);

        DungeonFloor previousFloor = getFloor(newPosition.getDimension());
        wdPlayer.setCurrentFloor(previousFloor == null ? "none" : ""+previousFloor.id);
        if (wdPlayer.getDepth() == 0) {this.exitDungeon(player);}

        CommandUtil.executeTeleportCommand(player, newPosition);
    }

    public void tick() {
        if (players.isEmpty() && active) {
            shutdownTimer -= 1;
        }

        if (shutdownTimer == 0) {
            shutdown();
        }
    }

    public void shutdown() {
        players.forEach(player -> player.rootRespawn(server));
        floors.forEach((key, floor) -> floor.shutdown());
        markedForShutdown = true;
    }

    public static class DungeonSessionManager {
        private static final DungeonSessionManager INSTANCE = new DungeonSessionManager();
        private Map<String, DungeonSession> sessions = new HashMap<>();

        private DungeonSessionManager(){}

        public DungeonSession getDungeonSession(BlockPos entrance) {
            return sessions.getOrDefault(buildDungeonSessionKey(entrance), null);
        }

        public DungeonSession getDungeonSession(String key) {
            return sessions.getOrDefault(key, null);
        }

        public DungeonSession getOrCreateDungeonSession(BlockPos entrance, DungeonComponents.DungeonTemplate template, MinecraftServer server) {
            return sessions.computeIfAbsent(buildDungeonSessionKey(entrance), k -> new DungeonSession(entrance, template, server));
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
    }
}
