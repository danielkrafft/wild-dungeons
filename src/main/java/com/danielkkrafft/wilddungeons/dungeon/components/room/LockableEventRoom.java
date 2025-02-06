package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class LockableEventRoom extends DungeonRoom {

    public static final int START_COOLDOWN = 100;
    public int startCooldown = START_COOLDOWN;

    public boolean started = false;
    public boolean generated = false;

    public LockableEventRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, position, settings, allConnectionPoints);
    }

    public void start() {
        if (this.started) return;
        WildDungeons.getLogger().info("STARTING LOCKABLE ROOM");
        this.started = true;
        this.getBranch().getFloor().getLevel().playSound(null, this.getPosition(), SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, .5f, 1f);

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
    public void tick() {
        super.tick();
        if (this.getActivePlayers().isEmpty() || this.isClear() || this.started) return;

        WildDungeons.getLogger().info("LOCKABLE ROOM TICKING");

        if (!this.started && this.startCooldown > 0) {
            this.startCooldown -= 1;
            if (this.getActivePlayers().size() >= this.getBranch().getActivePlayers().size() + this.getBranch().getFloor().getBranches().get(this.getBranch().getIndex() - 1).getActivePlayers().size()) {
                this.startCooldown -= 5;
            }
        }

        if (!this.started && (this.startCooldown <= 0)) {
            this.start();
        }
    }

    @Override
    public void onClear() {
        super.onClear();
        WildDungeons.getLogger().info("LOCKABLE ROOM CLEARING");
        this.getBranch().getFloor().getLevel().playSound(null, this.getPosition(), SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, .5f, 1f);
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) {
                point.unBlock(this.getBranch().getFloor().getLevel());
            }
        });
    }

    @Override
    public void reset() {
        super.reset();
        WildDungeons.getLogger().info("LOCKABLE ROOM RESETTING");
        this.started = false;
        startCooldown = START_COOLDOWN;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.unBlock(this.getBranch().getFloor().getLevel());
        });
        this.generated = false;
        this.onBranchEnter(null);
    }

    @Override
    public void onBranchEnter(WDPlayer wdPlayer) {
        super.onBranchEnter(wdPlayer);
        if (this.generated) return;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) {
                if (((point.getConnectedPoint().getRoom().getIndex() > this.getIndex()
                        && point.getConnectedPoint().getBranchIndex() == this.getBranch().getIndex()))
                        || point.getConnectedPoint().getBranchIndex() > this.getBranch().getIndex()) {
                    point.block(this.getBranch().getFloor().getLevel());
                } else {
                    switch (getTemplate().type()) {
                        case COMBAT -> point.combatRoomUnblock(this.getBranch().getFloor().getLevel());
                        case LOOT -> point.lootRoomUnblock(this.getBranch().getFloor().getLevel());
                        case null, default -> point.unBlock(this.getBranch().getFloor().getLevel());
                    }
                }
            }
        });
        this.generated = true;
    }
}