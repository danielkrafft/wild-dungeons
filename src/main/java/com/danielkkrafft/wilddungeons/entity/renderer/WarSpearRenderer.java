package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WarSpearItem;
import com.danielkkrafft.wilddungeons.item.WindHammer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WarSpearRenderer extends GeoItemRenderer<WarSpearItem> {
    private final ClientModel<WarSpearItem> WOOD = new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/wood_spear.png"));
    private final ClientModel<WarSpearItem> STONE = new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/stone_spear.png"));
    private final ClientModel<WarSpearItem> IRON = new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/iron_spear.png"));
    private final ClientModel<WarSpearItem> GOLD = new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/gold_spear.png"));
    private final ClientModel<WarSpearItem> DIAMOND = new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/diamond_spear.png"));
    private final ClientModel<WarSpearItem> NETHERITE = new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/netherite_spear.png"));
    private final ClientModel<WarSpearItem> HEAVY = new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/heavy_spear.png"));

    public WarSpearRenderer() {
        super(new ClientModel<>(null, WildDungeons.rl("geo/warspear/war_spear.geo.json"), WildDungeons.rl("textures/item/warspear/wood_spear.png")));
    }

    @Override
    public void preRender(PoseStack poseStack, WarSpearItem animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (this.getGeoModel().getBone("sharp1").isPresent()) this.getGeoModel().getBone("sharp1").get().setHidden(true);
        if (this.getGeoModel().getBone("sharp3").isPresent()) this.getGeoModel().getBone("sharp3").get().setHidden(true);
        if (this.getGeoModel().getBone("sharp5").isPresent()) this.getGeoModel().getBone("sharp5").get().setHidden(true);

        if (this.getGeoModel().getBone("smite1").isPresent()) this.getGeoModel().getBone("smite1").get().setHidden(true);
        if (this.getGeoModel().getBone("smite3").isPresent()) this.getGeoModel().getBone("smite3").get().setHidden(true);
        if (this.getGeoModel().getBone("smite5").isPresent()) this.getGeoModel().getBone("smite5").get().setHidden(true);

        if (this.getGeoModel().getBone("bane1").isPresent()) this.getGeoModel().getBone("bane1").get().setHidden(true);
        if (this.getGeoModel().getBone("bane3").isPresent()) this.getGeoModel().getBone("bane3").get().setHidden(true);
        if (this.getGeoModel().getBone("bane5").isPresent()) this.getGeoModel().getBone("bane5").get().setHidden(true);


        int sharp = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.SHARPNESS));
        int smite = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.SMITE));
        int bane = currentItemStack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.BANE_OF_ARTHROPODS));
        if (sharp > 0) {
            if (this.getGeoModel().getBone("base").isPresent()) this.getGeoModel().getBone("base").get().setHidden(true);

            if (sharp == 1 || sharp == 2) {
                if (this.getGeoModel().getBone("sharp1").isPresent()) this.getGeoModel().getBone("sharp1").get().setHidden(false);
            }
            if (sharp == 3 || sharp == 4) {
                if (this.getGeoModel().getBone("sharp3").isPresent()) this.getGeoModel().getBone("sharp3").get().setHidden(false);
            }
            if (sharp == 5) {
                if (this.getGeoModel().getBone("sharp5").isPresent()) this.getGeoModel().getBone("sharp5").get().setHidden(false);
            }
        } else if (smite > 0) {
            if (this.getGeoModel().getBone("base").isPresent()) this.getGeoModel().getBone("base").get().setHidden(true);

            if (smite == 1 || smite == 2) {
                if (this.getGeoModel().getBone("smite1").isPresent()) this.getGeoModel().getBone("smite1").get().setHidden(false);
            }
            if (smite == 3 || smite == 4) {
                if (this.getGeoModel().getBone("smite3").isPresent()) this.getGeoModel().getBone("smite3").get().setHidden(false);
            }
            if (smite == 5) {
                if (this.getGeoModel().getBone("smite5").isPresent()) this.getGeoModel().getBone("smite5").get().setHidden(false);
            }
        } else if (bane > 0) {
            if (this.getGeoModel().getBone("base").isPresent()) this.getGeoModel().getBone("base").get().setHidden(true);

            if (bane == 1 || bane == 2) {
                if (this.getGeoModel().getBone("bane1").isPresent()) this.getGeoModel().getBone("bane1").get().setHidden(false);
            }
            if (bane == 3 || bane == 4) {
                if (this.getGeoModel().getBone("bane3").isPresent()) this.getGeoModel().getBone("bane3").get().setHidden(false);
            }
            if (bane == 5) {
                if (this.getGeoModel().getBone("bane5").isPresent()) this.getGeoModel().getBone("bane5").get().setHidden(false);
            }
        } else {
            if (this.getGeoModel().getBone("base").isPresent()) this.getGeoModel().getBone("base").get().setHidden(false);

        }

    }

    @Override
    public GeoModel<WarSpearItem> getGeoModel() {
        switch (this.getAnimatable().getType()) {
            case WOOD : return WOOD;
            case STONE : return STONE;
            case IRON : return IRON;
            case GOLD : return GOLD;
            case DIAMOND : return DIAMOND;
            case NETHERITE : return NETHERITE;
            case HEAVY : return HEAVY;
        }


        return WOOD;
    }
}
