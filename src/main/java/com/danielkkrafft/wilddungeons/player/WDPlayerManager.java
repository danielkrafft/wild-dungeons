package com.danielkkrafft.wilddungeons.player;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.CombatRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.EnemyPurgeRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
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

            DungeonSession session = player.getCurrentDungeon();
            if (session != null) player.setCurrentLives(session.getLives());
            PacketDistributor.sendToPlayer(player.getServerPlayer(), new ClientboundUpdateWDPlayerPacket(Serializer.toCompoundTag(player)));
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
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getPlayer() instanceof ServerPlayer serverPlayer) {
            WildDungeons.getLogger().info("FOUND BLOCK BREAK");
            event.setCanceled(isProtectedBlock(serverPlayer, event.getPos(), serverLevel));
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).blocksBroken += 1;

            if (wdPlayer.getCurrentRoom() instanceof EnemyPurgeRoom enemyPurgeRoom) {
                if (event.getLevel().getBlockState(event.getPos()).is(Blocks.SPAWNER)) {
                    enemyPurgeRoom.discardByBlockPos(event.getPos());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof ServerPlayer serverPlayer) {
            event.setCanceled(isProtectedBlock(serverPlayer, event.getPos(), serverLevel));
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer.getStringUUID());
            if (wdPlayer.getCurrentDungeon() == null) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).blocksPlaced += 1;
        }
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

        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player.getStringUUID());
        if (wdPlayer.getCurrentBranch() != null && !room.getBranch().hasPlayerVisited(player.getStringUUID())) return true;

        if (room.getDestructionRule() == DungeonRoomTemplate.DestructionRule.NONE) {
            return false;
        }

        if (room.getDestructionRule() == DungeonRoomTemplate.DestructionRule.SHELL) {
            if (room.isPosInsideShell(pos)) return false;
        }

        WildDungeons.getLogger().info("POSITION IS PROTECTED");
        return true;
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player);
            if (wdPlayer.getCurrentDungeon() == null) return;

            WildDungeons.getLogger().info("TESTING DEATH WITH DUNGEON LIVES: {}", wdPlayer.getCurrentDungeon().getLives());

            if (wdPlayer.getCurrentDungeon().getLives() > 0) {

                CriteriaTriggers.USED_TOTEM.trigger(player, new ItemStack(Items.TOTEM_OF_UNDYING));
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);

                player.setHealth(1.0f);
                player.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                player.level().broadcastEntityEvent(player, (byte)35);
                BlockPos respawnPoint;
                DungeonRoom room = wdPlayer.getCurrentRoom();

                if (room == null) {
                    DungeonFloor floor = wdPlayer.getCurrentFloor();
                    int index = 0;
                    for (DungeonBranch branch : floor.getBranches()) {
                        if (branch.hasPlayerVisited(wdPlayer.getUUID())) {
                            if (branch.getIndex() > index) {
                                index = branch.getIndex();
                            }
                        }
                    }
                    room = floor.getBranches().get(index).getRooms().getFirst();
                }

                if (room.getBranch().getIndex() == 0) {
                    respawnPoint = room.getBranch().getRooms().getFirst().getSpawnPoint(room.getBranch().getFloor().getLevel());
                } else {
                    respawnPoint = room.getBranch().getFloor().getBranches().get(room.getBranch().getIndex()-1).getRooms().getLast().getSpawnPoint(room.getBranch().getFloor().getLevel());
                }

                wdPlayer.setRiftCooldown(140);
                player.teleportTo(respawnPoint.getX(), respawnPoint.getY(), respawnPoint.getZ());
                wdPlayer.getCurrentDungeon().getStats(wdPlayer).deaths += 1;
                wdPlayer.getCurrentDungeon().offsetLives(-1);
            } else {
                wdPlayer.getCurrentDungeon().offsetLives(-1);
                wdPlayer.getCurrentDungeon().fail();
            }

            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);

            if (wdPlayer.getCurrentDungeon() == null) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).mobsKilled += 1;
            if (wdPlayer.getCurrentRoom() instanceof EnemyPurgeRoom room) room.discardByUUID(event.getEntity().getStringUUID());
        }
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null || event.getSource().typeHolder().equals(DamageTypes.GENERIC_KILL)) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).damageDealt += event.getNewDamage();
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).damageTaken += event.getNewDamage();
        }
    }
}
