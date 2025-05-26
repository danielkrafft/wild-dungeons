package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;

import java.util.List;

public class GunDataRegistry {

    private static Object WDSoundEvents;
    public static final List<GunWeaponData> GUNS = List.of(
            new GunWeaponData(
                    "emerald_staff",
                    1,
                    2000,
                    7200,
                    itemStack -> itemStack.is(Items.EMERALD),
                    50,
                    UseAnim.NONE,
                    WDEntities.FRIENDLY_EMERALD_WISP,
                    Rarity.EPIC,
                    "emerald_staff",
                    "emerald_staff",
                    "emerald_staff",
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ILLUSIONER_CAST_SPELL),
                    true,
                    20,
                    2.0f,
                    0.0f,
                    1.5f,
                    "Summoned Wisp",
                    true
            ),
            new GunWeaponData(
                    "star_cannon",
                    1,
                    2000,
                    7200,
                    itemStack -> itemStack.is(Items.EMERALD),
                    50,
                    UseAnim.NONE,
                    WDEntities.SMALL_EMERALD_WISP,
                    Rarity.EPIC,
                    "star_cannon",
                    "star_cannon",
                    "star_cannon",
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BLAZE_SHOOT),
                    true,
                    1,
                    2.0f,
                    0.0f,
                    1.5f,
                    "Black Hole",
                    false
            )
    );

    public static GunWeaponData find(String name) {
        return GUNS.stream().filter(g -> g.name.equals(name)).findFirst().orElseThrow();
    }
}
