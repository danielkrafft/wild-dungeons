package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardian;
import com.danielkkrafft.wilddungeons.entity.boss.CondemnedGuardianSegment;
import com.danielkkrafft.wilddungeons.entity.boss.SkelepedeMain;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoRenderer;

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
    @Override
    public ResourceLocation getTextureResource(CondemnedGuardian animatable, @Nullable GeoRenderer<CondemnedGuardian> renderer) {
        return animatable.isShiny() ? WildDungeons.rl("textures/entity/condemned_guardian_shiny.png") : WildDungeons.rl("textures/entity/condemned_guardian.png");
    }
}
