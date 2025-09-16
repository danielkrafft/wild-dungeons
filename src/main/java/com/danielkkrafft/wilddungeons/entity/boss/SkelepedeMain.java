package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class SkelepedeMain extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final String SKELEPEDE_HEAD_CONTROLLER = "skelepede_head_controller";
    private final AnimationController<SkelepedeMain> mainController = new AnimationController<>(this, SKELEPEDE_HEAD_CONTROLLER, 5, animationPredicate());
    private static final String
            idle = "idle",
            bite = "bite";
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_20);

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

    public SkelepedeMain(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, .5f));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, .5,10));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false,
                player -> !((Player) player).isCreative() && !((Player) player).isSpectator()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof SkelepedeSegment) && !(li instanceof SkelepedeMain)));
    }
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    private AnimationController.AnimationStateHandler<SkelepedeMain> animationPredicate() {
        return (state) -> PlayState.STOP;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.STEP_HEIGHT, 2);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // animation logic here
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private boolean hasSetupSegments = false;//should this be a synced data parameter?
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;

        if (!hasSetupSegments){
            bossEvent.setVisible(false);
            if (shouldScanForSegments){
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
        int maxHistory = (int)(NUM_SEGMENTS * SEGMENT_SPACING * POSITION_HISTORY_MULTIPLIER);
        while (previousPositions.size() > maxHistory) {
            previousPositions.removeLast();
        }
        while (previousRotations.size() > maxHistory) {
            previousRotations.removeLast();
        }

        if (segments.isEmpty()){
            // No segments to update
            this.kill();
            return;
        }

        ProcessPossibleSplits();

        float totalHealth = 0;
        float totalMaxHealth = 0;

        // Update each segment's position to follow the main entity
        for (int i = 0; i < segments.size(); i++) {
            int index = (int)((i + 1) * SEGMENT_SPACING * 2);
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

        // Update boss health bar
        if (!segments.isEmpty())
            this.setHealth(totalHealth);
        bossEvent.setProgress(totalHealth / totalMaxHealth);

        if (getTarget()!=null){
            this.getLookControl().setLookAt(getTarget(), 10.0F, (float)this.getMaxHeadXRot());
            //body faces movement delta
            Vec3 moveDelta = this.getDeltaMovement();
            if (moveDelta.x != 0 || moveDelta.z != 0) {
                float targetYaw = (float) (Mth.atan2(moveDelta.z, moveDelta.x) * (180D / Math.PI)) - 90F;
                this.setYRot(targetYaw);
                this.yBodyRot = targetYaw;
            }
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
                // Remove dead segment from world and from list
                if (segment != null && !segment.isRemoved()) {
                    segment.kill();
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
                        serverLevel.addFreshEntity(newMain);
                        newMain.addSegments(splitSegments);
                        // Set parent key for split segments
                        String newMainID = newMain.getUniqueID();
                        for (SkelepedeSegment seg : splitSegments) {
                            seg.setSkelepedeParentKey(newMainID);
                        }
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
                previousPositions.add(this.position().add(0, 0, SEGMENT_SPACING * i/2) );
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

    public void ScanForSegments(){//when this happens and there are multiple skelepedes, they will all scan for segments and may pick up segments from other skelepedes.
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
                // uuids aren't retained on world reload and this should be fixed by adding some kind of persistent id to the segments and main entity and then linking them here
                //then remove the scanning system
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
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.removePlayer(serverPlayer);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity instanceof LivingEntity && !(entity instanceof SkelepedeSegment) && !(entity instanceof SkelepedeMain);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SKELETON_HURT;
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
}
