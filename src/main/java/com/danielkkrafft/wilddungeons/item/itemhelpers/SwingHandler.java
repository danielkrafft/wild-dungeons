package com.danielkkrafft.wilddungeons.item.itemhelpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface SwingHandler {
    void onSwing(Player player, ItemStack itemStack);
}
