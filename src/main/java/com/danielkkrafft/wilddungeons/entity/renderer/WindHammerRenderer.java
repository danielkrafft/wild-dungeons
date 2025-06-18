package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WindHammer;
import net.minecraft.world.item.enchantment.Enchantments;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;


public class WindHammerRenderer extends GeoItemRenderer<WindHammer> {
    private final ClientModel<WindHammer> DEF_MOD = new ClientModel<>(null, WildDungeons.rl("geo/wind_hammer.geo.json"), WildDungeons.rl("textures/item/wind_hammer.png"));
    private final ClientModel<WindHammer> BRE1_MOD = new ClientModel<>(null, WildDungeons.rl("geo/wind_hammer.breaching.geo.json"), WildDungeons.rl("textures/item/wind_hammer_breaching.png"));
    private final ClientModel<WindHammer> BRE2_MOD =  new ClientModel<>(null, WildDungeons.rl("geo/wind_hammer.breaching.geo.json"), WildDungeons.rl("textures/item/wind_hammer_breaching.png"));
    private final ClientModel<WindHammer> DEN1_MOD = new ClientModel<>(null, WildDungeons.rl("geo/wind_hammer.density.geo.json"), WildDungeons.rl("textures/item/wind_hammer_density.png"));
    private final ClientModel<WindHammer> DEN2_MOD = new ClientModel<>(null, WildDungeons.rl("geo/wind_hammer.density.2.geo.json"), WildDungeons.rl("textures/item/wind_hammer_density_2.png"));
    private final ClientModel<WindHammer> WIND_MOD = new ClientModel<>(null, WildDungeons.rl("geo/wind_hammer.windcharge.geo.json"), WildDungeons.rl("textures/item/wind_hammer_wind_charge.png"));

    public WindHammerRenderer() {
        super( new ClientModel<>(null, WildDungeons.rl("geo/wind_hammer.geo.json"), WildDungeons.rl("textures/item/wind_hammer.png")));
    }


    @Override
    public GeoModel<WindHammer> getGeoModel() {
        int breaching = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.BREACH));
        int density = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.DENSITY));
        int wind = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.WIND_BURST));

        return (breaching > 0 ||
                density > 0 ||
                wind > 0) ? wind > 0 ? WIND_MOD : (breaching > 0 ? (breaching >= 2 ? BRE2_MOD : BRE1_MOD) : (density >= 2 ? DEN2_MOD : DEN1_MOD)) : DEF_MOD;
    }
}