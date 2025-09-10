package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.*;
import com.danielkkrafft.wilddungeons.entity.boss.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, WildDungeons.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EssenceOrb>> ESSENCE_ORB = ENTITIES.register("essence_orb", () -> EntityType.Builder
            .<EssenceOrb>of(EssenceOrb::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(6)
            .updateInterval(20)
            .build(WildDungeons.rl("essence_orb").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownEssenceBottle>> ESSENCE_BOTTLE = ENTITIES.register("essence_bottle", () -> EntityType.Builder
            .<ThrownEssenceBottle>of(ThrownEssenceBottle::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(WildDungeons.rl("essence_bottle").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<Offering>> OFFERING = ENTITIES.register("offering", () -> EntityType.Builder
            .<Offering>of(Offering::new, MobCategory.MISC)
            .sized(1, 1)
            .clientTrackingRange(6)
            .updateInterval(20)
            .build(WildDungeons.rl("offering").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<WindChargeProjectile>> WIND_CHARGE_PROJECTILE = ENTITIES.register("wind_charge_projectile", () -> EntityType.Builder
            .of(WindChargeProjectile::new, MobCategory.MISC)
            .sized(0.3125f, 0.3125f)
            .build("wind_charge_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<GrapplingHook>> GRAPPLING_HOOK = ENTITIES.register("grappling_hook", () -> EntityType.Builder
            .<GrapplingHook>of(GrapplingHook::new, MobCategory.MISC)
            .sized(0.4f, 0.4f)
            .build(WildDungeons.rl("grappling_hook").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<Laserbeam>> LASER_BEAM = ENTITIES.register("laserbeam", () -> EntityType.Builder
            .<Laserbeam>of(Laserbeam::new, MobCategory.MISC)
            .sized(0.1f, 0.1f)
            .build(WildDungeons.rl("laserbeam").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PiercingArrow>> PIERCING_ARROW = ENTITIES.register("piercing_arrow",
            () -> EntityType.Builder
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

    public static final DeferredHolder<EntityType<?>, EntityType<AmogusEntity>> AMOGUS = ENTITIES.register("amogus", () -> EntityType.Builder
            .of(AmogusEntity::new, MobCategory.CREATURE)
            .sized(1f, 1.0f)
            .build(WildDungeons.rl("amogus").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<NetherDragonEntity>> NETHER_DRAGON = ENTITIES.register("nether_dragon", () -> EntityType.Builder
            .of(NetherDragonEntity::new, MobCategory.MONSTER)
            .sized(5, 2)
            .fireImmune()
            .build(WildDungeons.rl("nether_dragon").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BusinessGolem>> BUSINESS_GOLEM = ENTITIES.register("business_golem", () -> EntityType.Builder
            .of(BusinessGolem::new, MobCategory.MONSTER)
            .sized(1.4f, 2.7f)
            .build(WildDungeons.rl("business_golem").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BusinessVindicator>> BUSINESS_VINDICATOR = ENTITIES.register("business_vindicator", () -> EntityType.Builder
            .of(BusinessVindicator::new, MobCategory.MONSTER)
            .sized(0.6f, 1.8f)
            .build(WildDungeons.rl("business_vindicator").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BusinessEvoker>> BUSINESS_EVOKER = ENTITIES.register("business_evoker", () -> EntityType.Builder
            .of(BusinessEvoker::new, MobCategory.MONSTER)
            .sized(0.6f, 1.8f)
            .build(WildDungeons.rl("business_evoker").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<EmeraldWisp>> SMALL_EMERALD_WISP = ENTITIES.register("small_emerald_wisp", () -> EntityType.Builder
            .of(EmeraldWisp::new, MobCategory.MONSTER)
            .sized(0.6f, 0.3f)
            .build(WildDungeons.rl("small_emerald_wisp").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<LargeEmeraldWisp>> LARGE_EMERALD_WISP = ENTITIES.register("large_emerald_wisp", () -> EntityType.Builder
            .of(LargeEmeraldWisp::new, MobCategory.MONSTER)
            .sized(1f, 0.5f)
            .build(WildDungeons.rl("large_emerald_wisp").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<FriendlyEmeraldWisp>> FRIENDLY_EMERALD_WISP = ENTITIES.register("friendly_emerald_wisp", () -> EntityType.Builder
            .of(FriendlyEmeraldWisp::new, MobCategory.MONSTER)
            .sized(0.6f, 0.3f)
            .build(WildDungeons.rl("friendly_emerald_wisp").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<FriendlyLargeEmeraldWisp>> FRIENDLY_LARGE_EMERALD_WISP = ENTITIES.register("large_friendly_emerald_wisp", () -> EntityType.Builder
            .of(FriendlyLargeEmeraldWisp::new, MobCategory.MONSTER)
            .sized(1f, 0.5f)
            .build(WildDungeons.rl("large_friendly_emerald_wisp").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BusinessCEO>> BUSINESS_CEO = ENTITIES.register("business_ceo", () -> EntityType.Builder
            .of(BusinessCEO::new, MobCategory.MONSTER)
            .sized(1.1f, 2.8f)
            .build(WildDungeons.rl("business_ceo").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<EmeraldProjectileEntity>> EMERALD_PROJECTILE = ENTITIES.register("emerald_projectile", () -> EntityType.Builder
            .<EmeraldProjectileEntity>of(EmeraldProjectileEntity::new, MobCategory.MISC)
            .sized(0.3125F, 0.3125F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(WildDungeons.rl("emerald_projectile").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<WindArrow>> WIND_ARROW = ENTITIES.register("wind_arrow",
            () -> EntityType.Builder
                    .<WindArrow>of(WindArrow::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .build(WildDungeons.rl("wind_arrow").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BlackHole>> BLACK_HOLE = ENTITIES.register("black_hole",
            () -> EntityType.Builder
                    .of(BlackHole::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .updateInterval(1)
                    .clientTrackingRange(80)
                    .build(WildDungeons.rl("black_hole").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<Spiderling>> SPIDERLING = ENTITIES.register("spiderling", () -> EntityType.Builder
            .of(Spiderling::new, MobCategory.MONSTER)
            .sized(0.65F, 0.4F)
            .eyeHeight(0.2F)
            .build(WildDungeons.rl("spiderling").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SkelepedeMain>> SKELEPEDE = ENTITIES.register("skelepede_main", () -> EntityType.Builder
            .of(SkelepedeMain::new, MobCategory.MONSTER)
            .sized(1, 0.6F)
            .eyeHeight(0.2F)
            .build(WildDungeons.rl("skelepede_main").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SkelepedeSegment>> SKELEPEDE_SEGMENT = ENTITIES.register("skelepede_segment", () -> EntityType.Builder
            .of(SkelepedeSegment::new, MobCategory.MONSTER)
            .sized(1, 0.6F)
            .eyeHeight(0.2F)
            .build(WildDungeons.rl("skelepede_segment").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ToxicWisp>> SMALL_TOXIC_WISP = ENTITIES.register("small_toxic_wisp", () -> EntityType.Builder
            .of(ToxicWisp::new, MobCategory.MONSTER)
            .sized(0.3f, 0.8f)
            .build(WildDungeons.rl("small_toxic_wisp").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<LargeToxicWisp>> LARGE_TOXIC_WISP = ENTITIES.register("large_toxic_wisp", () -> EntityType.Builder
            .of(LargeToxicWisp::new, MobCategory.MONSTER)
            .sized(0.8f, 1f)
            .build(WildDungeons.rl("large_toxic_wisp").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CopperSentinel>> COPPER_SENTINEL = ENTITIES.register("copper_sentinel", () -> EntityType.Builder
            .of(CopperSentinel::new, MobCategory.MONSTER)
            .sized(3, 8)
            .build(WildDungeons.rl("copper_sentinel").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<EggSacArrow>> EGG_SAC_ARROW = ENTITIES.register("egg_sac_arrow",
            () -> EntityType.Builder.<EggSacArrow>of(EggSacArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("egg_sac_arrow"));
}
