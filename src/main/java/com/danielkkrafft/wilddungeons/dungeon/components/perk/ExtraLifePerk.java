package com.danielkkrafft.wilddungeons.dungeon.components.perk;

public class ExtraLifePerk extends DungeonPerk {
    public ExtraLifePerk(String sessionKey) {
        super(sessionKey);
    }

    @Override
    public void onCollect(boolean silent) {
        super.onCollect(silent);
        getSession().offsetLives(1);
    }
}
