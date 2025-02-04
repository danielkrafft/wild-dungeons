package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.*;

public class CombatRoom extends EnemyPurgeRoom {

    public static final int SPAWN_INTERVAL = 200;
    public static final int BASE_QUANTITY = 10;
    public static final float QUANTITY_VARIANCE = 2f;
    public static final int BASE_DIFFICULTY = 10;

    public int spawnTimer = 0;
    public int groupSize = 2;

    public List<DungeonTarget> toSpawn = new ArrayList<>();

    public CombatRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, position, settings, allConnectionPoints);
    }

    @Override
    public void start() {
        if (this.started) return;
        WildDungeons.getLogger().info("SPAWNING MOBS");
        List<DungeonRegistration.TargetTemplate> templates = this.getEnemyTable().randomResults(Mth.ceil(RandomUtil.randFloatBetween(BASE_QUANTITY / QUANTITY_VARIANCE, BASE_QUANTITY * QUANTITY_VARIANCE)), (int) (BASE_DIFFICULTY * this.getDifficulty()), 2);

        templates.forEach(template -> {
            DungeonTarget enemy = template.asEnemy();
            enemies.add(enemy);
            toSpawn.add(enemy);
        });
        super.start();
    }

    public void spawnNext() {
        for (int i = 0; i < Math.floor(groupSize * this.getDifficulty()); i++) {
            WildDungeons.getLogger().info("SPAWNING A GROUP OF {}", Math.floor(groupSize * this.getDifficulty()));
            if (toSpawn.isEmpty()) return;
            DungeonTarget enemy = toSpawn.removeFirst();
            enemy.spawn(this);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.started || this.isClear() || this.getActivePlayers().isEmpty()) return;
        if (spawnTimer == 0 || toSpawn.size() == enemies.size()) {spawnNext(); spawnTimer = SPAWN_INTERVAL;}
        spawnTimer -= 1;
    }
}
