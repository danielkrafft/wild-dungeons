package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.TargetTemplateRegistry.*;

public class EnemyPoolRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedPool<DungeonRegistration.TargetTemplate>> ENEMY_POOL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedPool<DungeonRegistration.TargetTemplate> EASY_ENEMY_POOL = create("EASY_ENEMY_POOL")
            .add(ZOMBIE_NORMAL, 5)
            .add(ZOMBIE_LEATHER, 4)
            .add(SKELETON_NORMAL, 3)
            .add(SKELETON_CHAIN, 2)
            .add(SKELETON_GOLD, 2)
            .add(SPIDER, 3)
            .add(ENDERMITE, 1)
            .add(SILVERFISH, 1)
            .add(SLIME, 1)
            .add(VEX, 1)
            .add(CREEPER, 2);
    public static final WeightedPool<DungeonRegistration.TargetTemplate> MEDIUM_ENEMY_POOL = create("MEDIUM_ENEMY_POOL")
            .add(BLAZE, 1)
            .add(MEDIUM_SLIME, 1)
            .add(ZOMBIE_IRON, 1)
            .add(BREEZE, 1)
            .add(BOGGED, 1)
            .add(MAGMA_CUBE, 1)
            .add(CAVE_SPIDER, 1)
            .add(PILLAGER, 1)
            .add(ENDERMAN, 1)
            .add(HUSK, 1)
            .add(BIG_SLIME, 1)
            .add(HUGE_SLIME, 1)
            .add(STRAY, 1)
            .add(PIGLIN, 1);
    public static final WeightedPool<DungeonRegistration.TargetTemplate> HARD_ENEMY_POOL = create("HARD_ENEMY_POOL")
            .add(VINDICATOR, 1)
            .add(HUMONGOUS_SLIME, 1)
            .add(INVISIBLE_WITCH, 1)
            .add(FAST_CAVE_SPIDER, 1)
            .add(ZOMBIE_DIAMOND, 1)
            .add(WITHER_SKELETON, 1)
            .add(PIGLIN_BRUTE, 1)
            .add(PIGLIN_BRUTE_GOLD, 1)
            .add(WITCH, 1)
            .add(HOGLIN, 1)
            .add(RAVAGER, 1)
            .add(BEEFY_BLAZE, 1)
            .add(EVOKER, 1)
            .add(GHAST, 1)
            .add(FAST_CREEPER, 1);
    public static final WeightedPool<DungeonRegistration.TargetTemplate> VERY_HARD_ENEMY_POOL = create("VERY_HARD_ENEMY_POOL")
            .add(WITHER, 1)
            .add(WARDEN, 1)
            .add(MUTANT_BOGGED, 1)
            .add(BREEZE_GOLEM, 1);



    public static WeightedPool<DungeonRegistration.TargetTemplate> create(String name) {
        WeightedPool<DungeonRegistration.TargetTemplate> pool = new WeightedPool<DungeonRegistration.TargetTemplate>().setName(name);
        ENEMY_POOL_REGISTRY.add(pool);
        return pool;
    }
}
