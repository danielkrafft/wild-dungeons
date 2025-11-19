package com.danielkkrafft.wilddungeons.network.packets;

import com.danielkkrafft.wilddungeons.entity.ThrownNautilusShield;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class ThrowNautilusShieldHandler {
    public class Client {
        public static void handleDataOnNetwork(ThrowNautilusShieldPayload data, final IPayloadContext context) {
            Player player = context.player();

            ItemStack stack = player.getUseItem();



            ThrownNautilusShield shield = new ThrownNautilusShield(player.level(), data.isLoyal(), data.canTrack(), player,stack.copy());
            shield.setPos(player.position().add(player.getLookAngle().scale(1.2)).add(0,0.5f,0));

            stack.shrink(1);
            player.level().addFreshEntity(shield);


        }


    }
}
