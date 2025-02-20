package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.List;

public class SecretRoom extends DungeonRoom {

    public SecretRoom(DungeonBranch branch, String templateKey, BlockPos position, StructurePlaceSettings settings) {
        super(branch, templateKey, position, settings);
    }

    @Override
    public void onGenerate() {
        super.onGenerate();
        ConnectionPoint entryPoint = null;
        for (ConnectionPoint point : super.getConnectionPoints()) {
            if (point.isConnected()) {
                entryPoint = point;
                break;
            }
        }
        if (entryPoint != null) {
            entryPoint.getConnectedPoint().hide(super.getBranch().getFloor().getLevel());
            entryPoint.getConnectedPoint().getRoom().getAlwaysBreakable().addAll(entryPoint.getConnectedPoint().getPositions(entryPoint.getConnectedPoint().getRoom().getSettings(), entryPoint.getConnectedPoint().getRoom().getPosition()));
        }
    }
}
