package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class AddRandomEntities extends PostProcessingStep{
    EntityType<?> entityType;
    int min;
    int max;

    public AddRandomEntities(EntityType<?> entityType, int min, int max) {
        super();
        this.entityType = entityType;
        this.min = min;
        this.max = max;
    }
    @Override
    public void handle(List<DungeonRoom> rooms) {
        ServerLevel level = rooms.getFirst().getBranch().getFloor().getLevel();
        for (DungeonRoom room : rooms) {
            int placed = 0;
            int amountPerRoom = RandomUtil.randIntBetween(min, max);

            List<BlockPos> positions = room.sampleSpawnablePositions(level,amountPerRoom,entityType);

            for (BlockPos pos : positions) {
                if (placed >= amountPerRoom) break;
                if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) {
                    Entity entity = entityType.create(level);
                    entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    level.addFreshEntity(entity);
                    placed++;
                }
            }
        }
    }
}
