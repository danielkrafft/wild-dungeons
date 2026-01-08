package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

import static net.minecraft.world.effect.MobEffects.POISON;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;

//446 original -> 436 now (10 saved)
public class SkelepedeMain extends WDBoss implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String SKELEPEDE_HEAD_CONTROLLER = "skelepede_head_controller";
    private final AnimationController<SkelepedeMain> mainController = new AnimationController<>(this, SKELEPEDE_HEAD_CONTROLLER, 5, animationPredicate());
    private static final String
            idle = "idle",
            bite = "bite";

    private ArrayList<SkelepedeSegment> segments = new ArrayList<>();

    // Number of segments and spacing between them
    private static final int NUM_SEGMENTS = 25;
    private static final double SEGMENT_SPACING = .5;
    private static final double POSITION_HISTORY_MULTIPLIER = 3;

    // Store previous positions for segment following
    private final LinkedList<Vec3> previousPositions = new LinkedList<>();
    // store previous rotations for segment following
    private final LinkedList<Float> previousRotations = new LinkedList<>();

    private String uniqueID = "";

    // Synced data accessors
    private static final EntityDataAccessor<Float> SYNCED_HEALTH =
            SynchedEntityData.defineId(SkelepedeMain.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SYNCED_BOSS_PROGRESS =
            SynchedEntityData.defineId(SkelepedeMain.class, EntityDataSerializers.FLOAT);

    public SkelepedeMain(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_20);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, .5f));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, .5, 10));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, true,
                player -> !((Player) player).isCreative() && !((Player) player).isSpectator()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, true, li -> !(li instanceof SkelepedeSegment) && !(li instanceof SkelepedeMain)));
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    private AnimationController.AnimationStateHandler<SkelepedeMain> animationPredicate() {
        return (state) -> PlayState.STOP;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.STEP_HEIGHT, 2).add(Attributes.MAX_HEALTH, NUM_SEGMENTS * 20).add(ATTACK_DAMAGE, 2f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // animation logic here
    }

    private boolean hasSetupSegments = false;//should this be a synced data parameter?

    @Override
    public void tick() {
        super.tick();
        // Update boss health bar
        float clientHealth = this.entityData.get(SYNCED_HEALTH);
        float clientBossProgress = this.entityData.get(SYNCED_BOSS_PROGRESS);
        this.setHealth(clientHealth);
        bossEvent.setProgress(clientBossProgress);
        if (level().isClientSide()) {
//            WildDungeons.getLogger().debug("Client SkelepedeMain tick - health: " + getHealth());
            return;
        }

        if (!hasSetupSegments) {
            bossEvent.setVisible(false);
            if (shouldScanForSegments) {
                ScanForSegments();
                bossEvent.setVisible(true);
            }
            return;
        }
        segments.removeAll(segments.stream().filter(Objects::isNull).toList());

        // Add current position to history if has moved
        if (previousPositions.isEmpty())
            previousPositions.addFirst(this.position());
        Vec3 previousPos = previousPositions.peekFirst();
        if (previousPos != null && !this.position().closerThan(previousPos, 0.1f)) {
            previousPositions.addFirst(this.position());
        }
        previousRotations.addFirst(this.getYRot());
        // Limit history size
        int maxHistory = (int) (NUM_SEGMENTS * SEGMENT_SPACING * POSITION_HISTORY_MULTIPLIER);
        while (previousPositions.size() > maxHistory) {
            previousPositions.removeLast();
        }
        while (previousRotations.size() > maxHistory) {
            previousRotations.removeLast();
        }

        ProcessPossibleSplits();

        List<SkelepedeSegment> list = new ArrayList<>();
        for (SkelepedeSegment skelepedeSegment : segments) {
            if (skelepedeSegment.isRemoved() || !skelepedeSegment.isAlive()) {
                list.add(skelepedeSegment);
            }
        }
        segments.removeAll(list);


        if (segments.isEmpty()) {
            // No segments to update
            this.kill();
            return;
        }

        float totalHealth = 0;
        float totalMaxHealth = 0;

        // Update each segment's position to follow the main entity
        for (int i = 0; i < segments.size(); i++) {
            int index = (int) ((i + 1) * SEGMENT_SPACING * 2);
            if (index < previousPositions.size()) {
                Vec3 targetPos = previousPositions.get(index);
                SkelepedeSegment segment = segments.get(i);
                if (segment == null) continue;
                if (segment.isRemoved() || !segment.isAlive()) continue;
                segment.setSegmentPosition(targetPos, previousRotations.get(index));
                totalHealth += segment.getHealth();
                totalMaxHealth += segment.getMaxHealth();
            }
        }
        if (!segments.isEmpty() && totalHealth <=0){
            //this happens when a new head is created from a split, because we are halfway through a tick when we do the split and the client hasn't synced the new head's health yet.
            totalHealth = 1;
        }
//        WildDungeons.getLogger().debug("Server SkelepedeMain tick - health: " + totalHealth + " , segments: " + segments.size());
        // Update synced data floats
        this.entityData.set(SYNCED_HEALTH, totalHealth);
        this.entityData.set(SYNCED_BOSS_PROGRESS, totalMaxHealth == 0 ? 0.0f : totalHealth / totalMaxHealth);

        if (getTarget() != null) {
            this.getLookControl().setLookAt(getTarget(), 10.0F, (float) this.getMaxHeadXRot());
        }
        //body faces movement delta
        Vec3 moveDelta = this.getDeltaMovement();
        if (moveDelta.x != 0 || moveDelta.z != 0) {
            float targetYaw = (float) (Mth.atan2(moveDelta.z, moveDelta.x) * (180D / Math.PI)) - 90F;
            this.setYRot(targetYaw);
            this.yBodyRot = targetYaw;
        }
    }

    private void ProcessPossibleSplits() {
        // Find any dead segment
        for (int i = 0; i < segments.size(); i++) {
            SkelepedeSegment segment = segments.get(i);
            if (segment == null || segment.isRemoved() || !segment.isAlive()) {
                // Split at this segment
                int splitIndex = i;
                // Segments after the dead one
                ArrayList<SkelepedeSegment> splitSegments = new ArrayList<>();
                for (int j = splitIndex + 1; j < segments.size(); j++) {
                    SkelepedeSegment seg = segments.get(j);
                    if (seg != null && seg.isAlive() && !seg.isRemoved()) {
                        splitSegments.add(seg);
                    }
                }
                // Remove all segments after splitIndex from this main
                for (int j = segments.size() - 1; j > splitIndex; j--) {
                    segments.remove(j);
                }
                // Spawn new SkelepedeMain for splitSegments
                if (!splitSegments.isEmpty() && level() instanceof ServerLevel serverLevel) {
                    SkelepedeMain newMain = WDEntities.SKELEPEDE.get().create(serverLevel);
                    if (newMain != null) {
                        newMain.moveTo(splitSegments.get(0).position());
                        newMain.setYRot(splitSegments.get(0).getYRot());
                        // Set parent key for split segments
                        String newMainID = newMain.getUniqueID();
                        for (SkelepedeSegment seg : splitSegments) {
                            seg.setSkelepedeParentKey(newMainID);
                        }
                        newMain.addSegments(splitSegments);
                        serverLevel.addFreshEntity(newMain);
                    }
                }
                break; // Only process one split per tick
            }
        }
    }

    public void addSegments(ArrayList<SkelepedeSegment> newSegments) {
        if (newSegments.isEmpty()) return;
        // Sort segments based on their distance to the main entity
        newSegments.sort(Comparator.comparingDouble(segment -> segment.position().distanceTo(this.position())));
        // Add segments to the list
        this.segments.addAll(newSegments);
        this.hasSetupSegments = true;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        // Setup segments if not already done
        if (!hasSetupSegments) {
            // Initialize segments
            for (int i = 0; i < NUM_SEGMENTS; i++) {
                SkelepedeSegment segment = WDEntities.SKELEPEDE_SEGMENT.get().create(level.getLevel());
                segment.finalizeSpawn(level, difficulty, spawnType, null);
                level.addFreshEntity(segment);
                segment.setSegmentPosition(this.position().add(0, 0, -SEGMENT_SPACING * (i + 1)), this.getYRot());
                segment.setSkelepedeParentKey(this.getUniqueID());
                segments.add(segment);
            }
            //initialize position history
            previousPositions.clear();
            for (int i = 0; i < NUM_SEGMENTS * SEGMENT_SPACING * POSITION_HISTORY_MULTIPLIER; i++) {
                previousPositions.add(this.position().add(0, 0, SEGMENT_SPACING * i / 2));
            }
            previousRotations.clear();
            for (int i = 0; i < NUM_SEGMENTS * SEGMENT_SPACING * POSITION_HISTORY_MULTIPLIER; i++) {
                previousRotations.add(this.getYRot());
            }
            this.hasSetupSegments = true;
        }
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private boolean shouldScanForSegments = false;
    private int segmentCount = 0;
    private ArrayList<SkelepedeSegment> foundSegments = new ArrayList<>();
    private ArrayList<String> segmentUIDs = new ArrayList<>();

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
        //load segments
        segmentCount = compound.getInt("SegmentCount");
        shouldScanForSegments = segmentCount > 0;
        for (int i = 0; i < segmentCount; i++) {
            String segmentUID = compound.getString("SegmentUID_" + i);
            if (!segmentUID.isEmpty()) {
                segmentUIDs.add(segmentUID);
            }
        }
    }

    public void ScanForSegments() {//when this happens and there are multiple skelepedes, they will all scan for segments and may pick up segments from other skelepedes.
        if (!shouldScanForSegments) return;
        shouldScanForSegments = false;
        foundSegments.clear();
        if (!(level() instanceof ServerLevel serverLevel)) return;
        List<SkelepedeSegment> segmentsInWorld = serverLevel.getEntitiesOfClass(SkelepedeSegment.class, new AABB(this.blockPosition()).inflate(100));
        if (segmentCount > 0 && !segmentsInWorld.isEmpty()) {
            for (int i = 0; i < segmentCount; i++) {
                String segmentUID = segmentUIDs.get(i);
                if (!segmentUID.isEmpty()) {
                    // Search for the segment entity in the world by its unique ID
                    for (SkelepedeSegment segment : segmentsInWorld) {
                        if (segment.getUniqueID().equals(segmentUID) && !segment.isRemoved() && segment.isAlive()) {
                            foundSegments.add(segment);
                            break;
                        }
                    }
                }
            }
        }
        if (!foundSegments.isEmpty()) {
            this.addSegments(foundSegments);
        } else {
            // If no segments were found, consider the SkelepedeMain invalid and remove it
            WildDungeons.getLogger().error("SkelepedeMain at " + this.blockPosition() + " found no segments and will be removed.");
            this.kill();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {

        //save segments
        ArrayList<String> segmentUIDs = new ArrayList<>();
        for (SkelepedeSegment segment : segments) {
            if (segment != null && !segment.isRemoved() && segment.isAlive()) {
                segmentUIDs.add(segment.getUniqueID());
            }
        }

        compound.putInt("SegmentCount", segmentUIDs.size());
//        WildDungeons.getLogger().debug("Saving Skelepede with " + segmentUUIDs.size() + " segments.");
        for (int i = 0; i < segmentUIDs.size(); i++) {
            compound.putString("SegmentUID_" + i, segmentUIDs.get(i));
        }
        super.addAdditionalSaveData(compound);
    }

    @Override
    protected void spawnSummonParticles(Vec3 pos) {}

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SYNCED_HEALTH, 100f);
        builder.define(SYNCED_BOSS_PROGRESS, 1f);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity instanceof LivingEntity && !(entity instanceof SkelepedeSegment) && !(entity instanceof SkelepedeMain);
    }

    private static final BossSounds SOUNDS = new BossSounds(null, SoundEvents.SKELETON_HURT, null);

    @Override
    protected BossSounds bossSounds() {
        return SOUNDS;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, .25f, 1.0F);
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.is(POISON)) return false;
        return super.addEffect(effectInstance, entity);
    }

    public String getUniqueID() {
        if (uniqueID.isEmpty()) {
            uniqueID = this.getStringUUID();
        }
        return uniqueID;
    }

    @Override
    public void kill() {
        super.kill();
        //set the health to 0 so clients know it's dead
        this.entityData.set(SYNCED_HEALTH, 0f);
        this.entityData.set(SYNCED_BOSS_PROGRESS, 0f);
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel p_level, DamageSource damageSource) {
        if (level() instanceof ServerLevel serverLevel) {
            List<SkelepedeMain> mainsInWorld = serverLevel.getEntitiesOfClass(SkelepedeMain.class, new AABB(this.blockPosition()).inflate(100));
            boolean otherMainsExist = mainsInWorld.stream().anyMatch(main -> main != this && !main.isRemoved() && main.isAlive());
            if (!otherMainsExist) {
                super.dropAllDeathLoot(p_level, damageSource);
            }
        }
    }

    @Override
    public double getAttributeValue(Holder<Attribute> attribute) {
        if (attribute.is(ATTACK_DAMAGE.getKey())){
            double baseDamage = super.getAttributeValue(attribute);
            return Math.max(0.5f, baseDamage * segments.size() / 5.0); //minimum 0.5 damage
        }
        return super.getAttributeValue(attribute);
    }
}