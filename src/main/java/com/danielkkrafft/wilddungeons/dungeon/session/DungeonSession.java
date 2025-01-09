package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.DungeonPerks;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.entity.blockentity.RiftBlockEntity;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class DungeonSession {
    public static final int SHUTDOWN_TIME = 300;
    public static final int LIVES_PER_PLAYER = 3;

    public BlockPos entrance;
    public ServerLevel entranceLevel;
    private final Set<String> playerUUIDs = new HashSet<>();
    public final List<DungeonFloor> floors = new ArrayList<>();
    public DungeonComponents.DungeonTemplate template;
    public int shutdownTimer = SHUTDOWN_TIME;
    public int lives = 0;
    public boolean markedForShutdown = false;
    public WeightedPool<DungeonMaterial> materials;

    public HashMap<DungeonPerks.Perks, DungeonPerk> perks = new HashMap();

    public WeightedTable<EntityType<?>> enemyTable;
    public double difficulty;

    public DungeonExitBehavior exitBehavior;

    protected DungeonSession(BlockPos entrance, ServerLevel entranceLevel, DungeonComponents.DungeonTemplate template) {
        this.entrance = entrance;
        this.entranceLevel = entranceLevel;
        this.template = template;
        this.materials = template.materials();
        this.enemyTable = template.enemyTable();
        this.difficulty = template.difficulty();
        this.exitBehavior = template.exitBehavior();
        DungeonPerk.addPerk(this, DungeonPerks.Perks.SWORD_DAMAGE_INCREASE);
    }

    public enum DungeonExitBehavior {DESTROY_RIFT, NEW_DUNGEON, NOTHING}

    public DungeonFloor getFloor(ResourceKey<Level> levelKey) {
        List<DungeonFloor> matches = floors.stream().filter(dungeonFloor -> dungeonFloor.LEVEL_KEY == levelKey).toList();
        return matches.isEmpty() ? null : matches.getFirst();
    }

    public DungeonFloor generateFloor(int index) {
        WDProfiler.INSTANCE.start();

        WeightedPool<String> destinations = floors.size() == template.floorTemplates().size()-1 ?
                new WeightedPool<String>().add("win", 1) :
                new WeightedPool<String>().add(""+(index+1), 1);

        DungeonFloor floor = template.floorTemplates().get(floors.size()).getRandom().placeInWorld(this, new BlockPos(0,0,0), index, destinations);
        floors.add(floor);

        WDProfiler.INSTANCE.logTimestamp("generateFloor");
        WDProfiler.INSTANCE.end();
        return floor;
    }

    public void onEnter(WDPlayer wdPlayer) {
        if (this.playerUUIDs.contains(wdPlayer.getUUID())) return;
        playerUUIDs.add(wdPlayer.getUUID());
        wdPlayer.setCurrentLives(this.lives);
        wdPlayer.setCurrentDungeon(this);
        if (floors.isEmpty()) generateFloor(0);
        floors.getFirst().onEnter(wdPlayer);
        shutdownTimer = SHUTDOWN_TIME;
        this.offsetLives(LIVES_PER_PLAYER);
    }

    public void onExit(WDPlayer wdPlayer) {
        if (!this.playerUUIDs.contains(wdPlayer.getUUID())) return;
        playerUUIDs.remove(wdPlayer.getUUID());
        wdPlayer.rootRespawn(wdPlayer.getServerPlayer().getServer());
        WildDungeons.getLogger().info("EXITED PLAYER WITH {} RIFT COOLDOWN", wdPlayer.getRiftCooldown());
        WDPlayerManager.syncAll(List.of(wdPlayer.getUUID()));
    }

    public int getLives() {return this.lives;}
    public int offsetLives(int offset) {
        WildDungeons.getLogger().info("OFFSETTING LIVES");
        this.lives += offset;
        for (String playerUUID : this.playerUUIDs) {
            WDPlayer player = WDPlayerManager.getInstance().getOrCreateWDPlayer(playerUUID);
            player.setCurrentLives(this.lives);
        }
        if (this.lives <= 0 && !this.playerUUIDs.isEmpty()) {
            this.fail();
        }
        WDPlayerManager.syncAll(this.playerUUIDs.stream().toList());
        return this.lives;
    }

    public Set<WDPlayer> getPlayers() {
        Set<WDPlayer> result = new HashSet<>();
        for (String uuid : this.playerUUIDs) {
            result.add(WDPlayerManager.getInstance().getOrCreateWDPlayer(uuid));
        }
        return result;
    }

    public void win() {
        for (WDPlayer wdPlayer : getPlayers()) {
            this.onExit(wdPlayer);
            wdPlayer.getServerPlayer().addItem(new ItemStack(Items.DIAMOND.asItem(), 1));
        }
        this.handleExitBehavior();
    }

    public void fail() {
        for (WDPlayer wdPlayer : getPlayers()) {
            this.onExit(wdPlayer);
        }
        this.handleExitBehavior();
    }

    public void handleExitBehavior() {
        switch (this.exitBehavior) {
            case DESTROY_RIFT -> {
                this.entranceLevel.setBlock(this.entrance, Blocks.AIR.defaultBlockState(), 2);
            }
            case NEW_DUNGEON -> {
                BlockEntity blockEntity = this.entranceLevel.getBlockEntity(this.entrance);
                if (blockEntity instanceof RiftBlockEntity riftBlockEntity) {
                    riftBlockEntity.destination = "wd-"+this.template.nextDungeon().getRandom().name();
                }
            }
        }
    }

    public void tick() {
        if (playerUUIDs.isEmpty() && !floors.isEmpty()) {shutdownTimer -= 1;}
        if (shutdownTimer == 0) {shutdown();return;}
        if (!playerUUIDs.isEmpty()) floors.forEach(DungeonFloor::tick);
    }

    public void shutdown() {
        getPlayers().forEach(this::onExit);
        floors.forEach(DungeonFloor::shutdown);
        markedForShutdown = true;
    }
}