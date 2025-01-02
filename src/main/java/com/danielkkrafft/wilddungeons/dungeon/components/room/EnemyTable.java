package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class EnemyTable {

    List<Pair<Integer, WeightedPool<EntityType<?>>>> difficultyMap = new ArrayList<>();

    public EnemyTable() {}

    public EnemyTable add(WeightedPool<EntityType<?>> pool, Integer difficulty) {
        this.difficultyMap.add(Pair.of(difficulty, pool));
        return this;
    }

    public List<EntityType<?>> getEntities(int quantity, int difficulty) {
        WildDungeons.getLogger().info("GETTING ENTITIES FOR QUANTITY {} AND DIFFICULTY {}", quantity, difficulty);
        List<EntityType<?>> result = new ArrayList<>();
        int difficultyTarget = difficulty;

        while (difficulty > difficultyTarget * 0.2 || quantity > 0) {
            double ratio = (double) difficulty / quantity;
            WildDungeons.getLogger().info("RATIO IS {} FOR REMAINING QUANTITY {} AND REMAINING DIFFICULTY {}", ratio, quantity, difficulty);
            int selectedDifficulty = RandomUtil.randIntBetween((int) Math.floor(ratio * 0.5), (int) Math.floor(ratio * 1.5));
            WildDungeons.getLogger().info("SELECTED DIFFICULTY: {}", selectedDifficulty);

            Pair<Integer, WeightedPool<EntityType<?>>> selectedPool = difficultyMap.getFirst();
            for (Pair<Integer, WeightedPool<EntityType<?>>> pair : difficultyMap) {
                if (pair.getFirst() <= selectedDifficulty) selectedPool = pair;
            }

            WildDungeons.getLogger().info("PICKED POOL WITH MOB: {}", selectedPool.getSecond().getRandom());

            result.add(selectedPool.getSecond().getRandom());
            difficulty -= selectedPool.getFirst();
            quantity -= 1;
        }

        return result;
    }

    public static class EnemyTables {

        public static final WeightedPool<EntityType<?>> EASY_BASIC_POOL = new WeightedPool<EntityType<?>>()
                .add(EntityType.ZOMBIE, 1).add(EntityType.SKELETON, 1);

        public static final WeightedPool<EntityType<?>> MEDIUM_BASIC_POOL = new WeightedPool<EntityType<?>>()
                .add(EntityType.CREEPER, 1).add(EntityType.CAVE_SPIDER, 1);

        public static final WeightedPool<EntityType<?>> HARD_BASIC_POOL = new WeightedPool<EntityType<?>>()
                .add(EntityType.ENDERMAN, 1).add(EntityType.BLAZE, 1);

        public static final EnemyTable BASIC_TABLE = new EnemyTable()
                .add(EASY_BASIC_POOL, 1).add(MEDIUM_BASIC_POOL, 5).add(HARD_BASIC_POOL, 10);

    }
}
