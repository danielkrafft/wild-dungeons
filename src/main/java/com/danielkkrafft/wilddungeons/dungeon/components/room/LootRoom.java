package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class LootRoom extends DungeonRoom {
    public static final int SET_PURGE_INTERVAL = 20;
    public static final int START_COOLDOWN = 100;

    public int checkTimer = SET_PURGE_INTERVAL;

    public boolean started = false;
    public boolean generated = false;
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

        if (!this.started && (this.startCooldown <= 0) || this.getActivePlayers().size() >= this.getBranch().getActivePlayers().size() + this.getBranch().getFloor().getBranches().get(this.getBranch().getIndex() - 1).getActivePlayers().size()) {
            this.start();
        }

        if (!this.started) return;

        if (getOfferingUUIDs().isEmpty()) {this.onClear(); return;}
        if (checkTimer == 0) {purgeEntitySet(); checkTimer = SET_PURGE_INTERVAL;}
        checkTimer -= 1;
    }

    public void start() {
        if (this.started) return;
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
    public void onBranchEnter(WDPlayer wdPlayer) {
        super.onBranchEnter(wdPlayer);
        if (this.generated) return;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected() && point.getConnectedPoint().getRoom().getIndex() > this.getIndex() && point.getConnectedPoint().getBranchIndex() == this.getBranch().getIndex()) {
                point.block(this.getBranch().getFloor().getLevel());
            }
        });
        this.generated = true;
    }

    @Override
    public void processOfferings() {
        List<DungeonRegistry.OfferingTemplate> entries = DungeonRegistry.OFFERING_TEMPLATE_TABLE_REGISTRY.get("FREE_PERK_OFFERING_TABLE").randomResults(this.getTemplate().offerings().size(), (int) this.getDifficulty() * this.getTemplate().offerings().size(), 1.2f);
        getTemplate().offerings().forEach(pos -> {
            if (entries.isEmpty()) return;
            Offering next = entries.removeFirst().asOffering(this.getBranch().getFloor().getLevel());
            Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
            WildDungeons.getLogger().info("ADDING FREE OFFERING AT {}", pos1);
            next.setPos(pos1);
            this.getBranch().getFloor().getLevel().addFreshEntity(next);
            this.getOfferingUUIDs().add(next.getStringUUID());
        });
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
        this.getOfferingUUIDs().forEach(uuid -> {
            Offering offering = (Offering) this.getBranch().getFloor().getLevel().getEntity(UUID.fromString(uuid));
            if (offering.isPurchased()) {
                toRemove.add(offering.getStringUUID());
            }
        });

        toRemove.forEach(livingEntity -> {
            getOfferingUUIDs().remove(livingEntity);
        });
    }
}
