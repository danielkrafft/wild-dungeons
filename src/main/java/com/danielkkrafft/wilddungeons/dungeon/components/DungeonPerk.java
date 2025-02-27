package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry.*;

public class DungeonPerk {

    public String name;
    public int count = 0;
    public String sessionKey;

    public DungeonPerk(String name, String sessionKey) {
        this.name = name;
        this.sessionKey = sessionKey;
    }

    public void onCollect(boolean silent) {
        List<WDPlayer> players = getSession().getPlayers();
        if (!silent)
            players.forEach(player -> player.getServerPlayer().sendSystemMessage(Component.translatable("dungeon.perk." + this.name), true));
        if (this.name.equals(EXTRA_LIFE.name())) {
            getSession().offsetLives(1);
        } else if (this.name.equals(FIRE_RESIST.name())){
            players.forEach(wdPlayer -> wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1)));
        } else if (this.name.equals(STRENGTH.name())){
            players.forEach(wdPlayer -> wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1)));
        } else if (this.name.equals(NIGHT_VISION.name())){
            players.forEach(wdPlayer -> wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1)));
        } else if (this.name.equals(HEALTH_BOOST.name())){
            players.forEach(wdPlayer -> wdPlayer.getServerPlayer().addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, -1,2)));
        }
    }

    public DungeonSession getSession() {
        return DungeonSessionManager.getInstance().getDungeonSession(this.sessionKey);
    }

    public DungeonPerkTemplate getTemplate() {return DUNGEON_PERK_REGISTRY.get(this.name);
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);

            if (wdPlayer.getCurrentDungeon() == null) return;
            if (serverPlayer.getWeaponItem().getItem() instanceof SwordItem) {

                if (wdPlayer.getCurrentDungeon().getPerks().containsKey("SWORD_DAMAGE")) {
                    DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerks().get("SWORD_DAMAGE");
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, perk.count)));
                }

            } else if (serverPlayer.getWeaponItem().getItem() instanceof AxeItem) {

                if (wdPlayer.getCurrentDungeon().getPerks().containsKey("AXE_DAMAGE")) {
                    DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerks().get("AXE_DAMAGE");
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, perk.count)));
                }

            } else if (event.getSource().is(DamageTypes.ARROW)) {

                if (wdPlayer.getCurrentDungeon().getPerks().containsKey("AXE_DAMAGE")) {
                    DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerks().get("AXE_DAMAGE");
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, perk.count)));
                }

            }

        }
    }

}
