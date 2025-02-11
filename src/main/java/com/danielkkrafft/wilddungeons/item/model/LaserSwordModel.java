package com.danielkkrafft.wilddungeons.item.model;

import com.danielkkrafft.wilddungeons.item.LaserSword;
import com.danielkkrafft.wilddungeons.item.renderer.LaserSwordRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LaserSwordModel extends GeoModel<LaserSword>
{
    @Override
    public ResourceLocation getModelResource(LaserSword object) {return LaserSwordRenderer.LASERSWORD_MODEL;}
    @Override
    public ResourceLocation getTextureResource(LaserSword object) {return LaserSwordRenderer.LASERSWORD_TEXTURE;}
    @Override
    public ResourceLocation getAnimationResource(LaserSword animatable) {return LaserSwordRenderer.LASERSWORD_ANIM;}
}