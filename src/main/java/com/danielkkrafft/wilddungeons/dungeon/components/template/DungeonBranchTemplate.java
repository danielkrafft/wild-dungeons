package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.helpers.LimitedRoomTracker;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonBranchRegistry;

import java.util.HashMap;

public final class DungeonBranchTemplate implements DungeonRegistration.DungeonComponent {
    private String name;
    private DungeonLayout<DungeonRoomTemplate> roomTemplates;
    private HashMap<DungeonRoomTemplate, Integer> mandatoryRooms = new HashMap<>();
    private LimitedRoomTracker limitedRooms = new LimitedRoomTracker();
    private int rootOriginBranchIndex = -1;

    public HashMap<HierarchicalProperty<?>, Object> PROPERTIES = new HashMap<>();
    public <T> DungeonBranchTemplate set(HierarchicalProperty<T> property, T value) { this.PROPERTIES.put(property, value); return this; }
    public <T> T get(HierarchicalProperty<T> property) { return (T) this.PROPERTIES.get(property); }
    public LimitedRoomTracker limitedRooms() {return this.limitedRooms;}

    public static DungeonBranchTemplate create(String name) {
        return new DungeonBranchTemplate().setName(name).set(HierarchicalProperty.DIFFICULTY_MODIFIER, 1.0);
    }


    public DungeonBranch placeInWorld(DungeonFloor floor) {
        DungeonBranch newBranch = new DungeonBranch(this.name, floor);
        newBranch.limitedRooms().clone(this.limitedRooms);
        int tries = 0;
        while (tries < 4) {
            try {
                if (newBranch.generateDungeonBranch()) return newBranch;
            } catch (Exception e) {
                e.printStackTrace();
                newBranch.destroyRooms();
            }
            tries++;
        }
        WildDungeons.getLogger().warn("Failed to generate branch {} after 4 tries", this.name);
        floor.getBranches().remove(newBranch);
        return null;
    }

    @Override
    public String name() {
        return name;
    }
    public DungeonLayout<DungeonRoomTemplate> roomTemplates() {
        return roomTemplates;
    }
    public HashMap<DungeonRoomTemplate, Integer> mandatoryRooms() {
        return mandatoryRooms;
    }

    public DungeonBranchTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonBranchTemplate setRoomTemplates(DungeonLayout<DungeonRoomTemplate> roomTemplates) {
        this.roomTemplates = roomTemplates;
        return this;
    }

    public DungeonBranchTemplate setMandatoryRooms(HashMap<DungeonRoomTemplate, Integer> mandatoryRooms) {
        this.mandatoryRooms = mandatoryRooms;
        return this;
    }

    public DungeonBranchTemplate setLimitedRooms(LimitedRoomTracker roomsToClone) {
        this.limitedRooms.reset();
        this.limitedRooms.clone(roomsToClone);
        return this;
    }

    public DungeonBranchTemplate addLimitedRoom(DungeonRoomTemplate template, Integer maxAmount) {

        limitedRooms.add(template, maxAmount);
        return this;
    }

    public DungeonBranchTemplate setProperties(HashMap<HierarchicalProperty<?>, Object> prop) {this.PROPERTIES = prop; return this;}

    public static DungeonBranchTemplate copyOf(DungeonBranchTemplate template, String newName) {
        DungeonBranchRegistry.DUNGEON_BRANCH_REGISTRY.add(new DungeonBranchTemplate()
                .setName(newName)
                .setRoomTemplates(template.roomTemplates)
                .setMandatoryRooms(template.mandatoryRooms)
                .setLimitedRooms(template.limitedRooms())
                .setRootOriginBranchIndex(template.rootOriginBranchIndex)
                .setProperties(new HashMap<>(template.PROPERTIES)));
        return DungeonBranchRegistry.DUNGEON_BRANCH_REGISTRY.get(newName);
    }

    public int rootOriginBranchIndex() {
        return rootOriginBranchIndex;
    }

    public DungeonBranchTemplate setRootOriginBranchIndex(int rootOriginBranchIndex) {
        this.rootOriginBranchIndex = rootOriginBranchIndex;
        return this;
    }

}
