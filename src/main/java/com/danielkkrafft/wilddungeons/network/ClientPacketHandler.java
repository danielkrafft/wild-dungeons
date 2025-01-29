package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.ui.ConnectionBlockEditScreen;
import com.danielkkrafft.wilddungeons.ui.WDLoadingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.LoadingOverlay;
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

    public static void handleNullScreen() {
        Minecraft.getInstance().setScreen(null);
    }

    public static void handleLoadingScreen() {
        Minecraft.getInstance().setScreen(new WDLoadingScreen());
    }
}
