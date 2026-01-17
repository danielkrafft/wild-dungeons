package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.model.ClientModel;
import com.danielkkrafft.wilddungeons.item.WarSpearItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class NautilusShieldRenderer extends GeoItemRenderer<WarSpearItem> {
    private final ClientModel<WarSpearItem> BASE = new ClientModel<>(null, WildDungeons.rl("geo/item/nautilus_shield.geo.json"), WildDungeons.rl("textures/item/nautilus_shield.png"));
    public NautilusShieldRenderer() {
        super(new ClientModel<>(null, WildDungeons.rl("geo/item/nautilus_shield.geo.json"), WildDungeons.rl("textures/item/nautilus_shield.png")));
    }


    @Override
    public GeoModel<WarSpearItem> getGeoModel() {
        return BASE;
    }
}
