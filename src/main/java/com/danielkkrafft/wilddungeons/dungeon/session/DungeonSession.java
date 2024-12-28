package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.TemplateHelper;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DungeonSession {
    public static final int SHUTDOWN_TIME = 300;

    public BlockPos entrance;
    private final List<WDPlayer> players = new ArrayList<>();
    private final HashMap<String, DungeonFloor> floors = new HashMap<>();
    public DungeonComponents.DungeonTemplate template;
    public int shutdownTimer = SHUTDOWN_TIME;
    public boolean markedForShutdown = false;
    public List<DungeonMaterial> materials;

    protected DungeonSession(BlockPos entrance, DungeonComponents.DungeonTemplate template) {
        this.entrance = entrance;
        this.template = template;
        this.materials = template.materials();
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
        WDPlayer.SavedTransform newPosition = floor.spawnPoint == null ? new WDPlayer.SavedTransform(new Vec3(0.0,0.0,0.0), 0.0, 0.0, floor.LEVEL_KEY) : new WDPlayer.SavedTransform(new Vec3(floor.spawnPoint.getX(), floor.spawnPoint.getY(), floor.spawnPoint.getZ()), 0.0, 0.0, floor.LEVEL_KEY);
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
        if (players.isEmpty() && !floors.isEmpty()) {shutdownTimer -= 1;}
        if (shutdownTimer == 0) {shutdown();}
    }

    public void shutdown() {
        players.forEach(player -> player.rootRespawn(DungeonSessionManager.getInstance().server));
        floors.forEach((key, floor) -> floor.shutdown());
        markedForShutdown = true;
    }
}