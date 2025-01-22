package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.TemplateHelper;
import com.danielkkrafft.wilddungeons.entity.Offering;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ShopRoom extends DungeonRoom {

    public ShopRoom(DungeonBranch branch, String templateKey, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, templateKey, level, position, settings, allConnectionPoints);
    }

    @Override
    public void processOfferings() {
        super.processOfferings();
        List<Offering.OfferingTemplate> entries = ShopTables.BASIC_TABLE.randomResults(this.getTemplate().offerings().size(), (int) this.getDifficulty() * this.getTemplate().offerings().size(), 1.2f);
        for (BlockPos pos : this.getTemplate().offerings()) {
            if (entries.isEmpty()) return;
            Offering next = entries.removeFirst().asOffering(this.getBranch().getFloor().getLevel());
            next.setPos(Vec3.atBottomCenterOf(TemplateHelper.transform(pos, this)).add(0.0, 0.5, 0.0));
            getBranch().getFloor().getLevel().addFreshEntity(next);
        }
    }
}
