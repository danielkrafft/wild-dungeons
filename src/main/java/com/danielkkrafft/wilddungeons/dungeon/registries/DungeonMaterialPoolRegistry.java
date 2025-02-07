package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialRegistry.*;

public class DungeonMaterialPoolRegistry {
    public static final WeightedPool<DungeonMaterial> ALL_MATERIAL_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_0 = new WeightedPool<>();
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_1 = new WeightedPool<>();
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_2 = new WeightedPool<>();
    public static final WeightedPool<DungeonMaterial> SANDY_MATERIAL_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonMaterial> RED_SANDY_MATERIAL_POOL = new WeightedPool<>();
    public static void setupMaterialPools(){
        ALL_MATERIAL_POOL
                .add(STONE_BRICK, 1)
                .add(SANDSTONEY, 1)
                .add(RED_SANDSTONEY, 1)
                .add(PRISMARINE, 1)
                .add(END_STONE,1)
                .add(OAK_WOOD,1)
                .add(OVERWORLD_MATERIAL_0,1)
                .add(OVERWORLD_MATERIAL_1,1)
                .add(OVERWORLD_MATERIAL_2,1);
        OVERWORLD_MATERIAL_POOL_0
                .add(OVERWORLD_MATERIAL_0,1);
        OVERWORLD_MATERIAL_POOL_1
                .add(OVERWORLD_MATERIAL_1,1);
        OVERWORLD_MATERIAL_POOL_2
                .add(OVERWORLD_MATERIAL_2,1);
        SANDY_MATERIAL_POOL
                .add(SANDSTONEY, 1);
        RED_SANDY_MATERIAL_POOL
                .add(RED_SANDSTONEY, 1);
    }
}
