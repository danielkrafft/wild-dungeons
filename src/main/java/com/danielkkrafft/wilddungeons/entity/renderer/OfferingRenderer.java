package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.Alignments;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.ColorUtil;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

import java.util.HexFormat;

public class OfferingRenderer extends EntityRenderer<Offering> {
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    private static final ResourceLocation MESSAGE_BUBBLE_TEXTURE = WildDungeons.rl("textures/gui/sprites/hud/message_bubble.png");
    private static final ResourceLocation PERK_RING_TEXTURE = WildDungeons.rl("textures/gui/sprites/hud/perk_ring.png");
    private static final ResourceLocation PERKS_TEXTURE = WildDungeons.rl("textures/gui/sprites/hud/perks.png");
    private static final Vector2i MESSAGE_BUBBLE_TEXTURE_RESOLUTION = new Vector2i(48, 32);
    private static final Vector2i PERKS_TEXTURE_RESOLUTION = new Vector2i(64, 64);
    private static final ResourceLocation RIFT_TEXTURE = WildDungeons.rl("textures/entity/rift.png");

    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/experience_orb.png");

    static RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
            .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(MESSAGE_BUBBLE_TEXTURE, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.CULL)
            .setOverlayState(RenderStateShard.OVERLAY)
            .setLightmapState(RenderType.LIGHTMAP)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .createCompositeState(false);
    private static final RenderType RENDER_TYPE = RenderType.create("message_bubble", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
    private static final RenderType RENDER_TYPE_3 = RenderType.entityTranslucent(EXPERIENCE_ORB_LOCATION);
    private static final RenderType RENDER_TYPE_4 = RenderType.entityCutout(PERK_RING_TEXTURE);
    private static final RenderType RENDER_TYPE_5 = RenderType.entityCutout(PERKS_TEXTURE);
    private static final RenderType RENDER_TYPE_6 = RenderType.itemEntityTranslucentCull(RIFT_TEXTURE);



    public OfferingRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    @Override
    public ResourceLocation getTextureLocation(Offering entity) {return TextureAtlas.LOCATION_BLOCKS;}

    public void render(Offering entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        if (entity.getOfferingType() == Offering.Type.ITEM) {
            renderItemModel(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        if (entity.getOfferingType() == Offering.Type.PERK) {
            renderPerk(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        if (entity.getOfferingType() == Offering.Type.RIFT) {
            renderRift(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        if (entity.getCostAmount() > 0) {
            renderBubble(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, int red, int green, int blue, float u, float v, int packedLight, int hueOffset) {
        int[] offsetColor = ColorUtil.applyHueOffset(red, green, blue, hueOffset);
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(offsetColor[0], offsetColor[1], offsetColor[2], 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int packedLight, float alpha) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(1.0f, 1.0f, 1.0f, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    public static int getSeedForItemStack(ItemStack stack) {
        return stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
    }

    public void renderItemModel(Offering entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.tickCount + partialTicks % 360));
        ItemStack itemstack = entity.getItemStack();
        this.random.setSeed(getSeedForItemStack(itemstack));
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, entity.level(), null, entity.getId());
        boolean flag = bakedmodel.isGui3d();
        renderMultipleFromCount(this.itemRenderer, poseStack, buffer, packedLight, itemstack, bakedmodel, flag, this.random);
        poseStack.popPose();
    }

    public void renderPerk(Offering entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        {
            poseStack.pushPose();
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            renderPerkRing(poseStack, entity, partialTicks, buffer, 0.4f);
            poseStack.popPose();
        }


        {
            poseStack.pushPose();
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.translate(0.0, 0.0, 0.03);

            float halfSize = 0.3f;

            VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE_5);
            PoseStack.Pose posestack$pose = poseStack.last();

            Vector2i coords = entity.getPerk().getTexCoords();

            float u1 = (float) (coords.x * 16) / PERKS_TEXTURE_RESOLUTION.x;
            float u2 = (float) (coords.x * 16 + 16) / PERKS_TEXTURE_RESOLUTION.x;

            float v1 = (float) (coords.y * 16) / PERKS_TEXTURE_RESOLUTION.y;
            float v2 = (float) (coords.y * 16 + 16) / PERKS_TEXTURE_RESOLUTION.y;

            vertex(vertexconsumer, posestack$pose, -halfSize, -halfSize, u1, v2, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, halfSize, -halfSize, u2, v2, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, halfSize, halfSize, u2, v1, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, -halfSize, halfSize, u1, v1, 0xF000F0, 1.0f);

            poseStack.popPose();

        }

    }

    public void renderRift(Offering entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        {
            poseStack.pushPose();
            poseStack.translate(0.0f, 0.5f, 0.0f);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            if (Minecraft.getInstance().player == null) return;
            float extraScaleFactor = (float) (2.5f - Math.min(entity.position().distanceTo(Minecraft.getInstance().player.position()) / 15.0f, 2.5f));
            poseStack.scale(0.1F+extraScaleFactor, 0.1F+extraScaleFactor, 0.1F+extraScaleFactor);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.endPortal());
            PoseStack.Pose posestack$pose = poseStack.last();
            drawCircle(0.0f, 0.0f, 20, 0.5f, vertexconsumer, posestack$pose, packedLight);
            //renderPerkRing(poseStack, entity, partialTicks, buffer, 0.6f);
            poseStack.popPose();
        }


    }

    public void renderPerkRing(PoseStack poseStack, Offering entity, float partialTicks, MultiBufferSource buffer, float radius) {
        {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 0.02);
            poseStack.mulPose(Axis.ZN.rotationDegrees(entity.tickCount + partialTicks % 360.0f));

            VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE_4);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertex(vertexconsumer, posestack$pose, -radius, -radius, 0.0f, 1.0f, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, radius, -radius, 1.0f, 1.0f, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, radius, radius, 1.0f, 0.0f, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, -radius, radius, 0.0f, 0.0f, 0xF000F0, 1.0f);

            poseStack.popPose();
        }
    }

    public void drawCircle(float centerX, float centerY, int vertices, float radius, VertexConsumer vertexConsumer, PoseStack.Pose poseStack$pose, int packedLight) {
        if (vertices <= 2) return;

        for (int v = 0; v < vertices; v++) {
            float radians = Mth.PI*2 / vertices * v;
            float radians2 = Mth.PI*2 / vertices * (v+1);
            float xPos = (centerX + (radius * Mth.cos(radians)));
            float xPos2 = (centerX + (radius * Mth.cos(radians2)));
            float yPos = (centerY + (radius * Mth.sin(radians)));
            float yPos2 = (centerY + (radius * Mth.sin(radians2)));
            float uPos = 0.5f + (Mth.cos(radians) * 0.5f);
            float uPos2 = 0.5f + (Mth.cos(radians2) * 0.5f);
            float vPos = 0.5f + (Mth.sin(radians) * 0.5f);
            float vPos2 = 0.5f + (Mth.sin(radians2) * 0.5f);
            vertex(vertexConsumer, poseStack$pose, centerX, centerY, 0.5f, 0.5f, packedLight, 1.0f);
            vertex(vertexConsumer, poseStack$pose, centerX, centerY, 0.5f, 0.5f, packedLight, 1.0f);
            vertex(vertexConsumer, poseStack$pose, xPos, yPos, uPos, vPos, packedLight, 1.0f);
            vertex(vertexConsumer, poseStack$pose, xPos2, yPos2, uPos2, vPos2, packedLight, 1.0f);
        }
    }

    public void renderBubble(Offering entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isLookingAtMe(Minecraft.getInstance().player) && entity.getCostAmount() > 0) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateClientWDPlayer(Minecraft.getInstance().player);

            String text = "x " + entity.getCostAmount();
            int textWidth = Minecraft.getInstance().font.width(text);
            int textHeight = Minecraft.getInstance().font.lineHeight;
            int totalWidth = (int) (textWidth + (textHeight * 1.5));

            float bubbleScale = (1.0f - entity.getBubbleTimer() / Offering.BUBBLE_ANIMATION_TIME);
            poseStack.pushPose();
            if (entity.getOfferingType().equals(Offering.Type.RIFT)) poseStack.translate(0.0f,0.5f,0.00f);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            if (entity.getOfferingType().equals(Offering.Type.RIFT)) poseStack.translate(0.0f,0.5f,0.03f);
            poseStack.scale(bubbleScale, bubbleScale, bubbleScale);



            poseStack.pushPose();
            float xPos = ((float) MESSAGE_BUBBLE_TEXTURE_RESOLUTION.x / MESSAGE_BUBBLE_TEXTURE_RESOLUTION.y) * 0.5f;

            VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertex(vertexconsumer, posestack$pose, -xPos, 0.25F, 0.0f, 1.0f, packedLight, 0.5f);
            vertex(vertexconsumer, posestack$pose, xPos, 0.25F, 1.0f, 1.0f, packedLight, 0.5f);
            vertex(vertexconsumer, posestack$pose, xPos, 1.25F, 1.0f, 0.0f, packedLight, 0.5f);
            vertex(vertexconsumer, posestack$pose, -xPos, 1.25F, 0.0f, 0.0f, packedLight, 0.5f);
            entity.setBubbleTimer(Math.max(entity.getBubbleTimer() - partialTicks, 0));
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch(RENDER_TYPE);
            poseStack.popPose();


            this.renderCost(entity, entityYaw, partialTicks, poseStack, buffer, packedLight, text, textWidth, textHeight, totalWidth, wdPlayer);
            poseStack.popPose();
        } else {
            entity.setBubbleTimer(Offering.BUBBLE_ANIMATION_TIME);
        }
    }

    public void renderCost(Offering entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, String text, int textWidth, int textHeight, int totalWidth, WDPlayer wdPlayer) {

        // Cost Label
        {

            int textColor = HexFormat.fromHexDigits("ff0000");

            int levels = switch(entity.getOfferingCostType()) {
                case XP_LEVEL -> Minecraft.getInstance().player.experienceLevel;
                case NETHER_XP_LEVEL -> Mth.floor(wdPlayer.getEssenceLevel("essence:nether"));
                case END_XP_LEVEL -> Mth.floor(wdPlayer.getEssenceLevel("essence:end"));
            };

            if (levels >= entity.getCostAmount()) {
                textColor = HexFormat.fromHexDigits("ffffff");
            }

            poseStack.pushPose();
            poseStack.translate(0.0f, 0.75f, 0.03f);
            poseStack.scale(0.03f,0.03f, 0.03f);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            Minecraft.getInstance().font.drawInBatch(text, (int) -((((float) textWidth / totalWidth) - 0.5) * totalWidth), -(Minecraft.getInstance().font.lineHeight / 2f), textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
            poseStack.popPose();
        }

        // Cost Icon
        {
            int hueOffset = switch (entity.getOfferingCostType()) {
                case XP_LEVEL -> 0;
                case END_XP_LEVEL -> Alignments.ALIGNMENTS.get("end").ORB_HUE_OFFSET();
                case NETHER_XP_LEVEL -> Alignments.ALIGNMENTS.get("nether").ORB_HUE_OFFSET();
                case null, default -> 0;
            };

            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 0.01f);

            float size = 0.3f;
            float xOrigin = ((float) textWidth / totalWidth) - 0.5f;
            float yOrigin = 0.75f;

            int i = 3;
            float f = (float)(i % 4 * 16 + 0) / 64.0F;
            float f1 = (float)(i % 4 * 16 + 16) / 64.0F;
            float f2 = (float)(i / 4 * 16 + 0) / 64.0F;
            float f3 = (float)(i / 4 * 16 + 16) / 64.0F;

            float f8 = ((float)entity.tickCount + partialTicks) / 2.0F;
            int j = (int)((Mth.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
            int k = 255;
            int l = (int)((Mth.sin(f8 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);

            VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE_3);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertex(vertexconsumer, posestack$pose, -0.25f - size/2 - xOrigin, yOrigin - size/2, j, k, l, f, f3, packedLight, hueOffset);
            vertex(vertexconsumer, posestack$pose, -0.25f + size/2 - xOrigin, yOrigin - size/2, j, k, l, f1, f3, packedLight, hueOffset);
            vertex(vertexconsumer, posestack$pose, -0.25f + size/2 - xOrigin, yOrigin + size/2, j, k, l, f1, f2, packedLight, hueOffset);
            vertex(vertexconsumer, posestack$pose, -0.25f - size/2 - xOrigin, yOrigin + size/2, j, k, l, f, f2, packedLight, hueOffset);
            poseStack.popPose();
        }
    }

    public static void renderMultipleFromCount(
            ItemRenderer itemRenderer,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            ItemStack item,
            BakedModel model,
            boolean isGui3d,
            RandomSource random
    ) {
        int i = getRenderedAmount(item.getCount());
        float f = model.getTransforms().ground.scale.x();
        float f1 = model.getTransforms().ground.scale.y();
        float f2 = model.getTransforms().ground.scale.z();
        if (!isGui3d) {
            float f3 = -0.0F * (float) (i - 1) * 0.5F * f;
            float f4 = -0.0F * (float) (i - 1) * 0.5F * f1;
            float f5 = -0.09375F * (float) (i - 1) * 0.5F * f2;
            poseStack.translate(f3, f4, f5);
        }

        boolean shouldSpread = net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(item).shouldSpreadAsEntity(item);
        for (int j = 0; j < i; j++) {
            poseStack.pushPose();
            if (j > 0 && shouldSpread) {
                if (isGui3d) {
                    float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    poseStack.translate(f7, f9, f6);
                } else {
                    float f8 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    poseStack.translate(f8, f10, 0.0F);
                }
            }

            itemRenderer.render(item, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);
            poseStack.popPose();
            if (!isGui3d) {
                poseStack.translate(0.0F * f, 0.0F * f1, 0.09375F * f2);
            }
        }
    }

    static int getRenderedAmount(int count) {
        if (count <= 1) {
            return 1;
        } else if (count <= 16) {
            return 2;
        } else if (count <= 32) {
            return 3;
        } else {
            return count <= 48 ? 4 : 5;
        }
    }
}
