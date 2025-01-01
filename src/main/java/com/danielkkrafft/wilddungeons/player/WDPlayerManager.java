package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.awt.event.InputEvent;
import java.util.*;

public class WDPlayerManager {
    private static final WDPlayerManager INSTANCE = new WDPlayerManager();
    private Map<String, WDPlayer> players = new HashMap<>();

    private WDPlayerManager(){}

    public WDPlayer getOrCreateWDPlayer(String playerUUID) {
        return players.computeIfAbsent(playerUUID, k -> new WDPlayer(playerUUID));
    }

    public WDPlayer getOrCreateWDPlayer(ServerPlayer serverPlayer) {
        return players.computeIfAbsent(serverPlayer.getStringUUID(), k -> new WDPlayer(serverPlayer.getStringUUID()));
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

            if (room.getDestructionRule() == DungeonRoom.DestructionRule.DEFAULT) {
                for (BoundingBox box : room.boundingBoxes) {
                    if (box.isInside(pos)) {
                        flag = true;
                        break;
                    }
                }
            }

            if (room.getDestructionRule() == DungeonRoom.DestructionRule.SHELL) {
                for (BoundingBox box : room.boundingBoxes) {
                    if (box.isInside(pos)) {
                        if (box.inflatedBy(-1).isInside(pos)) {
                            flag = true;
                            break;
                        }
                        for (BoundingBox otherBox : room.boundingBoxes) {
                            if (otherBox == box) continue;
                            boolean xConnected = otherBox.inflatedBy(1, 0, 0).isInside(pos);
                            boolean yConnected = otherBox.inflatedBy(0, 1, 0).isInside(pos);
                            boolean zConnected = otherBox.inflatedBy(0, 0, 1).isInside(pos);

                            //Only one axis is connected, indicating it's adjacent to another box, but not a corner
                            if ((xConnected ? 1 : 0) + (yConnected ? 1: 0) + (zConnected ? 1 : 0) == 1) {
                                if (box.inflatedBy(xConnected ? 0 : -1, yConnected ? 0 : -1, zConnected ? 0 : -1).isInside(pos)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }

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
