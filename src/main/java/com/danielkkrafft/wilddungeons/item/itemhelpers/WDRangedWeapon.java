package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.entity.BaseClasses.ArrowFactory;
import com.danielkkrafft.wilddungeons.entity.BaseClasses.SelfGovernedEntity;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.BaseProjectileData;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.BowWeaponData;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.GunWeaponData;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.world.item.BowItem.getPowerForTime;
import static net.neoforged.neoforge.event.EventHooks.onArrowLoose;
import static net.neoforged.neoforge.event.EventHooks.onArrowNock;

public abstract class WDRangedWeapon extends ProjectileWeaponItem implements GeoAnimatable, GeoItem {
    public String name = "default";
    // Animations
    protected WDItemAnimator animator;
    protected boolean hasIdle = true;
    public static final String DRAW_ANIM = "draw";
    public static final String RELEASE_ANIM = "release";
    public static final String IDLE_ANIM = "idle";
    public static final String FIRE_ANIM = "fire";
    public static final String RELOAD_ANIM = "reload";
    // Model
    protected ClientModel<WDRangedWeapon> model;
    protected boolean hasEmissive = false;
    // Ranged Weapon Data
    protected BaseProjectileData itemData;
    // Bow-specific
    protected ArrowFactory arrowFactory = (level, shooter) -> new Arrow(EntityType.ARROW, level);
    protected int lastUseDuration = 0;
    // Gun-specific
    protected boolean hasFire;
    protected boolean hasReload;
    protected boolean isGun;

    public WDRangedWeapon(BaseProjectileData data) {
        super(
                buildProperties(data)
        );
        this.itemData = data;
        this.name = data.name;
        this.hasEmissive = data.hasEmissive;
        this.model = new ClientModel<>(data.animations,data.baseModel,data.baseTexture);
        this.animator = new WDItemAnimator(name, this);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);

        // Bow-specific
        if (data instanceof BowWeaponData bowData) {
            this.model.setAltModel(bowData.bowModelCharged,bowData.bowTextureCharged);
            this.animator.addAnimation(DRAW_ANIM);
            this.animator.addAnimation(RELEASE_ANIM);
            this.arrowFactory = buildArrowFactory(bowData);
            this.isGun = false;
        }
        // Gun-specific
        else if (data instanceof GunWeaponData gunData) {
            this.hasFire = gunData.hasFire;
            this.hasReload = gunData.hasReload;
            if (gunData.hasIdle) animator.addLoopingAnimation(IDLE_ANIM);
            if (gunData.hasFire) animator.addAnimation(FIRE_ANIM);
            if (gunData.hasReload) animator.addAnimation(RELOAD_ANIM);
            this.isGun = true;
        }
        this.hasIdle = data.hasIdle;
    }

    //region GeoAnimatable
    public static class WDRangedWeaponRenderer<T extends WDRangedWeapon> extends GeoItemRenderer<T> {
        public WDRangedWeaponRenderer(ClientModel<T> model, boolean hasEmissive) {
            super(model);

            if (hasEmissive) this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
    }
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final GeoItemRenderer<?> renderer = new WDRangedWeaponRenderer<>(model, hasEmissive);

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        animator.registerControllersFromAnimator(this, registrar);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animator.getCache();
    }
    //endregion

    //region Vanilla Item Overrides
    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity livingEntity) {
        return itemData.useDuration;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return itemData.useAnim;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {
        if (!(entity instanceof Player player)) return;
        if (player.getCooldowns().isOnCooldown(this)) return;
        if (player.isUsingItem()) return;
        if (!hasIdle) return;
        if (animator == null) return;

        animator.playAnimation(this, "idle", itemStack, player, level);
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return itemStack -> itemStack.is(itemData.ammoType);
    }

    @Override
    public int getDefaultProjectileRange() {
        return itemData.projectileRange;
    }

    @Override
    protected void shoot(
            @NotNull ServerLevel level,
            @NotNull LivingEntity shooter,
            @NotNull InteractionHand hand,
            @NotNull ItemStack weapon,
            List<ItemStack> projectileItems,
            float velocity,
            float inaccuracy,
            boolean isCrit,
            @javax.annotation.Nullable LivingEntity target
    ) {
        float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        float angleStep = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
        float centerOffset = (float)((projectileItems.size() - 1) % 2) * angleStep / 2.0F;
        float direction = 1.0F;

        for (int i = 0; i < projectileItems.size(); i++) {
            ItemStack itemstack = projectileItems.get(i);
            if (!itemstack.isEmpty()) {
                float spreadAngle = centerOffset + direction * (float)((i + 1) / 2) * angleStep;
                direction = -direction;
                Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);//todo custom arrow entities don't face the correct direction
                this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, spreadAngle, target);
                level.addFreshEntity(projectile);
                weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
                if (weapon.isEmpty()) {
                    break;
                }
            }
        }
    }

    @Override
    protected void shootProjectile(@NotNull LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.setOwner(shooter);
        projectile.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        projectile.shootFromRotation(
                shooter,
                shooter.getXRot(),
                shooter.getYRot(),
                angle,
                velocity,
                inaccuracy
        );
    }

    @Override
    public void onStopUsing(@NotNull ItemStack stack, @NotNull LivingEntity entity, int count) {
        if (!isGun) {
            model.activateBaseModel();

            if (entity instanceof Player player && player.level() instanceof ServerLevel serverLevel) {
                animator.playAnimation(this, RELEASE_ANIM, stack, player, player.level());
            }
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isGun) {
            if (level.isClientSide) return InteractionResultHolder.pass(stack);
            boolean hasAmmo = !player.getProjectile(stack).isEmpty();

            InteractionResultHolder<ItemStack> ret = onArrowNock(stack, level, player, hand, hasAmmo);
            if (ret != null) return ret;

            if (!player.isCreative() && !hasAmmo) {
                player.displayClientMessage(Component.translatable("wilddungeons.missing_ammo",this.itemData.ammoType.getDescription() ), true);
                return InteractionResultHolder.fail(stack);
            }

            ItemStack ammo = player.getProjectile(stack);
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }

            if (hasFire) {
                animator.playAnimation(this, FIRE_ANIM, stack, player, level);
            }

            GunWeaponData gunData = (GunWeaponData) itemData;
            WDRangedWeapon.ProjectileFactory.spawnProjectile(
                    level,
                    gunData.projectileClass.get(),
                    player,
                    gunData.spawnDistanceOffset,
                    gunData.spawnHeightOffset,
                    gunData.projectileSpeed,
                    projectile -> {
                        if (gunData.projectileName != null && !gunData.projectileName.isEmpty())
                            projectile.setCustomName(Component.literal(gunData.projectileName));
                        if (projectile instanceof SelfGovernedEntity sG) {
                            sG.setFiredDirectionAndSpeed(player.getLookAngle(), gunData.projectileSpeed);
                        }
                    }
            );

            player.getCooldowns().addCooldown(this, gunData.cooldown);
            level.playSound(null, player.blockPosition(), gunData.fireSound.value(), SoundSource.PLAYERS, 1.0f, 1.0f);

            return super.use(level, player, hand);
        } else {
            boolean flag = !player.getProjectile(stack).isEmpty();

            InteractionResultHolder<ItemStack> ret = onArrowNock(stack, level, player, hand, flag);
            if (ret != null) return ret;

            if (!player.getAbilities().instabuild && !flag){
                return InteractionResultHolder.fail(stack);
            }
            else {
                model.activateAltModel();

                if (level instanceof ServerLevel serverLevel) {
                    BowWeaponData bowData = (BowWeaponData) itemData;
                    serverLevel.playSound(null, player, bowData.drawSound.value(), SoundSource.PLAYERS, 1f, 1f);
                    animator.playAnimation(this, DRAW_ANIM, stack, player, level);
                }
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            }
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeLeft) {
        if (!isGun) {
            model.activateBaseModel();

            if (livingEntity instanceof Player player) {
                ItemStack itemstack = player.getProjectile(stack);
                if (!itemstack.isEmpty()) {
                    lastUseDuration = this.getUseDuration(stack, livingEntity) - timeLeft;
                    lastUseDuration = onArrowLoose(stack, level, player, lastUseDuration, !itemstack.isEmpty());
                    if (lastUseDuration < 0) return;
                    float f = getPowerForTime(lastUseDuration);
                    if (!((double)f < 0.1)) {
                        List<ItemStack> list = draw(stack, itemstack, player);
                        if (level instanceof ServerLevel serverlevel && !list.isEmpty()) {
                            this.shoot(serverlevel, player, player.getUsedItemHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
                        }

                        level.playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.ARROW_SHOOT,
                                SoundSource.PLAYERS,
                                1.0F,
                                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
                        );

                        player.awardStat(Stats.ITEM_USED.get(this));
                    }
                }
            }
        }
        // Guns do not use releaseUsing
    }

    @Override
    protected @NotNull Projectile createProjectile(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack weapon, @NotNull ItemStack ammo, boolean isCrit) {
        AbstractArrow arrow = arrowFactory.create(level, shooter);
        arrow.setBaseDamage(2.5);
        arrow.setCritArrow(true);

        float power = getPowerForTime(lastUseDuration);

        arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0f, power * 3.0f, 1.0f);

        return this.customArrow(arrow, ammo, weapon);
    }
    //endregion


    public static Item.Properties buildProperties(BaseProjectileData data) {
        return new Item.Properties()
                .stacksTo(data.stacksTo)
                .rarity(data.rarity)
                .durability(data.durability);
    }

    // Utility class for spawning any registered projectile entity (for guns)
    public static class ProjectileFactory {
        public static <T extends Entity> T spawnProjectile(
                Level level,
                EntityType<T> entityType,
                LivingEntity shooter,
                float distance,
                float heightOffset,
                float speed,
                Consumer<T> config
        ) {
            T projectile = entityType.create(level);
            if (projectile == null) return null;

            var lookVec = shooter.getLookAngle();
            var spawnPos = shooter.getEyePosition()
                    .add(lookVec.scale(distance))
                    .add(0, heightOffset, 0);

            projectile.setPos(spawnPos);
            projectile.setYRot(shooter.getYRot());
            projectile.setXRot(shooter.getXRot());

            projectile.setDeltaMovement(lookVec.normalize().scale(speed));

            if (projectile instanceof net.minecraft.world.entity.Mob mob) {
                mob.setYBodyRot(shooter.getYRot());
                mob.setYHeadRot(shooter.getYRot());
            }

            if (projectile instanceof AbstractArrow a) {
                a.setOwner(shooter);
            }

            if (config != null) {
                config.accept(projectile);
            }

            level.addFreshEntity(projectile);
            return projectile;
        }
    }

    // Utility method to build the arrow factory for bow weapons
    private static ArrowFactory buildArrowFactory(BowWeaponData data) {
        return (level, livingEntity) -> {
            @SuppressWarnings("unchecked")
            EntityType<? extends AbstractArrow> type =
                    (EntityType<? extends AbstractArrow>) data.projectileClass.get();
            return type.create(level);
        };
    }

}
