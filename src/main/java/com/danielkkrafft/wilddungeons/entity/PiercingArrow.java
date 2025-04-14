package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

/**
 * {@link AbstractArrow#tick()}
 * {@link EntityType#ARROW}
 * Copied components from {@link AbstractArrow}
 */
public class PiercingArrow extends Projectile implements GeoEntity
{
    private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Float>PIERCELEVEL=SynchedEntityData.defineId(PiercingArrow.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float>PIERCEAMOUNT=SynchedEntityData.defineId(PiercingArrow.class,EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<BlockPos>CurrBlockPos=SynchedEntityData.defineId(PiercingArrow.class,EntityDataSerializers.BLOCK_POS);
    public PiercingArrow(EntityType<? extends PiercingArrow> entityType, Level level)
    {
        super(entityType, level);
    }
    public PiercingArrow(Level level, LivingEntity li,float pierceAmount)
    {
        this(WDEntities.PIERCING_ARROW.get(), level);
        Vec3 pos=li.position();
        this.setPos(pos.x, li.getEyeY()-0.1,pos.z);
        this.setOwner(li);
        setMaxPierce(pierceAmount);
        setPierce(pierceAmount);
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
    {
        builder.define(PIERCELEVEL,0f);
        builder.define(PIERCEAMOUNT,0f);
        builder.define(CurrBlockPos,new BlockPos(0,0,0));
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if(tag.contains("PierceAmount"))setPierce(tag.getFloat("PierceAmount"));
    }
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putFloat("PierceAmount",getPierce());
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult result)
    {

        Entity entity = result.getEntity();
        Entity owner = getOwner();

        if(entity instanceof LivingEntity li)
        {
            WildDungeons.getLogger().info("ENTITY IS LIVING ENTITY");

            DamageSource damageSource=owner instanceof LivingEntity ownerLiving?
                    damageSources().mobAttack(ownerLiving):damageSources().generic();

            WildDungeons.getLogger().info("DAMAGE SOURCE: {}", damageSource);

            li.hurt(damageSource,10);
            WildDungeons.getLogger().info("DID HURT");

            doKnockback(li);
            WildDungeons.getLogger().info("DID KNOCKBACK");

            setPierce(0);
            WildDungeons.getLogger().info("SET PIERCE");

            arrowHit(result.getLocation());
            WildDungeons.getLogger().info("DID ARROWHIT");

            discard();
            WildDungeons.getLogger().info("DISCARDED");
        }
    }
    protected void doKnockback(LivingEntity pEntity)
    {
        double d1 = Math.max(0.0, 1.0 - pEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(0.6 * d1);
        if (vec3.lengthSqr() > 0.0)pEntity.push(vec3.x, 0.1, vec3.z);
    }
    public boolean canPierce(){ return getPierce()>0;}
    public void setMaxPierce(float f){entityData.set(PIERCELEVEL,f);}
    public float getPierce(){return entityData.get(PIERCEAMOUNT);}
    public void setPierce(float f){entityData.set(PIERCEAMOUNT,f);}
    public void setCurrBlockPos(BlockPos pos){entityData.set(CurrBlockPos,pos);}
    public BlockPos getCurrBlockPos(){return entityData.get(CurrBlockPos);}
    @Override
    public void tick()
    {
        super.tick();
        Level level = level();
        Vec3 vec3 = getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F)
        {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * 180.0F / (float) Math.PI));
            this.setXRot((float) (Mth.atan2(vec3.y, d0) * 180.0F / (float) Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!blockstate.isAir())
        {
            VoxelShape voxelshape = blockstate.getCollisionShape(level, blockpos);
            if (!voxelshape.isEmpty())
            {
                Vec3 vec31 = this.position();
                for (AABB aabb : voxelshape.toAabbs())
                {
                    if (aabb.move(blockpos).contains(vec31))
                    {
                        BlockPos currPosCollided=getCurrBlockPos();
                        if(!blockpos.equals(currPosCollided))
                        {
                            setCurrBlockPos(blockpos);
                            if (getPierce() > 1)
                            {
                                setPierce(getPierce() - 1);
                                break;
                            }
                            else
                            {
                                arrowHit(vec31);
                                discard();
                                return;
                            }
                        }
                    }
                }
            }
        }
        Vec3 pos = position();
        Vec3 posDelta = pos.add(vec3);
        HitResult hitresult = level.clip(new ClipContext(pos, posDelta, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitresult.getType() != HitResult.Type.MISS) posDelta = hitresult.getLocation();
        while (!isRemoved())
        {
            EntityHitResult entityhitresult = findHitEntity(pos, posDelta);
            if (entityhitresult != null)
                hitresult = entityhitresult;

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY)
            {
                Entity entity = ((EntityHitResult) hitresult).getEntity();
                WildDungeons.getLogger().info("FOUND HIT ENTITY 2 {}", entity);
                Entity entity1 = this.getOwner();
                WildDungeons.getLogger().info("FOUND OWNER 2 {}", entity1);
                if (entity instanceof Player && entity1 instanceof Player && !((Player) entity1).canHarmPlayer((Player) entity)) {
                    hitresult = null;
                    entityhitresult = null;
                }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS)
            {
                WildDungeons.getLogger().info("POSTING EVENT");
                ProjectileImpactEvent event=new ProjectileImpactEvent(this,hitresult);
                NeoForge.EVENT_BUS.post(event);
                if (hitresult instanceof EntityHitResult ehr) this.onHitEntity(ehr);
                this.remove(RemovalReason.DISCARDED);
            }
            if (entityhitresult == null || getPierce() <= 0) break;
            hitresult = null;
        }
        //DanielKrafftMod.sendClientMessage("Pierce: "+getPierce());
        if (isRemoved())return;
        vec3 = this.getDeltaMovement();
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d1 = vec3.z;

        double d7 = this.getX() + d5;
        double d2 = this.getY() + d6;
        double d3 = this.getZ() + d1;
        double d4 = vec3.horizontalDistance();
        setYRot((float) (Mth.atan2(d5, d1) * (double) 180.0F / (double) (float) Math.PI));
        this.setXRot((float) (Mth.atan2(d6, d4) * (double) 180.0F / (double) (float) Math.PI));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
        float f = 0.99F;
        if (this.isInWater())
        {
            for (int j = 0; j < 4; ++j)
            {
                float f1 = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * f1, d2 - d6 * f1, d3 - d1 * f1, d5, d6, d1);
            }
            f = 0.6f;
        }
        setDeltaMovement(vec3.scale(f));
        applyGravity();
        setPos(d7, d2, d3);
        checkInsideBlocks();
    }
    @Override
    protected double getDefaultGravity() {return 0.05;}
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec)
    {
        return ProjectileUtil.getEntityHitResult(level(), this, pStartVec, pEndVec, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1), this::canHitEntity);
    }
    public boolean canHitEntity(Entity target)
    {
        return super.canHitEntity(target);
    }
    /**
     * Calculate distance inside of blocks (based on trajectory)<p>
     * trajectory of 0* pitch (flat) reslts in length of 1 block
     * trajectory of 45* pitch (assuming entering from a corner results in sqrt(3)? smt longer than 1 block)
     * once distancePierced<0, arrow stuck
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult result)
    {
        if(canPierce())
        {
            BlockPos currPosCollided=getCurrBlockPos(),blockPos=result.getBlockPos();
            if(!blockPos.equals(currPosCollided))
            {
                setCurrBlockPos(blockPos);
                float pierce=getPierce();
                if(pierce<1)
                {
                    setPierce(0);
                    arrowHit(position().add(getDeltaMovement().normalize().scale(pierce)));
                }
                else setPierce(pierce-1);
            }
        }
        else arrowHit(result.getLocation());
    }
    private void arrowHit(Vec3 hitLocation)
    {
        Vec3 vec3 = hitLocation.subtract(getX(), getY(), getZ());
        setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale(0.05F);
        setPosRaw(getX() - vec31.x, getY() - vec31.y, getZ() - vec31.z);
        playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        if(level()instanceof ServerLevel server)
        {
            UtilityMethods.sendParticles(server,ParticleTypes.CLOUD,true,1,getX(),getY(),getZ(),0,0,0,0);
            UtilityMethods.sendParticles(server,ParticleTypes.SPORE_BLOSSOM_AIR,true,40,getX(),getY(),getZ(),0,0,0,0);
            AreaEffectCloud cloud=new AreaEffectCloud(server,getX(),getY(),getZ());
            cloud.addEffect(new MobEffectInstance(MobEffects.POISON,20*10,1));
            cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20*10,0));
            cloud.setRadius(2);
            cloud.setRadiusPerTick(-0.07f);
            server.addFreshEntity(cloud);
        }
        discard();
    }
}