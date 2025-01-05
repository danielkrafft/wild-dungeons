package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.TemplateHelper;
import com.danielkkrafft.wilddungeons.entity.Offering;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ShopRoom extends DungeonRoom {

    public ShopRoom(DungeonBranch branch, DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, dungeonRoomTemplate, level, position, offset, settings, allConnectionPoints);
    }

    @Override
    public void processOfferings() {
        super.processOfferings();
        List<Offering.OfferingTemplate> entries = ShopTables.BASIC_TABLE.randomResults(this.template.offerings().size(), (int) this.difficulty * this.template.offerings().size(), 1.2f);
        for (BlockPos pos : this.template.offerings()) {
            if (entries.isEmpty()) return;
            Offering next = entries.removeFirst().asOffering(this.level);
            next.setPos(Vec3.atBottomCenterOf(TemplateHelper.transform(pos, this)).add(0.0, 0.5, 0.0));
            level.addFreshEntity(next);
        }
    }
}
