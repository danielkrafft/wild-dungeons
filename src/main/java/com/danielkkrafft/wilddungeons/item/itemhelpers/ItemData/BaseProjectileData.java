package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class BaseProjectileData extends BaseItemData {

    public Item ammoType;
    public int projectileRange;
    public DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>> projectileClass;
    public String projectileName;
}