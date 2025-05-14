package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.render.AnimatedTexture;
import com.danielkkrafft.wilddungeons.ui.ItemPreviewTooltipLayer;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector2i;

import java.util.HexFormat;


public class OfferingRenderer extends EntityRenderer<Offering> {
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    private static final ResourceLocation MESSAGE_BUBBLE_TEXTURE = WildDungeons.rl("textures/gui/sprites/hud/message_bubble.png");
    private static final ResourceLocation PERK_RING_TEXTURE = WildDungeons.rl("textures/gui/sprites/hud/perk_ring.png");
    private static final ResourceLocation ITEM_RING_TEXTURE = WildDungeons.rl("textures/gui/sprites/hud/item_ring.png");
    private static final ResourceLocation PERKS_TEXTURE = WildDungeons.rl("textures/gui/sprites/hud/perks.png");
    private static final Vector2i MESSAGE_BUBBLE_TEXTURE_RESOLUTION = new Vector2i(48, 32);
    private static final Vector2i PERKS_TEXTURE_RESOLUTION = new Vector2i(64, 64);
    private static final ResourceLocation RIFT_TEXTURE = WildDungeons.rl("textures/entity/rift.png");
    private static final AnimatedTexture RIFT_ANIMATION = AnimatedTexture.auto("textures/entity/rift", 100, 2);
    private static final AnimatedTexture RIFT_2_ANIMATION = AnimatedTexture.auto("textures/entity/rift2", 100, 2);

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
    private static final RenderType PERK_RING_RENDERTYPE = RenderType.entityCutout(PERK_RING_TEXTURE);
    private static final RenderType ITEM_RING_RENDERTYPE = RenderType.entityCutout(ITEM_RING_TEXTURE);
    private static final RenderType PERK_RENDERTYPE = RenderType.entityCutout(PERKS_TEXTURE);
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

        poseStack.pushPose();
        poseStack.scale(entity.getRenderScale(), entity.getRenderScale(), entity.getRenderScale());
        poseStack.translate(0.0f, 0.5f, 0.0f);

        switch (entity.getOfferingType()) {
            case ITEM -> renderItemModel(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            case PERK -> renderPerk(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            case RIFT -> renderRift(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            case null, default -> {
            }
        }

        if (entity.getCostAmount() > 0) {
            renderBubble(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        if (entity.isLookingAtMe(Minecraft.getInstance().player, 1.0)){
            ItemPreviewTooltipLayer.INSTANCE.setPreviewEntity(entity);
        }

        poseStack.popPose();

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

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int packedLight, float alpha, int color) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(color)
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
        renderMultipleFromCount(this.itemRenderer, poseStack, buffer, entity.renderItemHighlight() ? 0xF000F0 : packedLight, itemstack, bakedmodel, flag, this.random);
        poseStack.popPose();
        if (entity.renderItemHighlight()) {
            renderItemHighlight(poseStack, entity, partialTicks, buffer, entity.getRenderScale() * .2f);
        }
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

            VertexConsumer vertexconsumer = buffer.getBuffer(PERK_RENDERTYPE);
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

            //Custom Shader Version
//            if (WDShaders.RIFT_SHADER == null) return;
//            RenderSystem.setShader(() -> WDShaders.RIFT_SHADER);
//            VertexConsumer vertexconsumer = buffer.getBuffer(RiftRenderType.getRiftRenderType());
//            Vector3f pRGB = entity.getPrimaryColorRGB();
//            Vector3f sRGB = entity.getSecondaryColorRGB();
//            Vector3f bgRGB = entity.getBackgroundColorRGB();
//            WDShaders.RIFT_SHADER.safeGetUniform("BGColor").set(bgRGB.x,bgRGB.y,bgRGB.z);
//            WDShaders.RIFT_SHADER.safeGetUniform("PrimaryColor").set(pRGB.x,pRGB.y,pRGB.z);
//            WDShaders.RIFT_SHADER.safeGetUniform("SecondaryColor").set(sRGB.x,sRGB.y,sRGB.z);

            //Animated Texture Version

            PoseStack.Pose posestack$pose = poseStack.last();
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.itemEntityTranslucentCull(RIFT_ANIMATION.getCurrentFrame()));

            vertex(vertexconsumer, posestack$pose, -1.0f, -1.0f, 0.0f, 1.0f, 0xF000F0, 1.0f, entity.getSecondaryColor());
            vertex(vertexconsumer, posestack$pose, 1.0f, -1.0f, 1.0f, 1.0f, 0xF000F0, 1.0f, entity.getSecondaryColor());
            vertex(vertexconsumer, posestack$pose, 1.0f, 1.0f, 1.0f, 0.0f, 0xF000F0, 1.0f, entity.getSecondaryColor());
            vertex(vertexconsumer, posestack$pose, -1.0f, 1.0f, 0.0f, 0.0f, 0xF000F0, 1.0f, entity.getSecondaryColor());

            poseStack.translate(0.0f, 0.0f, 0.001f);

            vertexconsumer = buffer.getBuffer(RenderType.itemEntityTranslucentCull(RIFT_2_ANIMATION.getCurrentFrame()));

            vertex(vertexconsumer, posestack$pose, -1.0f, -1.0f, 0.0f, 1.0f, 0xF000F0, 1.0f, entity.getPrimaryColor());
            vertex(vertexconsumer, posestack$pose, 1.0f, -1.0f, 1.0f, 1.0f, 0xF000F0, 1.0f, entity.getPrimaryColor());
            vertex(vertexconsumer, posestack$pose, 1.0f, 1.0f, 1.0f, 0.0f, 0xF000F0, 1.0f, entity.getPrimaryColor());
            vertex(vertexconsumer, posestack$pose, -1.0f, 1.0f, 0.0f, 0.0f, 0xF000F0, 1.0f, entity.getPrimaryColor());

            poseStack.popPose();
        }
    }

    public void renderPerkRing(PoseStack poseStack, Offering entity, float partialTicks, MultiBufferSource buffer, float radius) {
        {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 0.02);
            poseStack.mulPose(Axis.ZN.rotationDegrees(entity.tickCount + partialTicks % 360.0f));

            VertexConsumer vertexconsumer = buffer.getBuffer(PERK_RING_RENDERTYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertex(vertexconsumer, posestack$pose, -radius, -radius, 0.0f, 1.0f, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, radius, -radius, 1.0f, 1.0f, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, radius, radius, 1.0f, 0.0f, 0xF000F0, 1.0f);
            vertex(vertexconsumer, posestack$pose, -radius, radius, 0.0f, 0.0f, 0xF000F0, 1.0f);

            poseStack.popPose();
        }
    }

    public void renderItemHighlight(PoseStack poseStack, Offering entity, float partialTicks, MultiBufferSource buffer, float radius) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.translate(0.0, 0.05, -0.05);
        poseStack.mulPose(Axis.ZN.rotationDegrees(-entity.tickCount));
        radius += 0.1f * Mth.sin((entity.tickCount + partialTicks) * 0.1f);


        VertexConsumer vertexconsumer = buffer.getBuffer(ITEM_RING_RENDERTYPE);
        PoseStack.Pose posestack$pose = poseStack.last();
        vertex(vertexconsumer, posestack$pose, -radius, -radius, 0.0f, 1.0f, 0xF000F0, 1.0f);
        vertex(vertexconsumer, posestack$pose, radius, -radius, 1.0f, 1.0f, 0xF000F0, 1.0f);
        vertex(vertexconsumer, posestack$pose, radius, radius, 1.0f, 0.0f, 0xF000F0, 1.0f);
        vertex(vertexconsumer, posestack$pose, -radius, radius, 0.0f, 0.0f, 0xF000F0, 1.0f);

        poseStack.popPose();
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
        if (entity.isLookingAtMe(Minecraft.getInstance().player, 1.0) && entity.getCostAmount() > 0) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateClientWDPlayer(Minecraft.getInstance().player);

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

            renderCost(entity, entityYaw, partialTicks, poseStack, buffer, packedLight, wdPlayer);
            poseStack.popPose();
        } else {
            entity.setBubbleTimer(Offering.BUBBLE_ANIMATION_TIME);
        }
    }

    public void renderCost(Offering entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, WDPlayer wdPlayer) {
        String text = "x " + entity.getCostAmount();
        int textWidth = Minecraft.getInstance().font.width(text);
        int totalWidth = (int) (textWidth + (Minecraft.getInstance().font.lineHeight * 1.5f));
        // Cost Label
        {
            int textColor = HexFormat.fromHexDigits("ff0000");
            int currencyAmount = switch (entity.getOfferingCostType()) {
                case OVERWORLD -> Minecraft.getInstance().player.experienceLevel;
                case NETHER -> Mth.floor(wdPlayer.getEssenceLevel(EssenceOrb.Type.NETHER));
                case END -> Mth.floor(wdPlayer.getEssenceLevel(EssenceOrb.Type.END));
                case ITEM -> wdPlayer.getServerPlayer().getInventory().countItem(entity.getCostItem());
            };

            if (currencyAmount >= entity.getCostAmount()) {
                textColor = HexFormat.fromHexDigits("ffffff");
            }

            poseStack.pushPose();
            poseStack.translate(0.25f, 0.9f, 0.03f);
            poseStack.scale(0.03f, 0.03f, 0.03f);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            Minecraft.getInstance().font.drawInBatch(text, (float) -textWidth * 0.5f, 0, textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
            poseStack.popPose();
        }

        // Cost Icon
        {
            Offering.CostType costType = entity.getOfferingCostType();
            switch (costType) {
                case OVERWORLD, NETHER, END -> {
                    int hueOffset = EssenceOrb.getHueOffset(costType);

                    poseStack.pushPose();
                    poseStack.translate(0, 0.8f, 0.01f);

                    float size = 0.3f;
                    float xOrigin = ((float) textWidth / totalWidth) - 0.5f;
                    float yOrigin = 0;

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
                case ITEM -> {
                    Item item = entity.getCostItem();
                    poseStack.pushPose();
                    float size = 0.35f;
                    float xOrigin = ((float) textWidth / totalWidth) - 0.5f;

                    // Position the item to the left of the text
                    poseStack.translate(-0.25f - xOrigin, .8f, 0.01f);
                    poseStack.scale(size, size, size);

                    // Create the itemstack with the correct count
                    ItemStack itemStack = new ItemStack(item);
                    itemStack.setCount(entity.getCostAmount());

                    // Render the item
                    BakedModel bakedModel = this.itemRenderer.getModel(itemStack, entity.level(), null, entity.getId());
                    this.itemRenderer.render(itemStack, ItemDisplayContext.GUI, false,
                            poseStack, buffer, packedLight,
                            OverlayTexture.NO_OVERLAY, bakedModel);

                    poseStack.popPose();
                }
            }
        }
    }

    public void renderInteractionText(Offering entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        if (entity.getOfferingType().equals(Offering.Type.PERK) && entity.getCostAmount() == 0){
            poseStack.translate(0.0f, 0.55f, 0.03f);
        } else {
            float bubbleScale = (1.0f - entity.getBubbleTimer() / Offering.BUBBLE_ANIMATION_TIME);
            poseStack.scale(bubbleScale, bubbleScale, bubbleScale);
            poseStack.translate(0.0f, 1f, 0.03f);
        }
        poseStack.scale(0.015f,0.015f, 0.015f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));

        String key = Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage().getString();
        String text;
        if (entity.getCostAmount() > 0){
            text = Component.translatable("wilddungeons.offering.interact", key).getString();
        } else {
            text = Component.translatable("wilddungeons.offering.interact_no_cost", key).getString();
        }
        int textWidth = Minecraft.getInstance().font.width(text);
        Minecraft.getInstance().font.drawInBatch(text, (float) -textWidth * 0.5f, -10, 0xFFFFFF, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);

        text = switch (entity.getOfferingType()) {
            case ITEM -> entity.getItemStack().getDisplayName().getString();
            case PERK -> entity.getPerk().name();//todo translatable names for perks
            case RIFT -> "Rift";//todo get the rift name
        };

        textWidth = Minecraft.getInstance().font.width(text);
        Minecraft.getInstance().font.drawInBatch(text, (float) -textWidth * 0.5f, 0, 0xFFFFFF, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
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

        boolean shouldSpread = IClientItemExtensions.of(item).shouldSpreadAsEntity(item);
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
