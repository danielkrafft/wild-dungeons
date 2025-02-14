package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

//todo this entire thing is a placeholder clone of the BreezeGolem and crashes the game because it's not at all set up right
public class NetherDragonEntity extends Monster implements RangedAttackMob, GeoEntity
{
    private static final String CONTROLLER="breezegolemcontroller";
    private static final String idle="idle",
            walk="walk",
            coreSpin="core_spin",
            rapidCannonStart="rapid_cannon_start",rapidCannonShoot="rapid_cannon_shoot",
            chargedCannonCharge="charged_canon_charge",chargedCannonShoot="charged_canon_shoot";
    private static final RawAnimation idleAnim=RawAnimation.begin().thenLoop(idle),
            walkAnim=RawAnimation.begin().thenLoop(walk),
            coreSpinAnim=RawAnimation.begin().thenPlay(coreSpin),
            rapidCannonStartAnim=RawAnimation.begin().thenPlay(rapidCannonStart),
            rapidCannonShootAnim=RawAnimation.begin().thenLoop(rapidCannonShoot),
            chargedCannonChargeAnim=RawAnimation.begin().thenLoop(chargedCannonCharge),
            chargedCanonShootAnim=RawAnimation.begin().thenPlay(chargedCannonShoot);
    private final AnimatableInstanceCache cache= GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> TICKSINVULNERABLE = SynchedEntityData.defineId(NetherDragonEntity.class,EntityDataSerializers.INT);
    private static final int SUMMONTICKS = 50;//5s
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS
    );

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,CONTROLLER,2,
                state->state.setAndContinue(state.isMoving()?walkAnim:idleAnim)).
                triggerableAnim(idle,idleAnim).
                triggerableAnim(walk,walkAnim).
                triggerableAnim(coreSpin,coreSpinAnim).
                triggerableAnim(rapidCannonStart,rapidCannonStartAnim).
                triggerableAnim(rapidCannonShoot,rapidCannonShootAnim).
                triggerableAnim(chargedCannonCharge,chargedCannonChargeAnim).
                triggerableAnim(chargedCannonShoot,chargedCanonShootAnim)
        );
    }
    @Override
    public void move(@NotNull MoverType type,@NotNull Vec3 velocity)
    {
        super.move(type,velocity);
        if(velocity.lengthSqr()>0.01)
            if(tickCount%7==0)
                playSound(WDSoundEvents.BREEZE_GOLEM_WALK.value(),0.6f,random.nextFloat()*0.4f+0.8f);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}
    public enum AttackType
    {
        HEAVYCORESPIN(35), RAPIDCANNON(65), CHARGEDCANNON(220);

        AttackType(int duration) {
            this.duration = duration;
        }

        public final int duration;
    }

    //serverside variables
    public int targetTime = 30 * 20;
    public AttackType attackType;
    private LivingEntity currentTarget;
    private int cannonCharge = 1;
    private int attackTicks = 0;
    private boolean attacking;
    private static final float ASCENDRATE=0.15f;
    public NetherDragonEntity(EntityType<? extends Monster> type, Level level)
    {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, false);
        xpReward = 100;
    }

    public static AttributeSupplier setAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 225).
                add(Attributes.MOVEMENT_SPEED, 0.35).
                add(Attributes.FOLLOW_RANGE, 50).
                add(Attributes.ATTACK_DAMAGE, 10).
                add(Attributes.ATTACK_KNOCKBACK, 2).
                add(Attributes.KNOCKBACK_RESISTANCE, 0.4).
                add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2).
                add(Attributes.FLYING_SPEED, 2).
                build();
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new SummonGoal(this));
        goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1, 30));
        goalSelector.addGoal(2, new BreezeGolemAttackGoal(this, 2, 60, 5));
        goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li->!(li instanceof NetherDragonEntity)));
    }
    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation path = new FlyingPathNavigation(this, level);
        path.setCanFloat(false);
        path.setCanPassDoors(true);
        return path;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(TICKSINVULNERABLE,0);
    }
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("InvulnerableTicks", getInvulnerableTicks());
        if (hasCustomName())
        {
            bossEvent.setName(getDisplayName());
        }
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        setInvulnerableTicks(compound.getInt("InvulnerableTicks"));
        if (hasCustomName())
        {
            bossEvent.setName(getDisplayName());
        }
    }
    public void setInvulnerableTicks(int i)
    {
        entityData.set(TICKSINVULNERABLE,i);
    }
    public void addInvulnerableTick()
    {
        setInvulnerableTicks(getInvulnerableTicks()+1);
    }
    public int getInvulnerableTicks()
    {
        return entityData.get(TICKSINVULNERABLE);
    }
    public boolean isInvulnerable()
    {
        return getInvulnerableTicks()<=SUMMONTICKS;
    }
    public boolean isAttacking()
    {
        return attacking;
    }

    public void spawn(EntityType<?> entity, int count) {}



    /**
     * {@link net.minecraft.world.item.FlintAndSteelItem}
     */
    @Override
    public void tick()
    {
        super.tick();
        Level level = level();
        if(isInvulnerable())
        {
            bossEvent.setVisible(false);
        }
        else
        {
            bossEvent.setVisible(true);
            float hp=getHealth()/getMaxHealth();
            bossEvent.setProgress(hp);
            Vec3 pos = position(),vel=getDeltaMovement();
            BlockPos block = blockPosition();
            if(!level.isClientSide&&!isDeadOrDying())
            {
                setRemainingFireTicks(0);
                double diff=-1;
                //inflict fire below
                for (int y = 1; y < 5; y++)
                {
                    BlockPos deltaBlock = block.below(y);
                    List<LivingEntity> nearby = level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, AABB.ofSize(deltaBlock.getCenter(), 1, 1, 1));
                    nearby.forEach(li -> li.setRemainingFireTicks(li.getRemainingFireTicks() + 20));
                    //isFlammable(level, deltaBlock, Direction.UP)
                    BlockState state=level.getBlockState(deltaBlock);
                    if (!state.isAir())
                    {
                        BlockState stateAbove=level.getBlockState(deltaBlock.above());
                        diff=0;
                        if(stateAbove.isAir())
                            level.setBlockAndUpdate(deltaBlock.above(), BaseFireBlock.getState(level,deltaBlock.above()));
                        break;
                    }
                }
                if (currentTarget != null)
                {
                    double targetY=currentTarget.position().y;
                    diff=targetY-pos.y+(new Vec3(currentTarget.position().x-pos.x,0,currentTarget.position().z-pos.z).lengthSqr()<9?0.5:6);
                    //make hover cycle per 5s (100 ticks), 2pi ticks in 100 ticks
                    //below target, ascend | above target, descend
                    if (--targetTime<=0|| currentTarget.isRemoved() || currentTarget.isDeadOrDying()) currentTarget = null;
                }
                if(Math.abs(diff)>0)
                    setDeltaMovement(vel.x,Math.clamp(diff/2,-1,1)*ASCENDRATE,vel.z);
                //setCustomName(Component.literal(attacking + "::" + (currentTarget != null ? currentTarget.getName().getString() : "---") + "=>" + attackType));
                //setCustomNameVisible(true);
                if (attacking)
                {
                    if(currentTarget==null||currentTarget.isRemoved()||currentTarget.isDeadOrDying()||!canAttack(currentTarget))EndAttack();
                    else
                    {
                        switch (attackType)
                        {
                            case HEAVYCORESPIN ->
                            {
                                if(attackTicks>=25&&attackTicks<=29)
                                {
                                    List<LivingEntity> nearby = level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, AABB.ofSize(pos, 10, 5, 10));
                                    nearby.forEach(li ->
                                    {
                                        if(li.hurtTime<=0)
                                        {
                                            Vec3 position=li.position();
                                            UtilityMethods.sendParticles((ServerLevel)level,ParticleTypes.SMALL_FLAME,true,3,position.x,li.getY(0.5),position.z,0,0,0,0.1f);
                                            UtilityMethods.sendParticles((ServerLevel)level,ParticleTypes.CRIT,true,10,position.x,li.getY(0.5),position.z,0.4f,0.4f,0.4f,0);
                                            playSound(WDSoundEvents.HAMMER_SMASH_LIGHT.value(),1f,random.nextFloat()*0.2f+0.8f);
                                            Vec3 kb=new Vec3(pos.x-position.x,pos.y-position.y,pos.z-position.z).
                                                    normalize().scale(1);
                                            li.knockback(1.5,kb.x,kb.z);
                                            li.hurt(level.damageSources().generic(), 10);
                                        }
                                    });
                                }
                            }
                            case RAPIDCANNON ->
                            {
                                if(attackTicks>=10)
                                {
                                    if (attackTicks % 5 == 0)
                                    {
                                        playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_SHOOT.value(),2f,1f);
                                        triggerAnim(CONTROLLER,rapidCannonShoot);
                                        if (!hasLineOfSight(currentTarget))
                                        {
                                            EndAttack();
                                            return;
                                        }
                                        WindChargeProjectile proj = WDEntities.WIND_CHARGE_PROJECTILE.get().create(level);
                                        if (proj != null)
                                        {
                                            Vec3 handPos=getPositionRelative(1,2,2.5);
                                            UtilityMethods.sendParticles((ServerLevel)level,ParticleTypes.CLOUD,true,10,getEyePosition().x,getEyePosition().y,getEyePosition().z,0,0,0,0.06f);
                                            proj.defaultCharge(false, false, new Vec3(currentTarget.getX() - handPos.x, currentTarget.getY(0.5) - handPos.y, currentTarget.getZ() - handPos.z).normalize().
                                                    scale(1.3),  this);
                                            proj.moveTo(handPos);
                                            level.addFreshEntity(proj);


                                            spawn(WDEntities.WIND_CHARGE_PROJECTILE.get(), 5);
                                            spawn(WDEntities.WIND_CHARGE_PROJECTILE.get(), 5);
                                        }
                                    }
                                }
                            }
                            case CHARGEDCANNON ->
                            {
                                if (cannonCharge == 14 || (hasLineOfSight(currentTarget)&&cannonCharge<=14))
                                {
                                    if(attackTicks>20)attackTicks=1;
                                    triggerAnim(CONTROLLER,chargedCannonShoot);
                                    if(attackTicks%5==0)
                                    {
                                        WindChargeProjectile proj = WDEntities.WIND_CHARGE_PROJECTILE.get().create(level);
                                        if (proj != null)
                                        {
                                            Vec3 handPos=getPositionRelative(1,2,2.5);
                                            UtilityMethods.sendParticles((ServerLevel)level,ParticleTypes.CLOUD,true,50+cannonCharge,getEyePosition().x,getEyePosition().y,getEyePosition().z,0,0,0,0.06f+(0.014f*cannonCharge));
                                            proj.setCompressions(false, false, new Vec3(currentTarget.getX() - handPos.x, currentTarget.getY(0.5) - handPos.y, currentTarget.getZ() - handPos.z).normalize().
                                                    scale(2), cannonCharge, this);
                                            proj.moveTo(handPos);
                                            level.addFreshEntity(proj);
                                            playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_SHOOT.value(),1f,1-(cannonCharge/14f*0.3f));
                                        }
                                        cannonCharge=15;
                                    }
                                }
                                if (attackTicks % 20 == 0)
                                {
                                    if(cannonCharge==15)EndAttack();
                                    else
                                    {
                                        playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 0.05f*cannonCharge+.8f);
                                        playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_CHARGE.value(),1f,1f);
                                        cannonCharge++;
                                    }
                                }
                            }
                        }
                    }
                    if (++attackTicks >= attackType.duration)EndAttack();
                }
                else
                {
                    if(navigation.isInProgress())
                    {
                        triggerAnim(CONTROLLER,walk);
                    }
                    else triggerAnim(CONTROLLER,idle);
                }
            }
        }
    }
    public Vec3 getPositionRelative(double forwardDistance, double rightDistance, double upDistance)
    {
        float viewXRot=(float)lerpXRot,viewYRot=(float)lerpYRot;
        return position().add(forwardDistance==0?Vec3.ZERO:Vec3.directionFromRotation(viewXRot,viewYRot).scale(forwardDistance)).
                add(rightDistance==0?Vec3.ZERO:Vec3.directionFromRotation(viewXRot,viewYRot+90).scale(rightDistance)).
                add(upDistance==0?Vec3.ZERO:new Vec3(0,1,0).scale(upDistance));
    }
    private void EndAttack()
    {
        attacking = false;
        currentTarget = null;
    }
    @Override
    public boolean hurt(@NotNull DamageSource source, float damage)
    {
        //fire, lava, drown, freeze, status
        if (source.is(DamageTypes.IN_FIRE) ||
                source.is(DamageTypes.ON_FIRE) ||
                source.is(DamageTypes.LAVA) ||
                source.is(DamageTypes.DROWN) ||
                source.is(DamageTypes.FREEZE) ||
                source.is(DamageTypes.MAGIC) ||
                source.is(DamageTypes.INDIRECT_MAGIC)||
                source.is(DamageTypes.FALL)
        )
            return false;
        return super.hurt(source, damage);
    }
    @Override
    public void die(@NotNull DamageSource source)
    {
        super.die(source);
        bossEvent.removeAllPlayers();
    }
    @Override
    protected void dropAllDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource source)
    {
        super.dropAllDeathLoot(level, source);
        spawnAtLocation(new ItemStack(Items.BREEZE_ROD, UtilityMethods.RNG(32, 64)));
    }
    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float vel)
    {
        if (!level().isClientSide)
        {
            if (!attacking)
            {
                attacking = true;
                attackTicks = 0;
                if (this.currentTarget == null) this.currentTarget = target;
                cannonCharge = 3;
                targetTime=600;
                double distSqr = distanceToSqr(target);
                if (hasLineOfSight(target))
                {
                    if (distSqr < 8*8&&Math.abs(position().y-target.position().y)<=5)
                    {
                        playSound(WDSoundEvents.BREEZE_GOLEM_CORE.value(),1f,1f);
                        attackType = AttackType.HEAVYCORESPIN;//same as saying dist<3, negate a sqrt function
                        triggerAnim(CONTROLLER,coreSpin);
                    }
                    else
                    {
                        playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_START.value(),1f,1f);
                        attackType = AttackType.RAPIDCANNON;
                        triggerAnim(CONTROLLER,rapidCannonStart);
                    }
                }
                else
                {
                    playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_CHARGE.value(),1f,1f);
                    attackType = AttackType.CHARGEDCANNON;
                    triggerAnim(CONTROLLER,chargedCannonCharge);
                }
            }
        }
    }
    @Override
    protected @Nullable SoundEvent getAmbientSound()
    {
        return WDSoundEvents.BREEZE_GOLEM_AMBIENT.value();
    }
    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource)
    {
        return getAmbientSound();
    }
    @Override
    protected @NotNull SoundEvent getDeathSound()
    {
        return WDSoundEvents.BREEZE_GOLEM_DEATH.value();
    }
    @Override
    public @NotNull SoundSource getSoundSource()
    {
        return SoundSource.HOSTILE;
    }
    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer)
    {
        bossEvent.addPlayer(serverPlayer);
    }
    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer)
    {
        bossEvent.removePlayer(serverPlayer);
    }
    public static class SummonGoal extends Goal
    {
        private final NetherDragonEntity golem;

        public SummonGoal(@NotNull NetherDragonEntity golem)
        {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK, Flag.TARGET));
        }
        @Override
        public void tick()
        {
            int ticks=golem.getInvulnerableTicks();
            golem.addInvulnerableTick();
            if (ticks % 10 == 0)
                golem.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 2f, 2f);
        }
        @Override
        public void start()
        {
            golem.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 2f, 0.7f);
            golem.setInvulnerable(true);
        }
        @Override
        public boolean canUse()
        {
            return golem.isInvulnerable();
        }
        @Override
        public void stop()
        {
            //psuedo explosion
            Vec3 pos=golem.position();
            List<LivingEntity>list=golem.level().getEntitiesOfClass(LivingEntity.class,AABB.ofSize(golem.position(),10,10,10),golem::hasLineOfSight);
            for(LivingEntity li:list)
            {
                Vec3 kb=new Vec3(pos.x-li.position().x,pos.y-li.position().y,pos.z-li.position().z).
                        normalize().scale(2);
                li.knockback(1.5,kb.x,kb.z);
                li.setRemainingFireTicks(li.getRemainingFireTicks()+100);
                li.hurt(new DamageSource(golem.level().damageSources().generic().typeHolder()), 10);
            }
            golem.playSound(SoundEvents.GENERIC_EXPLODE.value(),2f,0.8f);
            UtilityMethods.sendParticles((ServerLevel)golem.level(), ParticleTypes.EXPLOSION_EMITTER,true,1,pos.x,pos.y,pos.z,0,0,0,0);
            UtilityMethods.sendParticles((ServerLevel)golem.level(),ParticleTypes.LAVA,true,200,pos.x,pos.y,pos.z,2,2,2,0.06f);
            UtilityMethods.sendParticles((ServerLevel)golem.level(),ParticleTypes.FLAME,true,400,pos.x,pos.y,pos.z,4,4,4,0.08f);
            golem.setInvulnerable(false);
        }
    }

    public static class BreezeGolemAttackGoal extends Goal
    {
        private final NetherDragonEntity mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private final int attackInterval;
        private final float attackRadiusSqr;

        public BreezeGolemAttackGoal(NetherDragonEntity pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius)
        {
            this.rangedAttackMob = pRangedAttackMob;
            this.mob = pRangedAttackMob;
            this.speedModifier = pSpeedModifier;
            this.attackInterval = pAttackInterval;
            this.attackRadiusSqr = pAttackRadius*pAttackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            LivingEntity livingentity = mob.currentTarget!=null?mob.currentTarget:this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        @Override
        public void stop() {
            this.target = null;
            this.attackTime = -1;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick()
        {
            double d0 = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            boolean flag = mob.getSensing().hasLineOfSight(target);

            if (flag&&d0<=attackRadiusSqr) mob.getNavigation().stop();
            else
            {
                mob.getNavigation().moveTo(this.target, this.speedModifier);
            }

            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if(mob.isAttacking())attackTime = mob.attackType== AttackType.HEAVYCORESPIN?2:attackInterval;
            else if(--this.attackTime <= 0)
            {
                this.rangedAttackMob.performRangedAttack(this.target, 1);
                attackTime = attackInterval;
            }
        }
    }
}