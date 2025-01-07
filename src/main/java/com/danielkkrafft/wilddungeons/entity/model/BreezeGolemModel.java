package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import net.minecraft.resources.ResourceLocation;

public class BreezeGolemModel extends ClientModel<BreezeGolem>
{
    public BreezeGolemModel()
    {
        super(
                WildDungeons.rl("animations/entity/breeze_golem.animation.json"),
                WildDungeons.rl("geo/entity/breeze_golem.geo.json"),
                WildDungeons.rl("textures/entity/breeze_golem.png")
        );
    }
}
