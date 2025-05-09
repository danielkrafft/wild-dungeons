package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.block.EmeraldPileBlock;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class AddEmeraldPiles extends PostProcessingStep{
    int amountPerRoom;
    public AddEmeraldPiles(int amountPerRoom) {
        super();
        this.amountPerRoom = amountPerRoom;
    }

    @Override
    public void handle(List<DungeonRoom> rooms) {
        ServerLevel level = rooms.getFirst().getBranch().getFloor().getLevel();

        for (DungeonRoom room : rooms) {
            int placed = 0;
            int tries = amountPerRoom * 4;
            //search for a random position in the room
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            while (tries > 0 && placed < amountPerRoom) {
                BoundingBox innerShell = room.getBoundingBoxes().get(RandomUtil.randIntBetween(0, room.getBoundingBoxes().size() - 1));

                int randX = RandomUtil.randIntBetween(innerShell.minX(), innerShell.maxX());
                int randZ = RandomUtil.randIntBetween(innerShell.minZ(), innerShell.maxZ());

                for (int y = innerShell.minY(); y < innerShell.maxY(); y++) {
                    mutableBlockPos.set(randX, y, randZ);
                    if (level.getBlockState(mutableBlockPos).isAir() && level.getBlockState(mutableBlockPos.below()).isFaceSturdy(level, mutableBlockPos.below(), net.minecraft.core.Direction.UP)) {
                        setBlockFast(level, mutableBlockPos, EmeraldPileBlock.getRandomPile().rotate(Rotation.getRandom(level.random)));
                        placed++;
                        break;
                    }
                }
                tries--;
            }
        }
    }
}
