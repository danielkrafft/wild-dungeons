package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.item.WindBow;
import net.minecraft.resources.ResourceLocation;

public class WindBowModel extends ClientModel<WindBow>
{
    public static final ResourceLocation MOD_IDLE=  WildDungeons.rl("geo/wind_bow.geo.json"),
            MOD_NOCKED = WildDungeons.rl("geo/wind_bow_nocked.geo.json"),
            STILL = WildDungeons.rl("textures/item/wind_bow.png"),
            CHARGE = WildDungeons.rl("textures/item/wind_bow_charge.png");

    public WindBowModel()
    {
        super(WildDungeons.rl("animations/wind_bow.animation.json"),
                MOD_IDLE,
                STILL);
    }
}