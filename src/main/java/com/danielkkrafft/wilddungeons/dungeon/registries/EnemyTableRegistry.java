package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.TargetTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;

import java.util.ArrayList;

import static com.danielkkrafft.wilddungeons.dungeon.registries.EnemyPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.TargetTemplateRegistry.BREEZE_GOLEM;
import static com.danielkkrafft.wilddungeons.dungeon.registries.TargetTemplateRegistry.NETHER_DRAGON;

public class EnemyTableRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<WeightedTable<TargetTemplate>> ENEMY_TABLE_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static final ArrayList<WeightedTable<TargetTemplate>> enemyTables = new ArrayList<>();

    public static final WeightedTable<TargetTemplate> BASIC_ENEMY_TABLE = create("BASIC_ENEMY_TABLE")
            .add(EASY_ENEMY_POOL,1)
            .add(MEDIUM_ENEMY_POOL,5)
            .add(HARD_ENEMY_POOL,20)
            .add(VERY_HARD_ENEMY_POOL, 400);

    public static final WeightedTable<TargetTemplate> NETHER_DRAGON_ARENA = create("NETHER_DRAGON_ARENA")
            .add(new WeightedPool<TargetTemplate>().add(NETHER_DRAGON, 1), 1);

    public static final WeightedTable<TargetTemplate> BREEZE_GOLEM_ARENA = create("BREEZE_GOLEM_ARENA")
            .add(new WeightedPool<TargetTemplate>().add(BREEZE_GOLEM, 1), 1);

    public static WeightedTable<TargetTemplate> create(String name){
        WeightedTable<TargetTemplate> enemyTable = new WeightedTable<TargetTemplate>();
        enemyTables.add(enemyTable);
        return enemyTable;
    }
    public static void setupEnemyTables(){
        enemyTables.forEach(ENEMY_TABLE_REGISTRY::add);
    }
}
