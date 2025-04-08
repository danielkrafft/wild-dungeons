package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.render.AnimatedTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.awt.*;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = WildDungeons.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ItemPreviewLayer  implements LayeredDraw.Layer {
    public static final ItemPreviewLayer INSTANCE = new ItemPreviewLayer();
    public static final ResourceLocation RIGHT_CLICK_ANIMATION = WildDungeons.rl("textures/gui/sprites/right_click/0001.png");
    public static final ResourceLocation RIGHT_CLICK_ANIMATION_2 = WildDungeons.rl("textures/gui/sprites/right_click/0002.png");
    public static final AnimatedTexture RIGHT_CLICK_ANIMATION_TEST = AnimatedTexture.auto("textures/gui/sprites/right_click", 2, 10);

    public static final float lerpFactor = 0.1f;

    private Entity previewEntity;
    private Vector2i previewPosition = new Vector2i(0, 0);

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.jumpableVehicle() == null) {
            if (previewEntity != null && previewEntity instanceof Offering offering) {
                if (!offering.isLookingAtMe(player)) {
                    clearPreviewEntity();
                    return;
                }
                RenderTooltip(guiGraphics, offering, player);
                RenderCursor(guiGraphics, offering, player);
            }
        }
    }

    private void RenderTooltip(@NotNull GuiGraphics guiGraphics, Offering offering, LocalPlayer player) {
        Vector2i pos = offering.getScreenPosition(player);
        pos = new Vector2i(pos.x + guiGraphics.guiWidth() / 2 + 16, pos.y + guiGraphics.guiHeight() / 2 + 16);

        pos.x = (int) (previewPosition.x * (1 - lerpFactor) + pos.x * lerpFactor);
        pos.y = (int) (previewPosition.y * (1 - lerpFactor) + pos.y * lerpFactor);

        switch (offering.getOfferingType()) {
            case ITEM -> {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, offering.getItemStack(), pos.x, pos.y);
            }
            case PERK -> {
                List<Component> list = List.of(Component.translatable("wilddungeons.perk." + offering.getPerk().name()), Component.translatable("wilddungeons.perk." + offering.getPerk().name() + ".desc"));
                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, list, pos.x, pos.y);
            }
            case RIFT -> {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("wilddungeons.offering." + offering.getOfferingId()), pos.x, pos.y);
            }
        }
        previewPosition = pos;
    }


    private void RenderCursor(@NotNull GuiGraphics guiGraphics, Offering offering, LocalPlayer player) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (entity != offering) {
                return;
            }

            // Center position
            int x = guiGraphics.guiWidth()/2;
            int y = guiGraphics.guiHeight()/2;

            // Get current animation frame
            ResourceLocation frameTexture = RIGHT_CLICK_ANIMATION_TEST.getCurrentFrame();

            // Size of the cursor
            int width = 16;
            int height = 16;

            // Save current render state
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            // Center the cursor around the crosshair
            guiGraphics.blit(frameTexture, x - width / 2, y -height/2, 0, 0, width, height, width, height);

            // Restore render state
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
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
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, WildDungeons.rl("item_preview"), ItemPreviewLayer.INSTANCE);
    }
}