package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.network.ServerPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WDPostDungeonScreen extends Screen {

    public final HashMap<String, DungeonSession.DungeonStats> stats;
    public final HashMap<String, DungeonSession.DungeonSkinDataHolder> skins;
    public String title;
    public String icon;
    public int primaryColor;
    public int secondaryColor;
    public int progressColor;
    public int targetTime;
    public int targetDeaths;
    public int targetScore;

    public final float clearTicks;
    public final int clearSeconds;
    public final int clearMinutes;
    public final int clearHours;
    public final boolean perfectTime;

    public final int clearDeaths;
    public final boolean perfectDeaths;

    public final int clearScore;
    public final List<Pair<Integer, CompletableFuture<PlayerSkin>>> clearScores = new ArrayList<>();
    public final boolean perfectScore;
    public final PlayerSkin defaultSkin;

    public final List<AnimationStep> steps = new ArrayList<>();
    public int step = 0;
    private long timestamp;
    private float elapsedMs = 0;
    int xOffset = 0;
    public interface QuadConsumer<T, U, V, W> { void accept(T t, U u, V v, W w);}

    public static class AnimationStep {
        public final float minXRatio;
        public final float minYRatio;
        public final float maxXRatio;
        public final float maxYRatio;
        public final int animMs;
        public final int totalMs;
        public float drawMs;
        public QuadConsumer<GuiGraphics, Integer, Integer, AnimationStep> drawLogic;
        public Screen screen;
        public int id = 0;

        public AnimationStep(float minXRatio, float minYRatio, float maxXRatio, float maxYRatio, int animMs, int totalMs, QuadConsumer<GuiGraphics, Integer, Integer, AnimationStep> drawLogic, Screen screen) {
            this.minXRatio = minXRatio;
            this.minYRatio = minYRatio;
            this.maxXRatio = maxXRatio;
            this.maxYRatio = maxYRatio;
            this.animMs = animMs;
            this.totalMs = totalMs;
            this.drawLogic = drawLogic;
            this.screen = screen;
        }

        public AnimationStep copy(int id) {
            AnimationStep copiedStep = new AnimationStep(this.minXRatio, this.minYRatio, this.maxXRatio, this.maxYRatio, this.animMs, this.totalMs, this.drawLogic, this.screen);
            copiedStep.id = id;
            return copiedStep;
        }

        public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean animate, WDPostDungeonScreen screen) {
            this.drawMs = animate ? screen.elapsedMs : this.animMs;
            drawLogic.accept(guiGraphics, mouseX, mouseY, this);
            if (animate && this.drawMs >= this.totalMs) {
                screen.step++;
                screen.elapsedMs = 0;
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
        public float completion() {return Math.min(this.drawMs/this.animMs, 1);}
    }

    public WDPostDungeonScreen(CompoundTag data) {
        super(GameNarrator.NO_TITLE);
        timestamp = System.currentTimeMillis();
        defaultSkin = Minecraft.getInstance().getSkinManager().getInsecureSkin(Minecraft.getInstance().getGameProfile());

        DungeonSession.DungeonStatsHolder holder = Serializer.fromCompoundTag(data);
        this.stats = holder == null ? null : holder.playerStats;
        this.skins = holder == null ? null : holder.playerSkins;
        this.title = holder == null ? "WAIT OF THE WORLD" : holder.title;
        this.icon = holder == null ? "1-4" : holder.icon;
        this.primaryColor = holder == null ? 0xFFFF0000 : holder.primaryColor;
        this.secondaryColor = holder == null ? 0xFFFF0000 : holder.secondaryColor;
        int alpha = (primaryColor >> 24) & 0xFF;
        alpha /= 2;
        progressColor = (alpha << 24) | (primaryColor & 0x00FFFFFF);
        this.targetTime = holder == null ? 1200 : holder.targetTime;
        this.targetDeaths = holder == null ? 0 : holder.targetDeaths;
        this.targetScore = holder == null ? 1000 : holder.targetScore;

        clearTicks = stats == null ? 300 : this.stats.values().stream().sorted(Comparator.comparingInt(o -> o.time)).toList().getLast().time;
        clearSeconds = Mth.floor((clearTicks / 20) % 60);
        clearMinutes = Mth.floor((clearTicks / 1200) % 60);
        clearHours = Mth.floor((clearTicks / 72000) % 24);
        perfectTime = this.targetTime / clearTicks >= 1;

        clearDeaths = stats == null ? 0 : this.stats.values().stream().mapToInt(o -> o.deaths).sum();
        perfectDeaths = clearDeaths <= targetDeaths;

        if (this.stats != null) {
            for (Map.Entry<String, DungeonSession.DungeonStats> entry : this.stats.entrySet()) {
                String uuid = entry.getKey();
                DungeonSession.DungeonStats dungeonStats = entry.getValue();
                DungeonSession.DungeonSkinDataHolder dataHolder = this.skins.get(uuid);
                Property property = dataHolder == null ? null : new Property(dataHolder.name, dataHolder.value, dataHolder.signature);
                this.clearScores.add(new Pair<>(dungeonStats.getScore(), Minecraft.getInstance().getSkinManager().skinCache.getUnchecked(new SkinManager.CacheKey(UUID.fromString(uuid), property))));
            }
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
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ServerPacketHandler.Packets.RESTORE_PLAYER_GAMEMODE.toString());
        PacketDistributor.sendToServer(new SimplePacketManager.ServerboundTagPacket(tag));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        float elapsed = System.currentTimeMillis() - timestamp;
        timestamp = System.currentTimeMillis();
        this.elapsedMs += elapsed;
        int size = this.steps.size();
        for (int i = 0; i <= step; i++) {
            if (i >= size) return;
            steps.get(i).draw(guiGraphics, mouseX, mouseY, i == step, this);
        }
    }

    public final AnimationStep TITLE_BACKGROUND = new AnimationStep(0.0f, 0.1f, 1.0f, 0.2f, 500, 750, this::drawTitleBackground, this);

    public void drawTitleBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        int maxX = Mth.lerpInt(Math.min(step.drawMs / step.animMs, 1), 0, this.width);
        guiGraphics.fill(0, step.minY(), maxX, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep TITLE = new AnimationStep(TITLE_BACKGROUND.minXRatio, TITLE_BACKGROUND.minYRatio, TITLE_BACKGROUND.maxXRatio, TITLE_BACKGROUND.maxYRatio, 500, 750, this::drawTitle, this);
    public String tempTitle = "";

    public void drawTitle(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        String finalTitle = this.title;
        String title = finalTitle.substring(0, Mth.lerpInt(step.completion(), 0, finalTitle.length()));
        if (!title.equals(tempTitle))
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(WDSoundEvents.UI_BEEP.value(), 1.0f, 0.3f));
        tempTitle = title;

        if (step.drawMs < step.animMs) title = title + WDFont.getRandomGlitch() + WDFont.getRandomGlitch();
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter(), step.yCenter(), step.ySize() / 2, 0xFFFFFFFF);
    }

    public final AnimationStep ICON_BACKGROUND = new AnimationStep(0.0f, 0.05f, 0.1f, 0.15f, 250, 250, this::drawIconBackground, this);

    public void drawIconBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        int maxX = Mth.lerpInt(step.completion(), step.minX(), step.maxX());
        guiGraphics.fill(step.minX(), step.minY(), maxX, step.maxY(), 0xFFFFFFFF);
    }

    public final AnimationStep ICON = new AnimationStep(ICON_BACKGROUND.minXRatio, ICON_BACKGROUND.minYRatio, ICON_BACKGROUND.maxXRatio, ICON_BACKGROUND.maxYRatio, 250, 250, this::drawIcon, this);

    public void drawIcon(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        String finalTitle = this.icon;
        String title = finalTitle.substring(0, Mth.lerpInt(step.completion(), 0, finalTitle.length()));
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter(), step.yCenter(), step.ySize() / 2, this.primaryColor);
    }

    public final AnimationStep TIME_BACKGROUND = new AnimationStep(0.35f, 0.38f, 0.65f, 0.52f, 500, 750, this::drawTimeBackground, this);

    public void drawTimeBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        float ratio = Math.min(this.targetTime / this.clearTicks, 1.0f);
        int maxX1 = Mth.lerpInt(step.completion(), step.minX(), step.maxX());
        int maxX2 = Mth.lerpInt(step.completion(), step.minX(), (int) (step.minX() + step.xSize() * ratio));
        guiGraphics.fill(step.minX() + xOffset, step.minY(), maxX2 + xOffset, step.maxY(), progressColor);
        guiGraphics.fill(step.minX() + xOffset, step.minY(), maxX1 + xOffset, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep TIME = new AnimationStep(TIME_BACKGROUND.minXRatio, TIME_BACKGROUND.minYRatio, TIME_BACKGROUND.maxXRatio, TIME_BACKGROUND.maxYRatio, 500, 750, this::drawTime, this);

    public void drawTime(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        int hours = (int) (clearHours * step.completion());
        int minutes = (int) (clearMinutes * step.completion());
        int seconds = (int) (clearSeconds * step.completion());

        String title = "Time: ";
        if (hours < 10) title += "0";
        title += hours + ":";
        if (minutes < 10) title += "0";
        title += minutes + ":";
        if (seconds < 10) title += "0";
        title += seconds;

        WDFont.drawCenteredString(guiGraphics, title, step.xCenter() + xOffset, step.yCenter(), step.ySize() / 5, 0xFFFFFFFF);
    }

    public final AnimationStep PERFECT_TIME = new AnimationStep(TIME_BACKGROUND.minXRatio, TIME_BACKGROUND.minYRatio, TIME_BACKGROUND.maxXRatio, TIME_BACKGROUND.maxYRatio, 500, 750, this::drawPerfectTime, this);

    public void drawPerfectTime(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), Mth.lerpInt(step.completion(), step.minX(), step.maxX()) + xOffset, step.maxY(), this.primaryColor);
        drawTime(guiGraphics, mouseX, mouseY, TIME);
    }

    public final AnimationStep DEATHS_BACKGROUND = new AnimationStep(TIME_BACKGROUND.minXRatio, 0.53f, TIME_BACKGROUND.maxXRatio, 0.67f, 500, 750, this::drawDeathsBackground, this);

    public void drawDeathsBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), (int) ((step.completion() * step.xSize()) + step.minX() + xOffset), step.maxY(), 0x800d0f18);
    }

    public final AnimationStep DEATHS = new AnimationStep(DEATHS_BACKGROUND.minXRatio, DEATHS_BACKGROUND.minYRatio, DEATHS_BACKGROUND.maxXRatio, DEATHS_BACKGROUND.maxYRatio, 500, 750, this::drawDeaths, this);

    public void drawDeaths(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        String title = "Deaths: " + (int) (this.clearDeaths * step.completion());
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter() + xOffset, step.yCenter(), step.ySize() / 5, 0xFFFFFFFF);
    }

    public final AnimationStep PERFECT_DEATHS = new AnimationStep(DEATHS_BACKGROUND.minXRatio, DEATHS_BACKGROUND.minYRatio, TIME_BACKGROUND.maxXRatio, DEATHS_BACKGROUND.maxYRatio, 500, 750, this::drawPerfectDeaths, this);

    public void drawPerfectDeaths(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), Mth.lerpInt(step.completion(), step.minX(), step.maxX()) + xOffset, step.maxY(), this.primaryColor);
        drawDeaths(guiGraphics, mouseX, mouseY, DEATHS);
    }

    public final AnimationStep SCORE_BACKGROUND = new AnimationStep(TIME_BACKGROUND.minXRatio, 0.68f, TIME_BACKGROUND.maxXRatio, 0.82f, 500, 750, this::drawScoreBackground, this);

    public void drawScoreBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        float ratio = Math.min((float) this.clearScore / this.targetScore, 1.0f);
        int maxX1 = Mth.lerpInt(step.completion(), step.minX(), step.maxX());
        int maxX2 = Mth.lerpInt(step.completion(), step.minX(), (int) (step.minX() + step.xSize() * ratio));
        guiGraphics.fill(step.minX() + xOffset, step.minY(), maxX2 + xOffset, step.maxY(), progressColor);
        guiGraphics.fill(step.minX() + xOffset, step.minY(), maxX1 + xOffset, step.maxY(), 0x800d0f18);
    }

    public final AnimationStep SCORE = new AnimationStep(SCORE_BACKGROUND.minXRatio, SCORE_BACKGROUND.minYRatio, SCORE_BACKGROUND.maxXRatio, SCORE_BACKGROUND.maxYRatio, 500, 750, this::drawScore, this);

    public void drawScore(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        String title = "Score: " + (int) (clearScore * step.completion());
        WDFont.drawCenteredString(guiGraphics, title, step.xCenter() + xOffset, step.yCenter(), step.ySize() / 5, 0xFFFFFFFF);
    }

    public final AnimationStep PERFECT_SCORE = new AnimationStep(SCORE_BACKGROUND.minXRatio, SCORE_BACKGROUND.minYRatio, SCORE_BACKGROUND.maxXRatio, SCORE_BACKGROUND.maxYRatio, 500, 750, this::drawPerfectScore, this);

    public void drawPerfectScore(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        guiGraphics.fill(step.minX() + xOffset, step.minY(), Mth.lerpInt(step.completion(), step.minX(), step.maxX()) + xOffset, step.maxY(), this.primaryColor);
        drawScore(guiGraphics, mouseX, mouseY, SCORE);
    }

    public final AnimationStep PERFECT_CLEAR = new AnimationStep(TITLE_BACKGROUND.minXRatio, TITLE_BACKGROUND.minYRatio, TITLE_BACKGROUND.maxXRatio, TITLE_BACKGROUND.maxYRatio, 500, 750, this::drawPerfectClear, this);

    public void drawPerfectClear(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        guiGraphics.fill(step.minX(), step.minY(), Mth.lerpInt(step.completion(), step.minX(), step.maxX()), step.maxY(), this.primaryColor);
        drawTitle(guiGraphics, mouseX, mouseY, TITLE);
        drawIconBackground(guiGraphics, mouseX, mouseY, ICON_BACKGROUND);
        drawIcon(guiGraphics, mouseX, mouseY, ICON);
    }

    public final AnimationStep SWIPE_TO_CHART = new AnimationStep(0.0f, 0.0f, 0.0f, 0.0f, 500, 750, this::swipeToChart, this);

    public void swipeToChart(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        this.xOffset = (int) -(step.completion() * this.width);
    }

    public final AnimationStep CHART_BACKGROUND = new AnimationStep(0.25f, 0.3f, 0.75f, 1.0f, 500, 750, this::drawChartBackground, this);

    public void drawChartBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        guiGraphics.fill(step.minX() + xOffset + this.width, Mth.lerpInt(step.completion(), step.maxY(), step.minY()), step.maxX() + xOffset + this.width, step.maxY(), 0x800d0f18);
    }


    public final AnimationStep CHART_ENTRY = new AnimationStep(CHART_BACKGROUND.minXRatio, CHART_BACKGROUND.minYRatio, CHART_BACKGROUND.maxXRatio, CHART_BACKGROUND.maxYRatio, 500, 750, this::drawChartEntry, this);

    public void drawChartEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, AnimationStep step) {
        int padding = (int) (step.xSize() * 0.05f);
        int barWidth = ((step.xSize() - padding * 3) / this.clearScores.size()) - padding;
        int minX = step.minX() + padding * 2 + (step.id * (barWidth + padding));
        int maxX = minX + barWidth;
        float ratio = (float) clearScores.get(step.id).getFirst() / clearScores.getLast().getFirst();
        int minY = Mth.lerpInt(ratio, step.maxY() - padding * 2, step.minY() + padding * 2);
        int maxY = step.maxY() - padding * 2;

        ResourceLocation skin = clearScores.get(step.id).getSecond().getNow(defaultSkin).texture();

        guiGraphics.fill(minX + xOffset + this.width, Mth.lerpInt(step.completion(), maxY, minY), maxX + xOffset + this.width, maxY, this.primaryColor);
        drawCenteredSquare(guiGraphics, skin, minX + ((maxX - minX) / 2), maxY, (int) (step.completion() * padding * 2), 8f / 64f, 8f / 64f, 16f / 64f, 16f / 64f, 0xFFFFFFFF);
        WDFont.drawCenteredString(guiGraphics, String.valueOf((int) (clearScores.get(step.id).getFirst() * step.completion())), minX + ((maxX - minX) / 2), Math.min(minY - padding, maxY - padding * 2), padding, 0xFFFFFFFF);
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
        drawTexturedQuad(guiGraphics, texture, x - size / 2, y - size / 2, x + size / 2, y + size / 2, minU, minV, maxU, maxV, color);
    }
}