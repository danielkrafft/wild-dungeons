package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity;

public class NetherDragonModel extends ClientModel<NetherDragonEntity>
{
    public NetherDragonModel()
    {
        super(
//                WildDungeons.rl("animations/entity/nether_dragon.animation.json"),
//                WildDungeons.rl("geo/entity/nether_dragon.geo.json"),
//                WildDungeons.rl("textures/entity/nether_dragon.png")
                WildDungeons.rl("animations/entity/breeze_golem.animation.json"),
                WildDungeons.rl("geo/entity/breeze_golem.geo.json"),
                WildDungeons.rl("textures/entity/breeze_golem.png")
        );
    }
}
