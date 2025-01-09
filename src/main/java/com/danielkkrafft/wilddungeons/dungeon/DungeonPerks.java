package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.joml.Vector2i;

import java.util.HashMap;

public class DungeonPerks {

    public enum Perks {
        SWORD_DAMAGE_INCREASE(0, new Vector2i(0, 0)),
        AXE_DAMAGE_INCREASE(1, new Vector2i(1, 0)),
        BOW_DAMAGE_INCREASE(2, new Vector2i(2, 0)),
        EXTRA_LIFE(3, new Vector2i(3, 0));

        private final int index;
        private final Vector2i texCoords;
        Perks(int index, Vector2i texCoords) {this.index = index; this.texCoords = texCoords; ID_MAP.put(index, this);}
        public int getIndex() {return index;}
        public Vector2i getTexCoords() {return texCoords;}
    }

    private static final HashMap<Integer, Perks> ID_MAP = new HashMap<>();
    public static Perks getById(int id) {
        return ID_MAP.get(id);
    }

    public static void onPerk(Perks perk, DungeonSession session) {
        switch (perk) {
            case EXTRA_LIFE -> session.offsetLives(1);
        }
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);

            if (wdPlayer.getCurrentDungeon() == null) return;
            if (serverPlayer.getWeaponItem().getItem() instanceof SwordItem) {

                if (wdPlayer.getCurrentDungeon().perks.containsKey(DungeonPerks.Perks.SWORD_DAMAGE_INCREASE)) {
                    DungeonPerk perk = wdPlayer.getCurrentDungeon().perks.get(DungeonPerks.Perks.SWORD_DAMAGE_INCREASE);
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, perk.count)));
                }

            } else if (serverPlayer.getWeaponItem().getItem() instanceof AxeItem) {

                if (wdPlayer.getCurrentDungeon().perks.containsKey(DungeonPerks.Perks.AXE_DAMAGE_INCREASE)) {
                    DungeonPerk perk = wdPlayer.getCurrentDungeon().perks.get(DungeonPerks.Perks.AXE_DAMAGE_INCREASE);
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, perk.count)));
                }

            } else if (event.getSource().is(DamageTypes.ARROW)) {

                if (wdPlayer.getCurrentDungeon().perks.containsKey(DungeonPerks.Perks.BOW_DAMAGE_INCREASE)) {
                    DungeonPerk perk = wdPlayer.getCurrentDungeon().perks.get(DungeonPerks.Perks.BOW_DAMAGE_INCREASE);
                    event.setNewDamage((float) (event.getOriginalDamage() * Math.pow(1.1, perk.count)));
                }

            }

        }
    }

}
