package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.Laserbeam;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LaserbeamRenderer<T extends Laserbeam> extends EntityRenderer<T> {
    private static final ResourceLocation
            BEAM = WildDungeons.rl("textures/entity/beam.png"),
            BEAMCROSS = WildDungeons.rl("textures/entity/laserbeam_beam.png");

    public LaserbeamRenderer(EntityRendererProvider.Context c) {
        super(c);
    }

    private float roll;

    public void render(@NotNull T en, float p_114081_, float deltaTime, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int light) {
        roll += 5 * deltaTime;
        if (roll > 360) roll -= 360;
        int tick = en.tickCount;
        float scaleMul, beamMul = 0;
        if (tick < en.getChargeTime() * 4) scaleMul = (float) tick / (en.getChargeTime() * 8f);
        else scaleMul = 1 - ((float) en.getLifetimeAfterHit() / en.getMaxLifetimeAfterHit());
        if (tick > en.getChargeTime() * 20) beamMul = 1f;
        CubeRenderer.cross(0, en.getLength(), new Vec3(0, 0, 0), beamMul * scaleMul * en.getRadius(), BEAMCROSS, pose, buffer, en.getYRot(), en.getXRot(), 2 * roll, 1, 1, 1, 1f);
        CubeRenderer.cube(0, en.getLength(), new Vec3(0, 0, 0), beamMul * scaleMul * en.getRadius() * 0.5f, BEAM, pose, buffer, en.getYRot(), en.getXRot(), 45 + (2 * roll), 1, 1, 1, 1f);
    }

    //@Override public boolean shouldRender(@NotNull T en, @NotNull Frustum frustum, double p_114493_, double p_114494_, double p_114495_){return true;}
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T laserbeam) {
        return BEAM;
    }
}
