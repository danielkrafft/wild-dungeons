package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public abstract class BaseProjectileData extends BaseItemData {

    public Predicate<ItemStack> ammoType;
    public int projectileRange;
    public DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>> projectileClass;
    public String projectileName;
    public String ammoDisplayName;
}