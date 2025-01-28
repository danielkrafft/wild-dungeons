package com.danielkkrafft.wilddungeons.util.debug;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DebugItem extends Item {

    public DebugItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(usedHand));

        DungeonSession dungeonSession = WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getCurrentDungeon();
        DungeonFloor dungeonFloor = WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getCurrentFloor();
        DungeonBranch dungeonBranch = WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getCurrentBranch();
        DungeonRoom dungeonRoom = WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getCurrentRoom();
        DungeonSession.DungeonStats dungeonStats = dungeonSession.getStats(String.valueOf(player.getUUID()));

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

//        WildDungeons.getLogger().info("CURRENT DUNGEON SESSIONS: {}", DungeonSessionManager.getInstance().getSessionNames());
//        WildDungeons.getLogger().info("CURRENT WDPlayers: {}", WDPlayerManager.getInstance().getPlayerNames(level.getServer()));
//        WildDungeons.getLogger().info("CURRENT DUNGEON: {}", dungeonSession == null ? "none" : dungeonSession.getTemplate().name());
//        WildDungeons.getLogger().info("CURRENT PLAYERS IN DUNGEON: {}", dungeonSession == null ? "none" : dungeonSession.getPlayers());
//
//        WildDungeons.getLogger().info("CURRENT FLOOR: {}", dungeonFloor == null ? "none" : dungeonFloor.getTemplate().name());
//        WildDungeons.getLogger().info("CURRENT BRANCH: {}", dungeonBranch == null ? "none" : dungeonBranch.getTemplate().name());
//        WildDungeons.getLogger().info("CURRENT ROOM: {}", dungeonRoom == null ? "none" : dungeonRoom.getTemplate().name());
//        WildDungeons.getLogger().info("CURRENT ROOM DIFFICULTY: {}", dungeonRoom == null ? "none" : dungeonRoom.getDifficulty());
//        WildDungeons.getLogger().info("CURRENT POSITIONS: {}", WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getPositions().values().stream().map(SavedTransform::getBlockPos).toList());
//        WildDungeons.getLogger().info("CURRENT RESPAWNS: {}", WDPlayerManager.getInstance().getPlayers().get(player.getStringUUID()).getRespawns().values().stream().map(SavedTransform::getBlockPos).toList());
//        WildDungeons.getLogger().info("CURRENT LIVES: {}", dungeonSession == null ? "none" : dungeonSession.getLives());

        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

}
