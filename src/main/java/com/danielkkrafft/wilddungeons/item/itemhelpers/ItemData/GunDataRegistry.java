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
                    Items.EMERALD,
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
                    true,
                    true,
                    false,
                    false
            ),
            new GunWeaponData(
                    "star_cannon",
                    1,
                    2000,
                    7200,
                    Items.NETHER_STAR,
                    50,
                    UseAnim.NONE,
                    WDEntities.BLACK_HOLE,
                    Rarity.EPIC,
                    "star_cannon",
                    "star_cannon",
                    "star_cannon",
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SHULKER_SHOOT),
                    true,
                    20,
                    2.0f,
                    0.0f,
                    3f,
                    "Black Hole",
                    false,//todo idle anim
                    true,
                    false,//todo reload anim
                    true
            )
    );

    public static GunWeaponData find(String name) {
        return GUNS.stream().filter(g -> g.name.equals(name)).findFirst().orElseThrow();
    }
}
