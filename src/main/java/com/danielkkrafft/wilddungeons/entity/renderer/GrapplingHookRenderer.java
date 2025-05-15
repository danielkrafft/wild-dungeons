package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.GrapplingHook;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * {@link net.minecraft.client.renderer.entity.FishingHookRenderer}
 * @param <T>
 */
@OnlyIn(Dist.CLIENT)
public class GrapplingHookRenderer<T extends GrapplingHook> extends EntityRenderer<T> {
    private static final ResourceLocation HOOKS = WildDungeons.rl("textures/entity/grapplinghook_hooks.png");
    private static final ResourceLocation BODY = WildDungeons.rl("textures/entity/grapplinghook_body.png");
    private static final ResourceLocation CHAIN = WildDungeons.rl("textures/entity/grapplinghook_chain.png");
    private static final float chainSegmentLength = 0.5f;

    public GrapplingHookRenderer(EntityRendererProvider.Context c) {
        super(c);
    }

    public void render(T grapplingHookEntity, float p_114081_, float deltaTime, PoseStack pose, MultiBufferSource buffer, int light) {
        Level world = grapplingHookEntity.level();
        Player player = grapplingHookEntity.getPlayerOwner(world);
        ItemStack meathook = grapplingHookEntity.lookForStack(player);
        if (meathook != null) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).equals(meathook) || player.getItemInHand(InteractionHand.OFF_HAND).equals(meathook)) {
                Vec3 playerLocLerp = new Vec3(Mth.lerp(deltaTime, player.xo, player.getX()), Mth.lerp(deltaTime, player.yo, player.getY()) + player.getEyeHeight(), Mth.lerp(deltaTime, player.zo, player.getZ())),
                        hookLocLerp = new Vec3(Mth.lerp(deltaTime, grapplingHookEntity.xo, grapplingHookEntity.getX()), Mth.lerp(deltaTime, grapplingHookEntity.yo, grapplingHookEntity.getY()), Mth.lerp(deltaTime, grapplingHookEntity.zo, grapplingHookEntity.getZ()));

                Vec3 playerDisplacement = this.entityRenderDispatcher.camera.isDetached() ?
                        MathUtil.displaceVector(0.9, playerLocLerp.add(0, -0.97, 0), (float) Mth.lerp((double) deltaTime, player.yBodyRotO, player.yBodyRot) + (player.getOffhandItem().equals(meathook) ? -25 : 25), 0) :
                        MathUtil.displaceVector(0.3, playerLocLerp.add(0, -0.3, 0), (float) Mth.lerp((double) deltaTime, player.yHeadRotO, player.yHeadRot) + (player.getOffhandItem().equals(meathook) ? -85 : 85), player.getXRot());
                Vec3 chainDisp = MathUtil.displaceVector(0, hookLocLerp, Mth.lerp(deltaTime, grapplingHookEntity.yRotO, grapplingHookEntity.getYRot()), Mth.lerp(deltaTime, grapplingHookEntity.xRotO, grapplingHookEntity.getXRot())).add(0, 0.25, 0);
                float[] rotationAngles = MathUtil.entitylookAtEntity(chainDisp, playerDisplacement);
                float distance = (float) MathUtil.distance(chainDisp, playerDisplacement);

                for (float i = 0; i < distance; i += chainSegmentLength) {
                    CubeRenderer.cross(i, chainSegmentLength, Vec3.ZERO, 0.1f, CHAIN, pose, buffer, rotationAngles[0], rotationAngles[1], 0, 1, 1, 1, 1);
                }
            }
        }
        float yawDisp = 180f;
        //draw the hook itself
        CubeRenderer.cube(0, 0.6f, new Vec3(0, 0.25, 0), 0.1f, BODY, pose, buffer, grapplingHookEntity.getYRot() + yawDisp, -grapplingHookEntity.getXRot(), 0, 1, 1, 1, 1);
        CubeRenderer.cube(0, 0.3f, new Vec3(0, 0.25, 0), 0.07f, HOOKS, pose, buffer, grapplingHookEntity.getYRot() + yawDisp, -grapplingHookEntity.getXRot() + 45, 0, 1, 1, 1, 1);
        CubeRenderer.cube(0, 0.3f, new Vec3(0, 0.25, 0), 0.07f, HOOKS, pose, buffer, grapplingHookEntity.getYRot() + yawDisp, -grapplingHookEntity.getXRot() - 45, 0, 1, 1, 1, 1);
        CubeRenderer.cube(0, 0.3f, new Vec3(0, 0.25, 0), 0.07f, HOOKS, pose, buffer, grapplingHookEntity.getYRot() + yawDisp + 45, -grapplingHookEntity.getXRot(), 0, 1, 1, 1, 1);
        CubeRenderer.cube(0, 0.3f, new Vec3(0, 0.25, 0), 0.07f, HOOKS, pose, buffer, grapplingHookEntity.getYRot() + yawDisp - 45, -grapplingHookEntity.getXRot(), 0, 1, 1, 1, 1);
        super.render(grapplingHookEntity, p_114081_, deltaTime, pose, buffer, light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T g) {
        return BODY;
    }
    //@Override public boolean shouldRender(@NotNull T g, @NotNull Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {return true;}
}
