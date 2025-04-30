package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.ArrayList;
import java.util.List;

public class WeightedPool<T> implements DungeonRegistration.DungeonComponent {

    private final List<Pair<T, Integer>> pool;
    private final SimplexNoise noise = new SimplexNoise(RandomSource.create());
    private final List<Integer> cumulativeWeights = new ArrayList<>();
    public WeightedPool() { pool = new ArrayList<>(); }
    public static <T> WeightedPool<T> of(T item) {return new WeightedPool<T>().add(item, 1);}
    public WeightedPool<T> add(T item, int weight) { pool.add(Pair.of(item, weight)); this.totalWeight = -1; return this; }
    public int size() {return this.pool.size();}
    public String name = "none";
    private int totalWeight = -1;

    public T getRandom() {
        if (totalWeight == -1) computeTotalWeight();

        int value = RandomUtil.randIntBetween(0, totalWeight);
        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (value < cumulativeWeights.get(i)) {
                return this.pool.get(i).getFirst();
            }
        }
        return this.pool.getFirst().getFirst();
    }

    public T getNoisyRandom(BlockPos pos, double noiseScale) {
        if (totalWeight == -1) computeTotalWeight();

        // Scale our coordinates to get bigger/smaller patches, apply random origin to avoid similar generators giving the same result
        double nx = pos.getX() * noiseScale + noise.xo;
        double ny = pos.getY() * noiseScale + noise.yo;
        double nz = pos.getZ() * noiseScale + noise.zo;

        double raw = noise.getValue(nx, ny, nz); // Returns value between -1.0 and 1.0
        double normalized = (raw + 1.0) * 0.5; // Scales the raw value between 0.0 and 1.0

        int value = (int) (normalized * totalWeight);
        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (value < cumulativeWeights.get(i)) {
                return this.pool.get(i).getFirst();
            }
        }
        return this.pool.getFirst().getFirst();
    }

    @SafeVarargs
    //Uses int division, use bigger numbers
    public static <T> WeightedPool<T> combine(Pair<WeightedPool<T>, Integer>... pairs) {
        WeightedPool<T> result = new WeightedPool<>();
        for (Pair<WeightedPool<T>, Integer> pair : pairs) {
            int totalWeight = pair.getFirst().pool.stream().mapToInt(Pair::getSecond).sum();
            int factor = pair.getSecond() / totalWeight;

            for (Pair<T, Integer> entryPair : pair.getFirst().pool) {
                result.add(entryPair.getFirst(), entryPair.getSecond() * factor);
            }
        }
        return result;
    }

    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        for (Pair<T, Integer> pair : this.pool) {
            result.add(pair.getFirst());
        }
        return result;
    }

    public List<Pair<T, Integer>> getAllWithWeights() {
        return this.pool;
    }

    public void computeTotalWeight() {
        this.totalWeight = 0;
        this.cumulativeWeights.clear();
        for (Pair<T, Integer> pair : this.pool) {
            this.totalWeight += pair.getSecond();
            cumulativeWeights.add(totalWeight);
        }
    }

    public WeightedPool<T> setName(String name) {this.name = name; return this;}
    public WeightedPool<T> pool(WeightedPool<WeightedPool<T>> pool, int weight) {pool.add(this, weight); return this;}
    @Override
    public String name() {
        return this.name;
    }
}
