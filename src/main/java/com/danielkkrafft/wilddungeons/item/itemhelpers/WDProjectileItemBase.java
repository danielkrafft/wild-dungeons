package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.BaseClasses.ArrowFactory;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.world.item.BowItem.getPowerForTime;

public abstract class WDProjectileItemBase extends ProjectileWeaponItem implements GeoAnimatable, GeoItem {

    public String name = "default";

    protected WDItemAnimator animator;
    protected boolean hasIdle = false;
    protected static final int PROJECTILE_RANGE = 15;
    protected ArrowFactory arrowFactory = (level, shooter) -> new Arrow(EntityType.ARROW, level);
    private final GeoItemRenderer<?> projectileItemRenderer;

    protected int lastUseDuration = 0;

    public WDProjectileItemBase(String name, GeoItemRenderer<?> renderer) {
        this(
                name,
                renderer,
                new Item.Properties()
                .rarity(Rarity.RARE)
                .durability(1000)
        );
    }

    public WDProjectileItemBase(String name, GeoItemRenderer<?> renderer, Properties properties) {
        super(properties);
        this.name = name;
        projectileItemRenderer = renderer;
        animator = new WDItemAnimator(name, this);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {

        animator.registerControllersFromAnimator(this, registrar);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {

        return animator.getCache();
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {

        consumer.accept(new GeoRenderProvider() {
            private final GeoItemRenderer<?> renderer = projectileItemRenderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public abstract int getUseDuration(ItemStack stack, LivingEntity livingEntity);

    @Override
    public abstract @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack);

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {
        if (entity instanceof Player player && !player.getCooldowns().isOnCooldown(this) && !player.isUsingItem() && hasIdle) {
            animator.playAnimation(this, animator.getAnimationName(0), itemStack, player, level);
        }
    }

//    public static class WDWeaponRenderer<T extends WDProjectileItemBase> extends GeoItemRenderer<T> {
//        public WDWeaponRenderer() {
//            super(new WDProjectileItemBase.WDWeaponModel<T>());
//        }
//    }

    public static class WDWeaponModel<T extends WDProjectileItemBase> extends GeoModel<T> {

        @Override @SuppressWarnings("removal") // Must be overridden, warning is unavoidable
        public ResourceLocation getModelResource(T animatable) {
            return WildDungeons.rl("geo/" + animatable.name + ".geo.json");
        }

        @Override @SuppressWarnings("removal") // Must be overridden, warning is unavoidable
        public ResourceLocation getTextureResource(T animatable) {
            return WildDungeons.rl("textures/item/" + animatable.name + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(T animatable) {
            return WildDungeons.rl("animations/" + animatable.name + ".animation.json");
        }
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return PROJECTILE_RANGE;
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
        float f1 = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
        float f2 = (float)((projectileItems.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for (int i = 0; i < projectileItems.size(); i++) {
            ItemStack itemstack = projectileItems.get(i);
            if (!itemstack.isEmpty()) {
                float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
                f3 = -f3;
                Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);
                this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, f4, target);
                level.addFreshEntity(projectile);
                weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
                if (weapon.isEmpty()) {
                    break;
                }
            }
        }
    }

    @Override
    public abstract void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft);

    @Override
    protected @NotNull Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {

        AbstractArrow arrow = arrowFactory.create(level, shooter);
        arrow.setBaseDamage(2.5);
        arrow.setCritArrow(true);

        float power = getPowerForTime(lastUseDuration);

        arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0f, power * 3.0f, 1.0f);

        return this.customArrow(arrow, ammo, weapon);
    }
}
