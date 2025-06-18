package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.GunWeaponData;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDRangedWeapon;
/**
 * The Emerald Staff allows players to summon an Emerald Wisp when used.
 * <p>
 * This class is part of the new system for ranged weapons, which is easier to make new entries with and allows for @override if you need to get granular.
 *
* @see EmeraldStaffDep The Old Way
* */
public class EmeraldStaff extends WDRangedWeapon {//this uses the new system for ranged weapons, which is easier to make new entries with and allows for @override if you need to get granular. @see meraldStaffDep
    public EmeraldStaff(GunWeaponData newGunWeaponData) {
        super(newGunWeaponData);
    }
}
