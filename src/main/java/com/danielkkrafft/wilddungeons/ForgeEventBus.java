package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.util.CameraShakeUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ForgeEventBus {

    public static void onCameraShake(ViewportEvent.ComputeCameraAngles event) {
        float shake = CameraShakeUtil.getShakeStrength();
        if (shake > 0f) {
            double yawOffset = (Math.random() - 0.5) * shake;
            double pitchOffset = (Math.random() - 0.5) * shake;
            event.setYaw(event.getYaw() + (float)yawOffset);
            event.setPitch(event.getPitch() + (float)pitchOffset);
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        float delta = Minecraft.getInstance().getFrameTimeNs() / 50_000_000f; // convert to 20/sec
        CameraShakeUtil.tick(delta);
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ForgeEventBus::onClientTick);
        NeoForge.EVENT_BUS.addListener(ForgeEventBus::onCameraShake);
    }
}
