package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.Spiderling;

public class TemplateModel extends ClientModel<Spiderling>
{
    public TemplateModel()
    {
        super(
                WildDungeons.rl("animations/entity/blackstone_spiderling.animation.json"),
                WildDungeons.rl("geo/entity/blackstone_spiderling.geo.json"),
                WildDungeons.rl("textures/entity/spiderling.png")
        );
    }
}
