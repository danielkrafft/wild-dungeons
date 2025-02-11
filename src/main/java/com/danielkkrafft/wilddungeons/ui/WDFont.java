package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class WDFont {
    private static final ResourceLocation FONT_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/font/ascii.png");

    private static final int[] characterSizes = {
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            4,1,3,5,5,5,5,1,3,3,3,5,1,5,1,5,
            5,5,5,5,5,5,5,5,5,5,1,1,4,5,4,5,
            6,5,5,5,5,5,5,5,5,3,5,5,5,5,5,5,
            5,5,5,5,5,5,5,5,5,5,5,3,5,3,5,5,
            2,5,5,5,5,5,4,5,5,1,5,4,2,5,5,5,
            5,5,5,5,3,5,5,5,5,5,5,3,1,3,5,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,5,0,0,5,
            0,0,0,0,0,0,4,4,0,0,5,0,0,0,6,6,
            8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
            8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
            8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
            0,0,0,0,0,0,0,0,0,0,0,0,0,7,5,0,
            6,5,5,5,7,4,5,6,4,5,0,6,4,4,5,0
    };

    public static int width(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int charSize = characterSizes[c];
            result += charSize == 8 ? charSize : charSize + 1;
        }
        return result;
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, String text, int x, int y, int height, int color) {
        float ratio = (float) WDFont.width(text) / 7;
        float width = ratio * height;

        int minX = (int) (x - (width / 2.0f));
        int maxX = (int) (x + (width / 2.0f));
        int minY = (int) (y - (height / 2.0f));
        int maxY = (int) (y + (height / 2.0f));

        WDFont.drawString(guiGraphics, text, minX, minY, maxX, maxY, color);
    }

    public static void drawString(GuiGraphics guiGraphics, String text, int minX, int minY, int maxX, int maxY, int color) {
        if (text.length() <= 0) return;

        PoseStack poseStack = guiGraphics.pose();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, FONT_TEXTURE);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        int width = width(text);
        int height = maxY - minY;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String subString = i > 0 ? text.substring(0, i) : "";
            int charWidth = width(String.valueOf(c));
            int subWidth = width(subString);
            int charMinX = Mth.lerpInt((float) (subWidth) / width, minX, maxX);
            int charMaxX = Mth.lerpInt((float) (subWidth+charWidth) / width, minX, maxX);
            drawCharacter(poseStack.last().pose(), buffer, c, charMinX, minY, charMaxX, maxY + (height / 7), color);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void drawCharacter(Matrix4f pose, BufferBuilder buffer, char c, int minX, int minY, int maxX, int maxY, int color) {
        int charWidth = width(String.valueOf(c));

        float minU = ((int) c % 16) / 16.0f;
        float minV = ((int) c / 16) / 16.0f;
        float maxU = minU + (charWidth / 8.0f / 16.0f);
        float maxV = minV + (1.0f / 16.0f);

        buffer.addVertex(pose, minX, maxY, 0.0f).setUv(minU, maxV).setColor(color);
        buffer.addVertex(pose, maxX, maxY, 0.0f).setUv(maxU, maxV).setColor(color);
        buffer.addVertex(pose, maxX, minY, 0.0f).setUv(maxU, minV).setColor(color);
        buffer.addVertex(pose, minX, minY, 0.0f).setUv(minU, minV).setColor(color);
    }

    public static char getRandomGlitch() {
        return (char) RandomUtil.randIntBetween(192, 223);
    }
}
