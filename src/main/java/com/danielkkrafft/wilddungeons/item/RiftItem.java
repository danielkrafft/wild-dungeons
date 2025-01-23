package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.Offering;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

public class RiftItem extends Item {

    public RiftItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResultHolder.pass(context.getPlayer().getItemInHand(context.getHand())).getResult();
        int cost = context.getPlayer().isCreative() ? 0 : 2;
        Offering offering = new Offering(context.getLevel(), Offering.Type.RIFT, 1, "random", Offering.CostType.XP_LEVEL, cost);
        Vec3 clickLocation = context.getClickLocation().add(0.0,0.5,0.0);
        offering.setPos(new Vec3(Math.round(clickLocation.x*2.0)/2.0, Math.round(clickLocation.y*2.0)/2.0, Math.round(clickLocation.z*2.0)/2.0));
        WildDungeons.getLogger().info("SPAWNING RIFT AT {}", offering.position());
        context.getLevel().addFreshEntity(offering);
        return InteractionResultHolder.pass(context.getPlayer().getItemInHand(context.getHand())).getResult();
    }

}
