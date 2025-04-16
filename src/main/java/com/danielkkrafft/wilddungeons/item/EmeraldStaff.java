package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.EmeraldWisp;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmeraldStaff extends WDWeapon{
    public static final String NAME = "emerald_staff";
    public enum AnimationList { idle, summon }

    public EmeraldStaff() {
        super(NAME);
        this.addLoopingAnimation(AnimationList.idle.toString());
        this.addAnimation(AnimationList.summon.toString());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        //check the players inventory for an emerald, if they have one, remove it and spawn a wisp
        boolean hasEmerald = player.isCreative();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.EMERALD)) {
                stack.shrink(1);
                hasEmerald = true;
                break;
            }
        }
        if (!hasEmerald) {
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }

        this.setAnimation(AnimationList.summon.toString(), player.getItemInHand(usedHand), player, level);
        // Find an open space around the player with solid ground beneath
        BlockPos pos = player.blockPosition();
        for (int i = 0; i < 20; i++) { // Try up to 20 times
            int offsetX = level.random.nextIntBetweenInclusive(-1, 1); // Random offset in x direction
            int offsetY = level.random.nextIntBetweenInclusive(1, 2); // Random offset in y direction
            int offsetZ = level.random.nextIntBetweenInclusive(-1, 1); // Random offset in z direction
            BlockPos testPos = pos.offset(offsetX, offsetY, offsetZ);

            // Check for a valid spawn location
            if (level.getBlockState(testPos).isAir() &&
                    level.getBlockState(testPos.above()).isAir()) {
                pos = testPos;
                break;
            }
        }

        EmeraldWisp wisp = WDEntities.FRIENDLY_EMERALD_WISP.get().create(level);

        if (wisp != null) {
            wisp.setPos(pos.getCenter());

            wisp.setYRot(player.getYRot());
            wisp.setXRot(player.getXRot());
            wisp.setYBodyRot(player.getYRot());
            wisp.setYHeadRot(player.getYRot());

            Vec3 lookVec = player.getLookAngle();
            wisp.lookAt(EntityAnchorArgument.Anchor.EYES,
                    player.getEyePosition().add(lookVec.scale(5.0)));

            wisp.setOwner(player);
            level.addFreshEntity(wisp);
            player.getCooldowns().addCooldown(this, 20); // 1 second cooldown
            level.playSound(null, pos, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.wilddungeons.emerald_staff_1"));
        tooltipComponents.add(Component.translatable("tooltip.wilddungeons.emerald_staff_2"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
