package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class PermanentStrengthPerk extends DungeonPerk{
    public PermanentStrengthPerk(String sessionKey) {
        super(sessionKey);
    }

    @Override
    public void onCollect(boolean silent) {
        super.onCollect(silent);
        List<WDPlayer> players = getSession().getPlayers();
        players.forEach(wdPlayer -> {
            wdPlayer.getServerPlayer().removeEffect(MobEffects.DAMAGE_BOOST);
            wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, this.count-1));
        });
    }

    @Override
    public void onDungeonEnter(WDPlayer wdPlayer) {
        super.onDungeonEnter(wdPlayer);
        wdPlayer.getServerPlayer().removeEffect(MobEffects.DAMAGE_BOOST);
        wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, this.count-1));
    }

    @Override
    public void onPlayerRespawn(WDPlayer wdPlayer) {
        super.onPlayerRespawn(wdPlayer);
        wdPlayer.getServerPlayer().removeEffect(MobEffects.DAMAGE_BOOST);
        wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, this.count-1));
    }
}
