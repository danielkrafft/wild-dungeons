package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.network.chat.Component;

import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry.*;

public class DungeonPerk {

    public int count = 0;
    public String sessionKey;

    public DungeonPerk(String sessionKey) {this.sessionKey = sessionKey;}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonPerkTemplate getTemplate() {return DUNGEON_PERK_REGISTRY.get(this.getClass().getSimpleName());}

    public void onCollect(boolean silent) {
        List<WDPlayer> players = getSession().getPlayers();
        if (!silent) players.forEach(player -> player.getServerPlayer().sendSystemMessage(Component.translatable("dungeon.perk." + this.getTemplate().name()), true));
    }

    public void onDungeonEnter(WDPlayer wdPlayer) {}
    public void onPlayerRespawn(WDPlayer wdPlayer) {}
}
