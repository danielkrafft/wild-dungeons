package com.danielkkrafft.wilddungeons.entity.model.windhammer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindHammer;

public class WindHammerDensityModel extends ClientModel<WindHammer> {
    public WindHammerDensityModel() {
        super(
                null,
                WildDungeons.rl("geo/wind_hammer/hammer.density.geo.json"),
                WildDungeons.rl("textures/item/wind_hammer/wind_hammer_density.png")
        );
    }
}
