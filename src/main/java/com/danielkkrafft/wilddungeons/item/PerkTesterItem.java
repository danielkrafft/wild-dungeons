package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplatePoolRegistry;
import com.danielkkrafft.wilddungeons.entity.Offering;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

public class PerkTesterItem extends Item {

    public PerkTesterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResultHolder.pass(context.getPlayer().getItemInHand(context.getHand())).getResult();
        Offering offering = OfferingTemplatePoolRegistry.FREE_PERK_POOL.getRandom().asOffering(context.getLevel());
        offering.setCostAmount(0);
        Vec3 clickLocation = context.getClickLocation().add(0.0,0.0,0.0);
        offering.setPos(new Vec3(Math.round(clickLocation.x*2.0)/2.0, Math.round(clickLocation.y*2.0)/2.0, Math.round(clickLocation.z*2.0)/2.0));
        context.getLevel().addFreshEntity(offering);
        return InteractionResultHolder.pass(context.getPlayer().getItemInHand(context.getHand())).getResult();
    }
}
