package com.danielkkrafft.wilddungeons.entity.model.windhammer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindHammer;

public class WindHammerModel extends ClientModel<WindHammer> {//todo remove this class
    public WindHammerModel() {
        super(
                null,
                WildDungeons.rl("geo/wind_hammer/hammer.geo.json"),
                WildDungeons.rl("textures/item/wind_hammer/wind_hammer.png")
        );
    }
}
