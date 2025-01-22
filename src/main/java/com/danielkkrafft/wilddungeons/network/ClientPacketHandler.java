package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.ui.ConnectionBlockEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

public class ClientPacketHandler {

    public static void handleOpenConnectionBlockUI(CompoundTag data) {

        Minecraft.getInstance().setScreen(new ConnectionBlockEditScreen(
                data.getString("unblockedBlockstate"),
                data.getString("pool"),
                data.getString("type"),
                data.getInt("x"),
                data.getInt("y"),
                data.getInt("z")));

    }
}
