package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WDWindChargeItem extends Item
{
    public WDWindChargeItem()
    {
        super(new Properties().stacksTo(64));
    }
    @Override@NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level lvl, @NotNull Player p, @NotNull InteractionHand hand)
    {
        ItemStack it=p.getItemInHand(hand);
        p.getCooldowns().addCooldown(this,10);
        if(!lvl.isClientSide)
        {
            WindChargeProjectile proj= WDEntities.WIND_CHARGE_PROJECTILE.value().create(lvl);
            if(proj!=null)
            {
                proj.defaultCharge(false,false,new Vec3(2.5,2.5,2.5),p);
                proj.moveTo(new Vec3(p.getX(),p.getEyeY(),p.getZ()));
                lvl.addFreshEntity(proj);
            }
        }
        p.awardStat(Stats.ITEM_USED.get(this));
        if(!p.getAbilities().instabuild)it.shrink(1);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS,it);
    }
}
