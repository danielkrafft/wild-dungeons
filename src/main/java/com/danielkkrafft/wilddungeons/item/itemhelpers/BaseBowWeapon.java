package com.danielkkrafft.wilddungeons.item.itemhelpers;

import com.danielkkrafft.wilddungeons.entity.BaseClasses.ArrowFactory;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.AbstractProjectileParent;
import com.danielkkrafft.wilddungeons.item.itemhelpers.ItemData.BowWeaponData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.danielkkrafft.wilddungeons.WildDungeons.makeGeoModelRL;
import static com.danielkkrafft.wilddungeons.WildDungeons.makeItemTextureRL;
import static net.minecraft.world.item.BowItem.getPowerForTime;
import static net.neoforged.neoforge.event.EventHooks.onArrowLoose;
import static net.neoforged.neoforge.event.EventHooks.onArrowNock;

public class BaseBowWeapon extends AbstractProjectileParent<BaseBowWeapon, BowWeaponData, BaseBowWeapon.BowFactoryModel> {

    public static final String DRAW_ANIM = "draw";
    public static final String RELEASE_ANIM = "release";

    public BaseBowWeapon(BowWeaponData newBowWeaponData) {
        super(
                newBowWeaponData,
                new BowFactoryModel(newBowWeaponData),
                buildProperties(newBowWeaponData)
        );

        animator.addAnimation(DRAW_ANIM);
        animator.addAnimation(RELEASE_ANIM);
        this.arrowFactory = buildArrowFactory();
    }

    public static class BowFactoryModel extends ProjectileRenderModel<BaseBowWeapon> {

        public ResourceLocation CHARGED_MODEL;
        public ResourceLocation CHARGED_TEXTURE;

        public BowFactoryModel(BowWeaponData data) {
            super(data.animations, data.baseModel, data.baseTexture);

            CHARGED_MODEL = makeGeoModelRL(data.bowModelCharged);
            CHARGED_TEXTURE = makeItemTextureRL(data.bowTextureCharged);
        }
    }

    private ArrowFactory buildArrowFactory() {
        return (level, livingEntity) -> {
            @SuppressWarnings("unchecked")
            EntityType<? extends AbstractArrow> type =
                    (EntityType<? extends AbstractArrow>) itemData.projectileClass.get();

            return type.create(level);
        };
    }

    @Override
    public void onStopUsing(@NotNull ItemStack stack, @NotNull LivingEntity entity, int count) {
        itemModel.setModel(itemModel.BASE_MODEL);
        itemModel.setTex(itemModel.BASE_TEXTURE);

        if (entity instanceof Player player && player.level() instanceof ServerLevel serverLevel) {
            animator.playAnimation(this, RELEASE_ANIM, stack, player, player.level());
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(stack).isEmpty();

        InteractionResultHolder<ItemStack> ret = onArrowNock(stack, level, player, hand, flag);
        if (ret != null) return ret;

        if (!player.getAbilities().instabuild && !flag){
            return InteractionResultHolder.fail(stack);
        }
        else {
            itemModel.setModel(itemModel.CHARGED_MODEL);
            itemModel.setTex(itemModel.CHARGED_TEXTURE);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, player, itemData.drawSound.value(), SoundSource.PLAYERS, 1f, 1f);
                animator.playAnimation(this, DRAW_ANIM, stack, player, level);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        itemModel.setModel(itemModel.BASE_MODEL);
        itemModel.setTex(itemModel.BASE_TEXTURE);

        if (livingEntity instanceof Player player) {
            ItemStack itemstack = player.getProjectile(stack);
            if (!itemstack.isEmpty()) {
                lastUseDuration = this.getUseDuration(stack, livingEntity) - timeLeft;
                lastUseDuration = onArrowLoose(stack, level, player, lastUseDuration, !itemstack.isEmpty());
                if (lastUseDuration < 0) return;
                float f = getPowerForTime(lastUseDuration);
//                WildDungeons.getLogger().info("PowerForTime: {}", f);
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
}
