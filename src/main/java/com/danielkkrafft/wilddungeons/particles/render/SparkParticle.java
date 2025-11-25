package com.danielkkrafft.wilddungeons.particles.render;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;


public class SparkParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private float oRotX, oRotY;
    private float rotX, rotY;
    public static final ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType() {
        public BufferBuilder begin(Tesselator p_351047_, TextureManager p_107463_) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ONE
            );
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return p_351047_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT";
        }

        public boolean isTranslucent() {
            return false;
        }
    };

    public SparkParticle(ClientLevel level, double x, double y, double z, double speedX, double speedY, double speedZ, SpriteSet spriteSet) {
        super(level, x, y, z, speedX, speedY, speedZ);
        this.spriteSet = spriteSet;
        this.gravity = 0.51f;
        this.lifetime = 10;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(spriteSet);
        this.oRoll = this.roll;
        this.roll = this.roll + 0.1f;
        super.tick();
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        RenderSystem.enableBlend();
        Quaternionf rotation = getVelocityRotation();

        float spin = Mth.lerp(partialTicks, this.oRoll, this.roll);
        rotation.rotateZ(spin);

        renderRotatedQuad(buffer, renderInfo, rotation, partialTicks);

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.disableBlend();

    }


    public void renderRotatedParticle(VertexConsumer buffer, Camera renderInfo,float x, float y, float partialTicks) {
        Quaternionf quaternionf = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternionf, renderInfo, partialTicks);
        //if (this.roll != 0.0F) {
        //    quaternionf.rotateZYX(0,y,x);
        //}

        this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);
    }
    private Quaternionf getVelocityRotation() {
        Vec3 motion = new Vec3(this.xd, this.yd, this.zd);
        if (motion.lengthSqr() == 0) {
            return new Quaternionf();
        }

        motion = motion.normalize();


        float yaw = (float) (Math.atan2(motion.z, motion.x));
        float pitch = (float) (Math.asin(motion.y));


        Quaternionf q = new Quaternionf();
        q.rotateY(-yaw + (float)Math.PI / 2f);
        q.rotateX(pitch);

        return q;
    }
    @Override
    public int getLightColor(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return PARTICLE_SHEET_LIT;
    }
}
