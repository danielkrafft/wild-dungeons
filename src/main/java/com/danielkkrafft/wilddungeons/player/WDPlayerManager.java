package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.room.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class WDPlayerManager {
    private static final WDPlayerManager INSTANCE = new WDPlayerManager();
    private Map<String, WDPlayer> players = new HashMap<>();

    private WDPlayerManager(){}

    public WDPlayer getOrCreateWDPlayer(Player player) {
        return players.computeIfAbsent(player.getStringUUID(), k -> new WDPlayer(player.getStringUUID()));
    }

    public WDPlayer getOrCreateWDPlayer(String uuid) {
        return players.computeIfAbsent(uuid, k -> new WDPlayer(uuid));
    }

    public void replaceWDPlayer(String playerUUID, WDPlayer wdPlayer) {
        players.put(playerUUID, wdPlayer);
    }

    public static void syncAll(List<String> playerUUIDs) {
        WildDungeons.getLogger().info("SYNCING {} PLAYERS", playerUUIDs.size());
        for (int i = 0; i < playerUUIDs.size(); i++) {
            WDPlayer player = getInstance().getOrCreateWDPlayer(playerUUIDs.get(i));
            WildDungeons.getLogger().info("SYNCING PLAYER {} OF {}", i, playerUUIDs.size());
            PacketDistributor.sendToPlayer(player.getServerPlayer(), new ClientboundUpdateWDPlayerPacket(Serializer.toCompoundTag(player)));
            //PacketDistributor.sendToPlayer(player.getServerPlayer(), new ClientboundUpdateWDPlayerPacket(player.toCompoundTag()));
        }
    }

    public Map<String, WDPlayer> getPlayers() {return this.players;}
    public void setPlayers(Map<String, WDPlayer> map) {this.players = map;}
    public static WDPlayerManager getInstance() {return INSTANCE;}

    public List<String> getPlayerNames(MinecraftServer server) {
        List<String> result = new ArrayList<>();
        players.forEach((k,v) -> {
            result.add(server.getPlayerList().getPlayer(UUID.fromString(k)).getName().toString());
        });
        return result;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            WildDungeons.getLogger().info("FOUND BLOCK BREAK");
            event.setCanceled(isProtectedBlock(event.getPos(), serverLevel));
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            event.setCanceled(isProtectedBlock(event.getPos(), serverLevel));
            event.getLevel().getServer();
        }
    }

    public static boolean isProtectedBlock(BlockPos pos, ServerLevel level) {
        DungeonSession session = DungeonSessionManager.getInstance().getFromKey(level.dimension());
        if (session == null) return false;
        WildDungeons.getLogger().info("FOUND DUNGEON SESSION");

        for (WDPlayer wdPlayer : session.getPlayers()) {
            DungeonRoom room = wdPlayer.getCurrentRoom();
            if (room == null) continue;
            WildDungeons.getLogger().info("FOUND DUNGEON ROOM WITH DESTRUCTION RULE: {}", room.getDestructionRule());
            if (room.alwaysBreakable.contains(pos)) return false;

            if (room.getDestructionRule() == DungeonRoom.DestructionRule.DEFAULT) {
                for (BoundingBox box : room.boundingBoxes) {
                    if (box.isInside(pos)) return false;
                }
            }

            if (room.getDestructionRule() == DungeonRoom.DestructionRule.SHELL) {
                if (room.isPosInsideShell(pos)) return false;
            }
        }

        WildDungeons.getLogger().info("POSITION IS PROTECTED");
        return true;
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player);
            if (wdPlayer.getCurrentDungeon() == null) return;

            if (wdPlayer.getCurrentDungeon().getLives() > 0) {

                CriteriaTriggers.USED_TOTEM.trigger(player, new ItemStack(Items.TOTEM_OF_UNDYING));
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);

                player.setHealth(1.0f);
                player.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                player.level().broadcastEntityEvent(player, (byte)35);
                player.teleportTo(player.getRespawnPosition().getX(), player.getRespawnPosition().getY(), player.getRespawnPosition().getZ());
                wdPlayer.getCurrentDungeon().offsetLives(-1);
            } else {
                wdPlayer.getCurrentDungeon().offsetLives(-1);
                wdPlayer.getCurrentDungeon().fail();
            }

            event.setCanceled(true);

        }
    }
}
