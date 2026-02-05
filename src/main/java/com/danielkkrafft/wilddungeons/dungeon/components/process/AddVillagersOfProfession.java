//package com.danielkkrafft.wilddungeons.dungeon.components.process;
//
//import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
//import com.danielkkrafft.wilddungeons.util.RandomUtil;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.EntitySpawnReason;
//import net.minecraft.world.entity.npc.villager.Villager;
//import net.minecraft.world.entity.npc.villager.VillagerData;
//import net.minecraft.world.entity.npc.villager.VillagerProfession;
//import net.minecraft.world.entity.npc.villager.VillagerType;
//
//import java.util.List;
//
//import static net.minecraft.world.entity.EntityType.VILLAGER;
//
//public class AddVillagersOfProfession extends PostProcessingStep{ TODO - Uncomment once DungeonRoom is fixed for 1.21.11 (This class also has some weird stuff with new villager logic and data types)
//    int min;
//    int max;
//    ResourceKey<VillagerProfession> profession;
//
//    public AddVillagersOfProfession(ResourceKey<VillagerProfession> profession,int min, int max) {
//        super();
//        this.min = min;
//        this.max = max;
//        this.profession = profession;
//    }
//
//    @Override
//    public void handle(List<DungeonRoom> rooms) {
//        ServerLevel level = rooms.getFirst().getBranch().getFloor().getLevel();
//        for (DungeonRoom room : rooms) {
//            int placed = 0;
//            int amountPerRoom = RandomUtil.randIntBetween(min, max);
//
//            List<BlockPos> positions = room.sampleSpawnablePositions(level,amountPerRoom,VILLAGER);
//
//            for (BlockPos pos : positions) {
//                if (placed >= amountPerRoom) break;
//                if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) {
//                    Villager entity = VILLAGER.create(level, EntitySpawnReason.SPAWNER);
//                    entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//                    ResourceKey<VillagerType> villagerType = switch (RandomUtil.randIntBetween(0, 6)) {
//                        case 0 -> VillagerType.DESERT;
//                        case 1 -> VillagerType.JUNGLE;
//                        case 2 -> VillagerType.PLAINS;
//                        case 3 -> VillagerType.SAVANNA;
//                        case 4 -> VillagerType.SNOW;
//                        case 5 -> VillagerType.SWAMP;
//                        default -> VillagerType.TAIGA;
//                    };
//                    VillagerData villagerData = new VillagerData(BuiltInRegistries.VILLAGER_TYPE.getOrThrow(villagerType), BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(profession),RandomUtil.randIntBetween(0,3));
//                    entity.setVillagerData(villagerData);
//                    level.addFreshEntity(entity);
//                    placed++;
//                }
//            }
//        }
//    }
//}
