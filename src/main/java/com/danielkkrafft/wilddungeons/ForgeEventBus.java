package com.danielkkrafft.wilddungeons;

import com.danielkkrafft.wilddungeons.item.itemhelpers.SwingHandler;
import com.danielkkrafft.wilddungeons.network.ServerPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.util.CameraShakeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

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

    private static boolean wasSwinging = false;

    public static void onClientTick(ClientTickEvent.Post event) {

        // --- Camera Shake ---
        float delta = Minecraft.getInstance().getFrameTimeNs() / 50_000_000f; // for your camera shake
        CameraShakeUtil.tick(delta);

        // --- Swing Detection ---
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var player = mc.player;
        var item = player.getMainHandItem().getItem();
        boolean isSwinging = player.swingTime > 0;

        if (isSwinging && !wasSwinging && item instanceof SwingHandler handler) {
            handler.onSwing(player, player.getMainHandItem()); // Fires once per swing

            CompoundTag tag = new CompoundTag();
            tag.putString("packet", ServerPacketHandler.Packets.ON_SWING.toString());
            PacketDistributor.sendToServer(new SimplePacketManager.ServerboundTagPacket(tag));
        }

        wasSwinging = isSwinging; // Store state for next tick
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ForgeEventBus::onClientTick);
        NeoForge.EVENT_BUS.addListener(ForgeEventBus::onCameraShake);
    }
}
