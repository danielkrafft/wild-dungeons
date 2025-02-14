package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.TargetPurgeRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2i;

import java.util.*;

public class WDPlayerManager {
    private static final WDPlayerManager INSTANCE = new WDPlayerManager();
    private Map<String, WDPlayer> serverPlayers = new HashMap<>();
    private WDPlayer clientPlayer = null;

    private WDPlayerManager(){}

    public WDPlayer getOrCreateClientWDPlayer(LocalPlayer player) {
        return this.clientPlayer == null ? new WDPlayer(player.getStringUUID()) : this.clientPlayer;
    }

    public WDPlayer getOrCreateClientWDPlayer(String uuid) {
        return this.clientPlayer == null ? new WDPlayer(uuid) : this.clientPlayer;
    }

    public void replaceClientPlayer(WDPlayer wdPlayer) {
        this.clientPlayer = wdPlayer;
    }

    public WDPlayer getOrCreateServerWDPlayer(ServerPlayer player) {
        return this.getServerPlayers().computeIfAbsent(player.getStringUUID(), k -> new WDPlayer(player.getStringUUID()));
    }

    public WDPlayer getOrCreateServerWDPlayer(String uuid) {
        return this.getServerPlayers().computeIfAbsent(uuid, k -> new WDPlayer(uuid));
    }

    public static void syncAll(List<String> playerUUIDs) {
        WildDungeons.getLogger().info("SYNCING {} PLAYERS", playerUUIDs.size());
        for (int i = 0; i < playerUUIDs.size(); i++) {
            WDPlayer player = getInstance().getOrCreateServerWDPlayer(playerUUIDs.get(i));

            DungeonSession session = player.getCurrentDungeon();
            if (session != null) player.setCurrentLives(session.getLives());
            PacketDistributor.sendToPlayer(player.getServerPlayer(), new ClientboundUpdateWDPlayerPacket(Serializer.toCompoundTag(player)));
        }
    }

    public Map<String, WDPlayer> getServerPlayers() {
        return this.serverPlayers;
    }
    public void setServerPlayers(Map<String, WDPlayer> map) {
        this.serverPlayers = map;
    }
    public static WDPlayerManager getInstance() {return INSTANCE;}

    public List<String> getPlayerNames(MinecraftServer server) {
        List<String> result = new ArrayList<>();
        getServerPlayers().forEach((k,v) -> {
            result.add(server.getPlayerList().getPlayer(UUID.fromString(k)).getName().toString());
        });
        return result;
    }

    public static boolean isProtectedBlock(ServerPlayer player, BlockPos pos, ServerLevel level) {
        DungeonFloor floor = DungeonSessionManager.getInstance().getFloorFromKey(level.dimension());
        if (floor == null) return false;
        WildDungeons.getLogger().info("FOUND DUNGEON FLOOR");

        DungeonRoom room = null;

        for (Vector2i vec2 : floor.getChunkMap().getOrDefault(new ChunkPos(pos), new ArrayList<>())) {
            DungeonRoom possibleMatch = floor.getBranches().get(vec2.x).getRooms().get(vec2.y);
            if (possibleMatch == null) continue;
            for (BoundingBox box : possibleMatch.getBoundingBoxes()) {
                if (box.isInside(pos)) {
                    room = possibleMatch;
                }
            }
        }

        if (room == null) return false;
        WildDungeons.getLogger().info("FOUND DUNGEON ROOM");

        if (room.getAlwaysBreakable().contains(pos)) return false;

        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(player.getStringUUID());
        if (wdPlayer.getCurrentBranch() != null && !room.getBranch().hasPlayerVisited(player.getStringUUID())) return true;

//        if (room.getDestructionRule() == DungeonRoomTemplate.DestructionRule.NONE) {
//            return false;
//        }
//
//        if (room.getDestructionRule() == DungeonRoomTemplate.DestructionRule.SHELL) {
//            if (room.isPosInsideShell(pos)) return false;
//        }
//
//        if (room.getDestructionRule() == DungeonRoomTemplate.DestructionRule.SHELL_CLEAR && room.isClear()) {
//            return false;
//        }
        return false;
    }
}
