package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.world.entity.EntityType;

public record DungeonTemplate(String name, String openBehavior, DungeonRegistration.DungeonLayout<DungeonFloorTemplate> floorTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty, double difficultyScaling, DungeonSession.DungeonExitBehavior exitBehavior, WeightedPool<DungeonTemplate> nextDungeon) implements DungeonComponent {

    public static DungeonTemplate build(String name, String openBehavior, DungeonRegistration.DungeonLayout<DungeonFloorTemplate> floorTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty, double difficultyScaling, DungeonSession.DungeonExitBehavior exitBehavior, WeightedPool<DungeonTemplate> nextDungeon) {
        return new DungeonTemplate(name, openBehavior, floorTemplates, materials, enemyTable, difficulty, difficultyScaling, exitBehavior, nextDungeon);
    }

    public DungeonTemplate pool(WeightedPool<DungeonTemplate> pool, Integer weight) {pool.add(this, weight); return this;}
}
