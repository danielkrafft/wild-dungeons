package com.danielkkrafft.wilddungeons.dungeon.session;

import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.DungeonPerks;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.*;

public class DungeonSession {
    public static final int SHUTDOWN_TIME = 300;

    public BlockPos entrance;
    private final Set<WDPlayer> players = new HashSet<>();
    public final List<DungeonFloor> floors = new ArrayList<>();
    public DungeonComponents.DungeonTemplate template;
    public int shutdownTimer = SHUTDOWN_TIME;
    public boolean markedForShutdown = false;
    public WeightedPool<DungeonMaterial> materials;

    public HashMap<DungeonPerks.Perks, DungeonPerk> perks = new HashMap();

    public WeightedTable<EntityType<?>> enemyTable;
    public double difficulty;

    protected DungeonSession(BlockPos entrance, DungeonComponents.DungeonTemplate template) {
        this.entrance = entrance;
        this.template = template;
        this.materials = template.materials();
        this.enemyTable = template.enemyTable();
        this.difficulty = template.difficulty();
        DungeonPerk.addPerk(this, DungeonPerks.Perks.SWORD_DAMAGE_INCREASE);
    }

    public DungeonFloor getFloor(ResourceKey<Level> levelKey) {
        List<DungeonFloor> matches = floors.stream().filter(dungeonFloor -> dungeonFloor.LEVEL_KEY == levelKey).toList();
        return matches.isEmpty() ? null : matches.getFirst();
    }

    public DungeonFloor generateFloor(int index) {
        WeightedPool<String> destinations = floors.size() == template.floorTemplates().size()-1 ?
                new WeightedPool<String>().add("win", 1) :
                new WeightedPool<String>().add(""+(index+1), 1);

        DungeonFloor floor = template.floorTemplates().get(floors.size()).getRandom().placeInWorld(this, new BlockPos(0,0,0), index, destinations);
        floors.add(floor);
        return floor;
    }

    public void onEnter(WDPlayer wdPlayer) {
        players.add(wdPlayer);
        wdPlayer.setCurrentDungeon(this);
        if (floors.isEmpty()) generateFloor(0);
        floors.getFirst().onEnter(wdPlayer);
        shutdownTimer = SHUTDOWN_TIME;
    }

    public void onExit(WDPlayer wdPlayer) {
        players.remove(wdPlayer);
        wdPlayer.rootRespawn(wdPlayer.getServerPlayer().getServer());
        wdPlayer.setCurrentDungeon(null);
        wdPlayer.setCurrentFloor(null);
        wdPlayer.setCurrentBranch(null);
        wdPlayer.setCurrentRoom(null);
    }

    public void tick() {
        if (players.isEmpty() && !floors.isEmpty()) {shutdownTimer -= 1;}
        if (shutdownTimer == 0) {shutdown();return;}
        if (!players.isEmpty()) floors.forEach(DungeonFloor::tick);
    }

    public void shutdown() {
        players.forEach(this::onExit);
        floors.forEach(DungeonFloor::shutdown);
        markedForShutdown = true;
    }
}