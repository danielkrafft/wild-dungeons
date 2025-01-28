package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class LootRoom extends DungeonRoom {
    public static final int SET_PURGE_INTERVAL = 20;
    public static final int START_COOLDOWN = 100;

    public int checkTimer = SET_PURGE_INTERVAL;

    public boolean started = false;
    public boolean generated = false;
    public Set<String> aliveUUIDs = new HashSet<>();
    public int startCooldown = START_COOLDOWN;

    public LootRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, level, position, settings, allConnectionPoints);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getActivePlayers().isEmpty() || this.isClear()) return;

        if (!this.started && this.startCooldown > 0) {
            this.startCooldown-=1;
        }

        if (!this.started && this.startCooldown <= 0) {
            this.start();
        }

        if (!this.started) return;

        if (aliveUUIDs.isEmpty()) {this.onClear(); return;}
        if (checkTimer == 0) {purgeEntitySet(); checkTimer = SET_PURGE_INTERVAL;}
        checkTimer -= 1;
    }

    public void start() {
        this.started = true;

        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.block(this.getBranch().getFloor().getLevel());
        });
    }

    @Override
    public void onExit(WDPlayer wdPlayer) {
        super.onExit(wdPlayer);
        if (this.getActivePlayers().isEmpty() && !this.isClear()) {
            this.reset();
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.started = false;
        this.startCooldown = START_COOLDOWN;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.unBlock(this.getBranch().getFloor().getLevel());
        });
        this.generated = false;
        this.onBranchEnter(null);
    }

    @Override
    public void onBranchEnter(WDPlayer player) {
        super.onBranchEnter(player);
        if (this.generated) return;

        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected() && point.getConnectedPoint().getRoom().getIndex() > this.getIndex()) {
                point.block(this.getBranch().getFloor().getLevel());
            }
        });

        List<DungeonRegistry.OfferingTemplate> toSpawn = DungeonRegistry.OFFERING_TEMPLATE_TABLE_REGISTRY.get("FREE_PERK_OFFERING_TABLE").randomResults(this.getTemplate().offerings().size(), this.getTemplate().offerings().size(), 1);
        for (Vec3 pos : this.getTemplate().offerings()) {
            Offering next = toSpawn.removeFirst().asOffering(this.getBranch().getFloor().getLevel());
            next.setPos(pos);
            this.aliveUUIDs.add(next.getStringUUID());
            this.getBranch().getFloor().getLevel().addFreshEntity(next);
        }
        this.generated = true;
    }

    @Override
    public void onClear() {
        super.onClear();
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.unBlock(this.getBranch().getFloor().getLevel());
        });
    }

    public void purgeEntitySet() {
        List<String> toRemove = new ArrayList<>();
        this.aliveUUIDs.forEach(uuid -> {
            Offering offering = (Offering) this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (offering.isPurchased()) {
                toRemove.add(offering.getStringUUID());
            }
        });

        toRemove.forEach(livingEntity -> {
            aliveUUIDs.remove(livingEntity);
        });
    }
}
