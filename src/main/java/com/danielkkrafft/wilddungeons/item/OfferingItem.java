package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.room.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.ShopTables;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OfferingItem extends Item {

    public OfferingItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResultHolder.pass(context.getPlayer().getItemInHand(context.getHand())).getResult();
        Offering offering = ShopTables.BASIC_TABLE.randomResults(1, 5, 3.0f).getFirst().asOffering(context.getLevel());
        Vec3 clickLocation = context.getClickLocation().add(0.0,0.5,0.0);
        offering.setPos(new Vec3(Math.round(clickLocation.x*2.0)/2.0, Math.round(clickLocation.y*2.0)/2.0, Math.round(clickLocation.z*2.0)/2.0));
        WildDungeons.getLogger().info("SPAWNING OFFERING AT {}", offering.position());
        context.getLevel().addFreshEntity(offering);
        return InteractionResultHolder.pass(context.getPlayer().getItemInHand(context.getHand())).getResult();
    }

}
