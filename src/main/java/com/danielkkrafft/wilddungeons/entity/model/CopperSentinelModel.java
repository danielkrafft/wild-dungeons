package com.danielkkrafft.wilddungeons.entity.model;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.CopperSentinel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoRenderer;

public class CopperSentinelModel extends ClientModel<CopperSentinel>
{
    public CopperSentinelModel()
    {
        super(
                WildDungeons.rl("animations/entity/copper_sentinel.animation.json"),
                WildDungeons.rl("geo/entity/copper_sentinel.geo.json"),
                WildDungeons.rl("textures/entity/copper_sentinel.png")
        );
    }

    @Override
    public ResourceLocation getTextureResource(CopperSentinel sentinel, @Nullable GeoRenderer<CopperSentinel> renderer) {
        float hpPercent = sentinel.getHealth() / sentinel.getMaxHealth();
        if (hpPercent < 0.25f) {
            return WildDungeons.rl("textures/entity/copper_sentinel_oxidized_3.png");
        }
        else if (hpPercent < 0.5f) {
            return WildDungeons.rl("textures/entity/copper_sentinel_oxidized_2.png");
        }
        else if (hpPercent < 0.75f) {
            return WildDungeons.rl("textures/entity/copper_sentinel_oxidized_1.png");
        }
        return super.getTextureResource(sentinel, renderer);
    }
}
