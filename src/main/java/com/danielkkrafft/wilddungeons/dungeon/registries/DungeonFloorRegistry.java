package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.core.BlockPos;

import java.util.Arrays;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonBranchRegistry.*;

public class DungeonFloorRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonFloorTemplate> DUNGEON_FLOOR_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final DungeonFloorTemplate TEST_FLOOR = DungeonFloorTemplate.create("test_floor")//must be lowercase or the game will crash
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(STARTER_BRANCH)
                    .add(new WeightedPool<DungeonBranchTemplate>()
                            .add(TEST_BRANCH, 1), 1)
                    .addSimple(ENDING_BRANCH));

    public static final DungeonFloorTemplate OVERWORLD_BASIC_FLOOR = DungeonFloorTemplate.create("overworld_basic_floor")//must be lowercase or the game will crash
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(OVERWORLD_STARTER_BRANCH)
                    .addSimple(OVERWORLD_SPRAWL_0)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_0)
                    .addSimple(OVERWORLD_SPRAWL_1)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_1)
                    .addSimple(OVERWORLD_SPRAWL_2)
                    .addSimple(OVERWORLD_FREE_STUFF_BRANCH_2)
                    .addSimple(OVERWORLD_ENDING_BRANCH))
            .setOrigin(new BlockPos(0, 150, 0));
    public static final DungeonFloorTemplate OVERWORLD_SANDY_FLOOR = DungeonFloorTemplate.create("overworld_sandy_floor")
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(SANDY_STARTER_BRANCH)
                    .addSimple(SANDY_SPRAWL_0)
                    .addSimple(SANDY_FREE_STUFF_BRANCH_0)
                    .addSimple(SANDY_SPRAWL_0)
                    .addSimple(SANDY_FREE_STUFF_BRANCH_0)
                    .addSimple(SANDY_SPRAWL_0)
                    .addSimple(SANDY_FREE_STUFF_BRANCH_0)
                    .addSimple(SANDY_ENDING_BRANCH))
            .setOrigin(new BlockPos(0, 150, 0));
    public static final DungeonFloorTemplate OVERWORLD_RED_SANDY_FLOOR = DungeonFloorTemplate.create("overworld_red_sandy_floor")
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(RED_SANDY_STARTER_BRANCH)
                    .addSimple(RED_SANDY_SPRAWL_0)
                    .addSimple(RED_SANDY_FREE_STUFF_BRANCH_0)
                    .addSimple(RED_SANDY_SPRAWL_0)
                    .addSimple(RED_SANDY_FREE_STUFF_BRANCH_0)
                    .addSimple(RED_SANDY_SPRAWL_0)
                    .addSimple(RED_SANDY_FREE_STUFF_BRANCH_0)
                    .addSimple(RED_SANDY_ENDING_BRANCH))
            .setOrigin(new BlockPos(0, 150, 0));

    public static final DungeonFloorTemplate PIGLIN_FACTORY_FLOOR = DungeonFloorTemplate.create("piglin_factory_floor")
            .setBranchTemplates(new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(PIGLIN_FACTORY_START_BRANCH)
                    .addSimple(PIGLIN_FACTORY_CAVE_BRANCH)
                    .addSimple(PIGLIN_FACTORY_CAVE_BRANCH)
                    .addSimple(PIGLIN_FACTORY_CAVE_BRANCH)
                    .addSimple(PIGLIN_FACTORY_CAVE_END_BRANCH)
            )
            .setOrigin(new BlockPos(0, 100, 0));


    public static void setupFloors() {
        Arrays.asList(PIGLIN_FACTORY_FLOOR).forEach(DUNGEON_FLOOR_REGISTRY::add);
        Arrays.asList(TEST_FLOOR, OVERWORLD_BASIC_FLOOR, OVERWORLD_SANDY_FLOOR, OVERWORLD_RED_SANDY_FLOOR).forEach(DUNGEON_FLOOR_REGISTRY::add);
    }

}