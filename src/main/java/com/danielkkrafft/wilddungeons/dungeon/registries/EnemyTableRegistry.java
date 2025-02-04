package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.world.entity.EntityType;

import static com.danielkkrafft.wilddungeons.dungeon.registries.EnemyPoolRegistry.*;

public class EnemyTableRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedTable<EntityType<?>>> ENEMY_TABLE_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final WeightedTable<EntityType<?>> BASIC_ENEMY_TABLE = new WeightedTable<EntityType<?>>().setName("BASIC_ENEMY_TABLE");

    public static void setupEnemyTables(){
        BASIC_ENEMY_TABLE
                .add(EASY_ENEMY_POOL,1)
                .add(MEDIUM_ENEMY_POOL,5)
                .add(HARD_ENEMY_POOL,10);
        ENEMY_TABLE_REGISTRY.add(BASIC_ENEMY_TABLE);
    }
}
