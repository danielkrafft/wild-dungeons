package com.danielkkrafft.wilddungeons.entity.renderer;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.Laserbeam;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LaserbeamRenderer<T extends Laserbeam> extends EntityRenderer<T>
{
    private static final ResourceLocation
            RING = WildDungeons.rl("textures/entity/laserbeam_ring.png"),
            BEAM = WildDungeons.rl("textures/entity/beam.png"),
            BEAMCROSS = WildDungeons.rl("textures/entity/laserbeam_beam.png");

    public LaserbeamRenderer(EntityRendererProvider.Context c) {super(c);}
    private float roll;
    public void render(@NotNull T en, float p_114081_, float deltaTime, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int light)
    {
        //float[][]c= {{0,0.6f,1},{0,0.48f,0.8f},{0,0.2f,0.8f},{0.2f,0.4f,1},{0.2f,0.2f,1f},{0,0,0.8f},{0.4f,0.85f,1},{0,0.45f,0.6f}};
        //float[] rand=c[Methods.RNG(0,c.length-1)];
        roll+=5*deltaTime;
        if(roll>360)roll-=360;
        int tick=en.tickCount;
        float scaleMul,beamMul=0;
        if(tick<2*20)scaleMul=tick/40f;
        else scaleMul=1-(en.getCounter()/30f);
        if(tick>9*20)beamMul=1f;
        CubeRenderer.plane(0,new Vec3(0,0,0),scaleMul*en.getRadius()*6,RING,pose,buffer,en.getYRot(),en.getXRot(),roll,1f,1f,1f,1f);
        CubeRenderer.plane(0.5f,new Vec3(0,0,0),scaleMul*en.getRadius()*4,RING,pose,buffer,en.getYRot(),en.getXRot(),-roll,1f,1f,1f,1f);
        CubeRenderer.cross(0,en.getLength(),new Vec3(0,0,0),beamMul*scaleMul*en.getRadius(),BEAMCROSS,pose,buffer,en.getYRot(),en.getXRot(),2*roll,1,1,1,1f);
        CubeRenderer.cube(0,en.getLength(),new Vec3(0,0,0),beamMul*scaleMul*en.getRadius()*0.5f,BEAM,pose,buffer,en.getYRot(),en.getXRot(),45+(2*roll),1,1,1,1f);
    }
    //@Override public boolean shouldRender(@NotNull T en, @NotNull Frustum frustum, double p_114493_, double p_114494_, double p_114495_){return true;}
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T laserbeam) {return BEAM;}
}
