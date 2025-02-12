package com.danielkkrafft.wilddungeons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class DungeonKeyItem extends Item {
    public DungeonKeyItem(Properties properties) {
        super(properties);
        properties.stacksTo(1)
                .fireResistant()
                .rarity(Rarity.RARE);
    }
}
