package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

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
    public int getAmplifier() {
        int templateAmplifier = this.getTemplate().getAmplifier();
        if (templateAmplifier >= 0) return templateAmplifier;
        // If the template does not specify an amplifier, use the count as the amplifier
        if (this.count <= 0) return 0; // Prevent negative amplifier
        return this.count - 1; // Count starts at 1, so we subtract 1 to get the correct amplifier
    }

    public void onCollect(boolean silent) {
        List<WDPlayer> players = getSession().getPlayers();
        players.forEach(player -> {
            if (!silent)
                player.getServerPlayer().sendSystemMessage(Component.translatable("dungeon.perk." + this.getTemplate().name() + ".desc"), true);
            this.applyEffect(player);
        });
    }

    public void onDungeonEnter(WDPlayer wdPlayer) {
            applyEffect(wdPlayer);
    }
    public void onPlayerRespawn(WDPlayer wdPlayer) {
        if (this.getTemplate().getEffect()!=null && !this.getTemplate().getEffect().is(MobEffects.ABSORPTION.getKey()))//this is a hack to prevent the absorption effect from being reapplied on respawn and should be replaced with something better
            applyEffect(wdPlayer);
    }
    public void onDungeonLeave(WDPlayer wdPlayer) {
        if (getEffectHolder() != null) {
            wdPlayer.getServerPlayer().removeEffect(getEffectHolder());
        }
    }

    private void applyEffect(WDPlayer player) {
        if (getEffectHolder() == null){
            WildDungeons.getLogger().error("Tried to apply perk {} to player {}, but it has no effect defined.", getTemplate().name(), player.getServerPlayer().getName().getString());
            return;
        }
        player.getServerPlayer().addEffect(new MobEffectInstance(getEffectHolder(), -1, getAmplifier()));
        WildDungeons.getLogger().info("Applied perk {} to player {} at amplifier {}", getTemplate().name(), player.getServerPlayer().getName().getString(), getAmplifier());
    }
}
