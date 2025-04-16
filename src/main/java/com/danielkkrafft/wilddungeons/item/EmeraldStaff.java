package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.EmeraldWisp;
import com.danielkkrafft.wilddungeons.entity.LargeEmeraldWisp;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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
        //check the players inventory for an emerald, if they have one, remove it
        //and spawn a wisp
        boolean hasEmerald = false;
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
        EquipmentSlot slot = usedHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        player.getItemInHand(usedHand).hurtAndBreak(1, player, slot);
        HitResult hitResult = player.pick(5, 1, false);
        Vec3 pos = hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getLocation() : player.position();

        EmeraldWisp wisp = WDEntities.SMALL_EMERALD_WISP.get().create(level);

        if (wisp != null) {
            wisp.setPos(pos);
            wisp.setOwner(player);
            level.addFreshEntity(wisp);
            player.getCooldowns().addCooldown(this, 20); // 1 second cooldown
        }
        return super.use(level, player, usedHand);
    }
}
