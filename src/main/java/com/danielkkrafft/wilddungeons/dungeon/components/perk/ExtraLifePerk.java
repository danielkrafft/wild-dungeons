package com.danielkkrafft.wilddungeons.dungeon.components.perk;

public class ExtraLifePerk extends DungeonPerk {
    public ExtraLifePerk(String sessionKey, String templateKey) {
        super(sessionKey, templateKey);
    }

    @Override
    public void onCollect(boolean silent) {
        super.onCollect(silent);
        getSession().offsetLives(1);
    }
}
