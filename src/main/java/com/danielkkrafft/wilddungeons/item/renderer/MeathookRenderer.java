package com.danielkkrafft.wilddungeons.item.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.item.Meathook;
import com.danielkkrafft.wilddungeons.item.model.MeathookModel;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MeathookRenderer extends GeoItemRenderer<Meathook>
{
    public static final ResourceLocation MEATHOOK_ANIM = WildDungeons.rl("animations/meathook.animation.json"),
            MEATHOOK_MODEL = WildDungeons.rl("geo/meathook.geo.json"),
            MEATHOOK_FIRED_MODEL = WildDungeons.rl("geo/meathook_fired.geo.json"),
            MEATHOOK_TEXTURE = WildDungeons.rl("textures/item/meathook_texture.png");

    public static final String idleAnim="hook.idle",chargeAnim="hook.charge",chargeHoldAnim="hook.charge.hold",fireAnim="hook.fire";

    public MeathookRenderer()
    {
        super(new MeathookModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, Meathook animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if(model instanceof MeathookModel meathookModel)
        {
            if(Meathook.getHookUUID(stack)!=null)
            {
                //if(!meathookModel.getModelResource((Meathook) WDItems.MEATHOOK_ITEM.get()).equals(MEATHOOK_FIRED_MODEL))
                    meathookModel.setModel(MEATHOOK_FIRED_MODEL);
            }
            else
            {
                //if(!meathookModel.getModelResource((Meathook) WDItems.MEATHOOK_ITEM.get()).equals(MEATHOOK_MODEL))
                    meathookModel.setModel(MEATHOOK_MODEL);
            }
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
