package com.danielkkrafft.wilddungeons.util.debug;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WDProfiler {

    public static final WDProfiler INSTANCE = new WDProfiler();
    public WDProfiler() {}

    public List<Pair<String, Long>> timestamps = new CopyOnWriteArrayList<>();

    public void logTimestamp(String name) {
        timestamps.add(Pair.of(name, System.nanoTime()));
    }

    public void start() {
        timestamps.clear();
        timestamps.add(Pair.of("start", System.nanoTime()));
    }

    public void end() {
        printResults();
        timestamps.clear();
    }

    public void printResults() {
        HashMap<String, Pair<Integer, Long>> results = new HashMap<>(); // Count, Time

        Long lastTimestamp = timestamps.getFirst().getSecond();
        for (Pair<String, Long> pair : timestamps) {
            Pair<Integer, Long> entry = results.getOrDefault(pair.getFirst(), Pair.of(0, 0L));
            results.put(
                    pair.getFirst(), // Name
                    Pair.of(
                            entry.getFirst() + 1, // Add one to the counter
                            entry.getSecond() + (pair.getSecond() - lastTimestamp) //
                    ));
            lastTimestamp = pair.getSecond();
        }
        results.forEach((key, value) -> WildDungeons.getLogger().info("PROFILED: {}, USED {} TIMES WITH A TOTAL OF {} MILLISECONDS", key, value.getFirst(), value.getSecond() / 1_000_000.0));
    }
}
