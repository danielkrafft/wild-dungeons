package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.ArrayList;
import java.util.List;

public class BossRoom extends CombatRoom {

    public BossRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
    }

    @Override
    public List<DungeonRegistration.TargetTemplate> getTargetTemplates() {
        return this.getProperty(HierarchicalProperty.ENEMY_TABLE).randomResults(1,1,1);
    }

    @Override
    public List<BlockPos> sampleSpawnablePositions(ServerLevel level, int count, int inflation) {
        return List.of((TemplateHelper.transform(BlockPos.containing(this.getProperty(HierarchicalProperty.BOSS_SPAWN_POS)), this)));
    }
}
