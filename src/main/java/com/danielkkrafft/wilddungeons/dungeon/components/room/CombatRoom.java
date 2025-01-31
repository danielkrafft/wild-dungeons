package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class CombatRoom extends DungeonRoom {

    public static final int SET_PURGE_INTERVAL = 20;
    public static final int SPAWN_INTERVAL = 200;
    public static final int BASE_QUANTITY = 10;
    public static final float QUANTITY_VARIANCE = 2f;
    public static final int BASE_DIFFICULTY = 10;
    public static final int START_COOLDOWN = 100;

    public Set<String> aliveUUIDs = new HashSet<>();
    public List<String> toSpawn = new ArrayList<>();
    public int checkTimer = SET_PURGE_INTERVAL;
    public int spawnTimer = 0;
    public int groupSize = 2;
    public int startCooldown = START_COOLDOWN;

    public boolean started = false;
    public boolean generated = false;

    public CombatRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, level, position, settings, allConnectionPoints);
    }

    public void startBattle() {
        if (this.started) return;
        this.started = true;
        WildDungeons.getLogger().info("BLOCKING THE EXITS");

        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.block(this.getBranch().getFloor().getLevel());
        });

        WildDungeons.getLogger().info("SPAWNING MOBS");
        List<EntityType<?>> entities = this.getEnemyTable().randomResults(Mth.ceil(RandomUtil.randFloatBetween(BASE_QUANTITY / QUANTITY_VARIANCE, BASE_QUANTITY * QUANTITY_VARIANCE)), (int) (BASE_DIFFICULTY * this.getDifficulty()), 2);

        entities.forEach(entityType -> {
            toSpawn.add(EntityType.getKey(entityType).toString());
        });
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
    public void onExit(WDPlayer wdPlayer) {
        super.onExit(wdPlayer);
        if (this.getActivePlayers().isEmpty() && !this.isClear()) {
            this.reset();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getActivePlayers().isEmpty() || this.isClear()) return;

        if (!this.started && this.startCooldown > 0) {
            this.startCooldown-=1;
        }

        if (!this.started && (this.startCooldown <= 0) || this.getActivePlayers().size() >= this.getBranch().getActivePlayers().size() + this.getBranch().getFloor().getBranches().get(this.getBranch().getIndex() - 1).getActivePlayers().size()) {
            this.startBattle();
        }

        if (!this.started) return;

        if (aliveUUIDs.isEmpty() && toSpawn.isEmpty()) {this.onClear(); return;}
        if (checkTimer == 0) {purgeEntitySet(); checkTimer = SET_PURGE_INTERVAL;}
        if (spawnTimer == 0 || aliveUUIDs.isEmpty()) {spawnNext(); spawnTimer = SPAWN_INTERVAL;}
        checkTimer -= 1;
        spawnTimer -= 1;
    }

    @Override
    public void onClear() {
        super.onClear();
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) {
                point.unBlock(this.getBranch().getFloor().getLevel());
                point.getConnectedPoint().unBlock(this.getBranch().getFloor().getLevel());
            }
        });
    }

    public void purgeEntitySet() {
        List<String> toRemove = new ArrayList<>();
        this.aliveUUIDs.forEach(uuid -> {
            LivingEntity livingEntity = (LivingEntity) this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (livingEntity == null || !livingEntity.isAlive()) {
                toRemove.add(uuid);
            }
        });

        toRemove.forEach(livingEntity -> {
            aliveUUIDs.remove(livingEntity);
        });
    }

    @Override
    public void reset() {
        super.reset();
        this.started = false;
        startCooldown = START_COOLDOWN;
        toSpawn.clear();
        aliveUUIDs.forEach(uuid -> {
            LivingEntity livingEntity = (LivingEntity) this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (livingEntity != null) {
                livingEntity.remove(Entity.RemovalReason.DISCARDED);
            }
        });
        aliveUUIDs.clear();
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.unBlock(this.getBranch().getFloor().getLevel());
        });
        this.generated = false;
        this.onBranchEnter(null);

    }

    @Override
    public void onBranchEnter(WDPlayer wdPlayer) {
        super.onBranchEnter(wdPlayer);
        if (this.generated) return;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected() && point.getConnectedPoint().getRoom().getIndex() > this.getIndex() && point.getConnectedPoint().getBranchIndex() == this.getBranch().getIndex()) {
                point.block(this.getBranch().getFloor().getLevel());
            }
        });
        this.generated = true;
    }
}
