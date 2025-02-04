package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

public class EntityPurgeRoom extends LockableEventRoom {

    public static final int SET_PURGE_INTERVAL = 20;

    public Set<String> aliveUUIDs = new HashSet<>();
    public List<String> toSpawn = new ArrayList<>();
    public int checkTimer = SET_PURGE_INTERVAL;

    public EntityPurgeRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, level, position, settings, allConnectionPoints);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.started || this.isClear() || this.getActivePlayers().isEmpty()) return;
        if (aliveUUIDs.isEmpty() && toSpawn.isEmpty()) {this.onClear(); return;}
        if (checkTimer == 0) {purgeEntitySet(); checkTimer = SET_PURGE_INTERVAL;}
        checkTimer -= 1;
    }

    public void purgeEntitySet() {
        List<String> toRemove = new ArrayList<>();
        this.aliveUUIDs.forEach(uuid -> {
            Entity entity = this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (entity == null || !entity.isAlive()) {
                toRemove.add(uuid);
            }
        });
        toRemove.forEach(entity -> {
            aliveUUIDs.remove(entity);
        });
    }

    @Override
    public void reset() {
        super.reset();
        aliveUUIDs.forEach(uuid -> {
            LivingEntity livingEntity = (LivingEntity) this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (livingEntity != null) {
                livingEntity.remove(Entity.RemovalReason.DISCARDED);
            }
        });
        aliveUUIDs.clear();
        toSpawn.clear();
    }
}
