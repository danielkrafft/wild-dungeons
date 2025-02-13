package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.List;

public class BossRoom extends CombatRoom{

    public BossRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, position, settings, allConnectionPoints);
    }

    @Override
    public List<DungeonRegistration.TargetTemplate> getTargetTemplates() {
        return this.getProperty(HierarchicalProperty.ENEMY_TABLE).randomResults(1,1,1);
    }
}
