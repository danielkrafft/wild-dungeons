package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CombatRoom extends TargetPurgeRoom {

    public static final int SPAWN_INTERVAL = 200;
    public static final int BASE_QUANTITY = 10;
    public static final float QUANTITY_VARIANCE = 2f;
    public static final int BASE_DIFFICULTY = 10;

    public int spawnTimer = 0;
    public int groupSize = 2;

    public int totalSpawns = 0;

    public CombatRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, position, settings, allConnectionPoints);
    }

    @Override
    public void start() {
        if (this.started) return;
        WildDungeons.getLogger().info("SPAWNING MOBS");
        List<DungeonRegistration.TargetTemplate> templates = this.getProperty(HierarchicalProperty.ENEMY_TABLE).randomResults(Mth.ceil(RandomUtil.randFloatBetween(BASE_QUANTITY / QUANTITY_VARIANCE, BASE_QUANTITY * QUANTITY_VARIANCE)), (int) (BASE_DIFFICULTY * this.getDifficulty()), 2);

        templates.forEach(template -> {
            DungeonTarget enemy = template.asEnemy();
            targets.add(enemy);
            totalSpawns += 1;
        });
        super.start();
    }

    public void spawnNext() {
        WildDungeons.getLogger().info("SPAWNING A GROUP OF {}", Math.floor(groupSize * this.getDifficulty()));
        for (int i = 0; i < Math.floor(groupSize * this.getDifficulty()); i++) {
            if (totalSpawns <= 0) return;
            Optional<DungeonTarget> target = targets.stream().filter(t -> !t.spawned).findFirst();
            if (target.isPresent()) {
                target.get().spawn(this);
                totalSpawns--;
            } else {
                totalSpawns = 0;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.started || this.isClear() || this.getActivePlayers().isEmpty()) return;
        if (spawnTimer == 0 || totalSpawns == targets.size()) {spawnNext(); spawnTimer = SPAWN_INTERVAL;}
        spawnTimer -= 1;
    }

    @Override
    public void reset() {
        super.reset();
        totalSpawns = 0;
    }

    @Override
    public void onClear() {
        super.onClear();
        DungeonRegistration.OfferingTemplate offeringTemplate = getTemplate().roomClearOffering();
        if (offeringTemplate == null) return;
        Offering offering = offeringTemplate.asOffering(this.getBranch().getFloor().getLevel());
        List<BlockPos> validPoints = sampleSpawnablePositions(getBranch().getFloor().getLevel(), 5, -Mth.ceil(Math.max(offering.getBoundingBox().getXsize(), offering.getBoundingBox().getZsize())));
        BlockPos finalPos = validPoints.stream().map(pos -> {
            int score = 0;

            for (WDPlayer wdPlayer : getActivePlayers()) {
                ServerPlayer player = wdPlayer.getServerPlayer();
                if (player!=null)
                    score += pos.distManhattan( player.blockPosition());
            }

            return new Pair<>(pos, score);
        //unlike spawning enemies, we want to choose the closest spawn point to the players to make it more obvious
        }).min(Comparator.comparingInt(Pair::getSecond)).get().getFirst();
        offering.setPos(Vec3.atCenterOf(finalPos));
        this.getBranch().getFloor().getLevel().addFreshEntity(offering);
    }
}
