package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateTableRegistry;
import com.danielkkrafft.wilddungeons.entity.Offering;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LootRoom extends EntityPurgeRoom {

    public LootRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, level, position, settings, allConnectionPoints);
    }

    @Override
    public void start() {
        if (this.started) return;
        this.aliveUUIDs.addAll(this.getOfferingUUIDs());
        super.start();
    }

    @Override
    public void processOfferings() {
        List<DungeonRegistration.OfferingTemplate> entries = OfferingTemplateTableRegistry.FREE_PERK_OFFERING_TABLE.randomResults(this.getTemplate().offerings().size(), (int) this.getDifficulty() * this.getTemplate().offerings().size(), 1.2f);
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
    public void reset() {
        this.started = false;
        startCooldown = START_COOLDOWN;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) point.unBlock(this.getBranch().getFloor().getLevel());
        });
        this.generated = false;
        this.onBranchEnter(null);
        aliveUUIDs.clear();
        toSpawn.clear();
    }
}
