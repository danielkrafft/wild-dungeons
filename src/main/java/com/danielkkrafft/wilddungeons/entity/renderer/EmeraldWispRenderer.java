package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EmeraldWisp;
import com.danielkkrafft.wilddungeons.entity.model.EmeraldWispModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EmeraldWispRenderer extends MobRenderer<EmeraldWisp, EmeraldWispModel<EmeraldWisp>> {
    private static final ResourceLocation EMERALD_WISP = WildDungeons.rl("textures/entity/village/emerald_wisp.png");
    private static final ResourceLocation LARGE_EMERALD_WISP = WildDungeons.rl("textures/entity/village/large_emerald_wisp.png");

    public EmeraldWispRenderer(EntityRendererProvider.Context context, boolean isLarge) {
        super(context, new EmeraldWispModel<>(context.bakeLayer(isLarge ? EmeraldWispModel.LARGE_LAYER_LOCATION : EmeraldWispModel.SMALL_LAYER_LOCATION)), 0.25f);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull EmeraldWisp entity) {
        return entity.isLarge() ? LARGE_EMERALD_WISP : EMERALD_WISP;
    }
}