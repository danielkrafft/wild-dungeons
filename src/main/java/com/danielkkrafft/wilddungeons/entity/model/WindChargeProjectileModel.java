package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;

public class WindChargeProjectileModel extends ClientModel<WindChargeProjectile>
{
    public WindChargeProjectileModel()
    {
        super(null,
                WildDungeons.rl("geo/entity/wind_charge.geo.json"),
                WildDungeons.rl("textures/entity/wind_charge.png"));
    }
}
