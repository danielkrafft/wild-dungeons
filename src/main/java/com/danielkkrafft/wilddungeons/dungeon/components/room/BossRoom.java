package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

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
    public List<BlockPos> sampleSpawnablePositions(ServerLevel serverLevel, int count, EntityType<?> mobType) {
        return List.of((TemplateHelper.transform(BlockPos.containing(this.getProperty(HierarchicalProperty.BOSS_SPAWN_POS)), this)));
    }

    @Override
    public List<BlockPos> sampleSpawnablePositions(ServerLevel level, int count, int deflation) {
        return List.of((TemplateHelper.transform(BlockPos.containing(this.getProperty(HierarchicalProperty.BOSS_SPAWN_POS)), this)));
    }

    @Override
    public void spawnNext() {
        super.spawnNext();
        //find the first player in this room and look at them
        if (targets.isEmpty()) return;
        Vec3 pos = this.getActivePlayers().getFirst().getServerPlayer().position();
        Vec3 target = targets.getFirst().getEntity(this).position();
        Vec3 dir = pos.subtract(target).normalize();
        targets.getFirst().getEntity(this).setYRot((float) Math.toDegrees(Math.atan2(-dir.x, dir.z)));//todo this doesn't work
    }
}
