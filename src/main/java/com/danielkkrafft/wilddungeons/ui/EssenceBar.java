package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EssenceBar implements LayeredDraw.Layer {
    public static final EssenceBar INSTANCE = new EssenceBar();

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateClientWDPlayer(Minecraft.getInstance().player);
        int i = guiGraphics.guiWidth() / 2 - 91;
        if (Minecraft.getInstance().player.jumpableVehicle() == null && Minecraft.getInstance().gameMode.hasExperience() && !wdPlayer.getRecentEssence().equals(EssenceOrb.Type.OVERWORLD)) {
            this.renderEssenceBar(guiGraphics, deltaTracker, i, wdPlayer);
            this.renderEssenceLevel(guiGraphics, deltaTracker, wdPlayer);
        }
    }

    private void renderEssenceBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int x, WDPlayer wdPlayer) {
        EssenceOrb.Type type = wdPlayer.getRecentEssence();

        float progress = (float) (wdPlayer.getEssenceLevel(type) % 1.0);
        int j = 182;
        int k = (int) ((wdPlayer.getEssenceLevel(type) % 1.0) * 183.0F);
        int l = guiGraphics.guiHeight() - 32 + 3;

        RenderSystem.enableBlend();

        guiGraphics.blitSprite(EssenceOrb.getBarBackground(type), x, l, 182, 5);
        if (k > 0) {
            guiGraphics.blitSprite(EssenceOrb.getBarProgress(type), 182, 5, 0, 0, x, l, k, 5);
        }

        RenderSystem.disableBlend();
    }

    private void renderEssenceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, WDPlayer wdPlayer) {
        int level = Mth.floor(wdPlayer.getEssenceLevel(wdPlayer.getRecentEssence()));
        EssenceOrb.Type type = wdPlayer.getRecentEssence();

        Gui gui = Minecraft.getInstance().gui;
        if (level > 0) {

            String s = level + "";
            int j = (guiGraphics.guiWidth() - gui.getFont().width(s)) / 2;
            int k = guiGraphics.guiHeight() - 31 - 4;
            guiGraphics.drawString(gui.getFont(), s, j + 1, k, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j - 1, k, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j, k + 1, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j, k - 1, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j, k, EssenceOrb.getFontColor(type), false);
        }
    }
}
