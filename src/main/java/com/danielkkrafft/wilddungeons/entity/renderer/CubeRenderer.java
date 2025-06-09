package com.danielkkrafft.wilddungeons.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class CubeRenderer
{
    private CubeRenderer(){}
    private static Quaternionf eulerToQuaternion(float yaw, float pitch, float roll)
    {
        return null;
    }
    //plane
    public static void plane(float yOffset, Vec3 disp, float radius, ResourceLocation texture, PoseStack matrix, MultiBufferSource buffer, float yaw, float pitch, float roll, float r, float g, float b, float alpha)
    {
        matrix.pushPose();
        matrix.translate(disp.x,disp.y,disp.z);
        matrix.mulPose(Axis.YN.rotationDegrees(yaw));
        matrix.mulPose(Axis.XP.rotationDegrees(pitch));
        matrix.mulPose(Axis.ZP.rotationDegrees(roll));
        renderPlane(matrix,buffer,texture,r,g,b,alpha,yOffset,radius);
        matrix.popPose();
    }
    //cube
    public static void cube(float yOffset, float height, Vec3 disp, float radius, ResourceLocation texture, PoseStack matrix, MultiBufferSource buffer, float yaw, float pitch,float roll, float r, float g, float b, float alpha)
    {
        final float i=yOffset+height;
        final float textureScale=1f;
        matrix.pushPose();
        matrix.translate(disp.x,disp.y,disp.z);
        matrix.mulPose(Axis.YN.rotationDegrees(yaw));
        matrix.mulPose(Axis.XP.rotationDegrees(pitch));
        matrix.mulPose(Axis.ZP.rotationDegrees(roll));
        float f16 = height * textureScale * (0.15F / radius)-1f;
        renderCube(matrix,buffer,texture,r,g,b,alpha,yOffset,i,radius,-1,f16);
        matrix.popPose();
    }
    //cross
    public static void cross(float yOffset,float height,Vec3 disp,float radius,ResourceLocation texture,PoseStack matrix,MultiBufferSource buffer,float yaw,float pitch,float roll,float r,float g,float b,float alpha)
    {
        final float i=yOffset+height;
        final float textureScale=1f;
        matrix.pushPose();
        matrix.translate(disp.x,disp.y,disp.z);
        matrix.mulPose(Axis.YN.rotationDegrees(yaw));
        matrix.mulPose(Axis.XP.rotationDegrees(pitch));
        matrix.mulPose(Axis.ZP.rotationDegrees(roll));
        float f16 = height * textureScale * (0.15F / radius)-1f;
        renderCross(matrix,buffer,texture,r,g,b,alpha,yOffset,i,radius,-1,f16);
        matrix.popPose();
    }
    private static void renderPlane(PoseStack matrix,MultiBufferSource bufferIn,ResourceLocation texture,float r,float g,float b,float a,float yOffset,float radius)
    {
        renderPlane2(matrix, bufferIn.getBuffer(RenderType.itemEntityTranslucentCull(texture)),r,g,b,a,yOffset,-radius/2f,radius/2f,0, 1);
    }
    private static void renderPlane2(PoseStack matrix, VertexConsumer bufferIn, float red, float green, float blue, float alpha, float yMin, float x1, float z1,float u1, float u2)
    {
        PoseStack.Pose matrixstack$entry = matrix.last();
        Matrix4f matrixPos = matrixstack$entry.pose();
        Matrix3f matrixNormal = matrixstack$entry.normal();

        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, x1, x1, yMin, u2, u1);
        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, x1, z1, yMin, u2, u2);
        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, z1, z1, yMin, u1, u2);
        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, z1, x1, yMin, u1, u1);

        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, z1, x1, yMin, u1, u1);
        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, z1, z1, yMin, u1, u2);
        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, x1, z1, yMin, u2, u2);
        addVertex(matrixstack$entry, matrixPos, matrixNormal, bufferIn, red, green, blue, alpha, x1, x1, yMin, u2, u1);
    }
    private static void renderCube(PoseStack matrix,MultiBufferSource bufferIn,ResourceLocation texture,float r,float g,float b,float a,float yOffset,float i,float radius,float f15,float f16)
    {
        renderPartCube(matrix, bufferIn.getBuffer(RenderType.itemEntityTranslucentCull(texture)),r,g,b,a,yOffset,i,radius,0.0F, 1.0F, f16, f15);
    }
    private static void renderPartCube(PoseStack matrix, VertexConsumer bufferIn, float red, float green, float blue, float alpha, float yMin, float yMax, float radius,float u1, float u2, float v1, float v2)
    {
        PoseStack.Pose matrixstack$entry = matrix.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        //sides
        addVerticalQuad(matrixstack$entry, matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,-radius/2f,-radius/2f,radius/2f,-radius/2f,u1,u2,v1,v2);
        addVerticalQuad(matrixstack$entry, matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,radius/2f,radius/2f,-radius/2f,radius/2f,u1,u2,v1,v2);
        //top down
        addHorizontalQuad(matrixstack$entry, matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,-radius/2f,-radius/2f,radius/2f,-radius/2f,u1,u2,v1,v2);
        addHorizontalQuad(matrixstack$entry, matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,radius/2f,radius/2f,-radius/2f,radius/2f,u1,u2,v1,v2);

        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, radius/2f, -radius/2f, yMin, u1, u1);
        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, radius/2f, radius/2f, yMin, u1, u2);
        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, -radius/2f, radius/2f, yMin, u2, u2);
        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, -radius/2f, -radius/2f, yMin, u2, u1);

        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, -radius/2f, -radius/2f, yMax, u2, u1);
        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, -radius/2f, radius/2f, yMax, u2, u2);
        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, radius/2f, radius/2f, yMax, u1, u2);
        addVertex(matrixstack$entry, matrix4f, matrix3f, bufferIn, red, green, blue, alpha, radius/2f, -radius/2f, yMax, u1, u1);
    }
    private static void renderCross(PoseStack matrix,MultiBufferSource bufferIn,ResourceLocation texture,float r,float g,float b,float a,float yOffset,float i,float radius,float f15,float f16)
    {
        renderPartCross(matrix, bufferIn.getBuffer(RenderType.itemEntityTranslucentCull(texture)),r,g,b,a,yOffset,i,radius,0.0F, 1.0F, f16,f15);
    }
    private static void renderPartCross(PoseStack matrix, VertexConsumer bufferIn, float red, float green, float blue, float alpha, float yMin, float yMax, float radius, float u1, float u2, float v1, float v2)
    {
        PoseStack.Pose matrixstack$entry = matrix.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        addVerticalQuad(matrix.last(), matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,-radius/2f,0,radius/2f,0,u1,u2,v1,v2);
        addVerticalQuad(matrix.last(), matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,radius/2f,0,-radius/2f,0,u1,u2,v1,v2);

        addHorizontalQuad(matrix.last(), matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,-radius/2f,0,radius/2f,0,u1,u2,v1,v2);
        addHorizontalQuad(matrix.last(), matrix4f,matrix3f,bufferIn,red,green,blue,alpha,yMin,yMax,radius/2f,0,-radius/2f,0,u1,u2,v1,v2);
    }
    private static void addVerticalQuad(PoseStack.Pose pose, Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer bufferIn,float r,float g,float b, float alpha, float yMin,float yMax, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2)
    {
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, x1,z1,yMin, u1, u1);
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, x1,z2,yMax, u1, u2);
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, x2,z2,yMax, u2, u2);
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, x2,z1,yMin, u2, u1);
    }
    private static void addHorizontalQuad(PoseStack.Pose pose, Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer bufferIn,float r,float g,float b, float alpha, float yMin,float yMax, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2)
    {
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, z1,x2,yMin, u1, u1);
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, z2,x2,yMax, u1, u2);
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, z2,x1,yMax, u2, u2);
        addVertex(pose, matrixPos, matrixNormal, bufferIn, r, g, b, alpha, z1,x1,yMin, u2, u1);
    }
    private static void addVertex(PoseStack.Pose pose, Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer bufferIn, float red, float green, float blue, float alpha, float y, float x, float z, float texU, float texV){bufferIn.addVertex(matrixPos,x,y,z).setColor(red, green, blue, alpha).setUv(texU, texV).setUv2(0xF000F0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);}
}
