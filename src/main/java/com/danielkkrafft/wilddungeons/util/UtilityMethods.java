package com.danielkkrafft.wilddungeons.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public final class UtilityMethods
{
    private UtilityMethods(){}
    public static int RNG(int min,int max){return (int)Math.round(RNG((double)min,max));}
    public static double RNG(double min,double max){return (max-min)*Math.random()+min;}
    public static <T extends ParticleOptions>void sendParticles(@NotNull ServerLevel server, T t, boolean visible,int count,double x,double y,double z,float dispX,float dispY,float dispZ,float speed)
    {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(t,visible,x,y,z,dispX,dispY,dispZ,speed,count);
        server.getPlayers(sp->true).forEach(p->p.connection.send(packet));
    }
}
