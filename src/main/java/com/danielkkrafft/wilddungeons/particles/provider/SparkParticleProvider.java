package com.danielkkrafft.wilddungeons.particles.provider;

import com.danielkkrafft.wilddungeons.particles.render.SparkParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class SparkParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public SparkParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }


    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new SparkParticle(level, x, y, z,xSpeed,ySpeed,zSpeed, spriteSet);
    }
}
