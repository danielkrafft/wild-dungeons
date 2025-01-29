package com.danielkkrafft.wilddungeons.ui;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class WDLoadingScreen extends Screen {
    public WDLoadingScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderPanorama(guiGraphics, partialTick);
    }
}
