package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.world.item.Items.ARROW;

public abstract class WDWeapon extends Item implements GeoAnimatable, GeoItem {
    public final String name;
    protected final WDItemAnimator animator;
    protected final ClientModel<WDWeapon> model;
    protected boolean hasIdle = true;
    protected boolean hasEmissive = false;
    protected Predicate<ItemStack> ammoPredicate = s -> false;
    protected int projectileRange = 0;

    protected EntityType<? extends AbstractArrow> arrowType = EntityType.ARROW; // default
    protected int lastUseDuration = 0;

    protected WDWeapon(String name) {
        this(name, new Properties());
    }

    protected WDWeapon(String name, Properties properties) {
        super(properties);
        this.name = name;

        this.model = new ClientModel<>(name, "item");
        this.animator = new WDItemAnimator(name, this);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);

        configureModel(model);
        configureAnimator(animator);
    }

    protected void configureModel(ClientModel<WDWeapon> model) {}

    protected void configureAnimator(WDItemAnimator animator) {}

    /* -- overrides -- */

    protected int getMaxUseDuration() {
        return 72000; // same as bow
    }

    protected UseAnim getDefaultUseAnim() {
        return UseAnim.NONE;
    }

    @Override
    public final int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return getMaxUseDuration();
    }

    @Override
    public final @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return getDefaultUseAnim();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;
        if (!hasIdle) return;
        if (player.getCooldowns().isOnCooldown(this)) return;
        if (player.isUsingItem()) return;
        if (animator == null) return;

        String idleName = animator.getAnimationName(0);
        if (idleName != null) {
            animator.playAnimation(this, idleName, stack, player, level);
        }
    }

    /* -- ranged logic -- */

    protected Predicate<ItemStack> getAllSupportedProjectiles() {
        return ammoPredicate != null ? ammoPredicate : (stack -> false);
    }

    protected ItemStack findAmmo(Player player) {
        Predicate<ItemStack> isAmmo = getAllSupportedProjectiles();

        ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
        if (isAmmo.test(off)) return off;

        ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (isAmmo.test(main)) return main;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isAmmo.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    protected List<ItemStack> decrementAmmo(ItemStack ammo, LivingEntity shooter) {
        if (ammo.isEmpty()) return List.of();

        ItemStack shot = ammo.copyWithCount(1);

        if (!shooter.hasInfiniteMaterials()) {
            ammo.shrink(1);
            if (ammo.isEmpty() && shooter instanceof Player player) {
                player.getInventory().removeItem(ammo);
            }
        }

        return List.of(shot);
    }

    protected Projectile createProjectile(Level level, LivingEntity owner, boolean isCrit) {
        AbstractArrow arrow = arrowType.create(level);
        if (arrow == null) return null;

        arrow.setOwner(owner);
        if (isCrit) arrow.setCritArrow(true);

        return arrow;
    }
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, float velocity, float inaccuracy, float spreadAngleDegrees) {
        projectile.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + spreadAngleDegrees, 0.0f, velocity, inaccuracy);
    }

    protected <E extends Entity> E summonEntity(ServerLevel level, EntityType<E> type, Vec3 pos) {
        E e = type.create(level);
        if (e == null) return null;

        e.setPos(pos);

        level.addFreshEntity(e);
        return e;
    }

    /* -- rendering -- */

    public static class WDWeaponRenderer<T extends WDWeapon> extends GeoItemRenderer<T> {
        public WDWeaponRenderer(ClientModel<T> model, boolean hasEmissive) {
            super(model);
            if (hasEmissive) this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new WDWeaponRenderer<>(model, hasEmissive);

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        animator.registerControllersFromAnimator(this, controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animator.getCache();
    }
}
