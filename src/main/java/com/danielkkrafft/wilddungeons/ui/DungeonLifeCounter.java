package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.HexFormat;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = WildDungeons.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class DungeonLifeCounter implements LayeredDraw.Layer {
    public static final DungeonLifeCounter INSTANCE = new DungeonLifeCounter();

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(Minecraft.getInstance().player);
        if (Minecraft.getInstance().player.jumpableVehicle() == null && wdPlayer.getCurrentDungeon() != null) {
            this.renderLifeIcon(guiGraphics, deltaTracker, 10, 10);
            this.renderLifeCount(guiGraphics, deltaTracker, 42, 26 - Minecraft.getInstance().font.lineHeight / 2, wdPlayer);
        }
    }

    public void renderLifeIcon(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int x, int y) {

        RenderSystem.enableBlend();
        guiGraphics.blitSprite(WildDungeons.rl("hud/totem"), x, y, 32, 32);
        RenderSystem.disableBlend();

    }

    public void renderLifeCount(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int x, int y, WDPlayer wdPlayer) {
        int lives = wdPlayer.getCurrentLives();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float scale = 2.0f;
        poseStack.scale(scale, scale, scale);
        int adjustedX = (int) (x / scale);
        int adjustedY = (int) (y / scale);
        guiGraphics.drawString(Minecraft.getInstance().font, " X "+lives, adjustedX-1, adjustedY, 0, false);
        guiGraphics.drawString(Minecraft.getInstance().font, " X "+lives, adjustedX+1, adjustedY, 0, false);
        guiGraphics.drawString(Minecraft.getInstance().font, " X "+lives, adjustedX, adjustedY-1, 0, false);
        guiGraphics.drawString(Minecraft.getInstance().font, " X "+lives, adjustedX, adjustedY+1, 0, false);
        guiGraphics.drawString(Minecraft.getInstance().font, " X "+lives, adjustedX, adjustedY, HexFormat.fromHexDigits("ffffff"), false);
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR, WildDungeons.rl("essence_bar"), EssenceBar.INSTANCE);
        event.registerAbove(VanillaGuiLayers.HOTBAR, WildDungeons.rl("life_counter"), DungeonLifeCounter.INSTANCE);
    }
}
