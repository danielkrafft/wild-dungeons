package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.item.Meathook;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

public class GrapplingHook extends ThrowableProjectile {
    private static final EntityDataAccessor<String> OWNER = SynchedEntityData.defineId(GrapplingHook.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> HOOKDISP = SynchedEntityData.defineId(GrapplingHook.class, EntityDataSerializers.STRING);
    private Entity hook;
    private boolean entity;

    public GrapplingHook(Player player, Vec3 pos) {
        super(WDEntities.GRAPPLING_HOOK.get(), player.level());
        moveTo(pos);
        setPlayer(player);
    }

    public GrapplingHook(EntityType<? extends GrapplingHook> type, Level level) {
        super(type, level);
    }

    private boolean canFunction() {
        return this.level().isLoaded(blockPosition());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HOOKDISP, "");
        builder.define(OWNER, "");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("OwnerUUID")) getEntityData().set(OWNER, tag.getString("OwnerUUID"));
        if (tag.contains("HookDisp")) getEntityData().set(HOOKDISP, tag.getString("HookDisp"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("HookDisp", getEntityData().get(HOOKDISP));
        tag.putString("OwnerUUID", getEntityData().get(OWNER));
    }

    @Override
    public boolean canCollideWith(@NotNull Entity en) {
        return getPlayer() == null || (getPlayer() != null && !en.equals(getPlayerOwner(en.level())));
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    private static final Predicate<Entity> canHit = e -> e instanceof LivingEntity ||
            e instanceof EnderDragonPart ||
            e instanceof EndCrystal ||
            e instanceof Fireball;


    @Override
    protected void onHitEntity(@NotNull EntityHitResult en) {
        if (getHookDisp() == null) {
            Entity e = en.getEntity();
            Player p = getPlayerOwner(this.level());
            if (p != null && !e.equals(p) && canHit.test(e)) {
                setHookDisp(position().add(getDeltaMovement()).subtract(en.getLocation()));
                hook = e;
                entity = true;
                if (e instanceof EnderDragonPart) {
                    hook = ((EnderDragonPart) e).getParent();
                }
                if (e instanceof EndCrystal) {
                    EndCrystal ec = (EndCrystal) e;
                    ec.hurt(en.getEntity().level().damageSources().sonicBoom(this), 1);
                }
                if (e instanceof LivingEntity) {
                    LivingEntity l = (LivingEntity) e;
                    l.hurt(l.level().damageSources().indirectMagic(this, p), 5);
                }
            }
            this.level().playSound(null, blockPosition(), hitSound(), SoundSource.NEUTRAL, 1f, 1f);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult block) {
        if (getHookDisp() == null) {
            this.level().playSound(null, blockPosition(), hitSound(), SoundSource.NEUTRAL, 1f, 1f);
            setHookDisp(block.getLocation());
            hook = null;
            entity = false;
        }
    }

    private float prevYaw, prevPitch;

    //AbstractArrow
    @Override
    public void tick() {
        if (!canFunction()) {
            return;
        }
        super.tick();

        if (getHookDisp() != null) {
            setYRot(prevYaw);
            setXRot(prevPitch);
        } else {
            float[] velF = MathUtil.entitylookAtEntity(position(), position().add(getDeltaMovement()));
            setYRot(velF[0]);
            setXRot(velF[1]);
            prevYaw = velF[0];
            prevPitch = velF[1];
        }


        if (getPlayer() == null || getPlayerOwner(this.level()) == null) {
            discard();
            return;
        }

        Player playerOwner = getPlayerOwner(this.level());
        ItemStack itemStack = lookForStack(playerOwner);

        if (!playerOwner.level().equals(this.level())) {
            Meathook.resetHook(playerOwner, itemStack);
            if (Meathook.getHookUUID(itemStack) == null) {
                discard();
            }
        } else {
            float playerDistance = (float) MathUtil.distance(playerOwner.position(), position());
            if (playerDistance > 100) {
                Meathook.resetHook(playerOwner, itemStack);
                if (Meathook.getHookUUID(itemStack) == null) {
                    discard();
                }
            } else {
                if (getHookDisp() != null) {
                    final double maxVel = 0.5;
                    double vel = playerOwner.getDeltaMovement().length();
                    if (vel > maxVel && (playerOwner.onGround() || playerOwner.horizontalCollision || playerOwner.verticalCollision)) {
                        //Meathook.resetHook(playerOwner, itemStack);
                        if (Meathook.getHookUUID(itemStack) == null) {
                            playerOwner.setDeltaMovement(playerOwner.getDeltaMovement().multiply(0.1, 0, 0.1).add(0, 0.65, 0));
                            //discard();
                        }
                    } else {
                        // Check if player is trying to release the hook (crouching is more reliable for server use)
                        if (playerOwner.isShiftKeyDown()) {
                            Meathook.resetHook(playerOwner, itemStack);
                            if (Meathook.getHookUUID(itemStack) == null) {
                                discard();
                            }
                        } else {
                            if (tickCount % 5 == 0)
                                this.level().playSound(null, blockPosition(), reelSound(), SoundSource.NEUTRAL, 1f, 1f);

                            float[] f = MathUtil.entitylookAtEntity(playerOwner, this);
                            final double maxD = 0.12;
                            double delta = vel > 2 ? 0 : maxD;
                            if (playerDistance > 3) {//stop pulling when too close to let the player kinda dangle a bit

                                // Calculate movement vector with yaw adjusted for strafing
                                float adjustedYaw = f[0];
                                float adjustedPitch = f[1];
                                if (playerOwner.xxa > 0) {
                                    adjustedYaw -= 25;
                                } else if (playerOwner.xxa < 0) {
                                    adjustedYaw += 25;
                                } else if (playerOwner.zza > 0) {
                                    adjustedPitch += 25;
                                } else if (playerOwner.zza < 0) {
                                    adjustedPitch -= 25;
                                }

                                playerOwner.addDeltaMovement(MathUtil.velocity3d(delta, adjustedYaw, adjustedPitch));
                            }

                            if (itemStack == null) {
                                discard();
                            }

                            setDeltaMovement(Vec3.ZERO);
                            if (entity) {
                                if (hook != null && !hook.isRemoved() &&
                                        (hook instanceof LivingEntity && !((LivingEntity) hook).isDeadOrDying()) &&
                                        hook.level().equals(this.level())) {
                                    moveTo(hook.position().add(getHookDisp()));
                                } else {
                                    Meathook.resetHook(playerOwner, itemStack);
                                    if (Meathook.getHookUUID(itemStack) == null) {
                                        discard();
                                    }
                                }
                            } else {
                                moveTo(getHookDisp());
                            }
                        }
                    }
                }
            }
        }
    }

    public ItemStack lookForStack(Player p) {
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack it = inv.getItem(i);
            if (it.getItem() instanceof Meathook && Meathook.getHookUUID(it) != null && Meathook.getHookUUID(it).equals(getUUID())) {
                return it;
            }
        }
        return null;
    }

    @Override
    public void kill() {
        if (getPlayer() != null && getPlayerOwner(this.level()) != null) {
            Player p = getPlayerOwner(this.level());
            if (p != null) {
                ItemStack it = lookForStack(p);
                if (it != null) Meathook.resetHook(p, it);
            }
        }
        super.kill();
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

    public static SoundEvent hitSound() {
        return WDSoundEvents.MEATHOOK_HIT.value();
    }

    public static SoundEvent reelSound() {
        return WDSoundEvents.MEATHOOK_REEL.value();
    }

    //variables
    public void setHookDisp(Vec3 v) {
        getEntityData().set(HOOKDISP, MathUtil.serializeVec3(v));
    }

    public Vec3 getHookDisp() {
        if (getEntityData().get(HOOKDISP).isEmpty()) return null;
        return MathUtil.deserializeVec3(getEntityData().get(HOOKDISP));
    }

    public void setPlayer(Player p) {
        getEntityData().set(OWNER, p.getStringUUID());
    }

    public UUID getPlayer() {
        return (getEntityData().get(OWNER).isEmpty()) ? null : UUID.fromString(getEntityData().get(OWNER));
    }

    public Player getPlayerOwner(Level world) {
        return getPlayer() == null ? null : world.getPlayerByUUID(getPlayer());
    }
}
