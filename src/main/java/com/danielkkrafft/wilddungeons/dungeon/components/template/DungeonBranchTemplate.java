package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DungeonBranchTemplate implements DungeonComponent {
    private String name;
    private DungeonLayout<DungeonRoomTemplate> roomTemplates;
    private List<Pair<DungeonRoomTemplate, Integer>> mandatoryRooms = new ArrayList<>();
    private List<Pair<DungeonRoomTemplate, Integer>> limitedRooms = new ArrayList<>();
    private int rootOriginBranchIndex = -1;

    public HashMap<HierarchicalProperty<?>, Object> PROPERTIES = new HashMap<>();
    public <T> DungeonBranchTemplate set(HierarchicalProperty<T> property, T value) { this.PROPERTIES.put(property, value); return this; }
    public <T> T get(HierarchicalProperty<T> property) { return (T) this.PROPERTIES.get(property); }

    public static DungeonBranchTemplate create(String name) {
        return new DungeonBranchTemplate().setName(name);
    }


    public DungeonBranch placeInWorld(DungeonFloor floor, BlockPos origin) {
        DungeonBranch newBranch = new DungeonBranch(this.name, floor, origin);
        int tries = 0;
        while (tries < 5) {
            try {
                if (newBranch.generateDungeonBranch()) {
                    return newBranch;
                }
            } catch (Exception e) {
                e.printStackTrace();
                newBranch.destroy();
            }
            tries++;
        }
        WildDungeons.getLogger().warn("Failed to generate branch {} after 5 tries", this.name);
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
    public List<Pair<DungeonRoomTemplate, Integer>> mandatoryRooms() {
        return mandatoryRooms;
    }
    public List<Pair<DungeonRoomTemplate, Integer>> limitedRooms() {
        return limitedRooms;
    }

    public DungeonBranchTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonBranchTemplate setRoomTemplates(DungeonLayout<DungeonRoomTemplate> roomTemplates) {
        this.roomTemplates = roomTemplates;
        return this;
    }

    public DungeonBranchTemplate setMandatoryRooms(List<Pair<DungeonRoomTemplate, Integer>> mandatoryRooms) {
        this.mandatoryRooms = mandatoryRooms;
        return this;
    }

    public DungeonBranchTemplate setLimitedRooms(List<Pair<DungeonRoomTemplate, Integer>> limitedRooms) {
        this.limitedRooms = limitedRooms;
        return this;
    }

    public DungeonBranchTemplate setProperties(HashMap<HierarchicalProperty<?>, Object> prop) {this.PROPERTIES = prop; return this;}

    public static DungeonBranchTemplate copyOf(DungeonBranchTemplate template, String newName) {
        return new DungeonBranchTemplate()
                .setName(newName)
                .setRoomTemplates(template.roomTemplates)
                .setMandatoryRooms(template.mandatoryRooms)
                .setLimitedRooms(template.limitedRooms)
                .setRootOriginBranchIndex(template.rootOriginBranchIndex)
                .setProperties(new HashMap<>(template.PROPERTIES));
    }

    public int rootOriginBranchIndex() {
        return rootOriginBranchIndex;
    }

    public DungeonBranchTemplate setRootOriginBranchIndex(int rootOriginBranchIndex) {
        this.rootOriginBranchIndex = rootOriginBranchIndex;
        return this;
    }
}
