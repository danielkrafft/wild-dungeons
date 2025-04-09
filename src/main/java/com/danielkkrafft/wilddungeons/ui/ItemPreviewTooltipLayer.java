package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.render.AnimatedTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = WildDungeons.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ItemPreviewTooltipLayer implements LayeredDraw.Layer {
    public static final ItemPreviewTooltipLayer INSTANCE = new ItemPreviewTooltipLayer();
    private static final AnimatedTexture RIGHT_CLICK_ANIMATION_TEST = AnimatedTexture.auto("textures/gui/sprites/right_click", 2, 10);
    private static final float LERP_FACTOR = 0.1f;

    private Entity previewEntity;
    private Vector2i previewPosition = new Vector2i(0, 0);

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.jumpableVehicle() != null) return;

        if (previewEntity instanceof Offering offering && offering.isLookingAtMe(player)) {
            renderTooltip(guiGraphics, offering, player);
            renderCursor(guiGraphics, offering);
        } else {
            clearPreviewEntity();
        }
    }

    private void renderTooltip(@NotNull GuiGraphics guiGraphics, Offering offering, LocalPlayer player) {
        Vector2i pos = offering.getScreenPosition(player).add(guiGraphics.guiWidth() / 2 + 16, guiGraphics.guiHeight() / 2 + 16);
        pos = new Vector2i(
                (int) (previewPosition.x * (1 - LERP_FACTOR) + pos.x * LERP_FACTOR),
                (int) (previewPosition.y * (1 - LERP_FACTOR) + pos.y * LERP_FACTOR)
        );

        switch (offering.getOfferingType()) {
            case ITEM -> {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, offering.getItemStack(), pos.x, pos.y);
            }
            case PERK -> {
                List<Component> list = List.of(
                        Component.translatable("wilddungeons.perk." + offering.getPerk().name()).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD),
                        Component.translatable("wilddungeons.perk." + offering.getPerk().name() + ".desc")
                );
                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, list, pos.x, pos.y);
            }
            case RIFT -> {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("wilddungeons.offering." + offering.getOfferingId()).withStyle(ChatFormatting.OBFUSCATED), pos.x, pos.y);
            }
        }
        previewPosition = pos;
    }

    private void renderCursor(@NotNull GuiGraphics guiGraphics, Offering offering) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (!(hitResult instanceof EntityHitResult entityHitResult) || entityHitResult.getEntity() != offering) return;

        int x = guiGraphics.guiWidth() / 2, y = guiGraphics.guiHeight() / 2, size = 16;
        ResourceLocation frameTexture = RIGHT_CLICK_ANIMATION_TEST.getCurrentFrame();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        guiGraphics.blit(frameTexture, x - size / 2, y - size / 2, 0, 0, size, size, size, size);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    public void setPreviewEntity(Entity entity) {
        this.previewEntity = entity;
    }

    public void clearPreviewEntity() {
        this.previewEntity = null;
    }

    public boolean shouldCancelRender() {
        return previewEntity != null && Minecraft.getInstance().hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == previewEntity;
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, WildDungeons.rl("item_preview"), INSTANCE);
    }
}