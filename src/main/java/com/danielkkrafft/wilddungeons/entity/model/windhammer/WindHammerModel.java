package com.danielkkrafft.wilddungeons.entity.model.windhammer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindHammer;

public class WindHammerModel extends ClientModel<WindHammer> {
    public WindHammerModel() {
        super(
                null,
                WildDungeons.rl("geo/item/wind_hammer/hammer.geo.json"),
                WildDungeons.rl("textures/item/hammer.png")
        );
    }
}
