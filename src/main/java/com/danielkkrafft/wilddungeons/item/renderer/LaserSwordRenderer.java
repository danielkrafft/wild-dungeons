package com.danielkkrafft.wilddungeons.item.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.item.LaserSword;
import com.danielkkrafft.wilddungeons.item.model.LaserSwordModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LaserSwordRenderer extends GeoItemRenderer<LaserSword>
{
    public static final ResourceLocation
            LASERSWORD_ANIM = WildDungeons.rl("animations/laser_sword.animation.json"),
            LASERSWORD_MODEL = WildDungeons.rl("geo/laser_sword.geo.json"),
            LASERSWORD_TEXTURE = WildDungeons.rl("textures/item/laser_sword.png");
    public static final String
            idleAnim="animation.laser_sword.idle",
            transformAnim="animation.laser_sword.gun_transform",
            chargeAnim="animation.laser_sword.charging_up",
            fullChargeAnim="animation.laser_sword.fully_charged",
            shootAnim="animation.laser_sword.shoot",
            transformBackAnim="animation.laser_sword.sword_transform";
    public LaserSwordRenderer()
    {
        super(new LaserSwordModel());
    }
}
