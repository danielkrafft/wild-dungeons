package com.danielkkrafft.wilddungeons.dungeon.components.helpers;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LimitedRoomTracker {

    public List<RoomContainer> rooms = new ArrayList<>();

    public void add(DungeonRoomTemplate template, int maxAllowed) {
        rooms.add(new RoomContainer(template.name(), maxAllowed));
//        WildDungeons.getLogger().debug("Rooms after add: {}", rooms);
    }

    public boolean contains(DungeonRoomTemplate template) {
        return this.rooms.stream().anyMatch(entry -> entry.getRoomName().equals(template.name()));
    }

    public void increment(DungeonRoomTemplate template) {
        for (RoomContainer container : rooms) {
            if (container.getRoomName().equals(template.name())) {
//                WildDungeons.getLogger().debug("Incrementing room count for template: {}", template.name());
                container.increment();
                return;
            }
        }
    }

    public void reset() {
        rooms.forEach(RoomContainer::reset);
    }

    public Optional<RoomContainer> getRoomContainer(String otherName) {
        for (RoomContainer container : rooms) {
            if (container.getRoomName().equals(otherName)) {
                return Optional.of(container);
            }
        }
        return Optional.empty();
    }

    public boolean atMax(DungeonRoomTemplate template) {
        return getRoomContainer(template.name())
                .map(c -> c.getCurrentCount() >= c.getMaxAllowed())
                .orElseGet(() -> {
                    WildDungeons.getLogger().warn("RoomContainer not found for template: {}", template);
                    return false;
                });
    }

    public void clone(LimitedRoomTracker other) {
        this.rooms.clear();
        for (RoomContainer container : other.rooms) {
            this.rooms.add(container.copy());
        }
    }

    public void remove(LimitedRoomTracker other) {
        for (RoomContainer container : other.rooms) {
            getRoomContainer(container.getRoomName()).ifPresent(existingContainer -> {
                existingContainer.subtract(container.getCurrentCount());
            });
        }
    }

    public static class RoomContainer {
        private String template;
        private int maxAllowed;
        private int currentCount = 0;

        public RoomContainer(String templateName, int max) {
            this.template = templateName;
            this.maxAllowed = max;
        }

        public String getRoomName() { return template; }
        public int getMaxAllowed() { return maxAllowed; }
        public int getCurrentCount() { return currentCount; }
        public void increment(){
            this.currentCount++;
        }
        public void reset() {this.currentCount = 0;}
        public void subtract(int amount){
            this.currentCount = Math.max(0, this.currentCount - amount);
        }


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

        public RoomContainer copy() {
            return new RoomContainer(template, maxAllowed);
        }
    }
}