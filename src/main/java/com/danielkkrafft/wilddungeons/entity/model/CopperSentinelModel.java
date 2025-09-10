package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.CopperSentinel;

public class CopperSentinelModel extends ClientModel<CopperSentinel>
{
    public CopperSentinelModel()
    {
        super(
                WildDungeons.rl("animations/entity/copper_sentinel.animation.json"),
                WildDungeons.rl("geo/entity/copper_sentinel.geo.json"),
                WildDungeons.rl("textures/entity/copper_sentinel.png")
        );
    }
}
