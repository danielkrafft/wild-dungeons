package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.SaveSystem;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.nbt.CompoundTag;
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
import java.util.stream.Collectors;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRegistry.DUNGEON_REGISTRY;

public class DungeonSession {

    public static final int SHUTDOWN_TIME = 300;
    public static final int LIVES_PER_PLAYER = 3;

    private final String entranceUUID;
    private final ResourceKey<Level> entranceLevelKey;
    private final HashMap<String, Boolean> playersInside = new HashMap<>();
    private final HashMap<String, DungeonStats> playerStats = new HashMap<>();
    private final HashMap<String, DungeonPerk> perks = new HashMap<>();
    @Serializer.IgnoreSerialization private final List<DungeonFloor> floors = new ArrayList<>();
    private final String template;
    private int shutdownTimer = SHUTDOWN_TIME;
    private int lives = 0;
    private boolean markedForShutdown = false;

    public ServerLevel getEntranceLevel() {return DungeonSessionManager.getInstance().server.getLevel(this.entranceLevelKey);}
    public String getEntranceUUID() {return this.entranceUUID;}
    public List<DungeonFloor> getFloors() {return this.floors;}
    public DungeonTemplate getTemplate() {return DUNGEON_REGISTRY.get(this.template);}
    public int getLives() {return this.lives;}
    public boolean isMarkedForShutdown() {return this.markedForShutdown;}
    public HashMap<String, DungeonPerk> getPerks() {return this.perks;}
    public String getSessionKey() {return DungeonSessionManager.buildDungeonSessionKey(this.entranceUUID);}
    public DungeonStats getStats(String uuid) {return this.playerStats.get(uuid);}
    public List<WDPlayer> getPlayers() { return this.playersInside.keySet().stream().map(uuid -> WDPlayerManager.getInstance().getOrCreateServerWDPlayer(uuid)).toList(); }

    public enum DungeonExitBehavior {DESTROY, RANDOMIZE, RESET, NOTHING}
    public enum DungeonOpenBehavior {NONE, ESSENCE_REQUIRED, ITEMS_REQUIRED, ENTITY_REQUIRED, GUARD_REQUIRED, KILLS_REQUIRED}

    protected DungeonSession(String entranceUUID, ResourceKey<Level> entranceLevelKey, String template) {
        this.entranceUUID = entranceUUID;
        this.entranceLevelKey = entranceLevelKey;
        this.template = template;
    }

    /**
     * Places floors in the world until it can return the floor at the requested index
     *
     * @param index The index of the floor to be returned
     */
    public DungeonFloor generateOrGetFloor(int index) {
        while (floors.size() <= index) {
            getTemplate().floorTemplates().get(index).getRandom().placeInWorld(this, TemplateHelper.EMPTY_BLOCK_POS);
        }
        return floors.get(index);
    }

    /**
     * Run on server start. Branches set a flag when they are completely generated.
     * This removes any partially generated branches, and then regenerates the floor from that point onward.
     */
    public void validate() {
        floors.forEach(dungeonFloor -> {
            dungeonFloor.removedHalfGeneratedBranch();
            dungeonFloor.generateBranches();
        });
    }

    /**
     * Handles adding a player to any floor inside this DungeonSession, including cases where the player is moving in between floors.
     * DungeonFloor objects may not exist when a player is attempting to reach them, so logic is handled here instead.
     *
     * @param wdPlayer The player to handle entry for
     * @param floorIndex The index of the floor
     */
    public void onEnter(WDPlayer wdPlayer, int floorIndex) {
        DungeonFloor floor = generateOrGetFloor(floorIndex);
        if (!this.playersInside.containsKey(wdPlayer.getUUID())) {
            offsetLives(LIVES_PER_PLAYER);
        } else {
            WDPlayerManager.syncAll(this.playersInside.keySet().stream().toList());
        }
        this.playersInside.put(wdPlayer.getUUID(), true);
        playerStats.putIfAbsent(wdPlayer.getUUID(), new DungeonSession.DungeonStats());
        floor.attemptEnter(wdPlayer);
        shutdownTimer = SHUTDOWN_TIME;
    }

    /**
     * Handles removing a player from the dungeon, respawning them wherever they first entered the dungeon
     *
     * @param wdPlayer The player to remove from the dungeon
     */
    public void onExit(WDPlayer wdPlayer) {
        if (!this.playersInside.containsKey(wdPlayer.getUUID()) || !this.playersInside.get(wdPlayer.getUUID())) return;
        playersInside.put(wdPlayer.getUUID(), false);
        wdPlayer.rootRespawn(wdPlayer.getServerPlayer().getServer());
        wdPlayer.setRiftCooldown(100);
        wdPlayer.setSoundScape(null, 0, true);
        WDPlayerManager.syncAll(List.of(wdPlayer.getUUID()));
    }

    /**
     * Run every server tick
     */
    public void tick() {
        if (playersInside.values().stream().noneMatch(v -> v) && !floors.isEmpty() && floors.stream().noneMatch(dungeonFloor -> dungeonFloor.unsafeForPlayer)) {shutdownTimer -= 1;}
        if (shutdownTimer == 0) { shutdown(); return; }
        if (playersInside.values().stream().anyMatch(v -> v)) floors.forEach(DungeonFloor::tick);
        playersInside.keySet().forEach(uuid -> {
            if (this.playerStats.containsKey(uuid)) {
                this.playerStats.get(uuid).time++;
            }
        });
    }

    /**
     * Increments or decrements the session's life count and sends the new life count to clients.
     * Fails the dungeon if lives reach zero.
     *
     * @param offset The amount of lives to add (can be positive or negative)
     */
    public void offsetLives(int offset) {
        this.lives += offset;
        for (Map.Entry<String, Boolean> entry : this.playersInside.entrySet()) {
            WDPlayer player = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(entry.getKey());
            if (entry.getValue()) player.setCurrentLives(this.lives);
        }
        if (this.lives <= 0 && this.playersInside.values().stream().anyMatch(v -> v)) {
            this.fail();
        }
        WDPlayerManager.syncAll(this.playersInside.keySet().stream().toList());
    }

    /**
     * Adds a perk to the dungeon session. Perks can be stacked
     *
     * @param perkTemplate The perk to add
     */
    public void givePerk(DungeonPerkTemplate perkTemplate) {
        DungeonPerk perk = this.getPerks().containsKey(perkTemplate.name()) ? this.getPerks().get(perkTemplate.name()) : new DungeonPerk(perkTemplate.name(), DungeonSessionManager.buildDungeonSessionKey(this.getEntranceUUID()));

        perk.count++;
        this.getPerks().put(perkTemplate.name(), perk);
        perk.onCollect();
    }

    /**
     * Handles the case when a dungeon is successfully completed, usually by entering a rift with the "win" destination.
     * Primarily sends clients a Post-Dungeon stat screen
     */
    public void win() {
        for (WDPlayer wdPlayer : getPlayers()) {
            this.onExit(wdPlayer);
            wdPlayer.getServerPlayer().addItem(new ItemStack(Items.DIAMOND.asItem(), 1));
            wdPlayer.setLastGameMode(wdPlayer.getServerPlayer().gameMode.getGameModeForPlayer());
            DungeonStatsHolder holder = new DungeonStatsHolder(this.playerStats, this.getTemplate().get(HierarchicalProperty.DISPLAY_NAME), this.getTemplate().get(HierarchicalProperty.ICON), this.getTemplate().get(HierarchicalProperty.PRIMARY_COLOR), this.getTemplate().get(HierarchicalProperty.SECONDARY_COLOR), this.getTemplate().get(HierarchicalProperty.TARGET_TIME), this.getTemplate().get(HierarchicalProperty.TARGET_DEATHS), this.getTemplate().get(HierarchicalProperty.TARGET_SCORE));
            CompoundTag tag = new CompoundTag();
            tag.putString("packet", ClientPacketHandler.Packets.POST_DUNGEON_SCREEN.toString());
            tag.put("stats", Serializer.toCompoundTag(holder));
            PacketDistributor.sendToPlayer(wdPlayer.getServerPlayer(), new SimplePacketManager.ClientboundTagPacket(tag));
            wdPlayer.getServerPlayer().setGameMode(GameType.SPECTATOR);
        }
        this.handleExitBehavior();
    }

    /**
     * Handles the case when a dungeon is failed, usually by running out of lives.
     * Primarily ejects any remaining players
     */
    public void fail() {
        for (WDPlayer wdPlayer : getPlayers()) {
            wdPlayer.getServerPlayer().sendSystemMessage(Component.literal("DUNGEON FAILED - RESPAWNING"), true);
            this.onExit(wdPlayer);
        }
        this.handleExitBehavior();
    }

    /**
     * Called when a dungeon is either cleared or failed. Only one case is handled right now, which destroys the rift when the dungeon is completed
     */
    public void handleExitBehavior() {
        switch (this.getTemplate().get(HierarchicalProperty.EXIT_BEHAVIOR)) {
            case DESTROY -> DungeonSessionManager.getInstance().server.execute(() -> {
                Entity entity = DungeonSessionManager.getInstance().server.getLevel(this.getEntranceLevel().dimension()).getEntity(UUID.fromString(this.getEntranceUUID()));
                if (entity != null) entity.discard(); //TODO this doesn't always delete the rift
            });
        }
        this.shutdown();
    }

    /**
     * Destroys the entire dungeon and its associated files
     */
    public void shutdown() {
        floors.forEach(DungeonFloor::cancelGenerations);
        getPlayers().forEach(this::onExit);
        floors.forEach(DungeonFloor::shutdown);
        SaveSystem.DeleteSession(this);
        markedForShutdown = true;
    }

    /**
     * For sending data to the Post-Dungeon screen
     */
    public record DungeonStatsHolder(HashMap<String, DungeonStats> playerStats, String title, String icon, int primaryColor, int secondaryColor, int targetTime, int targetDeaths, int targetScore) { }

    /**
     * For tracking dungeon-specific player statistics
     */
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
}