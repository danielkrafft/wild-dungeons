package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.network.serverbound.ServerboundRestorePlayerGamemodePacket;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.*;

public class WDPostDungeonScreen extends Screen {

    public final HashMap<String, DungeonSession.DungeonStats> stats;
    public final float clearTicks;
    public final float targetTicks = 600;
    public final int clearSeconds;
    public final int clearMinutes;
    public final int clearHours;
    public final boolean perfectTime;

    public final int clearDeaths;
    public final int targetDeaths = 0;
    public final boolean perfectDeaths;

    public final int clearScore;
    public final List<Pair<Integer, ResourceLocation>> clearScores = new ArrayList<>();
    public final int targetScore = 100000;
    public final boolean perfectScore;
    public final ResourceLocation defaultSkin;

    public final List<AnimationStep> steps = new ArrayList<>();
    public int step = 0;
    public float ticks = 0.0f;
    int xOffset = 0;
    public interface QuadConsumer<T, U, V, W> { void accept(T t, U u, V v, W w);}

    public static class AnimationStep {
        public final float minXRatio;
        public final float minYRatio;
        public final float maxXRatio;
        public final float maxYRatio;
        public final float animTicks;
        public final float totalTicks;
        public QuadConsumer<GuiGraphics, Integer, Integer, AnimationStep> drawLogic;
        public Screen screen;
        public float drawTicks;
        public int id = 0;

        public AnimationStep(float minXRatio, float minYRatio, float maxXRatio, float maxYRatio, float animTicks, float totalTicks, QuadConsumer<GuiGraphics, Integer, Integer, AnimationStep> drawLogic, Screen screen) {
            this.minXRatio = minXRatio;
            this.minYRatio = minYRatio;
            this.maxXRatio = maxXRatio;
            this.maxYRatio = maxYRatio;
            this.animTicks = animTicks;
            this.totalTicks = totalTicks;
            this.drawLogic = drawLogic;
            this.screen = screen;
        }

        public AnimationStep copy(int id) {
            AnimationStep copiedStep = new AnimationStep(this.minXRatio, this.minYRatio, this.maxXRatio, this.maxYRatio, this.animTicks, this.totalTicks, this.drawLogic, this.screen);
            copiedStep.id = id;
            return copiedStep;
        }

        public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean animate, WDPostDungeonScreen screen) {
            this.drawTicks = animate ? screen.ticks : this.animTicks;
            drawLogic.accept(guiGraphics, mouseX, mouseY, this);
            if (animate && this.drawTicks >= this.totalTicks) {
                screen.step++;
                screen.ticks = 0f;
            }
        }

        public int ySize() {return (int) (screen.height*maxYRatio - screen.height*minYRatio);}
        public int xSize() {return (int) (screen.width*maxXRatio - screen.width*minXRatio);}
        public int yCenter() {return ((int) (screen.height*minYRatio) + (int) (ySize() / 2.0f));}
        public int xCenter() {return ((int) (screen.width*minXRatio) + (int) (xSize() / 2.0f));}
        public int minX() {return (int) (screen.width * minXRatio);}
        public int minY() {return (int) (screen.height * minYRatio);}
        public int maxX() {return (int) (screen.width * maxXRatio);}
        public int maxY() {return (int) (screen.height * maxYRatio);}
    }

    public WDPostDungeonScreen(CompoundTag data) {
        super(GameNarrator.NO_TITLE);
        defaultSkin = Minecraft.getInstance().getSkinManager().getInsecureSkin(Minecraft.getInstance().getGameProfile()).texture();
        stats = Serializer.fromCompoundTag(data);
        clearTicks = stats == null ? 300 : this.stats.values().stream().sorted(Comparator.comparingInt(o -> o.time)).toList().getLast().time;
        clearSeconds = Mth.floor((clearTicks / 20) % 60);
        clearMinutes = Mth.floor((clearTicks / 1200) % 60);
        clearHours = Mth.floor((clearTicks / 72000) % 24);
        perfectTime = targetTicks / clearTicks >= 1;

        clearDeaths = stats == null ? 0 : this.stats.values().stream().mapToInt(o -> o.deaths).sum();
        perfectDeaths = clearDeaths <= targetDeaths;

        if (this.stats != null) this.stats.forEach((key, value) -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(UUID.fromString(key));
            ResourceLocation skin = null;
            if (player != null) {
                GameProfile profile = player.getGameProfile();
                skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile).texture();
            }
            this.clearScores.add(new Pair<>(value.getScore(), skin));
        });
        if (this.stats == null) {
            clearScores.add(new Pair<>(100, defaultSkin));
            clearScores.add(new Pair<>(1000, defaultSkin));
            clearScores.add(new Pair<>(3000, defaultSkin));
        }

        clearScores.sort(Comparator.comparingInt(Pair::getFirst));
        clearScore = stats == null ? 758930 : clearScores.stream().mapToInt(Pair::getFirst).sum();
        perfectScore = clearScore / targetScore >= 1;

        this.steps.add(TITLE_BACKGROUND);
        this.steps.add(TITLE);
        this.steps.add(ICON_BACKGROUND);
        this.steps.add(ICON);

        this.steps.add(TIME_BACKGROUND);
        this.steps.add(TIME);
        if (this.perfectTime) this.steps.add(PERFECT_TIME);

        this.steps.add(DEATHS_BACKGROUND);
        this.steps.add(DEATHS);
        if (this.perfectDeaths) this.steps.add(PERFECT_DEATHS);

        this.steps.add(SCORE_BACKGROUND);
        this.steps.add(SCORE);
        if (this.perfectScore) this.steps.add(PERFECT_SCORE);

        if (this.perfectTime && this.perfectDeaths && this.perfectScore) this.steps.add(PERFECT_CLEAR);

        if (this.clearScores.size() > 1) {
            this.steps.add(SWIPE_TO_CHART);
            this.steps.add(CHART_BACKGROUND);
            for (int i = 0; i < this.clearScores.size(); i++) {
                this.steps.add(CHART_ENTRY.copy(i));
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x800d0f18);
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketDistributor.sendToServer(new ServerboundRestorePlayerGamemodePacket(new CompoundTag()));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.ticks += partialTick;
        int size = this.steps.size();
        for (int i = 0; i <= step; i++) {
            if (i >= size) return;
            steps.get(i).draw(guiGraphics, mouseX, mouseY, i == step, this);
        }
    }

    public final AnimationStep TITLE_BACKGROUND = new AnimationStep(0.0f, 0.1f, 1.0f, 0.2f, 10f, 15f, this::drawTitleBackground, this);
    public void drawTitleBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        int maxX = Mth.lerpInt(Math.min(step.drawTicks/step.animTicks, 1), 0, this.width);
        guiGraphics.fill(0, step.minY(), maxX, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep TITLE = new AnimationStep(TITLE_BACKGROUND.minXRatio, TITLE_BACKGROUND.minYRatio, TITLE_BACKGROUND.maxXRatio, TITLE_BACKGROUND.maxYRatio, 20f, 30f, this::drawTitle, this);
    public void drawTitle(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        String finalTitle = "WAIT OF THE WORLD";
        String title = finalTitle.substring(0, Mth.lerpInt(Math.min(step.drawTicks/step.animTicks, 1), 0, finalTitle.length()));

        if (step.drawTicks < step.animTicks) title = title + WDFont.getRandomGlitch() + WDFont.getRandomGlitch();
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter(), step.yCenter(), step.ySize()/2, 0xFFFFFFFF);
    }

    public final AnimationStep ICON_BACKGROUND = new AnimationStep(0.0f, 0.05f, 0.1f, 0.15f, 20f, 30f, this::drawIconBackground, this);
    public void drawIconBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        int maxX = this.width / 10;
        guiGraphics.fill(step.minX(), step.minY(), maxX, step.maxY(), 0xFFFFFFFF);
    }

    public final AnimationStep ICON = new AnimationStep(ICON_BACKGROUND.minXRatio, ICON_BACKGROUND.minYRatio, ICON_BACKGROUND.maxXRatio, ICON_BACKGROUND.maxYRatio, 20f, 30f, this::drawIcon, this);
    public void drawIcon(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        String title = "1-4";
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter(), step.yCenter(), step.ySize()/2, 0xFFFF0000);
    }

    public final AnimationStep TIME_BACKGROUND = new AnimationStep(0.35f, 0.38f, 0.65f, 0.52f, 20f, 30f, this::drawTimeBackground, this);
    public void drawTimeBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        float ratio = Math.min(this.targetTicks / this.clearTicks, 1.0f);
        guiGraphics.fill(step.minX() + xOffset, step.minY(), (int) (step.minX() + step.xSize() * ratio + xOffset), step.maxY(), 0x80FF0000);
        guiGraphics.fill(step.minX() + xOffset, step.minY(), step.maxX() + xOffset, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep TIME = new AnimationStep(TIME_BACKGROUND.minXRatio, TIME_BACKGROUND.minYRatio, TIME_BACKGROUND.maxXRatio, TIME_BACKGROUND.maxYRatio, 20f, 30f, this::drawTime, this);
    public void drawTime(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        String title = "Time: ";
        if (clearHours < 10) title += "0";
        title += clearHours + ":";
        if (clearMinutes < 10) title += "0";
        title += clearMinutes + ":";
        if (clearSeconds < 10) title += "0";
        title += clearSeconds;

        WDFont.drawCenteredString(guiGraphics, title, step.xCenter() + xOffset, step.yCenter(), step.ySize()/5, 0xFFFFFFFF);
    }

    public final AnimationStep PERFECT_TIME = new AnimationStep(TIME_BACKGROUND.minXRatio, TIME_BACKGROUND.minYRatio, TIME_BACKGROUND.maxXRatio, TIME_BACKGROUND.maxYRatio, 20f, 30f, this::drawPerfectTime, this);
    public void drawPerfectTime(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), step.maxX() + xOffset, step.maxY(), 0xFFFF0000);
        drawTime(guiGraphics, mouseX, mouseY, TIME);
    }

    public final AnimationStep DEATHS_BACKGROUND = new AnimationStep(TIME_BACKGROUND.minXRatio, 0.53f, TIME_BACKGROUND.maxXRatio, 0.67f, 20f, 30f, this::drawDeathsBackground, this);
    public void drawDeathsBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), step.maxX() + xOffset, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep DEATHS = new AnimationStep(DEATHS_BACKGROUND.minXRatio, DEATHS_BACKGROUND.minYRatio, DEATHS_BACKGROUND.maxXRatio, DEATHS_BACKGROUND.maxYRatio, 20f, 30f, this::drawDeaths, this);
    public void drawDeaths(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        String title = "Deaths: " + clearDeaths;
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter() + xOffset, step.yCenter(), step.ySize()/5, 0xFFFFFFFF);
    }

    public final AnimationStep PERFECT_DEATHS = new AnimationStep(DEATHS_BACKGROUND.minXRatio, DEATHS_BACKGROUND.minYRatio, TIME_BACKGROUND.maxXRatio, DEATHS_BACKGROUND.maxYRatio, 20f, 30f, this::drawPerfectDeaths, this);
    public void drawPerfectDeaths(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), step.maxX() + xOffset, step.maxY(), 0xFFFF0000);
        drawDeaths(guiGraphics, mouseX, mouseY, DEATHS);
    }

    public final AnimationStep SCORE_BACKGROUND = new AnimationStep(TIME_BACKGROUND.minXRatio, 0.68f, TIME_BACKGROUND.maxXRatio, 0.82f, 20f, 30f, this::drawScoreBackground, this);
    public void drawScoreBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        float ratio = Math.min((float) this.clearScore / this.targetScore, 1.0f);
        guiGraphics.fill(step.minX() + xOffset, step.minY(), (int) (step.minX() + ((step.xSize()) * ratio)) + xOffset, step.maxY(), 0x80FF0000);
        guiGraphics.fill(step.minX() + xOffset, step.minY(), step.maxX() + xOffset, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep SCORE = new AnimationStep(SCORE_BACKGROUND.minXRatio, SCORE_BACKGROUND.minYRatio, SCORE_BACKGROUND.maxXRatio, SCORE_BACKGROUND.maxYRatio, 20f, 30f, this::drawScore, this);
    public void drawScore(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        String title = "Score: " + clearScore;
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter() + xOffset, step.yCenter(), step.ySize()/5, 0xFFFFFFFF);
    }

    public final AnimationStep PERFECT_SCORE = new AnimationStep(SCORE_BACKGROUND.minXRatio, SCORE_BACKGROUND.minYRatio, SCORE_BACKGROUND.maxXRatio, SCORE_BACKGROUND.maxYRatio, 20f, 30f, this::drawPerfectScore, this);
    public void drawPerfectScore(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), step.maxX() + xOffset, step.maxY(), 0xFFFF0000);
        drawScore(guiGraphics, mouseX, mouseY, SCORE);
    }

    public final AnimationStep PERFECT_CLEAR = new AnimationStep(TITLE_BACKGROUND.minXRatio, TITLE_BACKGROUND.minYRatio, TITLE_BACKGROUND.maxXRatio, TITLE_BACKGROUND.maxYRatio, 20f, 30f, this::drawPerfectClear, this);
    public void drawPerfectClear(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        guiGraphics.fill(step.minX(), step.minY(), step.maxX(), step.maxY(), 0xFFFF0000);
        drawTitle(guiGraphics, mouseX, mouseY, TITLE);
        drawIconBackground(guiGraphics, mouseX, mouseY, ICON_BACKGROUND);
        drawIcon(guiGraphics, mouseX, mouseY, ICON);
    }

    public final AnimationStep SWIPE_TO_CHART = new AnimationStep(0.0f, 0.0f, 0.0f, 0.0f, 20f, 30f, this::swipeToChart, this);
    public void swipeToChart(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        this.xOffset = -this.width;
    }

    public final AnimationStep CHART_BACKGROUND = new AnimationStep(0.25f, 0.3f, 0.75f, 1.0f, 20f, 30f, this::drawChartBackground, this);
    public void drawChartBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        guiGraphics.fill(step.minX() + xOffset + this.width, step.minY(), step.maxX() + xOffset + this.width, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep CHART_ENTRY = new AnimationStep(CHART_BACKGROUND.minXRatio, CHART_BACKGROUND.minYRatio, CHART_BACKGROUND.maxXRatio, CHART_BACKGROUND.maxYRatio, 20f, 30f, this::drawChartEntry, this);
    public void drawChartEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step)
    {
        int padding = (int) (step.xSize()*0.05f);
        int barWidth = ((step.xSize()-padding*3) / this.clearScores.size()) - padding;

        int minX = step.minX() + padding*2 + (step.id * (barWidth + padding));
        int maxX = minX + barWidth;

        float ratio = (float) clearScores.get(step.id).getFirst() /clearScores.getLast().getFirst();

        int minY = Mth.lerpInt(ratio, step.maxY() - padding*2, step.minY() + padding*2);
        int maxY = step.maxY() - padding*2;

        guiGraphics.fill(minX + xOffset + this.width, minY, maxX + xOffset + this.width, maxY, 0xFFFF0000);
        drawCenteredSquare(guiGraphics, clearScores.get(step.id).getSecond(), minX + ((maxX-minX)/2), maxY, padding*2, 8f/64f, 8f/64f, 16f/64f, 16f/64f, 0xFFFFFFFF);
        WDFont.drawCenteredString(guiGraphics, String.valueOf(clearScores.get(step.id).getFirst()), minX + ((maxX-minX)/2), Math.min(minY-padding, maxY-padding*2), padding, 0xFFFFFFFF);
    }

    public static void drawTexturedQuad(GuiGraphics guiGraphics, ResourceLocation texture, int minX, int minY, int maxX, int maxY, float minU, float minV, float maxU, float maxV, int color) {
        PoseStack poseStack = guiGraphics.pose();
        Matrix4f pose = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(pose, minX, maxY, 0.0f).setUv(minU, maxV).setColor(color);
        buffer.addVertex(pose, maxX, maxY, 0.0f).setUv(maxU, maxV).setColor(color);
        buffer.addVertex(pose, maxX, minY, 0.0f).setUv(maxU, minV).setColor(color);
        buffer.addVertex(pose, minX, minY, 0.0f).setUv(minU, minV).setColor(color);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void drawCenteredSquare(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int size, float minU, float minV, float maxU, float maxV, int color) {
        drawTexturedQuad(guiGraphics, texture, x-size/2, y-size/2, x+size/2, y+size/2, minU, minV, maxU, maxV, color);
    }
}
