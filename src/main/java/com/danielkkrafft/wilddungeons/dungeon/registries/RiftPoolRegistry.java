package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateRegistry.*;

public class RiftPoolRegistry {

    public static WeightedPool<DungeonRegistration.OfferingTemplate> OVERWORLD_RIFT_POOL = new WeightedPool<DungeonRegistration.OfferingTemplate>()
            .add(OVERWORLD_TEST_RIFT, 1);;
    public static WeightedPool<DungeonRegistration.OfferingTemplate> NETHER_RIFT_POOL = new WeightedPool<DungeonRegistration.OfferingTemplate>()
            .add(NETHER_TEST_RIFT, 1);
    public static WeightedPool<DungeonRegistration.OfferingTemplate> END_RIFT_POOL = new WeightedPool<DungeonRegistration.OfferingTemplate>()
            .add(END_TEST_RIFT, 1);

}
