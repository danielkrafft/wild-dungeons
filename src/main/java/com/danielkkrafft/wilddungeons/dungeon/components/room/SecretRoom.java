package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import net.minecraft.core.BlockPos;

public class SecretRoom extends DungeonRoom {

    public SecretRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
    }

    @Override
    public void onBranchComplete() {
        super.onBranchComplete();
        ConnectionPoint entryPoint = null;
        for (ConnectionPoint point : super.getConnectionPoints()) {
            if (point.isConnected()) {
                entryPoint = point;
                break;
            }
        }
        if (entryPoint != null) {
            entryPoint.getConnectedPoint().hide();
            entryPoint.getConnectedPoint().getRoom().getAlwaysBreakable().addAll(entryPoint.getConnectedPoint().getPositions(entryPoint.getConnectedPoint().getRoom().getOrientation(), entryPoint.getConnectedPoint().getRoom().getPosition()));
        }
    }
}
