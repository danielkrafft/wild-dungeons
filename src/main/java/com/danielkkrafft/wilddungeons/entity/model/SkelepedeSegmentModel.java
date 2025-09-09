package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeSegment;

public class SkelepedeSegmentModel extends ClientModel<SkelepedeSegment>
{
    public SkelepedeSegmentModel()
    {
        super(
                WildDungeons.rl("animations/entity/skelepede_segment.animation.json"),
                WildDungeons.rl("geo/entity/skelepede_segment.geo.json"),
                WildDungeons.rl("textures/entity/skelepede.png")
        );
    }
}
