package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class PermanentHealthBoostPerk extends DungeonPerk {
    public PermanentHealthBoostPerk(String sessionKey) {
        super(sessionKey);
    }

    @Override
    public void onCollect(boolean silent) {
        super.onCollect(silent);
        List<WDPlayer> players = getSession().getPlayers();
        players.forEach(wdPlayer -> wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, -1)));
    }

    @Override
    public void onDungeonEnter(WDPlayer wdPlayer) {
        super.onDungeonEnter(wdPlayer);
        wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, -1));
    }

    @Override
    public void onPlayerRespawn(WDPlayer wdPlayer) {
        super.onPlayerRespawn(wdPlayer);
        wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, -1));
    }
}
