package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MathUtil {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static Vec3 displaceVector(double vel, Vec3 e, float yaw, float pitch)
    {
        Vec3 orig = e;
        double yRot = -1*Math.toRadians(pitch);
        double yRotX = Math.abs((float)Math.toRadians(pitch));
        double xRot = -1*Math.toRadians(yaw);
        return new Vec3(
                orig.x()+vel*(Math.sin((float)xRot)*Math.cos((float)yRotX)),
                orig.y()+vel*Math.sin((float)yRot),
                orig.z()+vel*(Math.cos((float)xRot)*Math.cos((float)yRotX)));
    }

    public static Vec3 velocity3d(double vel,double yaw,double pitch)
    {
        double vx = vel * -(Math.sin((float)Math.toRadians(yaw))*Math.abs(Math.cos((float)Math.toRadians(pitch))));
        double vy = vel * -(Math.sin((float)Math.toRadians(pitch)));
        double vz = vel * (Math.cos((float)Math.toRadians(yaw))*Math.abs(Math.cos((float)Math.toRadians(pitch))));
        return new Vec3(vx,vy,vz);
    }

    public static String serializeVec3(Vec3 v)
    {
        return v.x+"|"+v.y+"|"+v.z;
    }
    public static Vec3 deserializeVec3(String v)
    {
        if(v.isEmpty())return null;
        List<String> str=split(v,"|");return new Vec3(Double.parseDouble(str.get(0)),Double.parseDouble(str.get(1)),Double.parseDouble(str.get(2)));
    }

    public static List<String> split(String str,String regex)
    {
        List<String>list=new ArrayList<>();
        String element="";
        for(int i=0;i<str.length();i++)
        {
            String sub=str.substring(i,i+1);
            if(sub.equals(regex))
            {
                list.add(element);element="";
            }
            else element+=str.substring(i,i+1);
        }
        if(!element.isEmpty())list.add(element);
        return list;
    }

    public static float[] entitylookAtEntity(Entity e1, Entity e2)
    {
        return entitylookAtEntity(e1.position(),e2.position());
    }

    public static float[] entitylookAtEntityWith90DegLock(Vec3 from, Vec3 to)
    {
        float[] result = entitylookAtEntity(from, to);

        WildDungeons.getLogger().info("Yaw BEFORE: " + result[0]);

        result[0] %= 360;
        if (result[0] < 0) {
            result[0] += 360;
        }

        result[0] = Math.round(result[0] / 90f) * 90f;

        WildDungeons.getLogger().info("Yaw AFTER: " + result[0]);

        return result;
    }

    public static float[] entitylookAtEntity(Vec3 from,Vec3 to)
    {
        double dx = to.x()-from.x();
        double dy = to.y()-from.y();
        double dz = to.z()-from.z();

        double xzDist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz)); //Minecraft has its YAW turn clockwise from z-
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, xzDist));

        // Normalize the YAW tp {0, 360}
        if (yaw < 0) yaw += 360;
        return new float[] {yaw, pitch};
    }

    public static double distance(Vec3 b1,Vec3 b2)
    {
        return distance(b1.x(),b1.y(),b1.z(),b2.x(),b2.y(),b2.z());
    }
    public static double distance(BlockPos b1, BlockPos b2)
    {
        return distance(b1.getX(),b1.getY(),b1.getZ(),b2.getX(),b2.getY(),b2.getZ());
    }
    public static double distance(double x1,double y1,double z1,double x2,double y2,double z2)
    {
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)+Math.pow(z1-z2,2));
    }
}