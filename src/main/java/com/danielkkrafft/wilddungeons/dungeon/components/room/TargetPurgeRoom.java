package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TargetPurgeRoom extends LockableEventRoom {

    public static final int SET_PURGE_INTERVAL = 20;

    public List<DungeonTarget> targets = new ArrayList<>();
    public int checkTimer = SET_PURGE_INTERVAL;

    public TargetPurgeRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, position, settings, allConnectionPoints);
        this.processOfferings();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.started || this.isClear() || this.getActivePlayers().isEmpty()) return;
        if (targets.isEmpty()) {this.onClear(); return;}
        if (checkTimer == 0) {purgeEnemySet(); checkTimer = SET_PURGE_INTERVAL;}
        checkTimer -= 1;
    }

    public void purgeEnemySet() {
        List<DungeonTarget> toRemove = new ArrayList<>();
        this.targets.forEach(enemy -> {
            if (!enemy.isAlive(this)) toRemove.add(enemy);
        });
        toRemove.forEach(enemy -> {
            targets.remove(enemy);
        });
    }

    public void discardByUUID(String uuid) {
        WildDungeons.getLogger().info("DISCARDING ENTITY BY UUID: {}", uuid); //TODO enemies aren't purging after rejoin
        List<DungeonTarget> toRemove = new ArrayList<>();
        this.targets.forEach(target -> {
            if (!target.spawned) return;
            if (Objects.equals(target.uuid, uuid)) toRemove.add(target);
        });
        toRemove.forEach(target -> {
            targets.remove(target);
        });
    }

    public void discardByBlockPos(BlockPos pos) {
        List<DungeonTarget> toRemove = new ArrayList<>();
        this.targets.forEach(enemy -> {
            if (!enemy.spawned) return;
            if (enemy.type.equals(DungeonTarget.Type.SPAWNER.toString()) && enemy.startPos.equals(pos)) toRemove.add(enemy);
        });
        toRemove.forEach(enemy -> {
            targets.remove(enemy);
        });
    }

    @Override
    public void reset() {
        super.reset();
        targets.forEach(enemy -> enemy.discard(this));
        targets.clear();
    }
}
