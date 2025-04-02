package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import net.minecraft.world.entity.ai.attributes.Attributes;

public class StepHeightPerk extends DungeonPerk {
    public StepHeightPerk(String sessionKey) {
        super(sessionKey);
    }

    @Override
    public void onCollect(boolean silent) {
        super.onCollect(silent);
        getSession().getPlayers().forEach(player -> {
            player.getServerPlayer().getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6 + 0.5 * this.count);
        });
    }
}
