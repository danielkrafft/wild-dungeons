package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class Laserbeam extends Projectile {
    private static final EntityDataAccessor<String>
            OWNER = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float>
            YAW = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.FLOAT),
            PITCH = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.FLOAT),
            RADIUS = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.FLOAT),
            LENGTH = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.FLOAT),
            DAMAGE = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.FLOAT),
            RANGE = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer>
            LIFETIMEAFTERHIT = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.INT),
            CHARGETIME = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean>
            HIT = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.BOOLEAN),
            EXPLOSION = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.BOOLEAN),
            FIRE_DEBRIS = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float>
            EXPLOSION_RADIUS = SynchedEntityData.defineId(Laserbeam.class, EntityDataSerializers.FLOAT);

    public Laserbeam(Player player, Vec3 pos, float yaw, float pitch, float damage, float radius, float range, boolean explosion, float explosionradius, boolean fire_debris, int chargeTime) {
        this(WDEntities.LASER_BEAM.get(), player.level());
        moveTo(pos);
        setRotation(yaw, pitch);
        setPlayer(player);
        setRadius(radius);
        setLength(0);
        setDamage(damage);
        setRange(range);
        setChargeTime(chargeTime);
        if (explosion) {
            setExplosion(true);
            setFireDebris(fire_debris);
            setExplosionRadius(explosionradius);
        }
    }

    public Laserbeam(EntityType<? extends Laserbeam> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER, "");
        builder.define(YAW, 0f);
        builder.define(PITCH, 0f);
        builder.define(RADIUS, 0f);
        builder.define(LENGTH, 0f);
        builder.define(DAMAGE, 0f);
        builder.define(RANGE, 0f);
        builder.define(LIFETIMEAFTERHIT, 0);
        builder.define(HIT, false);
        builder.define(EXPLOSION, false);
        builder.define(FIRE_DEBRIS, false);
        builder.define(EXPLOSION_RADIUS, 0f);
        builder.define(CHARGETIME, 1);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("OwnerUUID")) getEntityData().set(OWNER, tag.getString("OwnerUUID"));
        if (tag.contains("Yaw")) getEntityData().set(YAW, tag.getFloat("Yaw"));
        if (tag.contains("Pitch")) getEntityData().set(PITCH, tag.getFloat("Pitch"));
        if (tag.contains("BeamRadius")) getEntityData().set(RADIUS, tag.getFloat("BeamRadius"));
        if (tag.contains("BeamLength")) getEntityData().set(LENGTH, tag.getFloat("BeamLength"));
        if (tag.contains("BeamDamage")) getEntityData().set(DAMAGE, tag.getFloat("BeamDamage"));
        if (tag.contains("BeamRange")) getEntityData().set(RANGE, tag.getFloat("BeamRange"));
        if (tag.contains("BeamCounter")) getEntityData().set(LIFETIMEAFTERHIT, tag.getInt("BeamCounter"));
        if (tag.contains("Explosion")) getEntityData().set(EXPLOSION, tag.getBoolean("Explosion"));
        if (tag.contains("FireDebris")) getEntityData().set(FIRE_DEBRIS, tag.getBoolean("FireDebris"));
        if (tag.contains("ExplosionRadius")) getEntityData().set(EXPLOSION_RADIUS, tag.getFloat("ExplosionRadius"));
        if (tag.contains("ChargeTime")) getEntityData().set(CHARGETIME, tag.getInt("ChargeTime"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("OwnerUUID", getEntityData().get(OWNER));
        tag.putFloat("Yaw", getEntityData().get(YAW));
        tag.putFloat("Pitch", getEntityData().get(PITCH));
        tag.putFloat("BeamRadius", getEntityData().get(RADIUS));
        tag.putFloat("BeamLength", getEntityData().get(LENGTH));
        tag.putFloat("BeamDamage", getEntityData().get(DAMAGE));
        tag.putFloat("BeamRange", getEntityData().get(RANGE));
        tag.putInt("BeamCounter", getEntityData().get(LIFETIMEAFTERHIT));
        tag.putBoolean("Explosion", getEntityData().get(EXPLOSION));
        tag.putBoolean("FireDebris", getEntityData().get(FIRE_DEBRIS));
        tag.putFloat("ExplosionRadius", getEntityData().get(EXPLOSION_RADIUS));
        tag.putInt("ChargeTime", getEntityData().get(CHARGETIME));
    }

    private static final float distanceStep = 10f;

    @Override
    public void tick() {
        super.tick();
        if (getPlayerUUID() == null) {
            discard();
        } else {
            setRot(getYaw(), getPitch());
            if (tickCount >= getChargeTime() * 20) {
                if (tickCount == getChargeTime() * 20) {
                    for (int i = 0; i < 50; i++)
                        this.level().addAlwaysVisibleParticle(ParticleTypes.SOUL_FIRE_FLAME, position().x, position().y, position().z, 0.2, 0.2, 0.2);
                    for (int i = 0; i < 250; i++)
                        this.level().addAlwaysVisibleParticle(ParticleTypes.POOF, position().x, position().y, position().z, 0.5, 0.5, 0.5);
                    this.level().addAlwaysVisibleParticle(ParticleTypes.FLASH, position().x, position().y, position().z, 0, 0, 0);

                    this.level().playSound(null, blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.2f, 0.5f);
                    this.level().playSound(null, blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS, 1.2f, 0.85f);
                    this.level().playSound(null, blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 1.2f, 0.5f);
                }
                Player playerOwner = getPlayer();
                for (float i = 0; i <= getLength() + distanceStep; i += 0.75f) {
                    Vec3 dd = MathUtil.displaceVector(i, position(), getYRot(), getXRot());
                    if (getDamage() > 0) {
                        for (LivingEntity li : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(dd.subtract(getRadius(), getRadius(), getRadius()), dd.add(getRadius(), getRadius(), getRadius())), li -> !li.equals(playerOwner))) {
                            if (li != null && li.isAlive())
                                li.hurt(this.level().damageSources().indirectMagic(this, playerOwner), getDamage());
                        }
                    }
                    if (!this.level().isEmptyBlock(new BlockPos((int) dd.x, (int) dd.y, (int) dd.z))) {
                        Vec3 disp = MathUtil.displaceVector(i - 0.75f, position(), getYRot(), getXRot());
                        setLength(i);
                        if (!getHit()) {
                            setHit(true);
                            this.level().playSound(null, new BlockPos((int) disp.x, (int) disp.y, (int) disp.z), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.2f, 0.5f);
                            if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !this.level().isClientSide && getExplosion() && getExplosionRadius() > 0) {
                                float radius = getExplosionRadius();
                                boolean debris = getFireDebris();

                                Explosion explosion = new Explosion(
                                        this.level(),
                                        playerOwner,
                                        disp.x,
                                        disp.y,
                                        disp.z,
                                        radius,
                                        debris,
                                        debris ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP
                                );
                                explosion.explode();
                                List<BlockPos> blocks = explosion.getToBlow();
                                if (debris && blocks.size() > 0) {
                                    int d = (int) (blocks.size() / 1250f);
                                    if (d < 1) d = 1;
                                    for (int c = 0; c < blocks.size(); c += d) {
                                        BlockPos block = blocks.get(c);
                                        FallingBlockEntity b = FallingBlockEntity.fall(this.level(), block, this.level().getBlockState(block));
                                        b.setDeltaMovement(new Vec3(RandomUtil.sample(0.5) ? 0.2 : -0.2, Math.min(2, radius / 10f), RandomUtil.sample(0.5f) ? 0.2 : -0.2));
                                        b.dropItem = true;

                                        this.level().setBlock(block, Blocks.AIR.defaultBlockState(), 0);
                                    }
                                }
                                explosion.finalizeExplosion(true);
                            }
                        }
                        break;
                    }
                }
                if (getHit()) {
                    if (getLifetimeAfterHit() >= getMaxLifetimeAfterHit()) discard();
                    else setLifetimeAfterHit(getLifetimeAfterHit() + 1);
                } else {
                    setLength(getLength() + distanceStep);
                    if (getLength() >= getRange()) setHit(true);
                }
            }
        }
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean canCollideWith(@NotNull Entity en) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    //getters and setters
    public void setPlayer(Player p) {
        getEntityData().set(OWNER, p.getStringUUID());
    }

    public UUID getPlayerUUID() {
        return (getEntityData().get(OWNER).isEmpty()) ? null : UUID.fromString(getEntityData().get(OWNER));
    }

    public Player getPlayer() {
        return getPlayerUUID() == null ? null : this.level().getPlayerByUUID(getPlayerUUID());
    }

    public void setRotation(float yaw, float pitch) {
        getEntityData().set(YAW, yaw);
        getEntityData().set(PITCH, pitch);
    }

    public float getYaw() {
        return getEntityData().get(YAW);
    }

    public float getPitch() {
        return getEntityData().get(PITCH);
    }

    public void setRadius(float radius) {
        getEntityData().set(RADIUS, Math.max(radius, 0));
    }

    public float getRadius() {
        return getEntityData().get(RADIUS);
    }

    public void setLength(float length) {
        getEntityData().set(LENGTH, Math.max(length, 0));
    }

    public float getLength() {
        return getEntityData().get(LENGTH);
    }

    public void setDamage(float damage) {
        getEntityData().set(DAMAGE, Math.max(damage, 0));
    }


    public float getDamage() {
        return getEntityData().get(DAMAGE);
    }

    public void setRange(float range) {
        getEntityData().set(RANGE, Math.max(range, 0));
    }

    public float getRange() {
        return getEntityData().get(RANGE);
    }

    public void setLifetimeAfterHit(int i) {
        getEntityData().set(LIFETIMEAFTERHIT, i);
    }

    public int getLifetimeAfterHit() {
        return getEntityData().get(LIFETIMEAFTERHIT);
    }

    public void setHit(boolean b) {
        getEntityData().set(HIT, b);
    }

    public boolean getHit() {
        return getEntityData().get(HIT);
    }

    public void setExplosion(boolean b) {
        getEntityData().set(EXPLOSION, b);
    }

    public boolean getExplosion() {
        return getEntityData().get(EXPLOSION);
    }

    public void setFireDebris(boolean b) {
        getEntityData().set(FIRE_DEBRIS, b);
    }

    public boolean getFireDebris() {
        return getEntityData().get(FIRE_DEBRIS);
    }

    public void setExplosionRadius(float explosionRadius) {
        getEntityData().set(EXPLOSION_RADIUS, Math.max(explosionRadius, 0));
    }

    public float getExplosionRadius() {
        return getEntityData().get(EXPLOSION_RADIUS);
    }

    public void setChargeTime(int chargeTime) {
        getEntityData().set(CHARGETIME, Math.max(chargeTime, 0));
    }

    public int getChargeTime() {
        return getEntityData().get(CHARGETIME);
    }

    public int getMaxLifetimeAfterHit() {
        return 30;
    }
}
