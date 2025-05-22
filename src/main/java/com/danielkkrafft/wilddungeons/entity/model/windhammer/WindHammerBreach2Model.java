package com.danielkkrafft.wilddungeons.entity.model.windhammer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindHammer;

public class WindHammerBreach2Model extends ClientModel<WindHammer> {
    public WindHammerBreach2Model() {
        super(
                null,
                WildDungeons.rl("geo/wind_hammer/hammer.breaching.geo.json"),
                WildDungeons.rl("textures/item/hammer_breaching.png")
        );
    }
}
