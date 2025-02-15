package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.dungeon.registries.SoundscapeTemplateRegistry;
import com.danielkkrafft.wilddungeons.sound.DynamicPitchSound;
import com.danielkkrafft.wilddungeons.sound.SoundscapeHandler;
import com.danielkkrafft.wilddungeons.ui.ConnectionBlockEditScreen;
import com.danielkkrafft.wilddungeons.ui.WDLoadingScreen;
import com.danielkkrafft.wilddungeons.ui.WDPostDungeonScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;

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

    public static HashSet<Integer> loopingSounds = new HashSet<>();
    public static void playDynamicSound(CompoundTag data) {
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.byId(data.getInt("soundEvent"));
        SoundSource soundSource = SoundSource.valueOf(data.getString("soundSource"));
        Entity entity = Minecraft.getInstance().level.getEntity(data.getInt("entityId"));
        if (entity != null && (!data.getBoolean("loop") || !loopingSounds.contains(data.getInt("soundEvent")))) {
            DynamicPitchSound dynamicPitchSound = new DynamicPitchSound(soundEvent, soundSource, data.getFloat("volume"), data.getFloat("pitch"), entity, data.getBoolean("loop"));
            Minecraft.getInstance().getSoundManager().play(dynamicPitchSound);
        }
    }

    public static void handleSwitchSoundscape(CompoundTag data) {
        SoundscapeHandler.handleSwitchSoundscape(SoundscapeTemplateRegistry.SOUNDSCAPE_TEMPLATE_REGISTRY.get(data.getString("sound_key")), data.getInt("intensity"));
    }
}
