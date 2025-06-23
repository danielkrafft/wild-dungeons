package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class BlackHole extends Entity {

    // How often the black hole updates its state. Every X ticks. Higher numbers mean less frequent updates, but more performance friendly.
    // Makes the black hole feel bad when you make the number too high
    private static final int PHYSICS_UPDATE_TICK_RATE = 2;
    // Core size logic
    private float mass = 1.0f;
    protected Vec3 firedDirection = Vec3.ZERO;
    protected float initialSpeed = 0.0f;

    private static final float MIN_MASS = 0.1f;                             // Minimum black hole size before death
    private static final float MAX_MASS = 50f;                               // Maximum black hole size before advanced decay begins
    private static final float GROWTH_PER_CONSUME = 0.02f;                  // Mass increase when consuming blocks/entities
    private static final float SHRINK_RATE = 0.999f;                        // Shrink per tick
    private static final float BASE_DECAY_RATE = 0.001f;                    // Base decay rate once max size is hit
    private static final float MAX_DECAY_MULTIPLIER = 1000f;                 // Max scaling of decay based on cooldown progress
    private static final int DECAY_GRACE_PERIOD_TICKS = 10;                 // Ticks before decay begins after hitting max size
    private int decayCooldownTicks = 0;                                     // Tracks remaining grace period ticks

    // Interaction and pull behavior
    private static final double RADIUS_OUTER_SCALE = 3;                   // Outer interaction radius multiplier
    private static final double RADIUS_INNER_SCALE = .5;                    // Inner kill/damage radius multiplier
    private static final int MIN_DAMAGE = 1;                                // Minimum damage to entities in range
    private static final int MAX_DAMAGE = 10;                               // Maximum damage scaled by mass
    private static final double MIN_PULL_STRENGTH = 0.1;                   // Minimum gravitational pull strength
    private static final double MAX_PULL_STRENGTH = .5;                      // Maximum gravitational pull strength
    private static final double PULL_BASE_MULTIPLIER = 0.5;                   // Scaling multiplier for pull strength
    private static final double PULL_FALLOFF_EXPONENT = 50;                  // How fast pull drops off with distance
    private static final float DIRECTION_LERP_MIN = 0.1f;                   // Minimum influence when smaller black hole redirects
    private static final float DIRECTION_LERP_MAX = 0.25f;                  // Maximum influence when smaller black hole redirects
    
    // New orbital movement parameters
    private static final double ORBITAL_STRENGTH_MIN = 0.02;                // Minimum orbital force strength
    private static final double ORBITAL_STRENGTH_MAX = 0.10;                // Maximum orbital force strength
    private static final double ORBITAL_DISTANCE_FACTOR = 0.6;              // How distance affects orbital force
    private static final double ORBITAL_MASS_FACTOR = 0.15;                 // How black hole mass affects orbital force

    // Entity and block destruction
    private static final int ENTITY_DAMAGE_COOLDOWN_TICKS = 10;             // Per-entity cooldown for repeated damage
    private static final int MIN_BLOCKS_DESTROYED = 10;                      // Minimum blocks randomly selected to destroy
    private static final int MAX_BLOCKS_DESTROYED = 100;                    // Maximum blocks randomly selected to destroy
    private static final int BLOCK_DESTROY_SPREAD_TICKS = 10;                // Number of ticks to spread block destruction across
    private Queue<BlockPos> pendingDestruction = new ArrayDeque<>();
    private int destructionSpreadTickCounter = 0;                           // Ticks remaining for this destruction round
    private Map<Integer, Integer> damageCooldowns = new HashMap<>();  // Tracks cooldowns for entity damage

    private static final EntityDataAccessor<Float> DATA_BLACK_HOLE_SIZE = SynchedEntityData.defineId(BlackHole.class, EntityDataSerializers.FLOAT);
    private boolean hasReachedMaxSize = false;

    public BlackHole(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
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
        super.tick();
    }

    private void handleMoving() {
        if (hasReachedMaxSize) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        mass = Mth.clamp(getMass() * SHRINK_RATE, MIN_MASS, MAX_MASS);
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

    private void applyPullForce(Entity target, Vec3 pullDir, Vec3 toTarget, double normDist) {
        double clampedNormDist = Mth.clamp(normDist, 0.0, 1.0);
        double sizeFactor = (getMass() - MIN_MASS) / (MAX_MASS - MIN_MASS);

        // Smoother pull strength calculation
        double distanceFactor = Math.exp(-PULL_FALLOFF_EXPONENT * clampedNormDist);
        double baseStrength = Math.max(0, sizeFactor * PULL_BASE_MULTIPLIER * distanceFactor);
        double pullStrength = Mth.clamp(baseStrength, MIN_PULL_STRENGTH, MAX_PULL_STRENGTH);

        // Calculate direct pull force
        Vec3 directPull = pullDir.scale(pullStrength);

        // Calculate orbital/tangential force
        // Cross product with UP vector to get perpendicular direction in horizontal plane
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 orbitalDir;

        // Handle special case for vertical alignment
        if (Math.abs(pullDir.x) < 0.001 && Math.abs(pullDir.z) < 0.001) {
            // Use a default orbital direction if pull is directly up/down
            orbitalDir = new Vec3(1, 0, 0);
        } else {
            Vec3 horizontalPullDir = new Vec3(pullDir.x, 0, pullDir.z).normalize();
            orbitalDir = horizontalPullDir.cross(up);
        }

        // Scale orbital force inversely with distance (stronger as you get closer)
        double orbitalFactor = Math.pow(1.0 - clampedNormDist, ORBITAL_DISTANCE_FACTOR) * sizeFactor * ORBITAL_MASS_FACTOR;
        double orbitalStrength = Mth.clamp(orbitalFactor, ORBITAL_STRENGTH_MIN, ORBITAL_STRENGTH_MAX);
        Vec3 orbitalForce = orbitalDir.scale(orbitalStrength);

        // Combine forces (direct pull + orbital)
        Vec3 totalForce = directPull.add(orbitalForce);

        if (isForceToward(totalForce, toTarget) || toTarget.lengthSqr() < getMass() * 1.5) {
            double momentumFactor = 0.25;
            Vec3 invertedPreservedMotion = target.getDeltaMovement().scale(-momentumFactor);

            if (target instanceof Player){
                target.addDeltaMovement(totalForce.scale(.25f));
            } else {
                target.addDeltaMovement(totalForce);
                target.addDeltaMovement(invertedPreservedMotion);
            }
            // Mark the entity as needing physics update
            if (tickCount % PHYSICS_UPDATE_TICK_RATE == 0 || target instanceof Player)
                target.hurtMarked = true;//necessary or players wont move, and entities will appear jittery
        }
    }

    private void handleEntityPullAndConsumption() {
        Vec3 pullCenter = position().add(0, 0.75, 0);
        double outerRadius = getMass() * RADIUS_OUTER_SCALE * 2f;
        double innerRadius = getMass() * RADIUS_INNER_SCALE;
        double outerDistSq = outerRadius * outerRadius;

        for (Entity target : level().getEntities(this, getBoundingBox().inflate(outerRadius))) {
            if (target == this) continue;

            Vec3 toTarget = pullCenter.subtract(target.position());
            Vec3 pullDir = toTarget.normalize();
            double distSq = toTarget.lengthSqr();
            double normDist = distSq / outerDistSq;

            if (distSq <= innerRadius * innerRadius) {
                consumeTarget(target);
            } else {
                applyPullForce(target, pullDir, toTarget, normDist);
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
                FallingBlockEntity.fall(level(), pos, state);
                level().destroyBlock(pos, false);
            }
        }
    }

    private boolean isForceToward(Vec3 force, Vec3 toCenter) {
        // More lenient check to allow some orbital movement while still ensuring
        // entities don't escape the pull radius
        double alignment = force.normalize().dot(toCenter.normalize());
        double distanceThreshold = toCenter.length() / (getMass() * RADIUS_OUTER_SCALE);
        
        // Allow more tangential movement when further away
        return alignment > -0.3 || distanceThreshold < 0.5;
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
        double sizeFactor = 1.1 - Math.clamp((getMass() - MIN_MASS) / (MAX_MASS - MIN_MASS),0,1);
        float newSize = (float) (getMass() + (GROWTH_PER_CONSUME * sizeFactor));
        setMass(newSize);
        decayCooldownTicks = DECAY_GRACE_PERIOD_TICKS;
    }

    public void setFiredDirectionAndSpeed(Vec3 newDirection, float newSpeed) {
        firedDirection = newDirection;
        initialSpeed = newSpeed;
        setDeltaMovement(firedDirection.scale(newSpeed * 0.2));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLACK_HOLE_SIZE, 1.0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.mass = tag.contains("mass") ? tag.getFloat("mass") : 1.0f;
        decayCooldownTicks = 0;
        pendingDestruction = new ArrayDeque<>();
        destructionSpreadTickCounter = 0;                           // Ticks remaining for this destruction round
        damageCooldowns = new HashMap<>();  // Tracks cooldowns for entity damage
        hasReachedMaxSize = tag.getBoolean("hasReachedMaxSize");
        firedDirection = new Vec3(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
        initialSpeed = tag.getFloat("initialSpeed");
        setMass(mass);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("mass", this.mass);
        tag.putBoolean("hasReachedMaxSize", this.hasReachedMaxSize);
        tag.putFloat("x", (float) this.firedDirection.x);
        tag.putFloat("y", (float) this.firedDirection.y);
        tag.putFloat("z", (float) this.firedDirection.z);
        tag.putFloat("initialSpeed", this.initialSpeed);
    }

    public float getMass() {
        return this.entityData.get(DATA_BLACK_HOLE_SIZE);
    }

    public void setMass(float mass) {
        this.entityData.set(DATA_BLACK_HOLE_SIZE, Mth.clamp(mass, MIN_MASS, MAX_MASS + 1.0f));
    }
}
