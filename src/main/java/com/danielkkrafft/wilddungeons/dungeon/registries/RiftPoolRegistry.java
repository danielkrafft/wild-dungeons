package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateRegistry.*;

public class RiftPoolRegistry {

    public static WeightedPool<DungeonRegistration.OfferingTemplate> OVERWORLD_RIFT_POOL = new WeightedPool<>();
    public static WeightedPool<DungeonRegistration.OfferingTemplate> NETHER_RIFT_POOL = new WeightedPool<>();
    public static WeightedPool<DungeonRegistration.OfferingTemplate> END_RIFT_POOL = new WeightedPool<>();

    public static void setupRiftPools(){
        OVERWORLD_RIFT_POOL.add(OVERWORLD_TEST_RIFT, 1);
        NETHER_RIFT_POOL.add(NETHER_TEST_RIFT, 1);
        END_RIFT_POOL.add(END_TEST_RIFT, 1);
    }
}
