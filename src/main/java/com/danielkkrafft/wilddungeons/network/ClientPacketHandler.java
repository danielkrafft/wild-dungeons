package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.ui.ConnectionBlockEditScreen;
import com.danielkkrafft.wilddungeons.ui.WDLoadingScreen;
import com.danielkkrafft.wilddungeons.ui.WDPostDungeonScreen;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

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

    public static void handlePostDungeonScreen(CompoundTag data) {
        Minecraft.getInstance().setScreen(new WDPostDungeonScreen(data));
    }
}
