package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.INTENSITY;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.SOUNDSCAPE;

public class CombatRoom extends TargetPurgeRoom {

    public static final int SPAWN_INTERVAL = 200;
    public static final float QUANTITY_VARIANCE = 2f;
    public static final int BASE_DIFFICULTY = 10;

    public int spawnTimer = 0;
    public int groupSize = 2;

    public int totalSpawns = 0;
    public boolean helpingGlow = false;

    public CombatRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
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
        this.getActivePlayers().forEach(player -> {
            player.setSoundScape(this.getProperty(SOUNDSCAPE), this.getProperty(INTENSITY)+1, false);
        });
        super.start();
    }

    public List<DungeonRegistration.TargetTemplate> getTargetTemplates() {
        float lowerWaveSize = this.getProperty(HierarchicalProperty.WAVE_SIZE) / QUANTITY_VARIANCE;
        float upperWaveSize = this.getProperty(HierarchicalProperty.WAVE_SIZE) * QUANTITY_VARIANCE;
        int quantity = Mth.ceil(RandomUtil.randFloatBetween(lowerWaveSize, upperWaveSize));
        int quality = (int) (BASE_DIFFICULTY * this.getDifficulty());
        return this.getProperty(HierarchicalProperty.ENEMY_TABLE).randomResults(quantity, quality, 2);
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
        if (!helpingGlow && targets.stream().allMatch(target -> target.spawned) && (long) targets.size() <= 3 && !(this instanceof BossRoom)) {
            TriggerGlowingHelper();
        }
    }

    public void TriggerGlowingHelper() {
        targets.forEach(target -> {
            if (target.type.equals(DungeonTarget.Type.ENTITY.toString())) {
                Entity entity = getBranch().getFloor().getLevel().getEntity(UUID.fromString(target.uuid));
                if (entity instanceof LivingEntity livingEntity) livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING,-1,0,true,false));
            }
        });
        helpingGlow = true;
    }

    @Override
    public void reset() {
        super.reset();
        totalSpawns = 0;
    }

    @Override
    public void onClear() {
        super.onClear();
        this.getActivePlayers().forEach(player -> {
            player.setSoundScape(this.getProperty(SOUNDSCAPE), this.getProperty(INTENSITY), false);
        });
        DungeonRegistration.OfferingTemplate offeringTemplate = getTemplate().roomClearOffering();
        if (offeringTemplate == null) return;
        Offering offering = offeringTemplate.asOffering(this.getBranch().getFloor().getLevel());
        List<BlockPos> validPoints = sampleSpawnablePositions(getBranch().getFloor().getLevel(), 5, Mth.ceil(Math.max(offering.getBoundingBox().getXsize(), offering.getBoundingBox().getZsize())));
        BlockPos finalPos = calculateClosestPoint(validPoints,5);
        offering.setPos(Vec3.atCenterOf(finalPos));
        this.getBranch().getFloor().getLevel().addFreshEntity(offering);
    }

    @Override public ResourceLocation getDecalTexture() {return ConnectionPoint.SWORD_TEXTURE;}
    @Override public int getDecalColor() {return 0xFFFF0000;}

}
