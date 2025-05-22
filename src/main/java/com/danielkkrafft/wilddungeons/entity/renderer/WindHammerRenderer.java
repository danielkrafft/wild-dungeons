package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.enchantment.WDEnchantmentEffects;
import com.danielkkrafft.wilddungeons.enchantment.WDEnchantments;
import com.danielkkrafft.wilddungeons.entity.model.windhammer.*;
import com.danielkkrafft.wilddungeons.item.WindHammer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Map;

public class WindHammerRenderer extends GeoItemRenderer<WindHammer>
{
    private final WindHammerModel DEF_MOD=new WindHammerModel();
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

        int breaching = currentItemStack.getEnchantmentLevel(getEnchantment(Enchantments.BREACH));
        int density = currentItemStack.getEnchantmentLevel(getEnchantment(Enchantments.DENSITY));
        int wind = currentItemStack.getEnchantmentLevel(getEnchantment(Enchantments.WIND_BURST));

        return (breaching > 0 ||
                density > 0 ||
                wind > 0) ? wind > 0 ? WIND_MOD : (breaching > 0 ? (breaching >= 2 ? BRE2_MOD : BRE1_MOD) : (density >= 2 ? DEN2_MOD : DEN1_MOD)) : DEF_MOD;
    }

    public static Holder<Enchantment> getEnchantment(ResourceKey<Enchantment> key) {
        return Minecraft.getInstance()
                .level
                .registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(key)
                .orElseThrow(); // Or use a fallback default holder if needed
    }
}