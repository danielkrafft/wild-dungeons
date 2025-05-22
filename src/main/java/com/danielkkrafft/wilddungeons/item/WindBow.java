package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.WindArrow;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDProjectileItemBase;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.world.item.BowItem.getPowerForTime;
import static net.neoforged.neoforge.event.EventHooks.onArrowLoose;
import static net.neoforged.neoforge.event.EventHooks.onArrowNock;

public class WindBow extends WDProjectileItemBase {

    public static class WindBowModel extends GeoModel<WindBow> {
        @Override
        public ResourceLocation getAnimationResource(WindBow animatable) {
            return WildDungeons.rl("animations/item/wind_bow.animation.json");
        }

        @Override
        public ResourceLocation getModelResource(WindBow animatable) {
            // You can return a default here, like MOD_IDLE, but it’ll be overridden in the renderer.
            return WindBowModel.MOD_IDLE;
        }

        @Override
        public ResourceLocation getTextureResource(WindBow animatable) {
            return WindBowModel.STILL;
        }

        // Optional: Static paths
        public static final ResourceLocation
                MOD_IDLE = WildDungeons.rl("geo/item/wind_bow.geo.json"),
                MOD_NOCKED = WildDungeons.rl("geo/item/wind_bow_nocked.geo.json"),
                STILL = WildDungeons.rl("textures/item/wind_bow.png"),
                CHARGE = WildDungeons.rl("textures/item/wind_bow_charge.png");
    }

    private static final String NAME = "wind_bow";
    private static final WindBowModel WIND_BOW_MODEL = new WindBowModel();

    private enum AnimationList {
        draw,
        release
    }

    public WindBow(){
        super(NAME,
                new Properties()
                        .stacksTo(1)
                        .durability(2000));
        animator.addAnimation(AnimationList.draw.toString());
        animator.addAnimation(AnimationList.release.toString());
        hasIdle = false;
        this.arrowFactory = WindArrow::new; // sets the projectile for this bow to be the wind arrow
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
//        WIND_BOW_MODEL.setTex(WindBowModel.STILL);
//        WIND_BOW_MODEL.setModel(WindBowModel.MOD_IDLE);

        if (entity instanceof Player player && player.level() instanceof ServerLevel serverLevel) {
            animator.playAnimation(this, AnimationList.release.toString(), stack, player, player.level());
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(stack).isEmpty();

        if (!player.getAbilities().instabuild && !flag){
            return InteractionResultHolder.fail(stack);
        }
        else {
//            WIND_BOW_MODEL.setTex(WindBowModel.CHARGE);
//            WIND_BOW_MODEL.setModel(WindBowModel.MOD_NOCKED);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, player, WDSoundEvents.WIND_BOW_DRAW.value(), SoundSource.PLAYERS, 1f, 1f);
                animator.playAnimation(this, AnimationList.draw.toString(), stack, player, level);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
//        WIND_BOW_MODEL.setTex(WindBowModel.STILL);
//        WIND_BOW_MODEL.setModel(WindBowModel.MOD_IDLE);

        if (entityLiving instanceof Player player) {
            ItemStack itemstack = player.getProjectile(stack);
            if (!itemstack.isEmpty()) {
                lastUseDuration = this.getUseDuration(stack, entityLiving) - timeLeft;
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
    protected void shoot(
            ServerLevel level,
            LivingEntity shooter,
            InteractionHand hand,
            ItemStack weapon,
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
}
