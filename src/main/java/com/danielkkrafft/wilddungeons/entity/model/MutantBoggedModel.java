package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;

public class MutantBoggedModel extends ClientModel<MutantBogged>
{
    public MutantBoggedModel()
    {
        super(WildDungeons.rl("animations/entity/mutant_bogged.animation.json"),
                WildDungeons.rl("geo/entity/mutant_bogged.geo.json"),
                WildDungeons.rl("textures/entity/mutant_bogged.png"));
    }

}