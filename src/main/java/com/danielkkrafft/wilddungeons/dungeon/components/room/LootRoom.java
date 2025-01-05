package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.TemplateHelper;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LootRoom extends DungeonRoom {
    public static final int SET_PURGE_INTERVAL = 20;

    public int checkTimer = SET_PURGE_INTERVAL;

    public boolean started = false;
    public boolean generated = false;
    public Set<Offering> alive = new HashSet<>();;
    public List<Offering.OfferingTemplate> toSpawn = new ArrayList<>();

    public LootRoom(DungeonBranch branch, DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, dungeonRoomTemplate, level, position, offset, settings, allConnectionPoints);
    }

    @Override
    public void tick() {
        super.tick();
        if (!started || this.players.isEmpty() || this.clear) return;

        if (alive.isEmpty()) {this.onClear(); return;}
        if (checkTimer == 0) {purgeEntitySet(); checkTimer = SET_PURGE_INTERVAL;}
        checkTimer -= 1;
    }

    @Override
    public void onEnterInner(WDPlayer player) {
        super.onEnterInner(player);
        if (this.started) return;
        this.started = true;

        this.connectionPoints.forEach(point -> {
            if (point.isConnected()) point.block(this.level);
        });

    }

    @Override
    public void onBranchEnter(WDPlayer player) {
        super.onBranchEnter(player);
        if (this.generated) return;

        this.toSpawn = OfferingTables.PERK_OFFERING_TABLE.randomResults(this.template.offerings().size(), this.template.offerings().size(), 1);
        for (BlockPos pos : this.template.offerings()) {
            Offering next = toSpawn.removeFirst().asOffering(this.level);
            next.setPos(Vec3.atBottomCenterOf(TemplateHelper.transform(pos, this)).add(0.0, 0.5, 0.0));
            this.alive.add(next);
            level.addFreshEntity(next);
        }
        this.generated = true;
    }

    @Override
    public void onClear() {
        super.onClear();
        this.connectionPoints.forEach(point -> {
            if (point.isConnected()) point.unBlock(this.level);
        });
    }

    public void purgeEntitySet() {
        List<Offering> toRemove = new ArrayList<>();
        this.alive.forEach(livingEntity -> {
            if (livingEntity.purchased) {
                toRemove.add(livingEntity);
            }
        });

        toRemove.forEach(livingEntity -> {
            alive.remove(livingEntity);
        });
    }
}
