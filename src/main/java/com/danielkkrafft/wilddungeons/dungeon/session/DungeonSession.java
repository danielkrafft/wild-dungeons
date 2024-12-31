package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DungeonSession {
    public static final int SHUTDOWN_TIME = 300;

    public BlockPos entrance;
    private final List<WDPlayer> players = new ArrayList<>();
    public final List<DungeonFloor> floors = new ArrayList<>();
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
        wdPlayer.setCurrentDungeon(this);
        enterFloor(player, 0);
    }

    public void exitDungeon(ServerPlayer player) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());
        players.remove(wdPlayer);
        wdPlayer.setCurrentDungeon(null);
        wdPlayer.setCurrentFloor(null);
        wdPlayer.setCurrentBranch(null);
        wdPlayer.setCurrentRoom(null);
        wdPlayer.rootRespawn(player.getServer());
    }

    public DungeonFloor getFloor(ResourceKey<Level> levelKey) {
        List<DungeonFloor> matches = floors.stream().filter(dungeonFloor -> dungeonFloor.LEVEL_KEY == levelKey).toList();
        return matches.isEmpty() ? null : matches.getFirst();
    }

    public void enterFloor(ServerPlayer player, int index) {
        if (floors.size() <= index) {
            List<String> destinations = floors.size() == template.floorTemplates().size()-1 ? List.of("win") : List.of(""+(index+1));
            WildDungeons.getLogger().info("PICKED DESTINATIONS FOR NEXT FLOOR: {}", destinations);
            DungeonFloor floor = template.floorTemplates().get(floors.size()).placeInWorld(this, new BlockPos(0,0,0), index, destinations);
            floors.add(floor);
        }

        DungeonFloor floor = floors.get(index);

        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());
        shutdownTimer = SHUTDOWN_TIME;

        SavedTransform oldRespawn = SavedTransform.fromRespawn(player);
        SavedTransform oldPosition = new SavedTransform(player);
        SavedTransform newPosition = floor.spawnPoint == null ? new SavedTransform(new Vec3(0.0,0.0,0.0), 0.0, 0.0, floor.LEVEL_KEY) : new SavedTransform(new Vec3(floor.spawnPoint.getX(), floor.spawnPoint.getY(), floor.spawnPoint.getZ()), 0.0, 0.0, floor.LEVEL_KEY);
        WDPlayer.setRespawnPosition(newPosition, player);
        wdPlayer.storeRespawn(oldRespawn);
        wdPlayer.storePosition(oldPosition);
        wdPlayer.setCurrentFloor(floor);

        CommandUtil.executeTeleportCommand(player, newPosition);
    }

    public void exitFloor(ServerPlayer player) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());

        SavedTransform newPosition = wdPlayer.removeLastPosition();
        WDPlayer.setRespawnPosition(wdPlayer.removeLastRespawn(), player);

        DungeonFloor previousFloor = getFloor(newPosition.getDimension());
        wdPlayer.setCurrentFloor(previousFloor);
        if (wdPlayer.getDepth() == 0) {this.exitDungeon(player);}

        CommandUtil.executeTeleportCommand(player, newPosition);
    }

    public void tick() {
        if (players.isEmpty() && !floors.isEmpty()) {shutdownTimer -= 1;}
        if (shutdownTimer == 0) {shutdown();}
    }

    public void shutdown() {
        players.forEach(player -> player.rootRespawn(DungeonSessionManager.getInstance().server));
        floors.forEach(DungeonFloor::shutdown);
        markedForShutdown = true;
    }
}