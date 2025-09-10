package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EggSacArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EggSacArrowRenderer extends ArrowRenderer<EggSacArrow> {
    public static final ResourceLocation RESOURCE_LOCATION = WildDungeons.rl("textures/entity/egg_sac_arrow.png");

    public EggSacArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EggSacArrow eggSacArrow) {
        return RESOURCE_LOCATION;
    }
}