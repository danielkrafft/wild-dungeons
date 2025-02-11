package com.danielkkrafft.wilddungeons.item.model;

import com.danielkkrafft.wilddungeons.item.FireworkGun;
import com.danielkkrafft.wilddungeons.item.renderer.FireworkGunRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireworkGunModel extends GeoModel<FireworkGun>
{
    @Override
    public ResourceLocation getModelResource(FireworkGun object) {return FireworkGunRenderer.FIREWORKGUN_MODEL;}
    @Override
    public ResourceLocation getTextureResource(FireworkGun object) {return FireworkGunRenderer.FIREWORKGUN_TEXTURE;}
    @Override
    public ResourceLocation getAnimationResource(FireworkGun animatable) {return FireworkGunRenderer.FIREWORKGUN_ANIM;}
}