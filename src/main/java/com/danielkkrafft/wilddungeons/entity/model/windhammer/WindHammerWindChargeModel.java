package com.danielkkrafft.wilddungeons.entity.model.windhammer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindHammer;

public class WindHammerWindChargeModel extends ClientModel<WindHammer> {
    public WindHammerWindChargeModel() {
        super(
                null,
                WildDungeons.rl("geo/wind_hammer/hammer.windcharge.geo.json"),
                WildDungeons.rl("textures/item/hammer_wind_charge.png")
        );
    }
}
