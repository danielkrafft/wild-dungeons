package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.dungeon.components.Alignments;
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
        if (Minecraft.getInstance().player.jumpableVehicle() == null && Minecraft.getInstance().gameMode.hasExperience() && !wdPlayer.getRecentEssence().equals("essence:overworld")) {
            this.renderEssenceBar(guiGraphics, deltaTracker, i, wdPlayer);
            this.renderEssenceLevel(guiGraphics, deltaTracker, wdPlayer);
        }
    }

    private void renderEssenceBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int x, WDPlayer wdPlayer) {
        String key = wdPlayer.getRecentEssence();

        float progress = (float) (wdPlayer.getEssenceLevel(key) % 1.0);
        int j = 182;
        int k = (int) ((wdPlayer.getEssenceLevel(key) % 1.0) * 183.0F);
        int l = guiGraphics.guiHeight() - 32 + 3;

        RenderSystem.enableBlend();

        guiGraphics.blitSprite(Alignments.ALIGNMENTS.get(key.split(":")[1]).ESSENCE_BAR_BACKGROUND_SPRITE(), x, l, 182, 5);
        if (k > 0) {
            guiGraphics.blitSprite(Alignments.ALIGNMENTS.get(key.split(":")[1]).ESSENCE_BAR_PROGRESS_SPRITE(), 182, 5, 0, 0, x, l, k, 5);
        }

        RenderSystem.disableBlend();
    }

    private void renderEssenceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, WDPlayer wdPlayer) {
        int level = Mth.floor(wdPlayer.getEssenceLevel(wdPlayer.getRecentEssence()));
        String key = wdPlayer.getRecentEssence();

        Gui gui = Minecraft.getInstance().gui;
        if (level > 0) {

            String s = level + "";
            int j = (guiGraphics.guiWidth() - gui.getFont().width(s)) / 2;
            int k = guiGraphics.guiHeight() - 31 - 4;
            guiGraphics.drawString(gui.getFont(), s, j + 1, k, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j - 1, k, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j, k + 1, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j, k - 1, 0, false);
            guiGraphics.drawString(gui.getFont(), s, j, k, Alignments.ALIGNMENTS.get(key.split(":")[1]).FONT_COLOR(), false);
        }
    }
}
