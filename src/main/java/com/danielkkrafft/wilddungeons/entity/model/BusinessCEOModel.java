package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.BusinessCEO;

public class BusinessCEOModel extends ClientModel<BusinessCEO>
{
    public BusinessCEOModel()
    {
        super(
                WildDungeons.rl("animations/entity/villager_ceo.animation.json"),//todo these don't exist yet
                WildDungeons.rl("geo/entity/village/villager_ceo.geo.json"),
                WildDungeons.rl("textures/entity/village/villager_ceo.png")
        );
    }
}
