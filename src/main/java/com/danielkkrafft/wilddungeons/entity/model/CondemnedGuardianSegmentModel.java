package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardianSegment;
import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeSegment;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoRenderer;

public class CondemnedGuardianSegmentModel extends ClientModel<CondemnedGuardianSegment>
{
    public CondemnedGuardianSegmentModel()
    {
        super(
                WildDungeons.rl("animations/entity/condemned_guardian_segment.animation.json"),
                WildDungeons.rl("geo/entity/condemned_guardian_segment_1.geo.json"),
                WildDungeons.rl("textures/entity/condemned_guardian.png")
        );
    }

    @Override
    public ResourceLocation getModelResource(CondemnedGuardianSegment animatable, @Nullable GeoRenderer<CondemnedGuardianSegment> renderer) {
        if (animatable.getIndex() % 3 == 0) return WildDungeons.rl("geo/entity/condemned_guardian_segment_2.geo.json");
        return super.getModelResource(animatable, renderer);
    }
}
