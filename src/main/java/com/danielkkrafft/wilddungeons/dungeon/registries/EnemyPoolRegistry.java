package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.TargetTemplateRegistry.*;

public class EnemyPoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<DungeonRegistration.TargetTemplate>> ENEMY_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static final WeightedPool<DungeonRegistration.TargetTemplate> EASY_ENEMY_POOL = new WeightedPool<DungeonRegistration.TargetTemplate>().setName("EASY_ENEMY_POOL");
    public static final WeightedPool<DungeonRegistration.TargetTemplate> MEDIUM_ENEMY_POOL = new WeightedPool<DungeonRegistration.TargetTemplate>().setName("MEDIUM_ENEMY_POOL");
    public static final WeightedPool<DungeonRegistration.TargetTemplate> HARD_ENEMY_POOL = new WeightedPool<DungeonRegistration.TargetTemplate>().setName("HARD_ENEMY_POOL");

    public static void setupEnemyPools(){
        EASY_ENEMY_POOL
                .add(ZOMBIE_NORMAL, 10)
                .add(ZOMBIE_LEATHER, 7)
                .add(SKELETON_NORMAL, 7)
                .add(SKELETON_CHAIN, 4)
                .add(SPIDER, 5)
                .add(CREEPER, 2);
        ENEMY_POOL_REGISTRY.add(EASY_ENEMY_POOL);

        MEDIUM_ENEMY_POOL
                .add(BLAZE, 3)
                .add(CAVE_SPIDER, 3)
                .add(PILLAGER, 1)
                .add(ENDERMAN, 1);
        ENEMY_POOL_REGISTRY.add(MEDIUM_ENEMY_POOL);

        HARD_ENEMY_POOL
                .add(HUSK, 1)
                .add(STRAY, 1)
                .add(VINDICATOR, 3)
                .add(WITHER_SKELETON, 1);
        ENEMY_POOL_REGISTRY.add(HARD_ENEMY_POOL);
    }
}
