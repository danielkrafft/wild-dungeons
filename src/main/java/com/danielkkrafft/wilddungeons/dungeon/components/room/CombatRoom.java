package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class CombatRoom extends EntityPurgeRoom {

    public static final int SPAWN_INTERVAL = 200;
    public static final int BASE_QUANTITY = 10;
    public static final float QUANTITY_VARIANCE = 2f;
    public static final int BASE_DIFFICULTY = 10;

    public int spawnTimer = 0;
    public int groupSize = 2;

    public CombatRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, level, position, settings, allConnectionPoints);
    }

    @Override
    public void start() {
        if (this.started) return;
        WildDungeons.getLogger().info("SPAWNING MOBS");
        List<EntityType<?>> entities = this.getEnemyTable().randomResults(Mth.ceil(RandomUtil.randFloatBetween(BASE_QUANTITY / QUANTITY_VARIANCE, BASE_QUANTITY * QUANTITY_VARIANCE)), (int) (BASE_DIFFICULTY * this.getDifficulty()), 2);

        entities.forEach(entityType -> {
            toSpawn.add(EntityType.getKey(entityType).toString());
        });
        super.start();
    }

    public void spawnNext() {
        for (int i = 0; i < Math.floor(groupSize * this.getDifficulty()); i++) {
            WildDungeons.getLogger().info("SPAWNING A GROUP OF {}", Math.floor(groupSize * this.getDifficulty()));
            if (toSpawn.isEmpty()) return;
            LivingEntity entity = (LivingEntity) EntityType.byString(toSpawn.removeFirst()).get().create(this.getBranch().getFloor().getLevel());
            List<BlockPos> validPoints = this.sampleSpawnablePositions(getBranch().getFloor().getLevel(), 3);

            BlockPos finalPos = validPoints.stream().map(pos -> {
                int score = 0;

                for (WDPlayer wdPlayer : this.getActivePlayers()) {
                    ServerPlayer player = wdPlayer.getServerPlayer();
                    if (player!=null)
                        score += pos.distManhattan( player.blockPosition());
                }

                return new Pair<>(pos, score);

            }).max(Comparator.comparingInt(Pair::getSecond)).get().getFirst();

            entity.setPos(Vec3.atCenterOf(finalPos));
            aliveUUIDs.add(entity.getStringUUID());
            this.getBranch().getFloor().getLevel().addWithUUID(entity);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.started || this.isClear() || this.getActivePlayers().isEmpty()) return;
        if (spawnTimer == 0 || aliveUUIDs.isEmpty()) {spawnNext(); spawnTimer = SPAWN_INTERVAL;}
        spawnTimer -= 1;
    }
}
