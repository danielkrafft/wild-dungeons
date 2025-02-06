package com.danielkkrafft.wilddungeons.util.debug;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundPostDungeonScreenPacket;
import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class DebugItem extends Item {

    public DebugItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateClientWDPlayer(player.getStringUUID());

        //PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundPostDungeonScreenPacket(new CompoundTag()));
        //logNonDungeonStuff(wdPlayer);
        logDungeonStuff((ServerLevel) level, wdPlayer);

        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    public void logNonDungeonStuff(WDPlayer player) {
        WildDungeons.getLogger().info("RECENT ESSENCE: {}", player.getRecentEssence());
    }

    public void logDungeonStuff(ServerLevel level, WDPlayer wdPlayer) {
        DungeonSession dungeonSession = wdPlayer.getCurrentDungeon();
        DungeonFloor dungeonFloor = wdPlayer.getCurrentFloor();
        DungeonBranch dungeonBranch = wdPlayer.getCurrentBranch();
        DungeonRoom dungeonRoom = wdPlayer.getCurrentRoom();
        DungeonSession.DungeonStats dungeonStats = dungeonSession.getStats(wdPlayer.getUUID());

        WildDungeons.getLogger().info("BEEN IN THIS DUNGEON FOR: {}", dungeonStats.time/20);
        WildDungeons.getLogger().info("FOUND {} FLOORS", dungeonStats.floorsFound);
        WildDungeons.getLogger().info("FOUND {} BRANCHES", dungeonStats.branchesFound);
        WildDungeons.getLogger().info("FOUND {} ROOMS", dungeonStats.roomsFound);
        WildDungeons.getLogger().info("KILLED {} MOBS", dungeonStats.mobsKilled);
        WildDungeons.getLogger().info("DEALT {} DAMAGE", dungeonStats.damageDealt);
        WildDungeons.getLogger().info("TAKEN {} DAMAGE", dungeonStats.damageTaken);
        WildDungeons.getLogger().info("DIED {} TIMES", dungeonStats.deaths);
        WildDungeons.getLogger().info("PLACED {} BLOCKS", dungeonStats.blocksPlaced);
        WildDungeons.getLogger().info("BROKE {} BLOCKS", dungeonStats.blocksBroken);
        WildDungeons.getLogger().info("RECEIVED {} SCORE", dungeonStats.getScore());

        WildDungeons.getLogger().info("CURRENT DUNGEON SESSIONS: {}", DungeonSessionManager.getInstance().getSessionNames());
        WildDungeons.getLogger().info("CURRENT WDPlayers: {}", WDPlayerManager.getInstance().getPlayerNames(level.getServer()));
        WildDungeons.getLogger().info("CURRENT DUNGEON: {}", dungeonSession == null ? "none" : dungeonSession.getTemplate().name());
        WildDungeons.getLogger().info("CURRENT PLAYERS IN DUNGEON: {}", dungeonSession == null ? "none" : dungeonSession.getPlayers());

        WildDungeons.getLogger().info("CURRENT FLOOR: {} INDEX: {}", dungeonFloor == null ? "none" : dungeonFloor.getTemplate().name(), dungeonFloor == null ? "-1" : dungeonFloor.getIndex());
        WildDungeons.getLogger().info("CURRENT BRANCH: {} INDEX: {}", dungeonBranch == null ? "none" : dungeonBranch.getTemplate().name(), dungeonBranch == null ? "-1" : dungeonBranch.getIndex());
        WildDungeons.getLogger().info("CURRENT ROOM: {} INDEX: {}", dungeonRoom == null ? "none" : dungeonRoom.getTemplate().name(), dungeonRoom == null ? "-1" : dungeonRoom.getIndex());
        WildDungeons.getLogger().info("CURRENT ROOM BOUNDING BOXES: {}", dungeonRoom == null ? "none" : dungeonRoom.getBoundingBoxes());
        WildDungeons.getLogger().info("CURRENT ROOM DIFFICULTY: {}", dungeonRoom == null ? "none" : dungeonRoom.getDifficulty());
        WildDungeons.getLogger().info("CURRENT POSITIONS: {}", WDPlayerManager.getInstance().getServerPlayers().get(wdPlayer.getUUID()).getPositions().values().stream().map(SavedTransform::getBlockPos).toList());
        WildDungeons.getLogger().info("CURRENT RESPAWNS: {}", WDPlayerManager.getInstance().getServerPlayers().get(wdPlayer.getUUID()).getRespawns().values().stream().map(SavedTransform::getBlockPos).toList());
        WildDungeons.getLogger().info("CURRENT LIVES: {}", dungeonSession == null ? "none" : dungeonSession.getLives());
    }

}
