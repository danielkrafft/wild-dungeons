package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttackSpeedPerk extends DungeonPerk {
    public AttackSpeedPerk(String sessionKey) {
        super(sessionKey);
    }

    @Override
    public void onCollect(boolean silent) {
        super.onCollect(silent);
        getSession().getPlayers().forEach(player -> {
            player.getServerPlayer().getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4.0 * Math.pow(1.1, this.count));
        });
    }
}
