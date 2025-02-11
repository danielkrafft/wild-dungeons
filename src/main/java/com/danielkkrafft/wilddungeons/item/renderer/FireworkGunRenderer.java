package com.danielkkrafft.wilddungeons.item.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.item.FireworkGun;
import com.danielkkrafft.wilddungeons.item.model.FireworkGunModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FireworkGunRenderer extends GeoItemRenderer<FireworkGun>
{
    public static final ResourceLocation
            FIREWORKGUN_ANIM = WildDungeons.rl("animations/firework_gun.animation.json"),
            FIREWORKGUN_MODEL = WildDungeons.rl("geo/firework_gun.geo.json"),
            FIREWORKGUN_TEXTURE = WildDungeons.rl("textures/item/firework_gun.png");
    public static final String
            idleAnim="animation.firework_gun.idle",
            rotateAnim="animation.firework_gun.rotate";
    public FireworkGunRenderer()
    {
        super(new FireworkGunModel());
    }
}
