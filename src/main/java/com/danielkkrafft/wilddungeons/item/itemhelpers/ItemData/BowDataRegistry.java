package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
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
                    UseAnim.NONE,
                    WDEntities.WIND_ARROW,
                    Rarity.EPIC,
                    //"WindBowRenderer",
                    "wind_bow",
                    "wind_bow",
                    "wind_bow_nocked",
                    "wind_bow",
                    "wind_bow_charge",
                    WDSoundEvents.WIND_BOW_DRAW,
                    false,
                    "Arrow"
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
