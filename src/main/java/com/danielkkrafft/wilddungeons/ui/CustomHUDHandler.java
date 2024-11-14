package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

public class CustomHUDHandler {

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiLayerEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {return;}
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());

        if (!wdPlayer.getRecentEssence().equals("essence:overworld")) {
            if (event.getName().getPath().equals("experience_level") || event.getName().getPath().equals("experience_bar")) {
                event.setCanceled(true);
            }
        }
    }
}
