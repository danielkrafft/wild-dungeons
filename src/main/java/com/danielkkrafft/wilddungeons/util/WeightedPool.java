package com.danielkkrafft.wilddungeons.util;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class WeightedPool<T> {

    private final List<Pair<T, Integer>> pool;
    public WeightedPool() { pool = new ArrayList<>(); }
    public WeightedPool<T> add(T item, int weight) { pool.add(Pair.of(item, weight)); return this; }
    public int size() {return this.pool.size();}

    public T getRandom() {
        List<Integer> cumulativeWeights = new ArrayList<>();
        int totalWeight = 0;
        for (Pair<T, Integer> pair : this.pool) {
            totalWeight += pair.getSecond();
            cumulativeWeights.add(totalWeight);
        }

        int value = RandomUtil.randIntBetween(0, totalWeight);
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
}
