package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.PrimalCreeper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoRenderer;

public class PrimalCreeperModel extends ClientModel<PrimalCreeper>
{
    public PrimalCreeperModel()
    {
        super(
                WildDungeons.rl("animations/entity/primal_creeper.animation.json"),
                WildDungeons.rl("geo/entity/primal_creeper.geo.json"),
                WildDungeons.rl("textures/entity/primal_creeper.png")
        );
    }

    @Override
    public ResourceLocation getTextureResource(PrimalCreeper creeper, @Nullable GeoRenderer<PrimalCreeper> renderer) {
        return WildDungeons.rl("textures/entity/primal_creeper.png");

    }
}
