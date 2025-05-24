package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.BaseClasses.ArrowFactory;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.BowWeaponData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Predicate;

import static com.danielkkrafft.wilddungeons.WildDungeons.*;
import static net.minecraft.world.item.BowItem.getPowerForTime;

public class BaseBowWeapon extends WDProjectileItemBase {

    private BowWeaponData bowData;

    public static final String DRAW_ANIM = "draw";
    public static final String RELEASE_ANIM = "release";

    private static BowFactoryModel<BaseBowWeapon> bowModel;

    public BaseBowWeapon(BowWeaponData newBowWeaponData) {
        super(
                newBowWeaponData.name,
                generateRenderer(newBowWeaponData),
                buildProperties(newBowWeaponData)
        );

        initializeBow(newBowWeaponData);
    }

    public static GeoItemRenderer<BaseBowWeapon> generateRenderer(BowWeaponData newBowWeaponData) {

        bowModel = new BowFactoryModel<BaseBowWeapon>(newBowWeaponData);
        return new BowFactoryRenderer<BaseBowWeapon>(bowModel);
    }

    public static class BowFactoryRenderer<T extends BaseBowWeapon> extends GeoItemRenderer<T> {
        public BowFactoryRenderer(ClientModel<T> bowModel) {
            super((GeoModel<T>) bowModel);
        }
    }

    private ArrowFactory buildArrowFactory() {
        return (level, livingEntity) -> {
            try {
                Constructor<?> ctor = Class.forName("com.danielkkrafft.wilddungeons.entity." + bowData.arrowClass)
                        .getConstructor(Level.class, LivingEntity.class);
                return (AbstractArrow) ctor.newInstance(level, livingEntity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Item.Properties buildProperties(BowWeaponData newBowData) {
        return new Item.Properties()
                .stacksTo(newBowData.stacksTo)
                .rarity(newBowData.bowRarity)
                .durability(newBowData.durability);
    }

    private void initializeBow(BowWeaponData newBowWeaponData) {

        bowData = newBowWeaponData;
        animator.addAnimation(DRAW_ANIM);
        animator.addAnimation(RELEASE_ANIM);
        hasIdle = false;
        this.arrowFactory = buildArrowFactory();
    }

    public static class BowFactoryModel<T extends BaseBowWeapon> extends ClientModel<T> {

        public static ResourceLocation STILL_MODEL;
        public static ResourceLocation STILL_TEXTURE;
        public static ResourceLocation CHARGED_MODEL;
        public static ResourceLocation CHARGED_TEXTURE;

        public BowFactoryModel(BowWeaponData newBowData) {
            this(
                    makeAnimationRL(newBowData.bowAnimations),
                    STILL_MODEL = makeGeoModelRL(newBowData.bowModelStill),
                    STILL_TEXTURE = makeItemTextureRL(newBowData.bowTextureStill));

            CHARGED_MODEL = makeGeoModelRL(newBowData.bowModelCharged);
            CHARGED_TEXTURE = makeItemTextureRL(newBowData.bowTextureCharged);
        }

        protected BowFactoryModel(ResourceLocation animations, ResourceLocation stillModel, ResourceLocation stillTexture) {
            super(animations, stillModel, stillTexture);
        }
    }

    @Override
    public void onStopUsing(@NotNull ItemStack stack, @NotNull LivingEntity entity, int count) {
        bowModel.setModel(BowFactoryModel.STILL_MODEL);
        bowModel.setTex(BowFactoryModel.STILL_TEXTURE);

        if (entity instanceof Player player && player.level() instanceof ServerLevel serverLevel) {
            animator.playAnimation(this, RELEASE_ANIM, stack, player, player.level());
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(stack).isEmpty();

        InteractionResultHolder<ItemStack> ret = net.neoforged.neoforge.event.EventHooks.onArrowNock(stack, level, player, hand, flag);
        if (ret != null) return ret;

        if (!player.getAbilities().instabuild && !flag){
            return InteractionResultHolder.fail(stack);
        }
        else {
            bowModel.setModel(BowFactoryModel.CHARGED_MODEL);
            bowModel.setTex(BowFactoryModel.CHARGED_TEXTURE);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, player, bowData.drawSound.value(), SoundSource.PLAYERS, 1f, 1f);
                animator.playAnimation(this, DRAW_ANIM, stack, player, level);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        bowModel.setModel(BowFactoryModel.STILL_MODEL);
        bowModel.setTex(BowFactoryModel.STILL_TEXTURE);

        if (livingEntity instanceof Player player) {
            ItemStack itemstack = player.getProjectile(stack);
            if (!itemstack.isEmpty()) {
                lastUseDuration = this.getUseDuration(stack, livingEntity) - timeLeft;
                lastUseDuration = net.neoforged.neoforge.event.EventHooks.onArrowLoose(stack, level, player, lastUseDuration, !itemstack.isEmpty());
                if (lastUseDuration < 0) return;
                float f = getPowerForTime(lastUseDuration);
                WildDungeons.getLogger().info("PowerForTime: {}", f);
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

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return bowData.useDuration;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return bowData.useAnim;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return bowData.ammoType;
    }

    @Override
    public int getDefaultProjectileRange() {
        return bowData.projectileRange;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

    }
}
