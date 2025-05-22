package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.WindArrow;

public class WindArrowModel extends ClientModel<WindArrow>
{
    public WindArrowModel()
    {
        super(null,
                WildDungeons.rl("geo/entity/wind_arrow.geo.json"),
                WildDungeons.rl("textures/entity/wind_arrow.png"));
    }
}
