package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class CombatRoom extends TargetPurgeRoom {

    public static final int SPAWN_INTERVAL = 200;
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
        List<DungeonRegistration.TargetTemplate> templates = getTargetTemplates();

        templates.forEach(template -> {
            DungeonTarget enemy = template.asEnemy();
            targets.add(enemy);
            totalSpawns += 1;
        });
        super.start();
    }

    public List<DungeonRegistration.TargetTemplate> getTargetTemplates() {
        return this.getProperty(HierarchicalProperty.ENEMY_TABLE).randomResults(Mth.ceil(RandomUtil.randFloatBetween(this.getProperty(HierarchicalProperty.WAVE_SIZE) / QUANTITY_VARIANCE, this.getProperty(HierarchicalProperty.WAVE_SIZE) * QUANTITY_VARIANCE)), (int) (BASE_DIFFICULTY * this.getDifficulty()), 2);
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
        BlockPos finalPos = calculateClosestPoint(validPoints,5);
        offering.setPos(Vec3.atCenterOf(finalPos));
        this.getBranch().getFloor().getLevel().addFreshEntity(offering);
    }

    @Override
    public void templateBasedUnblock(DungeonFloor floor, ConnectionPoint point) {
        point.combatRoomUnblock(floor.getLevel());
    }
}
