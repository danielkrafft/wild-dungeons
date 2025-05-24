package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;

import java.util.List;

import static net.minecraft.world.item.ProjectileWeaponItem.ARROW_ONLY;

public class BowDataRegistry {
    public static final List<BowWeaponData> BOWS = List.of(
            new BowWeaponData(
                    "wind_bow",
                    1,
                    2000,
                    7200,
                    ARROW_ONLY, // ammoType
                    18,
                    UseAnim.BOW,
                    "WindArrow",
                    Rarity.EPIC,
                    "WindBowRenderer",
                    "wind_bow",
                    "geo/wind_bow.geo.json",
                    "geo/wind_bow_nocked.geo.json",
                    "textures/item/wind_bow.png",
                    "textures/item/wind_bow_charge.png",
                    WDSoundEvents.WIND_BOW_DRAW
            )
            // Add more bows here
    );
    public static BowWeaponData find(String name) {
        return BOWS.stream()
                .filter(bow -> bow.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No bow with name: " + name));
    }
}
