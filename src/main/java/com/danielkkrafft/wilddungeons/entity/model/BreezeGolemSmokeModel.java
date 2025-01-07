package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;

public class BreezeGolemSmokeModel<T extends BreezeGolem> extends ClientModel<T>
{
    public BreezeGolemSmokeModel()
    {
        super(WildDungeons.rl("animation/entity/breeze_golem.animation.json"),
                WildDungeons.rl("geo/entity/breeze_golem_smoke.geo.json"),
                WildDungeons.rl("textures/entity/breeze_golem.png"));
    }
}
