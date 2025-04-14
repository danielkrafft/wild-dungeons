package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.AmogusEntity;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AmogusStaff extends Item {
    public AmogusStaff() {
        super(new Item.Properties()
                .durability(80));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        EquipmentSlot slot = usedHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        player.getItemInHand(usedHand).hurtAndBreak(1, player, slot);
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        AmogusEntity ae = new AmogusEntity(WDEntities.AMOGUS.get(), level);
        HitResult hitResult = player.pick(5, 1, false);
        Vec3 pos = hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getLocation() : player.position();
        ae.setPos(pos);
        level.addFreshEntity(ae);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }
}
