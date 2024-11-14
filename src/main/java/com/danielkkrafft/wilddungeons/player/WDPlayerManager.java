package com.danielkkrafft.wilddungeons.player;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class WDPlayerManager {
    private static final WDPlayerManager INSTANCE = new WDPlayerManager();
    private Map<String, WDPlayer> players = new HashMap<>();

    private WDPlayerManager(){}

    public WDPlayer getOrCreateWDPlayer(String playerUUID) {
        return players.computeIfAbsent(playerUUID, k -> new WDPlayer());
    }

    public void replaceWDPlayer(String playerUUID, WDPlayer wdPlayer) {
        players.put(playerUUID, wdPlayer);
    }

    public Map<String, WDPlayer> getPlayers() {return this.players;}
    public void setPlayers(Map<String, WDPlayer> map) {this.players = map;}
    public static WDPlayerManager getInstance() {return INSTANCE;}
}
