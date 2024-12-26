package com.danielkkrafft.wilddungeons.util.debug;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DebugItem extends Item {

    public DebugItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(usedHand));

        WildDungeons.getLogger().info("CURRENT DUNGEON SESSIONS: {}", DungeonSessionManager.getInstance().getSessionNames());
        WildDungeons.getLogger().info("CURRENT WDPlayers: {}", WDPlayerManager.getInstance().getPlayerNames(level.getServer()));
        WildDungeons.getLogger().info("CURRENT DUNGEON: {}", WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getCurrentDungeon());
        WildDungeons.getLogger().info("CURRENT FLOOR: {}", WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getCurrentFloor());

        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

}
