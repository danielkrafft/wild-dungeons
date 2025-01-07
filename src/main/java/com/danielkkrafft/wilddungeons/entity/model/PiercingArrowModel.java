package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.PiercingArrow;

public class PiercingArrowModel extends ClientModel<PiercingArrow>
{
    public PiercingArrowModel()
    {
        super(null,
                WildDungeons.rl("geo/entity/piercing_arrow.geo.json"),
                WildDungeons.rl("textures/entity/piercing_arrow.png"));
    }
}