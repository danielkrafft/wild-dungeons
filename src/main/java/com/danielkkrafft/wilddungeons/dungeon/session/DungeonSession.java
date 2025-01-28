package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.IgnoreSerialization;
import com.danielkkrafft.wilddungeons.util.SaveSystem;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.*;

public class DungeonSession {

    public static final int SHUTDOWN_TIME = 300;
    public static final int LIVES_PER_PLAYER = 3;

    private final String entranceUUID;
    private final ResourceKey<Level> entranceLevelKey;
    private final HashMap<String, PlayerStatus> playerStatuses = new HashMap<>();
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
    public DungeonStats getStats(String uuid) {return this.playerStats.get(uuid);}

    public enum DungeonExitBehavior {DESTROY, RANDOMIZE, RESET, NOTHING}

    protected DungeonSession(String entranceUUID, ResourceKey<Level> entranceLevelKey, String template) {
        this.entranceUUID = entranceUUID;
        this.entranceLevelKey = entranceLevelKey;
        this.template = template;
        WildDungeons.getLogger().info("DUNGEON MATERIALS: {}", this.getTemplate().materials().size());

    }

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
        playerStatuses.computeIfAbsent(wdPlayer.getUUID(), key -> new PlayerStatus());
        if (this.playerStatuses.get(wdPlayer.getUUID()).inside) return;
        this.playerStats.putIfAbsent(wdPlayer.getUUID(), new DungeonStats());
        this.offsetLives(LIVES_PER_PLAYER);
        playerStatuses.get(wdPlayer.getUUID()).inside = true;
        wdPlayer.setCurrentLives(this.lives);
        wdPlayer.setCurrentDungeon(this);
        if (floors.isEmpty()) generateFloor(0);
        floors.getFirst().onEnter(wdPlayer);
        shutdownTimer = SHUTDOWN_TIME;
        if (this.playerStats.containsKey(wdPlayer.getUUID())){
            WDPlayerManager.syncAll(this.playerStatuses.keySet().stream().toList());//only sync if player reenters because we sync all new players anyway
            return;
        }
    }

    public void onExit(WDPlayer wdPlayer) {
        if (!this.playerStatuses.containsKey(wdPlayer.getUUID()) || !this.playerStatuses.get(wdPlayer.getUUID()).inside) return;
        playerStatuses.get(wdPlayer.getUUID()).inside = false;
        wdPlayer.rootRespawn(wdPlayer.getServerPlayer().getServer());
        WildDungeons.getLogger().info("EXITED PLAYER WITH {} RIFT COOLDOWN", wdPlayer.getRiftCooldown());
        WDPlayerManager.syncAll(List.of(wdPlayer.getUUID()));
    }

    public int offsetLives(int offset) {
        WildDungeons.getLogger().info("OFFSETTING LIVES");
        this.lives += offset;
        for (String playerUUID : this.playerStatuses.keySet()) {
            WDPlayer player = WDPlayerManager.getInstance().getOrCreateWDPlayer(playerUUID);
            player.setCurrentLives(this.lives);
        }
        if (this.lives <= 0 && this.playerStatuses.values().stream().anyMatch(value -> value.inside)) {
            WildDungeons.getLogger().info("SHUTTING DOWN DUE TO LIVES");
            this.fail();
        }
        WDPlayerManager.syncAll(this.playerStatuses.keySet().stream().toList());
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
        for (String uuid : this.playerStatuses.keySet()) {
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
        WildDungeons.getLogger().info("SHUTTING DOWN DUE TO FAIL");
        this.handleExitBehavior();
    }

    public void handleExitBehavior() {
        WildDungeons.getLogger().info("SHUTTING DOWN DUE TO EXIT BEHAVIOR");
        switch (this.getTemplate().exitBehavior()) {
            case DESTROY -> {
                this.shutdown();
                Entity rift = this.getEntranceLevel().getEntity(UUID.fromString(this.getEntranceUUID()));
                if (rift != null) rift.remove(Entity.RemovalReason.DISCARDED);
            }
            case RANDOMIZE -> {
                this.shutdown();
                Offering rift = (Offering) this.getEntranceLevel().getEntity(UUID.fromString(this.getEntranceUUID()));
                if (rift != null) rift.setOfferingId("wd-"+this.getTemplate().nextDungeon().getRandom().name());
            }
            case RESET -> {
                this.shutdown();
            }
        }
    }

    public void tick() {
        if (playerStatuses.values().stream().noneMatch(v -> v.inside) && !floors.isEmpty()) {shutdownTimer -= 1;}
        if (shutdownTimer == 0) {
            WildDungeons.getLogger().info("SHUTTING DOWN DUE TO TIMER");
            shutdown();return;}
        if (playerStatuses.values().stream().anyMatch(v -> v.inside)) floors.forEach(DungeonFloor::tick);
        playerStatuses.keySet().forEach(uuid -> {
            if (this.playerStats.containsKey(uuid)) {
                this.playerStats.get(uuid).time++;
            }
        });
    }

    public void shutdown() {
        WildDungeons.getLogger().info("SHUTTING DOWN DUNGEON");
        getPlayers().forEach(this::onExit);
        floors.forEach(DungeonFloor::shutdown);
        SaveSystem.DeleteSession(this);
        markedForShutdown = true;
    }

    public void addFloor(DungeonFloor floor) {
        if (this.floors == null) this.floors = new ArrayList<>();
        this.floors.add(floor);
    }
    public void sortFloors() {
        this.floors.sort(Comparator.comparingInt(DungeonFloor::getIndex));
    }

    public static class DungeonStats {
        public int time = 0;
        public int floorsFound = 0;
        public int branchesFound = 0;
        public int roomsFound = 0;
        public int mobsKilled = 0;
        public float damageDealt = 0.0f;
        public float damageTaken = 0.0f;
        public int deaths = 0;
        public int blocksPlaced = 0;
        public int blocksBroken = 0;

        public int getScore() {
            return (int) Math.max(0, (damageDealt * 1) + (mobsKilled * 100) + (deaths * -1000));
        }
    }

    public static class PlayerStatus {
        public boolean inside = false;
        public boolean insideShell = false;
    }
}