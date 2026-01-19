package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.BlackHole;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDItemAnimator;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StarCannon extends WDWeapon {
    private static final String NAME = "star_cannon";

    private static final int DURABILITY = 2000;
    private static final int RANGE = 50;
    private static final int COOLDOWN_TICKS = 20;
    private static final float SPAWN_DISTANCE = 2.0f;
    private static final float SPAWN_HEIGHT = 0.0f;
    private static final float PROJECTILE_SPEED = 3.0f;

    public StarCannon() {
        super(NAME, new Properties()
                .stacksTo(1)
                .durability(DURABILITY)
                .rarity(Rarity.EPIC)
        );

        this.hasIdle = false;
        this.hasEmissive = true;

        this.ammoPredicate = stack -> stack.is(Items.NETHER_STAR);
        this.projectileRange = RANGE;
    }

    @Override
    protected void configureAnimator(WDItemAnimator animator) {
        animator.addAnimation("fire");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        ItemStack ammo = findAmmo(player);
        boolean hasAmmo = !ammo.isEmpty();

        if (!player.getAbilities().instabuild && !hasAmmo) {
            player.displayClientMessage(Component.translatable("wilddungeons.missing_ammo", Items.NETHER_STAR.getDescription()), true);
            return InteractionResultHolder.fail(stack);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.consume(stack);
        }

        // consume 1 nether star
        if (!player.getAbilities().instabuild && hasAmmo) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        }

        // spawn Black Hole
        spawnProjectile((ServerLevel) level, WDEntities.BLACK_HOLE.get(), player, SPAWN_DISTANCE, SPAWN_HEIGHT, PROJECTILE_SPEED, e -> {e.setCustomName(Component.literal("Black Hole"));
                    if (e instanceof BlackHole blackHole) {
                        var look = player.getLookAngle();
                        blackHole.setFiredDirectionAndSpeed(look, PROJECTILE_SPEED);
                    }
                }
        );

        animator.playAnimation(this, "fire", stack, player, level);
        level.playSound(null, player.blockPosition(), SoundEvents.SHULKER_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.consume(stack);
    }

}
