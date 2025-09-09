package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeMain;

public class SkelepedeMainModel extends ClientModel<SkelepedeMain>
{
    public SkelepedeMainModel()
    {
        super(
                WildDungeons.rl("animations/entity/skelepede.animation.json"),
                WildDungeons.rl("geo/entity/skelepede.geo.json"),
                WildDungeons.rl("textures/entity/skelepede.png")
        );
    }
}
