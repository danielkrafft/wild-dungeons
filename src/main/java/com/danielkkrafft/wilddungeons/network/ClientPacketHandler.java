package com.danielkkrafft.wilddungeons.network;

import com.danielkkrafft.wilddungeons.dungeon.registries.SoundscapeTemplateRegistry;
import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.sound.DynamicPitchSound;
import com.danielkkrafft.wilddungeons.sound.SoundscapeHandler;
import com.danielkkrafft.wilddungeons.ui.ConnectionBlockEditScreen;
import com.danielkkrafft.wilddungeons.ui.RoomExportScreen;
import com.danielkkrafft.wilddungeons.ui.WDLoadingScreen;
import com.danielkkrafft.wilddungeons.ui.WDPostDungeonScreen;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

public class ClientPacketHandler { //TODO - Set correct defaults for data getters, uncomment decal renderer lines when decal renderer is fixed for 1.21.11
    public enum Packets {
        REMOVE_DECAL, ADD_DECAL, SYNC_DECALS, SWITCH_SOUNDSCAPE, PLAY_DYNAMIC_SOUND, POST_DUNGEON_SCREEN, LOADING_SCREEN, NULL_SCREEN, OPEN_CONNECTION_BLOCK_UI, UPDATE_WD_PLAYER, OPEN_WAND_SCREEN, IS_UNDERWATER;

        public CompoundTag asTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("packet", this.toString());
            return tag;
        }
    }

    public static HashSet<Integer> loopingSounds = new HashSet<>();
    public static void handleInbound(CompoundTag data) {
        switch (Packets.valueOf(data.getStringOr("packet", ""))) {
            case REMOVE_DECAL -> {
                //DecalRenderer.removeClientDecal(Serializer.fromCompoundTag(data.getCompound("decal")));
            }
            case ADD_DECAL -> {
                //DecalRenderer.addClientDecal(Serializer.fromCompoundTag(data.getCompound("decal")));
            }
            case SYNC_DECALS -> {
                //DecalRenderer.CLIENT_DECALS_MAP = Serializer.fromCompoundTag(data.getCompound("decal"));
            }
            case SWITCH_SOUNDSCAPE -> {
                SoundscapeHandler.handleSwitchSoundscape(
                        SoundscapeTemplateRegistry.SOUNDSCAPE_TEMPLATE_REGISTRY.get(data.getStringOr("sound_key", "")),
                        data.getIntOr("intensity", 0),
                        data.getBooleanOr("reset", false));
            }
            case PLAY_DYNAMIC_SOUND -> {
                SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.byId(data.getIntOr("soundEvent", 0));
                SoundSource soundSource = SoundSource.valueOf(data.getStringOr("soundSource", ""));
                Entity entity = Minecraft.getInstance().level.getEntity(data.getIntOr("entityId", 0));
                if (entity != null && (!data.getBooleanOr("loop", false) || !loopingSounds.contains(data.getInt("soundEvent")))) {
                    DynamicPitchSound dynamicPitchSound = new DynamicPitchSound(soundEvent, soundSource, data.getFloatOr("volume", 0), data.getFloatOr("pitch", 0), entity, data.getBooleanOr("loop", false));
                    loopingSounds.add(data.getIntOr("soundEvent", 0));
                    Minecraft.getInstance().getSoundManager().play(dynamicPitchSound);
                }
            }
            case POST_DUNGEON_SCREEN -> {
//                WildDungeons.getLogger().info("POST DUNGEON SCREEN PACKET RECEIVED");
//                WildDungeons.getLogger().info("DATA: {}", data);
                Minecraft.getInstance().setScreen(new WDPostDungeonScreen(data.getCompoundOrEmpty("stats")));
            }
            case LOADING_SCREEN -> {
                Minecraft.getInstance().setScreen(new WDLoadingScreen());
            }
            case NULL_SCREEN -> {
                Minecraft.getInstance().setScreen(null);
            }
            case OPEN_CONNECTION_BLOCK_UI -> {
                Minecraft.getInstance().setScreen(new ConnectionBlockEditScreen(
                        data.getStringOr("unblockedBlockstate", ""),
                        data.getStringOr("pool", ""),
                        data.getStringOr("type", ""),
                        data.getIntOr("x", 0),
                        data.getIntOr("y", 0),
                        data.getIntOr("z" ,0)));
            }
            case UPDATE_WD_PLAYER -> {
                WDPlayerManager.getInstance().replaceClientPlayer(Serializer.fromCompoundTag(data.getCompoundOrEmpty("player")));
            }
            case OPEN_WAND_SCREEN -> {
                assert Minecraft.getInstance().player != null;
                ItemStack itemStack = Minecraft.getInstance().player.getItemInHand(Minecraft.getInstance().player.getUsedItemHand());
                ClientLevel level = Minecraft.getInstance().level;
                Minecraft.getInstance().setScreen(new RoomExportScreen(itemStack , ((RoomExportWand)itemStack.getItem()).getDungeonMaterials(itemStack, level)));
            }
            case IS_UNDERWATER -> {
                SoundscapeHandler.toggleUnderwater(data.getBooleanOr("isUnderwater", false));
            }
        }
    }
}
