package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.entity.BaseClasses.WDArrow;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemAnimator;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static com.danielkkrafft.wilddungeons.registry.WDSoundEvents.WIND_ARROW_FLYBY;

public class WindArrow extends WDArrow {

    public static final String NAME = "wind_arrow";
    private static final int DEFAULT_SPAWN_DELAY = 8;
    private static final int DEFAULT_CHARGE_SIZE = 1;
    private static final EntityDataAccessor<Integer> SPAWN_DELAY = SynchedEntityData.defineId(WindArrow.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHARGE_SIZE = SynchedEntityData.defineId(WindArrow.class,EntityDataSerializers.INT);

    //todo does not work with the server-side. Must be handled in a packet or replaced with a regular sound event
//    private WindArrowSound arrowSound = null;

    public WindArrow(EntityType<WindArrow> type, Level level) {
        super(type, level, NAME);
    }

    public WindArrow(Level level, LivingEntity livingEntity) {
        this(WDEntities.WIND_ARROW.value(), level);
        this.animator = new WDItemAnimator(NAME, this);
        this.model = new ClientModel<>(NAME, "item");

        Vec3 position = livingEntity.position();
        this.setPos(position.x, livingEntity.getEyeY() - 0.1, position.z);
        this.setOwner(livingEntity);
        if(livingEntity instanceof Player) this.pickup = Pickup.ALLOWED;
        //setWindTrailLevel(EnchantmentHelper.getEnchantmentLevel(WDEnchantments.WIND_TRAIL, livingEntity));
        //setWindGustLevel(EnchantmentHelper.getEnchantmentLevel(WDEnchantments.GUST, livingEntity));
    }

    public int getSpawnDelay() {
        return entityData.get(SPAWN_DELAY);
    }

    public void setWindTrailLevel(int trailLevel) {
        if (trailLevel > 0) {
            entityData.set(SPAWN_DELAY, DEFAULT_SPAWN_DELAY - 2 * Math.min(trailLevel, 3));
        }
    }

    public int getGustLevel() {
        return entityData.get(CHARGE_SIZE);
    }

    public void setWindGustLevel(int gustLevel) {
        if (gustLevel > 0) {
            entityData.set(CHARGE_SIZE, 1 + Math.min(gustLevel, 3));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(SPAWN_DELAY, DEFAULT_SPAWN_DELAY);
        builder.define(CHARGE_SIZE, DEFAULT_CHARGE_SIZE);
    }

    @Override
    public void tick() {
        super.tick();

        Level lvl=level();
        if(!isRemoved())
        {
            if (this.level() instanceof ServerLevel serverLevel && this.tickCount % 10 == 0) {
                CompoundTag payload = new CompoundTag();
                payload.putString("packet", ClientPacketHandler.Packets.PLAY_DYNAMIC_SOUND.toString());
                payload.putInt("soundEvent", BuiltInRegistries.SOUND_EVENT.getId(WIND_ARROW_FLYBY.value()));
                payload.putString("soundSource", SoundSource.NEUTRAL.toString());
                payload.putInt("entityId", this.getId());
                payload.putBoolean("loop", true);
                payload.putFloat("volume", 1.0f);
                payload.putFloat("pitch", 1.0f);
                PacketDistributor.sendToPlayersNear(serverLevel, null, this.getX(), this.getY(), this.getZ(), 50, new SimplePacketManager.ClientboundTagPacket(payload));
            }
            else
            {
                Entity owner=getOwner();
                if(owner instanceof LivingEntity li)
                {
                    if(inGround||tickCount%getSpawnDelay()==0)
                    {
                        WindChargeProjectile wind = WDEntities.WIND_CHARGE_PROJECTILE.value().create(lvl);
                        if(wind!=null)
                        {
                            wind.setCompressions(inGround,true,getDeltaMovement().scale(0.2),getGustLevel(),li);
                            wind.moveTo(position());
                            lvl.addFreshEntity(wind);
                        }
                    }
                }
                if(inGround)remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result)
    {
        super.onHitEntity(result);
        Level lvl=level();
        if(!lvl.isClientSide&&getOwner()instanceof LivingEntity li)
        {
            WindChargeProjectile wind = WDEntities.WIND_CHARGE_PROJECTILE.value().create(lvl);
            if(wind!=null)
            {
                wind.defaultCharge(true,true,getDeltaMovement().scale(0.2),li);
                wind.moveTo(position());
                lvl.addFreshEntity(wind);
            }
        }
    }
}