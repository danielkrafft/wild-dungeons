package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemAnimator;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WindBow extends WDWeapon {
    private static final String NAME = "wind_bow";
    private static final int DURABILITY = 2000;
    private static final int RANGE = 18;
    private static final int MAX_USE = 72000; // or 7200 if you want that exact number
    public static final String DRAW_ANIM = "draw";
    public static final String RELEASE_ANIM = "release";
    private boolean drawing = false;

    public WindBow() {
        super(NAME, new Properties()
                .stacksTo(1)
                .durability(DURABILITY)
                .rarity(Rarity.EPIC)
        );

        this.hasIdle = false;
        configureBow(Items.ARROW, RANGE, WDEntities.WIND_ARROW.get());
    }

    @Override
    protected int getMaxUseDuration() {
        return MAX_USE;
    }

    @Override
    protected UseAnim getDefaultUseAnim() {
        return UseAnim.BOW;
    }

    @Override
    protected void configureModel(ClientModel<WDWeapon> model) {
        model.withConditionalResources(this::isDrawing, "wind_bow_charge", "wind_bow_nocked", "item");
    }

    private boolean isDrawing(WDWeapon w) {
        return this.drawing;
    }

    @Override
    protected void configureAnimator(WDItemAnimator animator) {
        animator.addAnimation(DRAW_ANIM);
        animator.addAnimation(RELEASE_ANIM);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        boolean hasAmmo = !findAmmo(player).isEmpty();
        if (!player.getAbilities().instabuild && !hasAmmo) {
            return InteractionResultHolder.fail(stack);
        }

        this.drawing = true;

        if (!level.isClientSide) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), WDSoundEvents.WIND_BOW_DRAW, SoundSource.PLAYERS, 1.0f, 1.0f);
            animator.playAnimation(this, DRAW_ANIM, stack, player, level);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) return;

        this.drawing = false;

        ItemStack ammo = findAmmo(player);
        if (ammo.isEmpty() && !player.getAbilities().instabuild) return;

        lastUseDuration = getUseDuration(stack, livingEntity) - timeLeft;
        float power = BowItem.getPowerForTime(lastUseDuration);
        if ((double) power < 0.1) return;

        List<ItemStack> shots = decrementAmmo(stack, ammo, player);
        if (shots.isEmpty()) return;

        if (!level.isClientSide) {
            animator.playAnimation(this, RELEASE_ANIM, stack, player, level);

            float spreadStep = shots.size() == 1 ? 0.0F : 6.0F;

            for (int i = 0; i < shots.size(); i++) {
                ItemStack ammoStack = shots.get(i);
                if (ammoStack.isEmpty()) continue;

                float spreadAngle = -((shots.size() - 1) * spreadStep * 0.5f) + i * spreadStep;

                Projectile projectile = createArrowProjectile(level, player, stack, ammoStack, power == 1.0F);
                if (projectile == null) continue;

                shootProjectile(player, projectile, power * 3.0F, 1.0f, spreadAngle);
                level.addFreshEntity(projectile);

                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                if (stack.isEmpty()) break;
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }
    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int timeLeft) {
        this.drawing = false;
    }
}