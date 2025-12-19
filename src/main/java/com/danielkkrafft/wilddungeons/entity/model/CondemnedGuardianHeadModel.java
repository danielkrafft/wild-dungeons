package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardian;
import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeMain;

public class CondemnedGuardianHeadModel extends ClientModel<CondemnedGuardian>
{
    public CondemnedGuardianHeadModel()
    {
        super(
                WildDungeons.rl("animations/entity/condemned_guardian_head.animation.json"),
                WildDungeons.rl("geo/entity/condemned_guardian_head.geo.json"),
                WildDungeons.rl("textures/entity/condemned_guardian.png")
        );
    }
}
