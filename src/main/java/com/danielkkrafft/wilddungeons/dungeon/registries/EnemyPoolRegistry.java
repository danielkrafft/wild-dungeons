package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.entity.EntityType;

import static net.minecraft.world.entity.EntityType.*;

public class EnemyPoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<EntityType<?>>> ENEMY_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static final WeightedPool<EntityType<?>> EASY_ENEMY_POOL = new WeightedPool<EntityType<?>>().setName("EASY_ENEMY_POOL");
    public static final WeightedPool<EntityType<?>> MEDIUM_ENEMY_POOL = new WeightedPool<EntityType<?>>().setName("MEDIUM_ENEMY_POOL");
    public static final WeightedPool<EntityType<?>> HARD_ENEMY_POOL = new WeightedPool<EntityType<?>>().setName("HARD_ENEMY_POOL");

    public static void setupEnemyPools(){
        EASY_ENEMY_POOL
                .add(ZOMBIE, 10)
                .add(SKELETON, 7)
                .add(SPIDER, 3)
                .add(CREEPER, 1)
                .add(PILLAGER, 1);
        ENEMY_POOL_REGISTRY.add(EASY_ENEMY_POOL);

        MEDIUM_ENEMY_POOL
                .add(ZOMBIE, 10)
                .add(SKELETON, 7)
                .add(SPIDER, 5)
                .add(BLAZE, 3)
                .add(CREEPER, 3)
                .add(CAVE_SPIDER, 1)
                .add(PILLAGER, 1)
                .add(ENDERMAN, 1)
                .add(HUSK, 1)
                .add(STRAY, 1)
                .add(VINDICATOR, 1);
        ENEMY_POOL_REGISTRY.add(MEDIUM_ENEMY_POOL);

        HARD_ENEMY_POOL
                .add(ZOMBIE, 10)
                .add(SKELETON, 7)
                .add(SPIDER, 5)
                .add(BLAZE, 5)
                .add(CREEPER, 5)
                .add(CAVE_SPIDER, 3)
                .add(PILLAGER, 1)
                .add(ENDERMAN, 3)
                .add(HUSK, 1)
                .add(STRAY, 1)
                .add(VINDICATOR, 3)
                .add(WITHER_SKELETON, 1);
        ENEMY_POOL_REGISTRY.add(HARD_ENEMY_POOL);
    }
}
