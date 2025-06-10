package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.BaseClasses.SelfGovernedEntity;
import com.danielkkrafft.wilddungeons.registry.WDDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class BlackHole extends SelfGovernedEntity {

    // Core size logic
    private float mass = 1.0f;
    private static final float MIN_MASS = 0.1f;                             // Minimum black hole size before death
    private static final float MAX_MASS = 4f;                               // Maximum black hole size before advanced decay begins
    private static final float GROWTH_PER_CONSUME = 0.01f;                  // Mass increase when consuming blocks/entities
    private static final float SHRINK_RATE = 0.001f;                        // Shrink per tick when moving or at max size
    private static final float BASE_DECAY_RATE = 0.001f;                    // Base decay rate once max size is hit
    private static final float MAX_DECAY_MULTIPLIER = 1000f;                 // Max scaling of decay based on cooldown progress
    private static final int DECAY_GRACE_PERIOD_TICKS = 10;                 // Ticks before decay begins after hitting max size
    private int decayCooldownTicks = 0;                                     // Tracks remaining grace period ticks

    // Interaction and pull behavior
    private static final double RADIUS_OUTER_SCALE = 2.5;                   // Outer interaction radius multiplier
    private static final double RADIUS_INNER_SCALE = .5;                  // Inner kill/damage radius multiplier
    private static final int MIN_DAMAGE = 1;                                // Minimum damage to entities in range
    private static final int MAX_DAMAGE = 10;                               // Maximum damage scaled by mass
    private static final double MIN_PULL_STRENGTH = 0.1;                   // Minimum gravitational pull strength
    private static final double MAX_PULL_STRENGTH = 0.2;                      // Maximum gravitational pull strength
    private static final double PULL_BASE_MULTIPLIER = 0.25;                   // Scaling multiplier for pull strength
    private static final double PULL_FALLOFF_EXPONENT = 3;                  // How fast pull drops off with distance (quadratic)
    private static final float DIRECTION_LERP_MIN = 0.1f;                   // Minimum influence when smaller black hole redirects
    private static final float DIRECTION_LERP_MAX = 0.25f;                  // Maximum influence when smaller black hole redirects

    // Entity and block destruction
    private static final int ENTITY_DAMAGE_COOLDOWN_TICKS = 10;             // Per-entity cooldown for repeated damage
    private static final int MIN_BLOCKS_DESTROYED = 10;                      // Minimum blocks randomly selected to destroy
    private static final int MAX_BLOCKS_DESTROYED = 100;                    // Maximum blocks randomly selected to destroy
    private static final int BLOCK_DESTROY_SPREAD_TICKS = 3;                // Number of ticks to spread block destruction across
    private Queue<BlockPos> pendingDestruction = new ArrayDeque<>();
    private int destructionSpreadTickCounter = 0;                           // Ticks remaining for this destruction round
    private final Map<Integer, Integer> damageCooldowns = new HashMap<>();  // Tracks cooldowns for entity damage

    private static final EntityDataAccessor<Float> DATA_BLACK_HOLE_SIZE = SynchedEntityData.defineId(BlackHole.class, EntityDataSerializers.FLOAT);
    private boolean hasReachedMaxSize = false;

    public BlackHole(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if (wasFired) {
            if (!level().isClientSide) {
                if (getMass() >= MAX_MASS) {
                    hasReachedMaxSize = true;
                    handleDecay();
                } else {
                    handleMoving();
                }
                handleEating();
                handleFusion();

                if (getMass() <= MIN_MASS) {
                    triggerCollapseEffect();
                    WildDungeons.getLogger().warn("BLACK HOLE DESTROYED!");
                    discard();
                    return;
                }
            }
        }
        super.tick();
    }

    private void handleMoving() {
        if (hasReachedMaxSize) {
            mass = Mth.clamp(getMass() - SHRINK_RATE, MIN_MASS, MAX_MASS);
            setMass(mass);
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        mass = Mth.clamp(getMass() - SHRINK_RATE, MIN_MASS, MAX_MASS);
        setMass(mass);

        float baseSpeed = 0.25f;
        float minSpeed = 0.02f;
        float speed = Mth.clamp(baseSpeed - (mass * 0.01f), minSpeed, baseSpeed);

        Vec3 motion = firedDirection.normalize().scale(speed);

        BlackHole biggestNearby = null;
        float biggestSize = -1f;
        for (Entity entity : level().getEntities(this, new AABB(blockPosition()).inflate(getMass() * RADIUS_OUTER_SCALE * 2))) {
            if (entity instanceof BlackHole other && other != this && other.getMass() > biggestSize) {
                biggestSize = other.getMass();
                biggestNearby = other;
            }
        }

        if (biggestNearby != null && !getDeltaMovement().equals(Vec3.ZERO)) {
            Vec3 toTarget = biggestNearby.position().subtract(position()).normalize();
            float sizeRatio = Mth.clamp((biggestNearby.getMass() - getMass()) / MAX_MASS, 0f, 1f);
            float lerpFactor = Mth.lerp(sizeRatio, DIRECTION_LERP_MIN, DIRECTION_LERP_MAX);
            Vec3 blendedDirection = firedDirection.normalize().lerp(toTarget, lerpFactor).normalize();
            firedDirection = blendedDirection;
            motion = blendedDirection.scale(speed);
        }

        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
//        setDeltaMovement(motion);//this does nothing because this movement calculation is not vanilla friendly
    }

    private void handleFusion() {
        List<Entity> nearby = level().getEntities(this, new AABB(blockPosition()).inflate(getMass() * RADIUS_INNER_SCALE));
        for (Entity entity : nearby) {
            if (entity instanceof BlackHole other && other != this) {
                if (this.getMass() > other.getMass()) {
                    float drain = Math.min(0.01f, other.getMass());
                    other.setMass(other.getMass() - drain);
                    this.setMass(this.getMass() + drain);

                    if (other.getMass() <= MIN_MASS) {
                        other.triggerCollapseEffect();
                        other.discard();
                        WildDungeons.getLogger().info("Black hole fused and vanished.");
                    }
                }
            }
        }
    }

    private void handleDecay() {
        if (decayCooldownTicks > 0) decayCooldownTicks--;

        float decayAmount = BASE_DECAY_RATE;

        if (getMass() >= MAX_MASS) {
            float cooldownProgress = 1.0f - (decayCooldownTicks / (float) DECAY_GRACE_PERIOD_TICKS);
            float decayMultiplier = Mth.lerp(cooldownProgress, 1.0f, MAX_DECAY_MULTIPLIER);
            decayAmount *= decayMultiplier;
        }

        setMass(getMass() - decayAmount);
    }

    private void handleEating() {
        damageCooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));

        handleBlockDestruction();
        handleEntityPullAndConsumption();
    }

    private void handleEntityPullAndConsumption() {
        Vec3 pullCenter = position().add(0, 0.75, 0);
        double outerRadius = getMass() * RADIUS_OUTER_SCALE;
        double innerRadius = getMass() * RADIUS_INNER_SCALE;
        double outerDistSq = outerRadius * outerRadius;

        for (Entity target : level().getEntities(this, getBoundingBox().inflate(outerRadius))) {
            if (target == this) continue;

            Vec3 toTarget = pullCenter.subtract(target.position());
            Vec3 pullDir = toTarget.normalize();
            Vec3 motion = target.getDeltaMovement();
            double distSq = toTarget.lengthSqr();
            double normDist = distSq / outerDistSq;

            if (distSq <= innerRadius * innerRadius) {
                consumeTarget(target);
            } else {
                applyPullForce(target, motion, pullDir, toTarget, normDist);
            }

            if (target instanceof LivingEntity living) {
                living.hurtMarked = true;
            }
        }
    }

    private void consumeTarget(Entity target) {
        if (target instanceof BlackHole other && other.getMass() < getMass()) {
            float siphonAmount = 0.01f;
            float newOtherSize = Math.max(MIN_MASS, other.getMass() - siphonAmount);
            float siphoned = other.getMass() - newOtherSize;
            other.setMass(newOtherSize);
            setMass(getMass() + siphoned);
            decayCooldownTicks = DECAY_GRACE_PERIOD_TICKS;
        } else if (target instanceof LivingEntity living) {
            int id = living.getId();
            if (damageCooldowns.getOrDefault(id, 0) <= 0) {
                float damage = Mth.lerp((getMass() - MIN_MASS) / (MAX_MASS - MIN_MASS), MIN_DAMAGE, MAX_DAMAGE);
                living.hurt(blackHoleDamage(level().damageSources()), damage);
                onEatThing();
                damageCooldowns.put(id, ENTITY_DAMAGE_COOLDOWN_TICKS);
            }
        } else {
            target.discard();
            onEatThing();
        }
    }

    private void applyPullForce(Entity target, Vec3 motion, Vec3 pullDir, Vec3 toTarget, double normDist) {
        double clampedNormDist = Mth.clamp(normDist, 0.0, 1.0);
        double sizeFactor = (getMass() - MIN_MASS) / (MAX_MASS - MIN_MASS);
        double baseStrength = Math.max(0, sizeFactor * PULL_BASE_MULTIPLIER * (1.0 - Math.pow(clampedNormDist, PULL_FALLOFF_EXPONENT)));
        double pullStrength = Mth.clamp(baseStrength, MIN_PULL_STRENGTH, MAX_PULL_STRENGTH);
        Vec3 pullVelocity = pullDir.scale(pullStrength);

        if (isForceToward(pullVelocity, toTarget)) {
            // Apply velocity with limits for specific entity types
            if (target instanceof FallingBlockEntity || target instanceof ItemEntity) {
                // Get current velocity after adding pull force
                Vec3 newVelocity = target.getDeltaMovement().add(pullVelocity);
                // Limit maximum speed for these entities
                double maxSpeed = 0.5;
                if (newVelocity.lengthSqr() > maxSpeed * maxSpeed) {
                    newVelocity = newVelocity.normalize().scale(maxSpeed);
                }
                target.setDeltaMovement(newVelocity);
            } else {
                target.addDeltaMovement(pullVelocity);
            }
        }
    }

    private void handleBlockDestruction() {
        BlockPos center = blockPosition();
        double outerRadius = getMass() * RADIUS_OUTER_SCALE;
        double innerRadius = getMass() * RADIUS_INNER_SCALE;
        double outerDistSq = outerRadius * outerRadius;

        if (pendingDestruction.isEmpty()) {
            int blocksToSample = (int) Mth.lerp((getMass() - MIN_MASS) / (MAX_MASS - MIN_MASS), MIN_BLOCKS_DESTROYED, MAX_BLOCKS_DESTROYED);
            int maxRadius = Mth.ceil(outerRadius);

            // Start from center and expand outward in shells
            for (int r = 0; r <= maxRadius && (long) pendingDestruction.size() < blocksToSample; r++) {
                // Process each shell from the inside out
                for (int x = -r; x <= r && (long) pendingDestruction.size() < blocksToSample; x++) {
                    for (int y = -r; y <= r && (long) pendingDestruction.size() < blocksToSample; y++) {
                        for (int z = -r; z <= r && (long) pendingDestruction.size() < blocksToSample; z++) {
                            // Only consider blocks on the current shell
                            if (Math.abs(x) == r || Math.abs(y) == r || Math.abs(z) == r) {
                                BlockPos pos = center.offset(x, y, z);
                                if (pos.distSqr(center) <= outerRadius * outerRadius && !level().isEmptyBlock(pos) && !pendingDestruction.contains(pos) && !level().getBlockState(pos).is(Blocks.BEDROCK)) {
                                    pendingDestruction.add(pos);
                                }
                            }
                        }
                    }
                }
            }

            destructionSpreadTickCounter = BLOCK_DESTROY_SPREAD_TICKS;
        }

        int blocksThisTick = (int) Math.ceil((double) pendingDestruction.size() / destructionSpreadTickCounter);
        for (int i = 0; i < blocksThisTick && !pendingDestruction.isEmpty(); i++) {
            destroyBlockIfValid(pendingDestruction.poll(), center, innerRadius, outerDistSq);
        }

        if (destructionSpreadTickCounter > 0) destructionSpreadTickCounter--;
        if (destructionSpreadTickCounter <= 0) destructionSpreadTickCounter = 1;
    }

    private void destroyBlockIfValid(BlockPos pos, BlockPos center, double innerRadius, double outerDistSq) {
        if (pos == null || level().isEmptyBlock(pos)) return;

        BlockState state = level().getBlockState(pos);
        FluidState fluid = state.getFluidState();
        double distSq = pos.distSqr(center);
        double normDist = distSq / outerDistSq;
        double chance = Mth.clamp(1.0 - Math.pow(normDist, 2.2), 0.05, 1.0);

        float destroySpeed = state.getDestroySpeed(level(), pos);
        if (destroySpeed < 0) return; // Indestructible block like bedrock

        if (distSq <= innerRadius * innerRadius || (random.nextDouble() < chance && state.getDestroySpeed(level(), pos) >= 0)) {
            if (!fluid.isEmpty()) {
                level().setBlockAndUpdate(pos, Fluids.EMPTY.defaultFluidState().createLegacyBlock());
                level().addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.1, 0);
            } else {
                level().levelEvent(2001, pos, Block.getId(state));
                FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level(), pos, state);
                level().destroyBlock(pos, false);
//                onEatThing();
            }
        }
    }

    private boolean isForceToward(Vec3 force, Vec3 toCenter) {
        return force.normalize().dot(toCenter.normalize()) > 0.0;
    }

    private void flashAbsorbEffect(BlockPos pos) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLASH,
                    pos.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.3,
                    pos.getY() + 0.5 + (serverLevel.random.nextDouble() * 0.3),
                    pos.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.3,
                    1,
                    0, 0, 0, 0
            );
        }
    }

    private void triggerCollapseEffect() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLASH,
                    getX(), getY(), getZ(),
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    public static DamageSource blackHoleDamage(DamageSources sources) {
        return sources.source(WDDamageTypes.BLACKHOLE.getKey());
    }

    public void onEatThing() {
        float newSize = getMass() + GROWTH_PER_CONSUME;
        setMass(newSize);
        decayCooldownTicks = DECAY_GRACE_PERIOD_TICKS;
    }

    public void setFiredDirectionAndSpeed(Vec3 newDirection, float newSpeed) {
        super.setFiredDirectionAndSpeed(newDirection, newSpeed);
        setDeltaMovement(firedDirection.scale(newSpeed * 0.2));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLACK_HOLE_SIZE, 1.0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    public float getMass() {
        return this.entityData.get(DATA_BLACK_HOLE_SIZE);
    }

    public void setMass(float mass) {
        this.entityData.set(DATA_BLACK_HOLE_SIZE, Mth.clamp(mass, MIN_MASS, MAX_MASS + 1.0f));
    }
}
