package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.block.LockableBlock;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class KeyRequiredRoom extends TargetPurgeRoom {
    public ArrayList<BlockPos> lockableBlocks = new ArrayList<>();

    public KeyRequiredRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
    }

    @Override
    public void start() {
        if (this.started) return;
        this.started = true;
    }

    @Override
    public void onBranchComplete() {
        //find all LockableBlocks in the room
        getBoundingBoxes().forEach(box -> {
            for (int x = box.minX(); x < box.maxX(); x++) {
                for (int y = box.minY(); y < box.maxY(); y++) {
                    for (int z = box.minZ(); z < box.maxZ(); z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (getBranch().getFloor().getLevel().getBlockState(pos).getBlock() instanceof LockableBlock) {
                            lockableBlocks.add(pos);
                            DungeonTarget target = new DungeonTarget(pos);
                            targets.add(target);
                        }
                    }
                }
            }
        });
        super.onBranchComplete();
    }

    @Override
    public void reset() {

    }

    @Override
    public void onClear() {
        super.onClear();
        lockableBlocks.forEach(pos -> {
            getBranch().getFloor().getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        });
        lockableBlocks.clear();
    }

    @Override
    public void setPreviewDoorways() {
        if (isClear()) return;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) {
                //this code only works when the *next* branch is generated too
                if (point.getConnectedPoint().getBranchIndex() <= this.getBranch().getIndex() &&
                        (point.getConnectedPoint().getRoom().getIndex() <= this.getIndex() || point.getConnectedPoint().getBranchIndex() != this.getBranch().getIndex())) {
                    point.unBlockAndAddDecal();
                }
            }
        });
    }
}
