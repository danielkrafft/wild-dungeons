package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.List;

import static com.danielkkrafft.wilddungeons.util.MathUtil.entitylookAtEntity;

public class BossRoom extends CombatRoom {

    public BossRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
        this.bfacePlayerOnSpawn = true;
    }

    @Override
    public List<DungeonRegistration.TargetTemplate> getTargetTemplates() {
        return this.getProperty(HierarchicalProperty.ENEMY_TABLE).randomResults(1,1,1);
    }

    @Override
    public List<BlockPos> sampleSpawnablePositions(ServerLevel serverLevel, int count, EntityType<?> mobType) {
        return List.of((TemplateHelper.transform(BlockPos.containing(this.getProperty(HierarchicalProperty.BOSS_SPAWN_POS)), this)));
    }

    @Override
    public List<BlockPos> sampleSpawnablePositions(ServerLevel level, int count, int deflation) {
        return List.of((TemplateHelper.transform(BlockPos.containing(this.getProperty(HierarchicalProperty.BOSS_SPAWN_POS)), this)));
    }
}
