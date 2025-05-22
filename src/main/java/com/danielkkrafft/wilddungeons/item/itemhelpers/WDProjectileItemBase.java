package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.BaseClasses.ArrowFactory;
import com.danielkkrafft.wilddungeons.entity.BaseClasses.WDArrow;
import com.danielkkrafft.wilddungeons.entity.WindArrow;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
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

import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.world.item.BowItem.getPowerForTime;

public abstract class WDProjectileItemBase extends ProjectileWeaponItem implements GeoAnimatable, GeoItem {

    public String name = "default";

    protected WDItemAnimator animator;
    protected boolean hasIdle = false;
    protected static final int PROJECTILE_RANGE = 15;
    protected ArrowFactory arrowFactory = (level, shooter) -> new Arrow(EntityType.ARROW, level);

    protected int lastUseDuration = 0;

    public WDProjectileItemBase(String name) {
        this(name, new Item.Properties()
                .rarity(Rarity.RARE)
                .durability(1000)
        );
    }

    public WDProjectileItemBase(String name, Properties properties) {
        super(properties);
        this.name = name;
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
            private final BlockEntityWithoutLevelRenderer renderer = new WDProjectileItemBase.WDWeaponRenderer<>();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 100000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int slot, boolean inMainHand) {
        if (entity instanceof Player player && !player.getCooldowns().isOnCooldown(this) && !player.isUsingItem() && hasIdle) {
            animator.playAnimation(this, animator.getAnimationName(0), itemStack, player, level);
        }
    }

    public static class WDWeaponRenderer<T extends WDProjectileItemBase> extends GeoItemRenderer<T> {
        public WDWeaponRenderer() {
            super(new WDProjectileItemBase.WDWeaponModel<T>());
        }
    }

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
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return PROJECTILE_RANGE;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {

        AbstractArrow arrow = arrowFactory.create(level, shooter);
        arrow.setBaseDamage(2.5);
        arrow.setCritArrow(true);

        float power = getPowerForTime(lastUseDuration);

        arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0f, power * 3.0f, 1.0f);

        return this.customArrow(arrow, ammo, weapon);
    }
}
