package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

public class AmogusStaff extends WDWeapon {
    public AmogusStaff() {
        super("amogus_staff", new Item.Properties()
                .durability(80));
    }

    public AmogusStaff(Properties properties) {
        super("amogus_staff", properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        EquipmentSlot slot = usedHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        player.getItemInHand(usedHand).hurtAndBreak(1, player, slot);
        if (level.isClientSide()) return InteractionResult.PASS;
        HitResult hitResult = player.pick(5, 1, false);
        Vec3 pos = hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getLocation() : player.position();
        summonEntity((ServerLevel) level, WDEntities.AMOGUS.get(), pos);
        return InteractionResult.CONSUME;
    }

    //TODO - Implement correctly for 1.21.11
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }
}
