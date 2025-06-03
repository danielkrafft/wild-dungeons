package com.danielkkrafft.wilddungeons.dungeon.components.helpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LimitedRoomTracker {

    private List<RoomContainer> rooms = new ArrayList<>();

    public void add(DungeonRoomTemplate template, DungeonRoomTemplate fallbackTemplate, int maxAllowed) {
        rooms.add(new RoomContainer(template, fallbackTemplate, maxAllowed));
    }

    public boolean contains(DungeonRoomTemplate template) {
        return this.rooms.stream().anyMatch(entry -> entry.getRoomTemplate().equals(template));
    }

    public void increment(DungeonRoomTemplate template) {
        for (RoomContainer container : rooms) {
            if (container.getRoomTemplate().equals(template)) {
                container.increment();
                return;
            }
        }
    }

    public Optional<RoomContainer> getRoomContainer(DungeonRoomTemplate template) {
        for (RoomContainer container : rooms) {
            if (container.getRoomTemplate().equals(template)) {
                return Optional.of(container);
            }
        }
        return Optional.empty();
    }

    public boolean atMax(DungeonRoomTemplate template) {
        return getRoomContainer(template)
                .map(c -> c.getCurrentCount() >= c.getMaxAllowed())
                .orElseGet(() -> {
                    WildDungeons.getLogger().warn("RoomContainer not found for template: {}", template);
                    return false;
                });
    }


    public static class RoomContainer {
        private DungeonRoomTemplate template;
        private DungeonRoomTemplate fallbackTemplate;
        private int maxAllowed;
        private int currentCount = 0;

        public RoomContainer(DungeonRoomTemplate template, DungeonRoomTemplate fallbackTemplate, int max) {
            this.template = template;
            this.fallbackTemplate = fallbackTemplate;
            this.maxAllowed = max;
        }

        public DungeonRoomTemplate getRoomTemplate() { return template; }
        public int getMaxAllowed() { return maxAllowed; }
        public int getCurrentCount() { return currentCount; }
        public void increment(){
            this.currentCount++;
        }

        public DungeonRoomTemplate getFallbackTemplate() { return fallbackTemplate; };

        @Override
        public int hashCode() {
            return template != null ? template.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof RoomContainer other)) return false;
            return Objects.equals(template, other.template); // null-safe
        }
    }
}