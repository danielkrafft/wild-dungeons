package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.dungeon.registries.SoundscapeTemplateRegistry;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.render.DecalRenderer;
import com.danielkkrafft.wilddungeons.sound.DynamicPitchSound;
import com.danielkkrafft.wilddungeons.sound.SoundscapeHandler;
import com.danielkkrafft.wilddungeons.ui.ConnectionBlockEditScreen;
import com.danielkkrafft.wilddungeons.ui.WDLoadingScreen;
import com.danielkkrafft.wilddungeons.ui.WDPostDungeonScreen;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;

public class ClientPacketHandler {
    public enum Packets {
        REMOVE_DECAL, ADD_DECAL, SYNC_DECALS, SWITCH_SOUNDSCAPE, PLAY_DYNAMIC_SOUND, POST_DUNGEON_SCREEN, LOADING_SCREEN, NULL_SCREEN, OPEN_CONNECTION_BLOCK_UI, UPDATE_WD_PLAYER;

        public CompoundTag asTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("packet", this.toString());
            return tag;
        }
    }

    public static HashSet<Integer> loopingSounds = new HashSet<>();
    public static void handleInbound(CompoundTag data) {
        switch (Packets.valueOf(data.getString("packet"))) {
            case REMOVE_DECAL -> {
                DecalRenderer.removeClientDecal(Serializer.fromCompoundTag(data.getCompound("decal")));
            }
            case ADD_DECAL -> {
                DecalRenderer.addClientDecal(Serializer.fromCompoundTag(data.getCompound("decal")));
            }
            case SYNC_DECALS -> {
                DecalRenderer.CLIENT_DECALS_MAP = Serializer.fromCompoundTag(data.getCompound("decal"));
            }
            case SWITCH_SOUNDSCAPE -> {
                SoundscapeHandler.handleSwitchSoundscape(SoundscapeTemplateRegistry.SOUNDSCAPE_TEMPLATE_REGISTRY.get(data.getString("sound_key")), data.getInt("intensity"), data.getBoolean("reset"));
            }
            case PLAY_DYNAMIC_SOUND -> {
                SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.byId(data.getInt("soundEvent"));
                SoundSource soundSource = SoundSource.valueOf(data.getString("soundSource"));
                Entity entity = Minecraft.getInstance().level.getEntity(data.getInt("entityId"));
                if (entity != null && (!data.getBoolean("loop") || !loopingSounds.contains(data.getInt("soundEvent")))) {
                    DynamicPitchSound dynamicPitchSound = new DynamicPitchSound(soundEvent, soundSource, data.getFloat("volume"), data.getFloat("pitch"), entity, data.getBoolean("loop"));
                    Minecraft.getInstance().getSoundManager().play(dynamicPitchSound);
                }
            }
            case POST_DUNGEON_SCREEN -> {
//                WildDungeons.getLogger().info("POST DUNGEON SCREEN PACKET RECEIVED");
//                WildDungeons.getLogger().info("DATA: {}", data);
                Minecraft.getInstance().setScreen(new WDPostDungeonScreen(data.getCompound("stats")));
            }
            case LOADING_SCREEN -> {
                Minecraft.getInstance().setScreen(new WDLoadingScreen());
            }
            case NULL_SCREEN -> {
                Minecraft.getInstance().setScreen(null);
            }
            case OPEN_CONNECTION_BLOCK_UI -> {
                Minecraft.getInstance().setScreen(new ConnectionBlockEditScreen(
                        data.getString("unblockedBlockstate"),
                        data.getString("pool"),
                        data.getString("type"),
                        data.getInt("x"),
                        data.getInt("y"),
                        data.getInt("z")));
            }
            case UPDATE_WD_PLAYER -> {
                WDPlayerManager.getInstance().replaceClientPlayer(Serializer.fromCompoundTag(data.getCompound("player")));
            }
        }
    }
}
