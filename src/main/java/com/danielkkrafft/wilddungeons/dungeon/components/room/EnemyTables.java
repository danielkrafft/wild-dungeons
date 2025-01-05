package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.world.entity.EntityType;

public class EnemyTables {

    public static final WeightedPool<EntityType<?>> EASY_BASIC_POOL = new WeightedPool<EntityType<?>>()
            .add(EntityType.ZOMBIE, 1).add(EntityType.SKELETON, 1);

    public static final WeightedPool<EntityType<?>> MEDIUM_BASIC_POOL = new WeightedPool<EntityType<?>>()
            .add(EntityType.CREEPER, 1).add(EntityType.CAVE_SPIDER, 1);

    public static final WeightedPool<EntityType<?>> HARD_BASIC_POOL = new WeightedPool<EntityType<?>>()
            .add(EntityType.ENDERMAN, 1).add(EntityType.BLAZE, 1);

    public static final WeightedTable<EntityType<?>> BASIC_TABLE = new WeightedTable<EntityType<?>>()
            .add(EASY_BASIC_POOL, 1).add(MEDIUM_BASIC_POOL, 5).add(HARD_BASIC_POOL, 10);


}
