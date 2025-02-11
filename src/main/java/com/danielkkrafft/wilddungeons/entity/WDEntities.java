package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, WildDungeons.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EssenceOrb>> ESSENCE_ORB = ENTITIES.register("essence_orb", () -> EntityType.Builder
            .of(EssenceOrb::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(6)
            .updateInterval(20)
            .build(WildDungeons.rl("essence_orb").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<Offering>> OFFERING = ENTITIES.register("offering", () -> EntityType.Builder
            .<Offering>of(Offering::new, MobCategory.MISC)
            .sized(1.0F, 1.0F)
            .clientTrackingRange(6)
            .updateInterval(20)
            .build(WildDungeons.rl("offering").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<WindChargeProjectile>> WIND_CHARGE_PROJECTILE = ENTITIES.register("custom_wind_charge", () -> EntityType.Builder
            .of(WindChargeProjectile::new, MobCategory.MISC)
            .sized(0.3125f, 0.3125f)
            .build("custom_wind_charge"));

    public static final DeferredHolder<EntityType<?>, EntityType<GrapplingHook>> GRAPPLING_HOOK = ENTITIES.register("grappling_hook", () -> EntityType.Builder
            .<GrapplingHook>of(GrapplingHook::new, MobCategory.MISC)
            .sized(0.4f, 0.4f)
            .build(WildDungeons.rl("grappling_hook").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PiercingArrow>> PIERCING_ARROW = ENTITIES.register("piercing_arrow", () -> EntityType.Builder
            .<PiercingArrow>of(PiercingArrow::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .build("piercing_arrow"));

    public static final DeferredHolder<EntityType<?>, EntityType<BreezeGolem>> BREEZE_GOLEM = ENTITIES.register("breeze_golem", () -> EntityType.Builder
            .of(BreezeGolem::new, MobCategory.MONSTER)
            .sized(2f, 3.7f)
            .build("breeze_golem"));

    public static final DeferredHolder<EntityType<?>, EntityType<MutantBogged>> MUTANT_BOGGED = ENTITIES.register("mutant_bogged", () -> EntityType.Builder
            .of(MutantBogged::new, MobCategory.MONSTER)
            .sized(1f, 3.6f)
            .build("mutant_bogged"));
}
