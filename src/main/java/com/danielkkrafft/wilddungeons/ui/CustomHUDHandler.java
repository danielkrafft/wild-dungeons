package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = WildDungeons.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class CustomHUDHandler {

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiLayerEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {return;}
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateClientWDPlayer(player);

        if (!wdPlayer.getRecentEssence().equals(EssenceOrb.Type.OVERWORLD)) {
            if (event.getName().getPath().equals("experience_level") || event.getName().getPath().equals("experience_bar")) {
                event.setCanceled(true);
            }
        }

        if (event.getName().getPath().equals("crosshair") ) {
            event.setCanceled(ItemPreviewTooltipLayer.INSTANCE.shouldCancelRender());
        }
    }
}
