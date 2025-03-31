package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

public class BigAbsorptionPerk extends DungeonPerk {
    public BigAbsorptionPerk(String sessionKey) {
        super(sessionKey);
    }

    @Override
    public void onCollect(boolean silent) {
        super.onCollect(silent);
        List<WDPlayer> players = getSession().getPlayers();
        players.forEach(wdPlayer -> {
            wdPlayer.getServerPlayer().getAttribute(Attributes.MAX_ABSORPTION).setBaseValue(wdPlayer.getServerPlayer().getAttributeBaseValue(Attributes.MAX_ABSORPTION) + 80.0f);
            wdPlayer.getServerPlayer().setAbsorptionAmount(80.0f);
        });
    }
}
