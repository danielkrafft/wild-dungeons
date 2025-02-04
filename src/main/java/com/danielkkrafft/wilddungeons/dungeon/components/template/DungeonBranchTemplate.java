package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public final class DungeonBranchTemplate implements DungeonComponent {
    private String name;
    private DungeonLayout<DungeonRoomTemplate> roomTemplates;
    private WeightedPool<DungeonMaterial> materials = null;
    private WeightedTable<EntityType<?>> enemyTable = null;
    private double difficulty = 1.0;


    public static DungeonBranchTemplate create(String name) {
        return new DungeonBranchTemplate().setName(name);
    }

    public DungeonBranchTemplate pool(WeightedPool<DungeonBranchTemplate> pool, Integer weight) {
        pool.add(this, weight);
        return this;
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
        WildDungeons.getLogger().warn("Failed to generate branch {} after 50 tries", this.name);
        return null;
        //todo if we fail 50 times, regen the previous branch too
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

    public WeightedTable<EntityType<?>> enemyTable() {
        return enemyTable;
    }

    public double difficulty() {
        return difficulty;
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

    public DungeonBranchTemplate setEnemyTable(WeightedTable<EntityType<?>> enemyTable) {
        this.enemyTable = enemyTable;
        return this;
    }

    public DungeonBranchTemplate setDifficulty(double difficulty) {
        this.difficulty = difficulty;
        return this;
    }
}
