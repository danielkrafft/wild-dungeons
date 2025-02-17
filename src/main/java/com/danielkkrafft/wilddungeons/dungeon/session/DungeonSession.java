package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundPostDungeonScreenPacket;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.IgnoreSerialization;
import com.danielkkrafft.wilddungeons.util.SaveSystem;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.danielkkrafft.wilddungeons.util.debug.WDProfiler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRegistry.DUNGEON_REGISTRY;

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

    public ServerLevel getEntranceLevel() {return DungeonSessionManager.getInstance().server.getLevel(this.entranceLevelKey);}
    public String getEntranceUUID() {return this.entranceUUID;}
    public List<DungeonFloor> getFloors() {return this.floors;}
    public DungeonTemplate getTemplate() {return DUNGEON_REGISTRY.get(this.template);}
    public int getLives() {return this.lives;}
    public boolean isMarkedForShutdown() {return this.markedForShutdown;}
    public HashMap<String, DungeonPerk> getPerks() {return this.perks;}
    public String getSessionKey() {return DungeonSessionManager.buildDungeonSessionKey(this.entranceUUID);}
    public DungeonStats getStats(WDPlayer player) {return this.playerStats.get(player.getUUID());}
    public DungeonStats getStats(String uuid) {return this.playerStats.get(uuid);}

    public enum DungeonExitBehavior {DESTROY, RANDOMIZE, RESET, NOTHING}

    protected DungeonSession(String entranceUUID, ResourceKey<Level> entranceLevelKey, String template) {
        this.entranceUUID = entranceUUID;
        this.entranceLevelKey = entranceLevelKey;
        this.template = template;
    }

    public void generateFloor(int index) {
        WDProfiler.INSTANCE.start();
        getTemplate().floorTemplates().get(index).getRandom().placeInWorld(this, TemplateHelper.EMPTY_BLOCK_POS);
        WDProfiler.INSTANCE.logTimestamp("generateFloor");
        WDProfiler.INSTANCE.end();
    }

    public DungeonFloor generateOrGetFloor(int index) {
        while (floors.size() <= index) {
            generateFloor(floors.size());
        }
        return floors.get(index);
    }

    public void onEnter(WDPlayer wdPlayer, int floorIndex) {
        DungeonFloor floor = generateOrGetFloor(floorIndex);
        playerStatuses.putIfAbsent(wdPlayer.getUUID(), new DungeonSession.PlayerStatus());
        playerStats.putIfAbsent(wdPlayer.getUUID(), new DungeonSession.DungeonStats());
        this.playerStatuses.get(wdPlayer.getUUID()).inside = true;
        addInitialLives(wdPlayer);
        floor.attemptEnter(wdPlayer);
        shutdownTimer = SHUTDOWN_TIME;
    }

    public void onExit(WDPlayer wdPlayer) {
        if (!this.playerStatuses.containsKey(wdPlayer.getUUID()) || !this.playerStatuses.get(wdPlayer.getUUID()).inside) return;
        playerStatuses.get(wdPlayer.getUUID()).inside = false;
        wdPlayer.rootRespawn(wdPlayer.getServerPlayer().getServer());
        wdPlayer.setRiftCooldown(100);
        wdPlayer.setSoundScape(null, 0, true);
        WildDungeons.getLogger().info("EXITED PLAYER WITH {} RIFT COOLDOWN", wdPlayer.getRiftCooldown());
        WDPlayerManager.syncAll(List.of(wdPlayer.getUUID()));
    }

    public void addInitialLives(WDPlayer wdPlayer) {
        PlayerStatus status = this.playerStatuses.get(wdPlayer.getUUID());
        if (!status.hasVisitedThisRiftBefore) {
            status.hasVisitedThisRiftBefore = true;
            offsetLives(LIVES_PER_PLAYER);
        } else {
            WDPlayerManager.syncAll(this.playerStatuses.keySet().stream().toList());
        }
    }

    public int offsetLives(int offset) {
        WildDungeons.getLogger().info("OFFSETTING LIVES");
        this.lives += offset;
        for (String playerUUID : this.playerStatuses.keySet()) {
            WDPlayer player = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(playerUUID);
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
            result.add(WDPlayerManager.getInstance().getOrCreateServerWDPlayer(uuid));
        }
        return result;
    }

    public void win() {
        for (WDPlayer wdPlayer : getPlayers()) {
            this.onExit(wdPlayer);
            wdPlayer.getServerPlayer().addItem(new ItemStack(Items.DIAMOND.asItem(), 1));
            wdPlayer.setLastGameMode(wdPlayer.getServerPlayer().gameMode.getGameModeForPlayer());
            DungeonStatsHolder holder = new DungeonStatsHolder(this.playerStats, this.getTemplate().get(HierarchicalProperty.DISPLAY_NAME), this.getTemplate().get(HierarchicalProperty.ICON), this.getTemplate().get(HierarchicalProperty.PRIMARY_COLOR), this.getTemplate().get(HierarchicalProperty.SECONDARY_COLOR), this.getTemplate().get(HierarchicalProperty.TARGET_TIME), this.getTemplate().get(HierarchicalProperty.TARGET_DEATHS), this.getTemplate().get(HierarchicalProperty.TARGET_SCORE));
            PacketDistributor.sendToPlayer(wdPlayer.getServerPlayer(), new ClientboundPostDungeonScreenPacket(Serializer.toCompoundTag(holder)));
            wdPlayer.getServerPlayer().setGameMode(GameType.SPECTATOR);
        }
        this.handleExitBehavior();
    }

    public void fail() {
        for (WDPlayer wdPlayer : getPlayers()) {
            wdPlayer.getServerPlayer().sendSystemMessage(Component.literal("DUNGEON FAILED - RESPAWNING"), true);
            this.onExit(wdPlayer);
        }
        WildDungeons.getLogger().info("SHUTTING DOWN DUE TO FAIL");
        this.handleExitBehavior();
    }

    public void handleExitBehavior() {
        WildDungeons.getLogger().info("SHUTTING DOWN DUE TO EXIT BEHAVIOR");
        switch (this.getTemplate().get(HierarchicalProperty.EXIT_BEHAVIOR)) {
            case DESTROY -> {
                DungeonSessionManager.getInstance().server.execute(() -> {
                    Entity entity = DungeonSessionManager.getInstance().server.getLevel(this.getEntranceLevel().dimension()).getEntity(UUID.fromString(this.getEntranceUUID()));
                    if (entity != null) entity.discard();
                });
                this.shutdown(); //TODO this doesn't always delete the rift
            }
//            case RANDOMIZE -> {
//                this.shutdown();
//                Offering rift = (Offering) this.getEntranceLevel().getEntity(UUID.fromString(this.getEntranceUUID()));
//                if (rift != null) rift.setOfferingId("wd-"+this.getTemplate().nextDungeon().getRandom().name());
//            }
            case RESET -> {
                this.shutdown();
            }
        }
    }

    public void tick() {
        if (playerStatuses.values().stream().noneMatch(v -> v.inside) && !floors.isEmpty() && floors.stream().noneMatch(dungeonFloor -> dungeonFloor.unsafeForPlayer)) {shutdownTimer -= 1;}
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
        WildDungeons.getLogger().warn("SHUTTING DOWN DUNGEON SESSION");
        cancelGenerations();
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
        if (this.floors != null)
            this.floors.sort(Comparator.comparingInt(DungeonFloor::getIndex));
    }

    public void validate() {
        floors.forEach(dungeonFloor -> {
            WildDungeons.getLogger().info("VALIDATING FLOOR: {}", dungeonFloor.getIndex());
            dungeonFloor.removedHalfGeneratedBranch();
            dungeonFloor.generateBranches();
        });
    }

    public void cancelGenerations() {
        floors.forEach(DungeonFloor::cancelGenerations);
    }

    public static class DungeonStatsHolder {
        public HashMap<String, DungeonStats> playerStats;
        public String title;
        public String icon;
        public int primaryColor;
        public int secondaryColor;
        public int targetTime;
        public int targetDeaths;
        public int targetScore;

        public DungeonStatsHolder(HashMap<String, DungeonStats> playerStats, String title, String icon, int primaryColor, int secondaryColor, int targetTime, int targetDeaths, int targetScore) {
            this.playerStats = playerStats;
            this.title = title;
            this.icon = icon;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
            this.targetTime = targetTime;
            this.targetDeaths = targetDeaths;
            this.targetScore = targetScore;
        }
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
        public boolean hasVisitedThisRiftBefore = false;
    }
}