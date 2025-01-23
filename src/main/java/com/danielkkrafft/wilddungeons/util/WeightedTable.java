package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonComponent;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeightedTable<T> implements DungeonComponent {

    List<Pair<Integer, WeightedPool<T>>> weightMap = new ArrayList<>();
    public String name;
    public WeightedTable() {}

    public WeightedTable<T> add(WeightedPool<T> pool, Integer quality) {
        this.weightMap.add(Pair.of(quality, pool));
        weightMap.sort(Comparator.comparingInt(Pair::getFirst));
        return this;
    }

    public List<T> randomResults(int quantity, int quality, float deviance) {
        List<T> result = new ArrayList<>();
        int qualityTarget = quality;
        deviance = Math.max(1, deviance);

        while (quality > qualityTarget * 0.2 || quantity > 0) {
            double ratio = (double) quality / quantity;
            int selectedQuality = RandomUtil.randIntBetween(Mth.floor(ratio / deviance), Mth.floor(ratio * deviance));

            Pair<Integer, WeightedPool<T>> selectedPool = weightMap.getFirst();
            for (Pair<Integer, WeightedPool<T>> pair : weightMap) {
                if (pair.getFirst() <= selectedQuality) selectedPool = pair;
            }

            result.add(selectedPool.getSecond().getRandom());
            quality -= selectedPool.getFirst();
            quantity -= 1;


        }

        return result;

    }

    public WeightedTable<T> setName(String name) {this.name = name; return this;}

    @Override
    public String name() {
        return this.name;
    }
}
