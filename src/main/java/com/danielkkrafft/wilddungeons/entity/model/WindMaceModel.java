package com.danielkkrafft.wilddungeons.entity.model;


import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.item.WindMace;

public class WindMaceModel extends ClientModel<WindMace>
{
    public WindMaceModel()
    {
        super(
                WildDungeons.rl("animations/wind_mace.animation.json"),
                WildDungeons.rl("geo/wind_mace.geo.json"),
                WildDungeons.rl("textures/item/wind_mace.png")
        );
    }
}