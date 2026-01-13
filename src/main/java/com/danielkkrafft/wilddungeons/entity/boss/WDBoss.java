package com.danielkkrafft.wilddungeons.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public abstract class WDBoss extends Monster implements GeoEntity {
    protected final ServerBossEvent bossEvent;

    private static final EntityDataAccessor<Integer> TICKS_INVULNERABLE = SynchedEntityData.defineId(WDBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LOCOMOTION = SynchedEntityData.defineId(WDBoss.class, EntityDataSerializers.INT);
    protected int summonTicks = 50; // common override
    protected boolean attacking;

    public enum Locomotion {
        AERIAL,
        TERRESTRIAL,
        AQUATIC
    }

    protected WDBoss(EntityType<? extends Monster> type, Level level,
                     BossEvent.BossBarColor color,
                     BossEvent.BossBarOverlay overlay) {
        super(type, level);
        this.bossEvent = new ServerBossEvent(getDisplayName(), color, overlay);
    }

    protected void updateBossBar() {
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
    }

    /* -- locomotion (mode of transport) -- */

    protected void applyLocomotion(Locomotion mode) {
        this.navigation.stop();

        this.setNoGravity(mode == Locomotion.AERIAL);

        this.moveControl = createMoveControlFor(mode);
        this.navigation  = createNavigationFor(mode, level());
    }

    protected Locomotion defaultLocomotion() { return Locomotion.TERRESTRIAL; }

    public final Locomotion getLocomotion() {
        return Locomotion.values()[entityData.get(LOCOMOTION)];
    }

    public final void setLocomotion(Locomotion mode) {
        if (mode == getLocomotion()) return;
        entityData.set(LOCOMOTION, mode.ordinal());
        applyLocomotion(mode);
    }

    protected PathNavigation createAerialPath(Level level) {
        FlyingPathNavigation path = new FlyingPathNavigation(this, level);
        path.setCanFloat(true);
        path.setCanPassDoors(true);
        return path;
    }

    // splitting these out into their own methods like this makes it overridable so we can still have custom move controllers like the nether dragon's
    protected MoveControl createMoveControlFor(Locomotion mode) {
        return switch (mode) {
            case AERIAL -> new FlyingMoveControl(this, 10, false);
            case AQUATIC, TERRESTRIAL -> new MoveControl(this);
        };
    }

    protected PathNavigation createNavigationFor(Locomotion mode, Level level) {
        return switch (mode) {
            case AERIAL -> createAerialPath(level);
            case AQUATIC, TERRESTRIAL -> new GroundPathNavigation(this, level);
        };
    }

    @Override
    public void tick() {
        super.tick();

        if (getLocomotion() == Locomotion.AERIAL) {
            this.setNoGravity(true);
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return createNavigationFor(getLocomotion(), level);
    }

    /* -- saves/data -- */

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TICKS_INVULNERABLE, 0);
        builder.define(LOCOMOTION, defaultLocomotion().ordinal());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("InvulnerableTicks", getTicksInvulnerable());
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
        saveBossData(compound);
    }
    
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setTicksInvulnerable(compound.getInt("InvulnerableTicks"));
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
        loadBossData(compound);
    }

    protected void saveBossData(CompoundTag compound) {}

    protected void loadBossData(CompoundTag compound) {}

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(LOCOMOTION)) {
            applyLocomotion(getLocomotion());
        }
    }

    /* -- sounds -- */

    public record BossSounds(
            @Nullable SoundEvent ambient,
            @Nullable SoundEvent hurt,
            @Nullable SoundEvent death
    ) { public static final BossSounds DEFAULT = new BossSounds(null, null, null); }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected BossSounds bossSounds() {
        return BossSounds.DEFAULT;
    }

    @Override
    protected final @NotNull SoundEvent getAmbientSound() {
        return (bossSounds().ambient() != null) ? bossSounds().ambient() : null;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return (bossSounds().hurt() != null) ? bossSounds().hurt() : super.getHurtSound(source);
    }

    @Override
    protected final @NotNull SoundEvent getDeathSound() {
        return (bossSounds().death() != null) ? bossSounds().death() : super.getDeathSound();
    }

    /* -- summon goal -- */

    public boolean isInSummonPhase() {
        return getTicksInvulnerable() <= summonTicks;
    }

    protected void tickSummonPhase() {
        int ticks = getTicksInvulnerable();
        setTicksInvulnerable(ticks + 1);
        if (ticks % 10 == 0) {
            playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 2f, 2f);
        }
    }

    protected void startSummonPhase() {
        summonAnimation();
        playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 2f, 0.7f);
        setInvulnerable(true);
        bossEvent.setVisible(false);
    }

    protected void endSummonPhase() {
        performSummonExplosion();
        setInvulnerable(false);
        bossEvent.setVisible(true);
    }

    protected void performSummonExplosion() {
        Vec3 pos = position();
        List<LivingEntity> list = level().getEntitiesOfClass(
                LivingEntity.class,
                AABB.ofSize(position(), 10, 10, 10),
                this::hasLineOfSight
        );

        for (LivingEntity li : list) {
            if (li == this) continue; // Don't affect self
            Vec3 kb = new Vec3(pos.x - li.position().x, pos.y - li.position().y,
                    pos.z - li.position().z).normalize().scale(2);
            li.knockback(1.5, kb.x, kb.z);
            li.setRemainingFireTicks(li.getRemainingFireTicks() + 100);
            li.hurt(damageSources().generic(), 10);
        }

        playSound(SoundEvents.GENERIC_EXPLODE.value(), 2f, 0.8f);
        spawnSummonParticles(pos);
    }

    protected void summonAnimation() {}

    protected void spawnSummonParticles(Vec3 pos) {};

    public static class WDBossSummonGoal extends Goal {
        protected final WDBoss boss;

        public WDBossSummonGoal(WDBoss boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK, Flag.TARGET));
        }
        
        @Override
        public boolean canUse() {
            return boss.isInSummonPhase();
        }
        
        @Override
        public void start() {
            boss.startSummonPhase();
        }
        
        @Override
        public void tick() {
            boss.tickSummonPhase();
        }
        
        @Override
        public void stop() {
            boss.endSummonPhase();
        }
    }

    /* -- timed action (usually attack) goal -- */

    protected int getBossAction() { return 0; }
    protected void setBossAction(int action) {}
    protected int getBossCooldown() { return 0; }
    protected void setBossCooldown(int ticks) {}

    protected abstract class TimedActionGoal extends Goal {
        protected final WDBoss boss;
        protected int t;

        protected TimedActionGoal(WDBoss boss, EnumSet<Flag> flags) {
            this.boss = boss;
            this.setFlags(flags);
        }

        protected abstract int actionId();

        protected abstract int maxTime();

        protected int startCooldown() { return 0; }

        protected void onStart(LivingEntity target) {}

        protected abstract void onTick(LivingEntity target);

        protected void onStop() {}

        @Override
        public boolean canUse() {
            LivingEntity target = boss.getTarget();
            return target != null && target.isAlive() && boss.getBossAction() == actionId();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = boss.getTarget();
            return target != null && target.isAlive() && t < maxTime();
        }

        @Override
        public void start() {
            t = 0;
            boss.getNavigation().stop();
            int cd = startCooldown();
            if (cd > 0) boss.setBossCooldown(cd);

            LivingEntity target = boss.getTarget();
            if (target != null) onStart(target);
        }

        @Override
        public void tick() {
            LivingEntity target = boss.getTarget();
            if (target == null) return;

            t++;
            onTick(target);
        }

        @Override
        public void stop() {
            boss.setBossAction(0);
            boss.getNavigation().stop();
            onStop();
        }
    }

    /* -- random utils -- */

    @Override
    public boolean mayBeLeashed() {
        return false;
    }

    @Override
    public boolean canHaveALeashAttachedToIt() {
        return false;
    }

    protected boolean isImmuneToDamageType(DamageSource source) {
        return false;
    }

    protected float getDamageMultiplier(DamageSource source) {
        return 1.0f;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (isImmuneToDamageType(source)) {
            return false;
        }
        return super.hurt(source, amount * getDamageMultiplier(source));
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        bossEvent.removeAllPlayers();
    }

    public void setTicksInvulnerable(int ticks) {
        entityData.set(TICKS_INVULNERABLE, ticks);
    }

    public int getTicksInvulnerable() {
        return entityData.get(TICKS_INVULNERABLE);
    }

    public boolean isAttacking() {
        return attacking;
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    /* -- animation station -- */

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
