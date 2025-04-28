package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonBranchRegistry.*;

public class DungeonFloorRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonFloorTemplate> DUNGEON_FLOOR_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<DungeonFloorTemplate> dungeonFloors = new ArrayList<>();

    public static final DungeonFloorTemplate TEST_FLOOR = create("test_floor")//must be lowercase or the game will crash
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(STARTER_BRANCH)
                    .add(new WeightedPool<DungeonBranchTemplate>()
                            .add(TEST_BRANCH, 1), 1)
                    .addSimple(ENDING_BRANCH));

    public static final DungeonFloorTemplate MEGA_DUNGEON_FLOOR = create("mega_dungeon_floor")//must be lowercase or the game will crash
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(OVERWORLD_STARTER_BRANCH)
                    .addSimple(OVERWORLD_SPRAWL_0)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_0)
                    .addSimple(OVERWORLD_SPRAWL_1)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_1)
                    .addSimple(OVERWORLD_SPRAWL_2)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_2)
                    .addSimple(OVERWORLD_TRIAL_BRANCH)
                    .addSimple(OVERWORLD_TRIAL_EXIT_BRANCH))
            .set(HierarchicalProperty.SOUNDSCAPE, SoundscapeTemplateRegistry.MEGA_DUNGEON)
            .set(HierarchicalProperty.INTENSITY, 1)
            .setOrigin(new BlockPos(0, 150, 0));

    public static final DungeonFloorTemplate MEGA_DUNGEON_GAUNTLET_FLOOR = create("mega_dungeon_gauntlet_floor")//must be lowercase or the game will crash
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(OVERWORLD_STARTER_BRANCH)
                    .addSimple(OVERWORLD_SPRAWL_0)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_0)
                    .addSimple(OVERWORLD_SPRAWL_0)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_0)
                    .addSimple(OVERWORLD_SPRAWL_1)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_1)
                    .addSimple(OVERWORLD_SPRAWL_1)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_1)
                    .addSimple(OVERWORLD_SPRAWL_2)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_2)
                    .addSimple(OVERWORLD_SPRAWL_2)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_2)
                    .addSimple(OVERWORLD_TRIAL_BRANCH)
                    .addSimple(OVERWORLD_TRIAL_EXIT_BRANCH))
            .set(HierarchicalProperty.SOUNDSCAPE, SoundscapeTemplateRegistry.MEGA_DUNGEON)
            .set(HierarchicalProperty.INTENSITY, 1)
            .set(HierarchicalProperty.DIFFICULTY_MODIFIER, 2.0)
            .setOrigin(new BlockPos(0, 150, 0));


    public static final DungeonFloorTemplate PIGLIN_FACTORY_FLOOR = create("piglin_factory_floor")
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(PIGLIN_FACTORY_START_BRANCH)
                    .addSimple(PIGLIN_FACTORY_CAVE_BRANCH)
                    .addSimple(PIGLIN_FACTORY_CAVE_END_BRANCH)
                    .addSimple(PIGLIN_FACTORY_PIPEWORKS_FREE_PERK_BRANCH)
                    .addSimple(PIGLIN_FACTORY_PIPEWORKS_FREE_CHEST_BRANCH)
                    .addSimple(PIGLIN_FACTORY_PIPEWORKS_BRANCH)
                    .addSimple(PIGLIN_FACTORY_BRANCH)
                    .addSimple(PIGLIN_FACTORY_SIDE_BRANCH)
                    .addSimple(PIGLIN_FACTORY_SIDE_FREE_PERK)
                    .addSimple(PIGLIN_FACTORY_SIDE_BRANCH)
                    .addSimple(PIGLIN_FACTORY_SIDE_FREE_PERK)
                    .addSimple(PIGLIN_FACTORY_SIDE_BRANCH)
                    .addSimple(PIGLIN_FACTORY_SIDE_FREE_PERK)
                    .addSimple(NETHER_DRAGON_BOSS_BRANCH)
            )
            .set(HierarchicalProperty.MATERIAL, new WeightedPool<DungeonMaterial>().add(DungeonMaterialRegistry.PIGLIN_FACTORY_MATERIAL, 1))
            .set(HierarchicalProperty.SOUNDSCAPE, SoundscapeTemplateRegistry.NETHER_CAVES)
            .setOrigin(new BlockPos(0, 0, 0));

    public static final DungeonFloorTemplate VILLAGE_FLOOR = create("village")
            .setBranchTemplates(
                    new DungeonLayout<DungeonBranchTemplate>()
                            .addSimple(VILLAGE_SEWER_START_BRANCH)
                            .addSimple(VILLAGE_SEWER_ALL)
                            .addSimple(VILLAGE_SEWER_ALL)
                            .addSimple(VILLAGE_SEWER_ENDING_BRANCH)
                            .addSimple(VILLAGE_METRO_START_BRANCH)
                            .addSimple(VILLAGE_METRO_ENDING_BRANCH)//todo this is first so that we don't block it with other branches, but that can cause the difficulty to be off
                            .addSimple(VILLAGE_METRO_STREETS_BRANCH)
                            .addSimple(VILLAGE_METRO_STREETS_2_BRANCH)
                            .addSimple(VILLAGE_METRO_STREETS_BRANCH)
                            .addSimple(VILLAGE_METRO_STREETS_2_BRANCH)
                            .addSimple(VILLAGE_METRO_STREETS_BRANCH)
                            .addSimple(VILLAGE_METRO_STREETS_2_BRANCH)
            ).setOrigin(new BlockPos(0,-50,0));

    public static DungeonFloorTemplate copyOf(DungeonFloorTemplate floor, String name) {
        DungeonFloorTemplate copy = DungeonFloorTemplate.copyOf(floor, name);
        dungeonFloors.add(copy);
        return copy;
    }

    public static DungeonFloorTemplate create(String name) {
        DungeonFloorTemplate floor = DungeonFloorTemplate.create(name);
        dungeonFloors.add(floor);
        return floor;
    }

    public static void setupFloors() {
        dungeonFloors.forEach(DUNGEON_FLOOR_REGISTRY::add);
    }

}