package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.windhammer.*;
import com.danielkkrafft.wilddungeons.item.WindHammer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;


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
        int breaching = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.BREACH));
        int density = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.DENSITY));
        int wind = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.WIND_BURST));

        return (breaching > 0 ||
                density > 0 ||
                wind > 0) ? wind > 0 ? WIND_MOD : (breaching > 0 ? (breaching >= 2 ? BRE2_MOD : BRE1_MOD) : (density >= 2 ? DEN2_MOD : DEN1_MOD)) : DEF_MOD;
    }
}