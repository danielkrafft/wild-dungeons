package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.windhammer.*;
import com.danielkkrafft.wilddungeons.item.WindHammer;
import net.minecraft.world.item.enchantment.Enchantments;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;


public class WindHammerRenderer extends GeoItemRenderer<WindHammer>//todo this should probably not be six models, but rather one model with different 1-frame animation states
{
    private final WindHammerModel DEF_MOD=new WindHammerModel();//todo delete all these classes eventually
    private final WindHammerBreachModel BRE1_MOD=new WindHammerBreachModel();
    private final WindHammerBreach2Model BRE2_MOD=new WindHammerBreach2Model();
    private final WindHammerDensityModel DEN1_MOD=new WindHammerDensityModel();
    private final WindHammerDensity2Model DEN2_MOD=new WindHammerDensity2Model();
    private final WindHammerWindChargeModel WIND_MOD=new WindHammerWindChargeModel();
    public WindHammerRenderer()
    {
        super(new WindHammerModel());
    }


    @Override
    public GeoModel<WindHammer> getGeoModel()
    {
        int breaching = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.BREACH));
        int density = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.DENSITY));
        int wind = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.WIND_BURST));

        return (breaching > 0 ||
                density > 0 ||
                wind > 0) ? wind > 0 ? WIND_MOD : (breaching > 0 ? (breaching >= 2 ? BRE2_MOD : BRE1_MOD) : (density >= 2 ? DEN2_MOD : DEN1_MOD)) : DEF_MOD;
    }
}