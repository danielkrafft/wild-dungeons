package com.danielkkrafft.wilddungeons.entity.event;

import com.danielkkrafft.wilddungeons.entity.attachmenttypes.HomingTargetAttachmentType;
import com.danielkkrafft.wilddungeons.registry.WDAttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class ArrowBehaviorEvents {
    @SubscribeEvent
    public static void wd$onTickingArrow(EntityTickEvent.Pre pre) {
        if (pre.getEntity() instanceof AbstractArrow arrow) {
            if (!arrow.onGround()) {
                if (arrow.hasData(WDAttachmentTypes.HOMING_ATTACHMENT)) {
                    HomingTargetAttachmentType attachmentType = arrow.getData(WDAttachmentTypes.HOMING_ATTACHMENT);
                    int eLevel = attachmentType.getLevel();
                    if (attachmentType.getTarget().isEmpty()) {
                        float range = 6.3f;
                        float angle = 45f;
                        float cos = (float) Math.cos(Math.toRadians(angle));

                        Vec3 arrowDir = arrow.getDeltaMovement().normalize();

                        List<Mob> mobs = arrow.level()
                                .getEntitiesOfClass(Mob.class, arrow.getBoundingBox().inflate(range))
                                .stream()
                                .filter(m -> m.isAlive())
                                .filter(m -> {
                                    Vec3 toMob = m.position().subtract(arrow.position()).normalize();
                                    float dot = (float) arrowDir.dot(toMob);
                                    return dot > cos;
                                })
                                .toList();

                        if (!mobs.isEmpty()) {
                            Mob closest = mobs.stream()
                                    .min(Comparator.comparingDouble(m -> m.distanceToSqr(arrow)))
                                    .get();

                            HomingTargetAttachmentType newest = attachmentType.copy();
                            newest.setTarget(Optional.of(closest.getUUID()));
                            arrow.setData(WDAttachmentTypes.HOMING_ATTACHMENT, newest);
                        }

                    } else {
                        if (!arrow.level().isClientSide) {
                            Entity entity = ((ServerLevel) arrow.level()).getEntity(attachmentType.getTarget().get());
                            if (entity != null) {
                                Vec3 targetDir = entity.position()
                                        .add(0, entity.getBbHeight() * 0.5, 0)
                                        .subtract(arrow.position())
                                        .normalize(); // this is the target direction thingy

                                Vec3 currentVel = arrow.getDeltaMovement();
                                double speed = currentVel.length();
                                double blend = Math.clamp(0.1 * eLevel, 0., 0.9); //This is for the sharp angle thingy
                                Vec3 newVel = currentVel.scale(1.0 - blend)
                                        .add(targetDir.scale(blend * speed));
                                arrow.setDeltaMovement(newVel);
                                arrow.hurtMarked = true; //This force the update of the arrow thingy
                            } else {
                                HomingTargetAttachmentType newest = attachmentType.copy();
                                newest.setTarget(Optional.empty());
                                arrow.setData(WDAttachmentTypes.HOMING_ATTACHMENT, newest);
                            }
                        }
                    }
                }
            }
        }
    }
}
