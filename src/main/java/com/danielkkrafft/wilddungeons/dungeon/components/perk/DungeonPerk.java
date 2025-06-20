package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry.DUNGEON_PERK_REGISTRY;

public class DungeonPerk {

    public int count = 0;
    public String sessionKey;
    public String templateKey;

    public DungeonPerk(String sessionKey, String templateKey) {this.sessionKey = sessionKey; this.templateKey = templateKey;}
    public DungeonSession getSession() {return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);}
    public DungeonPerkTemplate getTemplate() {return DUNGEON_PERK_REGISTRY.get(templateKey);}
    public Holder<MobEffect> getEffectHolder() {return this.getTemplate().getEffect();}
    public int getAmplifier() {return this.getTemplate().getAmplifier();}

    public void onCollect(boolean silent) {
        List<WDPlayer> players = getSession().getPlayers();
        if (!silent) players.forEach(player -> {
            player.getServerPlayer().sendSystemMessage(Component.translatable("dungeon.perk." + this.getTemplate().name() + ".desc"), true);
        });
        if (getEffectHolder() != null) {
            players.forEach(this::applyEffect);
        }
    }

    public void onDungeonEnter(WDPlayer wdPlayer) {
        if (getEffectHolder() != null) {
            applyEffect(wdPlayer);
        }
    }
    public void onPlayerRespawn(WDPlayer wdPlayer) {
        if (getEffectHolder() != null) {
            applyEffect(wdPlayer);
        }
    }
    public void onDungeonLeave(WDPlayer wdPlayer) {
        if (getEffectHolder() != null) {
            wdPlayer.getServerPlayer().removeEffect(getEffectHolder());
        }
    }

    private void applyEffect(WDPlayer player) {
        player.getServerPlayer().removeEffect(getEffectHolder());
        int amplifier = getAmplifier() >= 0 ? getAmplifier() : this.count-1;
        player.getServerPlayer().addEffect(new MobEffectInstance(getEffectHolder(), -1, amplifier));
    }
}
