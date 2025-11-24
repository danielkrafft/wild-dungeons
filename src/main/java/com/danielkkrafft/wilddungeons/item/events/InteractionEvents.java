package com.danielkkrafft.wilddungeons.item.events;

import com.danielkkrafft.wilddungeons.entity.ThrownNautilusShield;
import com.danielkkrafft.wilddungeons.item.NautilusShieldItem;
import com.danielkkrafft.wilddungeons.network.packets.ThrowNautilusShieldPayload;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

//This class is there just cause minecraft dosen't want to register the left click on items
@EventBusSubscriber
public class InteractionEvents {
    private static boolean wasAttackPressed = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.level == null || player == null) return;

        boolean attackPressed = mc.options.keyAttack.isDown();

        boolean justPressed = attackPressed && !wasAttackPressed;

        if (justPressed) {
            if (player.isUsingItem()) {
                ItemStack stack = player.getUseItem();
                if (stack.getItem() instanceof NautilusShieldItem) {
                    System.out.println("IS THROWN");
                    boolean loyal = false, cantrack = false;
                    player.playSound(WDSoundEvents.NAUTILUS_SHIELD_THROW.value());
                    player.connection.send(new ThrowNautilusShieldPayload(loyal,cantrack,
                            player.getUsedItemHand() == InteractionHand.OFF_HAND));
                }
            }
        }

        wasAttackPressed = attackPressed;
    }
}
