package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import static com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry.DUNGEON_PERK_REGISTRY;

public class DungeonPerk {

    public String name;
    public int count = 0;
    public String sessionKey;

    public DungeonPerk(String name, String sessionKey) {
        this.name = name;
        this.sessionKey = sessionKey;
    }

    public void onCollect() {
        if (this.name.equals("EXTRA_LIFE")) {getSession().offsetLives(1);}
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
