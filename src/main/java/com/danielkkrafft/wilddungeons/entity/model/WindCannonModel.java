package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.item.WindCannon;
import net.minecraft.resources.ResourceLocation;

public class WindCannonModel extends ClientModel<WindCannon>
{
    public WindCannonModel()
    {
        super(
                WildDungeons.rl("animations/wind_cannon.animation.json"),
                WildDungeons.rl("geo/wind_cannon.geo.json"),
                WildDungeons.rl("textures/item/wind_cannon.png")
        );
    }
}