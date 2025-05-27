package com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData;

import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDProjectileItemBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.danielkkrafft.wilddungeons.WildDungeons.*;

public abstract class AbstractProjectileParent<

        SelfT extends AbstractProjectileParent<SelfT, DataT, ModelT>,
        DataT extends BaseProjectileData,
        ModelT extends AbstractProjectileParent.ProjectileRenderModel<SelfT>>
        extends WDProjectileItemBase {

    protected DataT itemData;

    protected ModelT itemModel;

    public AbstractProjectileParent(
            DataT data,
            ModelT model,
            Item.Properties properties
    ) {
        super(data.name, generateRenderer(model), properties);
        itemModel = model;
        itemData = data;
        hasIdle = data.hasIdle;
    }

    public ProjectileRenderModel<?> getItemModel() { return itemModel; }

    protected static Item.Properties buildProperties(BaseItemData newItemDataBase) {
        return new Item.Properties()
                .stacksTo(newItemDataBase.stacksTo)
                .rarity(newItemDataBase.rarity)
                .durability(newItemDataBase.durability);
    }

    public static <
            T extends AbstractProjectileParent<T, ?, ?>,
            ModelT extends ProjectileRenderModel<T>
            > GeoItemRenderer<T> generateRenderer(ModelT model) {

        return new FactoryRenderer<>(model);
    }

    public static class FactoryRenderer<T extends AbstractProjectileParent<T, ?, ?>> extends GeoItemRenderer<T> {
        public FactoryRenderer(ProjectileRenderModel<T> model) {
            super((GeoModel<T>) model);
        }
    }

    public static class ProjectileRenderModel<T extends AbstractProjectileParent<T, ?, ?>> extends ClientModel<T> {

        public ResourceLocation BASE_MODEL;
        public ResourceLocation BASE_TEXTURE;

        public ProjectileRenderModel(String animations, String model, String texture) {
            super(makeAnimationRL(animations), makeGeoModelRL(model), makeItemTextureRL(texture));
            this.BASE_MODEL = makeGeoModelRL(model);
            this.BASE_TEXTURE = makeItemTextureRL(texture);
        }
        public ResourceLocation getModel() {
            return this.model;
        }

        public ResourceLocation getTexture() {
            return this.texture;
        }

        public ResourceLocation getAnimation() {
            return this.animation;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return itemData.useDuration;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return itemData.useAnim;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return itemData.ammoType;
    }

    @Override
    public int getDefaultProjectileRange() {
        return itemData.projectileRange;
    }

    @Override
    protected void shootProjectile(
            @NotNull LivingEntity livingEntity,
            @NotNull Projectile projectile,
            int i, float v, float v1,
            float v2,
            @Nullable LivingEntity livingEntity1
    ) {}

    @Override
    public void releaseUsing(
            ItemStack stack,
            Level level,
            LivingEntity livingEntity,
            int timeLeft
    ) {}

    // Utility class for spawning any registered projectile entity
    public class ProjectileFactory {

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

            // Calculate forward spawn position based on look vector
            Vec3 lookVec = shooter.getLookAngle();
            Vec3 spawnPos = shooter.getEyePosition()
                    .add(lookVec.scale(distance))
                    .add(0, heightOffset, 0);

            projectile.setPos(spawnPos);
            projectile.setYRot(shooter.getYRot());
            projectile.setXRot(shooter.getXRot());

            // Set movement direction and speed
            projectile.setDeltaMovement(lookVec.normalize().scale(speed));

            // Align visual body rotation for mobs
            if (projectile instanceof Mob mob) {
                mob.setYBodyRot(shooter.getYRot());
                mob.setYHeadRot(shooter.getYRot());
            }

            if (projectile instanceof AbstractArrow a) {
                a.setOwner(shooter);
            }

            // Allow custom configuration (name, effects, tags, etc.)
            if (config != null) {
                config.accept(projectile);
            }

            level.addFreshEntity(projectile);
            return projectile;
        }
    }

}
