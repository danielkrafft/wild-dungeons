package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.TemplateHelper;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.IgnoreSerialization;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class DungeonSession {

    public static final int SHUTDOWN_TIME = 300;
    public static final int LIVES_PER_PLAYER = 3;

    private final String entranceUUID;
    private final ResourceKey<Level> entranceLevelKey;
    private final Set<String> playerUUIDs = new HashSet<>();
    private final HashMap<String, DungeonStats> playerStats = new HashMap<>();
    @IgnoreSerialization
    private List<DungeonFloor> floors = new ArrayList<>();
    private final String template;
    private int shutdownTimer = SHUTDOWN_TIME;
    private int lives = 0;
    private boolean markedForShutdown = false;
    private final HashMap<String, DungeonPerk> perks = new HashMap<>();
    private boolean safeToSerialize = false;

    public ServerLevel getEntranceLevel() {return DungeonSessionManager.getInstance().server.getLevel(this.entranceLevelKey);}
    public String getEntranceUUID() {return this.entranceUUID;}
    public List<DungeonFloor> getFloors() {return this.floors;}
    public DungeonTemplate getTemplate() {return DungeonRegistry.DUNGEON_REGISTRY.get(this.template);}
    public int getLives() {return this.lives;}
    public boolean isMarkedForShutdown() {return this.markedForShutdown;}
    public HashMap<String, DungeonPerk> getPerks() {return this.perks;}
    public String getSessionKey() {return DungeonSessionManager.buildDungeonSessionKey(this.entranceUUID);}
    public boolean isSafeToSerialize() {return this.safeToSerialize;}
    public DungeonStats getStats(WDPlayer player) {return this.playerStats.get(player.getUUID());}

    public enum DungeonExitBehavior {DESTROY_RIFT, NEW_DUNGEON, NOTHING}

    protected DungeonSession(String entranceUUID, ResourceKey<Level> entranceLevelKey, String template) {
        this.entranceUUID = entranceUUID;
        this.entranceLevelKey = entranceLevelKey;
        this.template = template;
        WildDungeons.getLogger().info("DUNGEON MATERIALS: {}", this.getTemplate().materials().size());

    }

//    public DungeonFloor getFloor(ResourceKey<Level> levelKey) {
//        List<DungeonFloor> matches = floors.stream().filter(dungeonFloor -> dungeonFloor.LEVEL_KEY == levelKey).toList();
//        return matches.isEmpty() ? null : matches.getFirst();
//    }

    public void generateFloor(int index) {
        WDProfiler.INSTANCE.start();
        safeToSerialize = false;

        WeightedPool<String> destinations = floors.size() == getTemplate().floorTemplates().size()-1 ?
                new WeightedPool<String>().add("win", 1) :
                new WeightedPool<String>().add(""+(index+1), 1);

        getTemplate().floorTemplates().get(floors.size()).getRandom().placeInWorld(this, TemplateHelper.EMPTY_BLOCK_POS, destinations);

        this.floors.forEach(dungeonFloor -> {
            dungeonFloor.getBranches().forEach(branch -> {
              branch.setTempFloor(null);
              branch.getRooms().forEach(room -> {
                  room.setTempBranch(null);
              });
            });
        });

        safeToSerialize = true;

        WDProfiler.INSTANCE.logTimestamp("generateFloor");
        WDProfiler.INSTANCE.end();
    }

    public void onEnter(WDPlayer wdPlayer) {
        if (this.playerUUIDs.contains(wdPlayer.getUUID())) return;
        playerUUIDs.add(wdPlayer.getUUID());
        wdPlayer.setCurrentLives(this.lives);
        wdPlayer.setCurrentDungeon(this);
        if (floors.isEmpty()) generateFloor(0);
        floors.getFirst().onEnter(wdPlayer);
        shutdownTimer = SHUTDOWN_TIME;
        if (this.playerStats.containsKey(wdPlayer.getUUID())) return;
        this.playerStats.put(wdPlayer.getUUID(), new DungeonStats());
        this.offsetLives(LIVES_PER_PLAYER);
    }

    public void onExit(WDPlayer wdPlayer) {
        if (!this.playerUUIDs.contains(wdPlayer.getUUID())) return;
        playerUUIDs.remove(wdPlayer.getUUID());
        wdPlayer.rootRespawn(wdPlayer.getServerPlayer().getServer());
        WildDungeons.getLogger().info("EXITED PLAYER WITH {} RIFT COOLDOWN", wdPlayer.getRiftCooldown());
        WDPlayerManager.syncAll(List.of(wdPlayer.getUUID()));
    }

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

    public void givePerk(DungeonPerkTemplate perkTemplate) {
        WildDungeons.getLogger().info("ADDING PERK: {}", perkTemplate.name());
        DungeonPerk perk = this.getPerks().containsKey(perkTemplate.name()) ? this.getPerks().get(perkTemplate.name()) : new DungeonPerk(perkTemplate.name(), DungeonSessionManager.buildDungeonSessionKey(this.getEntranceUUID()));

        perk.count++;
        this.getPerks().put(perkTemplate.name(), perk);
        perk.onCollect();
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
        switch (this.getTemplate().exitBehavior()) {
            case DESTROY_RIFT -> {
                this.shutdown();
                this.getEntranceLevel().getEntity(UUID.fromString(this.getEntranceUUID())).remove(Entity.RemovalReason.DISCARDED);
            }
            case NEW_DUNGEON -> {
                this.shutdown();
                Offering rift = (Offering) this.getEntranceLevel().getEntity(UUID.fromString(this.getEntranceUUID()));
                if (rift != null) rift.setOfferingId("wd-"+this.getTemplate().nextDungeon().getRandom().name());
            }
        }
    }

    public void tick() {
        if (playerUUIDs.isEmpty() && !floors.isEmpty()) {shutdownTimer -= 1;}
        if (shutdownTimer == 0) {shutdown();return;}
        if (!playerUUIDs.isEmpty()) floors.forEach(DungeonFloor::tick);
        playerUUIDs.forEach(uuid -> {
            if (this.playerStats.containsKey(uuid)) {
                this.playerStats.get(uuid).time++;
            }
        });
    }

    public void shutdown() {
        getPlayers().forEach(this::onExit);
        floors.forEach(DungeonFloor::shutdown);
        markedForShutdown = true;
    }

    public void addFloor(DungeonFloor floor) {
        if (this.floors == null) this.floors = new ArrayList<>();
        this.floors.add(floor);
    }

    public static class DungeonStats {
        public int time = 0;
        public DungeonStats() {}
    }
}