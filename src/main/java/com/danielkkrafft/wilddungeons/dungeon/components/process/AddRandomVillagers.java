package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

import java.util.List;

import static net.minecraft.world.entity.EntityType.VILLAGER;

public class AddRandomVillagers extends PostProcessingStep{
    int min;
    int max;

    public AddRandomVillagers(int min, int max) {
        super();
        this.min = min;
        this.max = max;
    }
    @Override
    public void handle(List<DungeonRoom> rooms) {
        ServerLevel level = rooms.getFirst().getBranch().getFloor().getLevel();
        for (DungeonRoom room : rooms) {
            int placed = 0;
            int amountPerRoom = RandomUtil.randIntBetween(min, max);

            List<BlockPos> positions = room.sampleSpawnablePositions(level,amountPerRoom,VILLAGER);

            for (BlockPos pos : positions) {
                if (placed >= amountPerRoom) break;
                if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) {
                    Villager entity = VILLAGER.create(level);
                    entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    VillagerType villagerType = switch (RandomUtil.randIntBetween(0, 6)) {
                        case 0 -> VillagerType.DESERT;
                        case 1 -> VillagerType.JUNGLE;
                        case 2 -> VillagerType.PLAINS;
                        case 3 -> VillagerType.SAVANNA;
                        case 4 -> VillagerType.SNOW;
                        case 5 -> VillagerType.SWAMP;
                        default -> VillagerType.TAIGA;
                    };
                    VillagerProfession villagerProfession = switch (RandomUtil.randIntBetween(0, 14)) {
                        case 0 -> VillagerProfession.ARMORER;
                        case 1 -> VillagerProfession.BUTCHER;
                        case 2 -> VillagerProfession.CARTOGRAPHER;
                        case 3 -> VillagerProfession.CLERIC;
                        case 4 -> VillagerProfession.FARMER;
                        case 5 -> VillagerProfession.FISHERMAN;
                        case 6 -> VillagerProfession.FLETCHER;
                        case 7 -> VillagerProfession.LEATHERWORKER;
                        case 8 -> VillagerProfession.LIBRARIAN;
                        case 9 -> VillagerProfession.MASON;
                        case 10 -> VillagerProfession.NITWIT;
                        case 11 -> VillagerProfession.SHEPHERD;
                        case 12 -> VillagerProfession.TOOLSMITH;
                        case 13 -> VillagerProfession.WEAPONSMITH;
                        default -> VillagerProfession.NONE;
                    };
                    VillagerData villagerData = new VillagerData(villagerType,villagerProfession,RandomUtil.randIntBetween(0,3));
                    entity.setVillagerData(villagerData);
                    level.addFreshEntity(entity);
                    placed++;
                }
            }
        }
    }
}
