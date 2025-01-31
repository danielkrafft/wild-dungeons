package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WDPostDungeonScreen extends Screen {

    public final HashMap<String, DungeonSession.DungeonStats> stats;
    public final List<QuadConsumer<GuiGraphics, Integer, Integer, Boolean>> steps = new ArrayList<>();
    public int step = 0;
    public float ticks = 0.0f;
    public interface QuadConsumer<T, U, V, W> { void accept(T t, U u, V v, W w);}

    public WDPostDungeonScreen(CompoundTag data) {
        super(GameNarrator.NO_TITLE);
        stats = Serializer.fromCompoundTag(data);
        this.steps.add(this::drawTitleBackground);
        this.steps.add(this::drawTitle);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x800d0f18);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        //WildDungeons.getLogger().info("ON STEP {} ON TICK {}", this.step, this.ticks);
        this.ticks += partialTick;
        for (int i = 0; i <= step; i++) {
            steps.get(i).accept(guiGraphics, mouseX, mouseY, i == step);
        }
    }

    final float DRAW_TITLE_BACKGROUND_TICKS = 10f;
    public void drawTitleBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean animate) {
        float drawTicks = animate ? this.ticks : DRAW_TITLE_BACKGROUND_TICKS;

        int maxX = Mth.lerpInt(Math.min(drawTicks/DRAW_TITLE_BACKGROUND_TICKS, 1), 0, this.width);

        guiGraphics.fill(0, this.height / 10, maxX, this.height / 5, 0x800d0f18);

        if (animate && drawTicks >= DRAW_TITLE_BACKGROUND_TICKS * 1.5) {
            this.step++;
            this.ticks = 0f;
        }
    }

    final float DRAW_TITLE_TICKS = 20f;
    public void drawTitle(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean animate) {
        float drawTicks = animate ? this.ticks : DRAW_TITLE_TICKS;

        String finalTitle = "WAIT OF THE WORLD";
        String title = finalTitle.substring(0, Mth.lerpInt(Math.min(drawTicks/DRAW_TITLE_TICKS, 1), 0, finalTitle.length()));
        if (drawTicks < DRAW_TITLE_TICKS) title = title + WDFont.getRandomGlitch() + WDFont.getRandomGlitch();

        int bgSize = (this.height / 5) - (this.height / 10);
        int padding = (int) (0.25f * bgSize);

        int minY = this.height / 10 + padding;
        int maxY = this.height / 5 - padding;

        float ratio = (float) WDFont.width(title) / 7;
        int height = maxY - minY;
        float width = ratio * height;

        int minX = (int) ((this.width / 2.0f) - (width / 2.0f));
        int maxX = (int) ((this.width / 2.0f) + (width / 2.0f));

        WildDungeons.getLogger().info("DRAWING STRING {} WITH RATIO {} TO WIDTH {} AND HEIGHT {} (COMPARED TO ORIGINAL WIDTH {} AND HEIGHT {})", title, ratio, width, height, this.font.width(title), this.font.lineHeight);

        WDFont.drawString(guiGraphics, title, minX, minY, maxX, maxY, 0xFFFFFFFF);

    }


}
