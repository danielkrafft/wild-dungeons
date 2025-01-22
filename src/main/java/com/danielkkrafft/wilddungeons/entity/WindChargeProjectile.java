package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * {@link net.minecraft.world.entity.projectile.windcharge.WindCharge}
 * {@link net.minecraft.world.entity.projectile.AbstractHurtingProjectile}
 */
public class WindChargeProjectile extends Projectile implements GeoEntity
{
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
            true, false, Optional.of(1.22F), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );

    private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer>COMPS=SynchedEntityData.defineId(WindChargeProjectile.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float>KB=SynchedEntityData.defineId(WindChargeProjectile.class,EntityDataSerializers.FLOAT),
            SIZE=SynchedEntityData.defineId(WindChargeProjectile.class,EntityDataSerializers.FLOAT),
            RADIUS=SynchedEntityData.defineId(WindChargeProjectile.class,EntityDataSerializers.FLOAT),
            DMG=SynchedEntityData.defineId(WindChargeProjectile.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean>REMOVE=SynchedEntityData.defineId(WindChargeProjectile.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean>GRAVITY=SynchedEntityData.defineId(WindChargeProjectile.class,EntityDataSerializers.BOOLEAN);
    private static final float
            defKB=1.8f,
            defSize=0.3125f,
            defRad=2.25f,
            defDmg=3f;
    private HitResult result;
    public WindChargeProjectile(EntityType<? extends Projectile> type, Level level)
    {
        super(type, level);
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controller){}
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}
    public void defaultCharge(boolean remove,boolean gravity,@NotNull Vec3 vel,@NotNull LivingEntity entity)
    {
        setCompressions(remove,gravity,vel,1,entity);
    }
    public void setCompressions(boolean remove,boolean gravity,@NotNull Vec3 vel,final int compressions,@NotNull LivingEntity entity)
    {
        setOwner(entity);
        setRot(entity.getYRot(), entity.getXRot());
        moveTo(entity.getX(),entity.getEyeY()-0.1,entity.getZ(),getYRot(),getXRot());
        reapplyPosition();
        setDeltaMovement(vel);//gravity?vel:entity.getLookAngle().multiply(vel));
        //Math.min(compressions,10); normally limit to 10
        entityData.set(COMPS,compressions);
        setKB(compressions/8f+defKB);
        setSize(compressions/8f+defSize);
        setRadius(compressions/5f+defRad);
        setDmg(compressions/6f+defDmg);
        setGravity(gravity);
        setRemove(remove);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
    {
        builder.define(COMPS,1);
        builder.define(KB,defKB);
        builder.define(SIZE,defSize);
        builder.define(RADIUS,defRad);
        builder.define(DMG,defDmg);
        builder.define(GRAVITY,false);
        builder.define(REMOVE,false);
    }
    @Override@NotNull
    public EntityDimensions getDimensions(@NotNull Pose pose)
    {
        float size=getSize();
        return EntityDimensions.fixed(size,size);
    }
    @Override@NotNull
    protected AABB makeBoundingBox()
    {
        float size=getSize();
        return AABB.ofSize(position(),size,size,size);
    }
    private void setKB(float kb){entityData.set(KB,kb);}
    private void setSize(float s){entityData.set(SIZE,s);}
    private void setRadius(float r){entityData.set(RADIUS,r);}
    private void setDmg(float dmg){entityData.set(DMG,dmg);}
    private void setGravity(boolean g){entityData.set(GRAVITY,g);}
    public int getComps(){return entityData.get(COMPS);}
    public float getKB(){return entityData.get(KB);}
    public float getSize(){return entityData.get(SIZE);}
    public float getRadius(){return entityData.get(RADIUS);}
    public float getDmg(){return entityData.get(DMG);}
    public boolean getGravityOn(){return entityData.get(GRAVITY);}
    public void setRemove(boolean r){entityData.set(REMOVE,r);tickCount=0;}
    public boolean getRemove(){return entityData.get(REMOVE);}
    /**
     * copied from
     * {@link net.minecraft.world.entity.projectile.ThrowableProjectile}
     */
    @Override
    public void tick()
    {
        Entity entity = this.getOwner();
        if(getRemove())
        {
            if(tickCount==1)destroy();
            if(tickCount>=2)remove(RemovalReason.DISCARDED);
        }
        else
        {
            if ((entity == null || !entity.isRemoved()) && level().hasChunkAt(blockPosition()))
            {
                Vec3 vec3 = this.getDeltaMovement();
                ProjectileUtil.rotateTowardsMovement(this, 0.2F);
                if(getGravityOn())vec3=vec3.add(0,-0.12,0).multiply(0.75,1,0.75);
                List<LivingEntity>list=radiusHit();
                if(getOwner()instanceof LivingEntity owner)
                {
                    list.forEach(li->
                    {
                        if(!li.hurtMarked&&!li.equals(owner))windChargeHitEntity(li,owner);
                    });
                }
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() != HitResult.Type.MISS)
                {
                    this.onHit(hitresult);
                    Vec3 hit=hitresult.getLocation();
                    setPos(hit.x,hit.y,hit.z);
                }
                else setPos(getX() + vec3.x,getY()+vec3.y,getZ()+vec3.z);
                setDeltaMovement(vec3);
                checkInsideBlocks();
                super.tick();
            }
            else remove(RemovalReason.DISCARDED);
        }
    }
    @Override
    protected void onHit(@NotNull HitResult result)
    {
        //called serverside
        super.onHit(result);
        HitResult.Type type=result.getType();
        if (type==HitResult.Type.ENTITY)
        {
            this.onHitEntity((EntityHitResult)result);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(),GameEvent.Context.of(this,null));
        }
        else if (type==HitResult.Type.BLOCK)
        {
            BlockHitResult blockhitresult = (BlockHitResult)result;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, level().getBlockState(blockpos)));
        }
    }
    public void destroy()
    {
        List<LivingEntity>list=radiusHit();
        DamageSource source=damageSources().generic();
        if(getOwner()instanceof LivingEntity owner)
        {
            source=damageSources().mobAttack(owner);
            list.forEach(li->windChargeHitEntity(li,owner));
        }
        int c=getComps();
        if(level()instanceof ServerLevel server)
        {
            if(c>5)
            {
                server.explode(this,source,EXPLOSION_DAMAGE_CALCULATOR,
                        getX(),getY(),getZ(),c>=20?12:c>=10?8:2,false,Level.ExplosionInteraction.MOB);
            }
            else
            {
                server.explode(this,source,EXPLOSION_DAMAGE_CALCULATOR,
                        getX(),getY(),getZ(),c,false,Level.ExplosionInteraction.TRIGGER,
                        ParticleTypes.GUST_EMITTER_SMALL,
                        ParticleTypes.GUST_EMITTER_LARGE,
                        SoundEvents.WIND_CHARGE_BURST);
                BlockPos pos=null;
                if(result instanceof BlockHitResult block)
                    pos=block.getBlockPos();
                if(result instanceof EntityHitResult entity)
                    pos=entity.getEntity().blockPosition();
                if(pos!=null)
                {
                    float r=getRadius();
                    //System.out.println("C:"+c+",R:"+r+",R/2="+(r/2));
                    for(int x=-(int)(r/2);x<r/2;x++)
                    {
                        for(int y=-(int)(r/2);y<r/2;y++)
                        {
                            for(int z=-(int)(r/2);z<r/2;z++)
                            {
                                BlockPos posBlock=new BlockPos(x+pos.getX(),y+pos.getY()+1,z+pos.getZ());
                                //UtilityMethods.sendParticles(server,ParticleTypes.CLOUD,true,1,posBlock.getX()+0.5,posBlock.getY(),posBlock.getZ()+0.5,0,0,0,0);
                                Block b=server.getBlockState(posBlock).getBlock();
                                if(c>2)
                                {
                                    if(b instanceof SpecialPlantable)
                                        server.destroyBlock(posBlock,true);
                                }
                            }
                        }
                    }
                }
            }
            float size=getSize();
            if(size>2.5)
            {
                UtilityMethods.sendParticles(server,ParticleTypes.FLASH,true,1,getX(),getY(),getZ(),0,0,0,0);
                UtilityMethods.sendParticles(server,ParticleTypes.EXPLOSION_EMITTER,true,1,getX(),getY(),getZ(),0.2f,0.2f,0.2f,0);
            }
            else
            {
                UtilityMethods.sendParticles(server,ParticleTypes.EXPLOSION,true,1,getX(),getY(),getZ(),0.2f,0.2f,0.2f,0);
            }
            UtilityMethods.sendParticles(server,ParticleTypes.CLOUD,true,(int)Math.min(size*5,100),getX(),getY(),getZ(),size/4f,size/4f,size/4f,Math.min(size/7f,1f));
        }
        else
        {
            LocalPlayer p= Minecraft.getInstance().player;
            if(p!=null)
            {
                float dist=Mth.clamp(c/(float)p.position().distanceTo(position()),0,1);
                p.playSound(WDSoundEvents.WIND_CHARGE_IMPACT.value(),dist<0.05f?0:dist,1f);
            }
        }
    }
    private List<LivingEntity> radiusHit()
    {
        float r=getRadius();
        return radiusHit(level(),position(),r,r,r,null);
    }
    public void windChargeHitEntity(@NotNull LivingEntity li,@NotNull LivingEntity owner)
    {
        float kb=getKB(),dmg=getDmg(),yKb=kb;
        li.setDeltaMovement(li.position().subtract(position()).normalize().multiply(kb,0,kb).add(0,yKb,0));
        if(!li.equals(owner))
            li.hurt(damageSources().mobProjectile(this,owner),dmg);
    }
    protected void onHitEntity(@NotNull EntityHitResult result)
    {
        if(!result.getEntity().equals(getOwner()))
        {
            setRemove(true);
            this.result=result;
        }
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult result)
    {
        setRemove(true);
        this.result=result;
    }
    @Override
    protected void onInsideBlock(@NotNull BlockState state)
    {
        if(!state.isAir())setRemove(true);
    }
    @Override
    public boolean hurt(@NotNull DamageSource source, float p_19947_) {return false;}
    public static List<LivingEntity> radiusHit(@NotNull Level level, @NotNull Vec3 position, float radX, float radY, float radZ,@Nullable Predicate<LivingEntity>conditions)
    {
        AABB radius=AABB.ofSize(position,radX,radY,radZ);
        return level.getEntitiesOfClass(LivingEntity.class,radius,conditions==null?EntitySelector.NO_SPECTATORS:conditions);
    }
}