package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindCannon;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WindCannonRenderer extends GeoItemRenderer<WindCannon>
{
    public WindCannonRenderer()
    {
        super(new ClientModel<>(WildDungeons.rl("animations/wind_cannon.animation.json"),
                WildDungeons.rl("geo/wind_cannon.geo.json"),
                WildDungeons.rl("textures/item/wind_cannon.png")));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        int comps=WindCannon.getCompressions(stack);
        if(comps>=10)
        {
            poseStack.translate(UtilityMethods.RNG(-0.01,0.01),UtilityMethods.RNG(-0.01,0.01),UtilityMethods.RNG(-0.01,0.01));
        }
        super.renderByItem(stack,transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
