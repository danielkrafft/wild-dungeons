package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DungeonBranchTemplate implements DungeonComponent {
    private String name;
    private DungeonLayout<DungeonRoomTemplate> roomTemplates;
    private List<Pair<DungeonRoomTemplate, Integer>> mandatoryRooms = new ArrayList<>();
    private List<Pair<DungeonRoomTemplate, Integer>> limitedRooms = new ArrayList<>();
    private WeightedPool<DungeonMaterial> materials = null;
    private WeightedTable<DungeonRegistration.TargetTemplate> enemyTable = null;
    private double difficulty = 1.0;
    private double difficultyScaling = -1;


    public static DungeonBranchTemplate create(String name) {
        return new DungeonBranchTemplate().setName(name);
    }


    public DungeonBranch placeInWorld(DungeonFloor floor, BlockPos origin) {
        DungeonBranch newBranch = new DungeonBranch(this.name, floor, origin);
        int tries = 0;
        while (tries < 50) {
            try {
                if (newBranch.generateDungeonBranch()) {
                    return newBranch;
                }
            } catch (Exception e) {
                WildDungeons.getLogger().warn("Failed to generate branch {} on try {}", this.name, tries);
                e.printStackTrace();
                newBranch.destroy();
            }
            tries++;
        }
        WildDungeons.getLogger().warn("Failed to generate branch {} after 50 tries. Shutting down Dungeon and ejecting Players", this.name);
        floor.getSession().shutdown();
        return null;
        //todo tell the player what happened
    }

    @Override
    public String name() {
        return name;
    }

    public DungeonLayout<DungeonRoomTemplate> roomTemplates() {
        return roomTemplates;
    }

    public WeightedPool<DungeonMaterial> materials() {
        return materials;
    }

    public WeightedTable<DungeonRegistration.TargetTemplate> enemyTable() {
        return enemyTable;
    }

    public double difficulty() {
        return difficulty;
    }

    public List<Pair<DungeonRoomTemplate, Integer>> mandatoryRooms() {
        return mandatoryRooms;
    }

    public List<Pair<DungeonRoomTemplate, Integer>> limitedRooms() {
        return limitedRooms;
    }

    public double difficultyScaling() {
        return difficultyScaling;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DungeonBranchTemplate) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.roomTemplates, that.roomTemplates) &&
                Objects.equals(this.materials, that.materials) &&
                Objects.equals(this.enemyTable, that.enemyTable) &&
                Double.doubleToLongBits(this.difficulty) == Double.doubleToLongBits(that.difficulty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roomTemplates, materials, enemyTable, difficulty);
    }

    @Override
    public String toString() {
        return "DungeonBranchTemplate[" +
                "name=" + name + ", " +
                "roomTemplates=" + roomTemplates + ", " +
                "materials=" + materials + ", " +
                "enemyTable=" + enemyTable + ", " +
                "difficulty=" + difficulty + ']';
    }

    public DungeonBranchTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonBranchTemplate setRoomTemplates(DungeonLayout<DungeonRoomTemplate> roomTemplates) {
        this.roomTemplates = roomTemplates;
        return this;
    }

    public DungeonBranchTemplate setMaterials(WeightedPool<DungeonMaterial> materials) {
        this.materials = materials;
        return this;
    }

    public DungeonBranchTemplate setEnemyTable(WeightedTable<DungeonRegistration.TargetTemplate> enemyTable) {
        this.enemyTable = enemyTable;
        return this;
    }

    public DungeonBranchTemplate setDifficulty(double difficulty) {
        this.difficulty = difficulty;
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

    public DungeonBranchTemplate setDifficultyScaling(double difficultyScaling) {
        this.difficultyScaling = difficultyScaling;
        return this;
    }
}
