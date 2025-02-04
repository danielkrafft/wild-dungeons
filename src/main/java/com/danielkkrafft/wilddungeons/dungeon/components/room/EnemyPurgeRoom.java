package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

public class EnemyPurgeRoom extends LockableEventRoom {

    public static final int SET_PURGE_INTERVAL = 20;

    public List<DungeonTarget> enemies = new ArrayList<>();
    public int checkTimer = SET_PURGE_INTERVAL;

    public EnemyPurgeRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, position, settings, allConnectionPoints);
        this.processOfferings();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.started || this.isClear() || this.getActivePlayers().isEmpty()) return;
        if (enemies.isEmpty()) {this.onClear(); return;}
        if (checkTimer == 0) {purgeEnemySet(); checkTimer = SET_PURGE_INTERVAL;}
        checkTimer -= 1;
    }

    public void purgeEnemySet() {
        List<DungeonTarget> toRemove = new ArrayList<>();
        this.enemies.forEach(enemy -> {
            if (!enemy.isAlive(this)) toRemove.add(enemy);
        });
        toRemove.forEach(enemy -> {
            enemies.remove(enemy);
        });
    }

    public void discardByUUID(String uuid) {
        List<DungeonTarget> toRemove = new ArrayList<>();
        this.enemies.forEach(enemy -> {
            if (!enemy.spawned) return;
            if (Objects.equals(enemy.uuid, uuid)) toRemove.add(enemy);
        });
        toRemove.forEach(enemy -> {
            enemies.remove(enemy);
        });
    }

    public void discardByBlockPos(BlockPos pos) {
        List<DungeonTarget> toRemove = new ArrayList<>();
        this.enemies.forEach(enemy -> {
            if (!enemy.spawned) return;
            if (enemy.type.equals(DungeonTarget.Type.SPAWNER.toString()) && enemy.startPos.equals(pos)) toRemove.add(enemy);
        });
        toRemove.forEach(enemy -> {
            enemies.remove(enemy);
        });
    }

    @Override
    public void reset() {
        super.reset();
        enemies.forEach(enemy -> enemy.discard(this));
        enemies.clear();
    }
}
