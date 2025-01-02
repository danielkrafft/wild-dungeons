package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.List;

public class SecretRoom extends DungeonRoom {

    public SecretRoom(DungeonBranch branch, DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        super(branch, dungeonRoomTemplate, level, position, offset, settings, allConnectionPoints);
    }

    @Override
    public void onGenerate() {
        super.onGenerate();
        ConnectionPoint entryPoint = null;
        for (ConnectionPoint point : super.connectionPoints) {
            if (point.isConnected()) {
                entryPoint = point;
                break;
            }
        }
        if (entryPoint != null) {
            entryPoint.getConnectedPoint().hide(super.level);
            entryPoint.getConnectedPoint().getRoom().alwaysBreakable.addAll(entryPoint.getConnectedPoint().getPositions());
        }
    }
}
