package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;

public abstract class BaseItemData {

    public String name = "";
    public int stacksTo = 0;
    public int durability = 0;
    public int useDuration = 0;
    public UseAnim useAnim = UseAnim.NONE;
    public Rarity rarity = Rarity.COMMON;
    public String animations;
    public String baseModel = "";
    public String baseTexture = "";
    public boolean hasIdle = false;
}
