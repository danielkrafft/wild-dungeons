package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.dungeon.components.room.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.*;

public class WDPlayerManager {
    private static final WDPlayerManager INSTANCE = new WDPlayerManager();
    private Map<String, WDPlayer> players = new HashMap<>();

    private WDPlayerManager(){}

    public WDPlayer getOrCreateWDPlayer(Player player) {
        return players.computeIfAbsent(player.getStringUUID(), k -> new WDPlayer(player.getStringUUID()));
    }

    public void replaceWDPlayer(String playerUUID, WDPlayer wdPlayer) {
        players.put(playerUUID, wdPlayer);
    }

    public Map<String, WDPlayer> getPlayers() {return this.players;}
    public void setPlayers(Map<String, WDPlayer> map) {this.players = map;}
    public static WDPlayerManager getInstance() {return INSTANCE;}

    public List<String> getPlayerNames(MinecraftServer server) {
        List<String> result = new ArrayList<>();
        players.forEach((k,v) -> {
            result.add(server.getPlayerList().getPlayer(UUID.fromString(k)).getName().toString());
        });
        return result;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = INSTANCE.getOrCreateWDPlayer(serverPlayer);

            event.setCanceled(!canPlayerModifyBlock(wdPlayer, event.getPos()));
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = INSTANCE.getOrCreateWDPlayer(serverPlayer);

            event.setCanceled(!canPlayerModifyBlock(wdPlayer, event.getPos()));
        }
    }

    public static boolean canPlayerModifyBlock(WDPlayer wdPlayer, BlockPos pos) {
        if (wdPlayer.getCurrentRoom() != null) {
            DungeonRoom room = wdPlayer.getCurrentRoom();
            boolean flag = false;

            if (room.alwaysBreakable.contains(pos)) return true;

            if (room.getDestructionRule() == DungeonRoom.DestructionRule.DEFAULT) {
                for (BoundingBox box : room.boundingBoxes) {
                    if (box.isInside(pos)) {
                        flag = true;
                        break;
                    }
                }
            }

            if (room.getDestructionRule() == DungeonRoom.DestructionRule.SHELL) {
                if (room.isPosInsideShell(pos)) {
                    return true;
                }
            }

            if (room.getDestructionRule() == DungeonRoom.DestructionRule.NONE) {
                return false;
            }

            return flag;
        } else {
            return true;
        }
    }
}
