package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WarSpearItem;
import com.danielkkrafft.wilddungeons.item.WarSpearItem.SpearType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WarSpearRenderer extends GeoItemRenderer<WarSpearItem> {

    private static final ClientModel<WarSpearItem> MODEL =
            new ClientModel<WarSpearItem>(WildDungeons.rl("animations/item/emerald_staff.animation.json"), WildDungeons.rl("geo/item/war_spear.geo.json"), WildDungeons.rl("textures/item/wood_spear.png"))
                    .withConditionalTexture(item -> item.getType() == SpearType.STONE, "stone_spear", "item")
                    .withConditionalTexture(item -> item.getType() == SpearType.IRON, "iron_spear", "item")
                    .withConditionalTexture(item -> item.getType() == SpearType.GOLD, "gold_spear", "item")
                    .withConditionalTexture(item -> item.getType() == SpearType.DIAMOND, "diamond_spear", "item")
                    .withConditionalTexture(item -> item.getType() == SpearType.NETHERITE, "netherite_spear", "item")
                    .withConditionalTexture(item -> item.getType() == SpearType.HEAVY, "heavy_spear", "item");

    public WarSpearRenderer() {
        super(MODEL);
    }

    @Override
    public GeoModel<WarSpearItem> getGeoModel() {
        return MODEL;
    }

    private static final String[] SHARP = {"sharp1", "sharp3", "sharp5"};
    private static final String[] SMITE = {"smite1", "smite3", "smite5"};
    private static final String[] BANE  = {"bane1",  "bane3",  "bane5"};

    private static int tierIndex(int lvl) {
        if (lvl <= 0) return -1;
        if (lvl <= 2) return 0;
        if (lvl <= 4) return 1;
        return 2;
    }

    private void setHidden(BakedGeoModel model, String boneName, boolean hidden) {
        model.getBone(boneName).ifPresent(bone -> bone.setHidden(hidden));
    }

    @Override
    public void preRender(PoseStack poseStack, WarSpearItem animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        for (String b : SHARP) setHidden(model, b, true);
        for (String b : SMITE) setHidden(model, b, true);
        for (String b : BANE)  setHidden(model, b, true);

        int sharp = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.SHARPNESS));
        int smite = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.SMITE));
        int bane  = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.BANE_OF_ARTHROPODS));

        boolean showBase = true;

        if (sharp > 0) {
            int idx = tierIndex(sharp);
            if (idx >= 0 && idx < SHARP.length) {
                setHidden(model, SHARP[idx], false);
                showBase = false;
            }
        } else if (smite > 0) {
            int idx = tierIndex(smite);
            if (idx >= 0 && idx < SMITE.length) {
                setHidden(model, SMITE[idx], false);
                showBase = false;
            }
        } else if (bane > 0) {
            int idx = tierIndex(bane);
            if (idx >= 0 && idx < BANE.length) {
                setHidden(model, BANE[idx], false);
                showBase = false;
            }
        }

        setHidden(model, "base", !showBase);
    }
}
