package com.danielkkrafft.wilddungeons.entity.model.windhammer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindHammer;

public class WindHammerDensity2Model extends ClientModel<WindHammer> {
    public WindHammerDensity2Model() {
        super(
                null,
                WildDungeons.rl("geo/wind_hammer/hammer.density.2.geo.json"),
                WildDungeons.rl("textures/item/wind_hammer/wind_hammer_density_2.png")
        );
    }
}
