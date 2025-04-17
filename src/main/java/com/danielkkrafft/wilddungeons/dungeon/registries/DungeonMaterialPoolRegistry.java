package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialRegistry.*;

public class DungeonMaterialPoolRegistry {
    public static final WeightedPool<DungeonMaterial> ALL_MATERIAL_POOL = new WeightedPool<DungeonMaterial>()
            .add(STONE_BRICK, 1)
            .add(PRISMARINE, 1)
            .add(END_STONE,1)
            .add(WOOD,1)
            .add(OVERWORLD_MATERIAL_0,1)
            .add(OVERWORLD_MATERIAL_1,1)
            .add(OVERWORLD_MATERIAL_2,1);
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_0 = new WeightedPool<DungeonMaterial>()
            .add(OVERWORLD_MATERIAL_0,1);
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_1 = new WeightedPool<DungeonMaterial>()
            .add(OVERWORLD_MATERIAL_1,1);
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_2 = new WeightedPool<DungeonMaterial>()
            .add(OVERWORLD_MATERIAL_2,1);
    public static final WeightedPool<DungeonMaterial> SANDY_MATERIAL_POOL = new WeightedPool<DungeonMaterial>()
            .add(SANDSTONEY, 1);
    public static final WeightedPool<DungeonMaterial> RED_SANDY_MATERIAL_POOL = new WeightedPool<DungeonMaterial>()
            .add(RED_SANDSTONEY, 1);
    public static final WeightedPool<DungeonMaterial> VILLAGE_SEWER_MATERIAL_POOL = new WeightedPool<DungeonMaterial>()
            .add(VILLAGE_SEWER_MATERIAL, 1);
    public static final WeightedPool<DungeonMaterial> VILLAGE_MATERIAL_POOL = new WeightedPool<DungeonMaterial>()
            .add(VILLAGE_MATERIAL, 1);

}
