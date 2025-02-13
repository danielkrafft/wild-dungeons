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
import org.jetbrains.annotations.NotNull;

/**
 * {@link net.minecraft.client.renderer.entity.FishingHookRenderer}
 * @param <T>
 */
public class GrapplingHookRenderer<T extends GrapplingHook> extends EntityRenderer<T>
{
    private static final ResourceLocation HOOKS = WildDungeons.rl("textures/entity/grapplinghook_hooks.png");
    private static final ResourceLocation BODY = WildDungeons.rl("textures/entity/grapplinghook_body.png");
    private static final ResourceLocation CHAIN = WildDungeons.rl("textures/entity/grapplinghook_chain.png");
    public GrapplingHookRenderer(EntityRendererProvider.Context c) {super(c);}
    public void render(T en, float p_114081_, float deltaTime, PoseStack pose, MultiBufferSource buffer, int light)
    {
        Level world=en.level();
        Player p=en.getPlayerOwner(world);
        ItemStack it=en.lookForStack(p);
        if(it!=null)
        {
            if(p.getItemInHand(InteractionHand.MAIN_HAND).equals(it)||p.getItemInHand(InteractionHand.OFF_HAND).equals(it))
            {
                Vec3 playerLocLerp=new Vec3(Mth.lerp(deltaTime,p.xo,p.getX()),Mth.lerp(deltaTime,p.yo,p.getY())+p.getEyeHeight(),Mth.lerp(deltaTime,p.zo,p.getZ())),
                        hookLocLerp=new Vec3(Mth.lerp(deltaTime,en.xo,en.getX()),Mth.lerp(deltaTime,en.yo,en.getY()),Mth.lerp(deltaTime,en.zo,en.getZ()));

                Vec3 disp=this.entityRenderDispatcher.camera.isDetached()? MathUtil.displaceVector(0.9,playerLocLerp.add(0,-0.97,0),(float)Mth.lerp((double) deltaTime,p.yBodyRotO,p.yBodyRot)+(p.getOffhandItem().equals(it)?-25:25),0):
                        MathUtil.displaceVector(0.3,playerLocLerp.add(0,-0.3,0),(float)Mth.lerp((double) deltaTime,p.yHeadRotO,p.yHeadRot)+(p.getOffhandItem().equals(it)?-85:85),p.getXRot());
                Vec3 chainDisp=MathUtil.displaceVector(-0.6,hookLocLerp,Mth.lerp(deltaTime,en.yRotO,en.getYRot()),Mth.lerp(deltaTime,en.xRotO,en.getXRot())).add(0,0.25,0);
                float[]f=MathUtil.entitylookAtEntity(chainDisp,disp);
                float d=(float) MathUtil.distance(chainDisp,disp);

                CubeRenderer.cross(0,d,Vec3.ZERO,0.1f,CHAIN,pose,buffer,f[0],f[1],0,1,1,1,1);
            }
        }
        float yawDisp=180f;
        CubeRenderer.cube(0,0.6f,new Vec3(0,0.25,0),0.1f,BODY,pose,buffer,en.getYRot()+yawDisp,-en.getXRot(),0,1,1,1,1);
        CubeRenderer.cube(0,0.3f,new Vec3(0,0.25,0),0.07f,HOOKS,pose,buffer,en.getYRot()+yawDisp,-en.getXRot()+45,0,1,1,1,1);
        CubeRenderer.cube(0,0.3f,new Vec3(0,0.25,0),0.07f,HOOKS,pose,buffer,en.getYRot()+yawDisp,-en.getXRot()-45,0,1,1,1,1);
        CubeRenderer.cube(0,0.3f,new Vec3(0,0.25,0),0.07f,HOOKS,pose,buffer,en.getYRot()+yawDisp+45,-en.getXRot(),0,1,1,1,1);
        CubeRenderer.cube(0,0.3f,new Vec3(0,0.25,0),0.07f,HOOKS,pose,buffer,en.getYRot()+yawDisp-45,-en.getXRot(),0,1,1,1,1);
        super.render(en, p_114081_, deltaTime, pose, buffer, light);
    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T g){return BODY;}
    //@Override public boolean shouldRender(@NotNull T g, @NotNull Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {return true;}
}
