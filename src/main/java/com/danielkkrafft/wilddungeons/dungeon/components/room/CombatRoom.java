package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.*;

public class CombatRoom extends DungeonRoom {

    public static final int SET_PURGE_INTERVAL = 20;
    public static final int SPAWN_INTERVAL = 200;
    public static final int BASE_QUANTITY = 10;
    public static final int BASE_DIFFICULTY = 10;

    public Set<String> aliveUUIDs = new HashSet<>();
    public List<String> toSpawn = new ArrayList<>();
    public int checkTimer = SET_PURGE_INTERVAL;
    public int spawnTimer = 0;
    public int groupSize = 2;

    public boolean started = false;

    public CombatRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, level, position, settings, allConnectionPoints);
    }

    @Override
    public void onEnterInner(WDPlayer player) {
        super.onEnterInner(player);
        if (this.started) return;

        WildDungeons.getLogger().info("BLOCKING THE EXITS");
        //Block all the exits once a player has entered
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.block(this.getBranch().getFloor().getLevel());
        });

        WildDungeons.getLogger().info("SPAWNING MOBS");
        List<EntityType<?>> entities = this.getEnemyTable().randomResults(BASE_QUANTITY, (int) (BASE_DIFFICULTY * this.getDifficulty()), 2);

        entities.forEach(entityType -> {
            toSpawn.add(EntityType.getKey(entityType).toString());
        });
        this.started = true;
    }

    public void spawnNext() {
        for (int i = 0; i < Math.floor(groupSize * this.getDifficulty()); i++) {
            WildDungeons.getLogger().info("SPAWNING A GROUP OF {}", Math.floor(groupSize * this.getDifficulty()));
            if (toSpawn.isEmpty()) return;
            LivingEntity entity = (LivingEntity) EntityType.byString(toSpawn.removeFirst()).get().create(this.getBranch().getFloor().getLevel());
            List<BlockPos> validPoints = this.sampleSpawnablePositions(getBranch().getFloor().getLevel(), 3);

            BlockPos finalPos = validPoints.stream().map(pos -> {
                int score = 0;

                for (WDPlayer wdPlayer : this.getPlayers()) {
                    score += pos.distManhattan(wdPlayer.getServerPlayer().blockPosition());
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
        if (!started || this.getPlayers().isEmpty() || this.isClear()) return;
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
            if (point.isConnected()) point.unBlock(this.getBranch().getFloor().getLevel());
        });
    }

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);

            if (wdPlayer.getCurrentDungeon() == null) return;
            if (wdPlayer.getCurrentRoom() instanceof CombatRoom room) room.aliveUUIDs.remove(event.getEntity().getStringUUID());
        }
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
}
