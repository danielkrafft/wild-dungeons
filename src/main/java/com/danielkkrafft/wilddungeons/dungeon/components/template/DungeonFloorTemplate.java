package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.core.BlockPos;

import java.util.Objects;

public final class DungeonFloorTemplate implements DungeonComponent {
    private String name;
    private DungeonRegistration.DungeonLayout<DungeonBranchTemplate> branchTemplates;
    private WeightedPool<DungeonMaterial> materials = null;
    private WeightedTable<DungeonRegistration.TargetTemplate> enemyTable = null;
    private double difficulty = 1.0;
    private double difficultyScaling = -1;
    private Boolean hasBedrockShell = null;
    private DungeonRoomTemplate.DestructionRule destructionRule = null;
    private BlockPos origin = null;

    public static DungeonFloorTemplate create(String name) {
        return new DungeonFloorTemplate().setName(name);
    }


    public DungeonFloor placeInWorld(DungeonSession session, BlockPos position) {
        WildDungeons.getLogger().info("PLACING FLOOR: {}", this.name());
        DungeonFloor newFloor = new DungeonFloor(this.name, session.getSessionKey(), position);
        newFloor.generateBranches();
        return newFloor;
    }

    @Override
    public String name() {
        return name;
    }

    public DungeonRegistration.DungeonLayout<DungeonBranchTemplate> branchTemplates() {
        return branchTemplates;
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

    public double difficultyScaling() {
        return difficultyScaling;
    }

    public Boolean hasBedrockShell() {return this.hasBedrockShell;}

    public DungeonRoomTemplate.DestructionRule getDestructionRule() {return this.destructionRule;}

    public BlockPos origin() {return this.origin;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DungeonFloorTemplate) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.branchTemplates, that.branchTemplates) &&
                Objects.equals(this.materials, that.materials) &&
                Objects.equals(this.enemyTable, that.enemyTable) &&
                Double.doubleToLongBits(this.difficulty) == Double.doubleToLongBits(that.difficulty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, branchTemplates, materials, enemyTable, difficulty);
    }

    @Override
    public String toString() {
        return "DungeonFloorTemplate[" +
                "name=" + name + ", " +
                "branchTemplates=" + branchTemplates + ", " +
                "materials=" + materials + ", " +
                "enemyTable=" + enemyTable + ", " +
                "difficulty=" + difficulty + ']';
    }

    public DungeonFloorTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonFloorTemplate setBranchTemplates(DungeonRegistration.DungeonLayout<DungeonBranchTemplate> branchTemplates) {
        this.branchTemplates = branchTemplates;
        return this;
    }

    public DungeonFloorTemplate setMaterials(WeightedPool<DungeonMaterial> materials) {
        this.materials = materials;
        return this;
    }

    public DungeonFloorTemplate setEnemyTable(WeightedTable<DungeonRegistration.TargetTemplate> enemyTable) {
        this.enemyTable = enemyTable;
        return this;
    }

    public DungeonFloorTemplate setDifficulty(double difficulty) {
        this.difficulty = difficulty;
        return this;
    }


    public DungeonFloorTemplate setDifficultyScaling(double difficultyScaling) {
        this.difficultyScaling = difficultyScaling;
        return this;
    }

    public DungeonFloorTemplate setBedrockShell(boolean hasBedrockShell) {
        this.hasBedrockShell = hasBedrockShell;
        return this;
    }

    public DungeonFloorTemplate setDestructionRule(DungeonRoomTemplate.DestructionRule rule) {
        this.destructionRule = rule;
        return this;
    }

    public DungeonFloorTemplate setOrigin(BlockPos origin) {
        this.origin = origin;
        return this;
    }

    public static DungeonFloorTemplate copyOf(DungeonFloorTemplate template, String newName) {
        return new DungeonFloorTemplate()
                .setName(newName)
                .setBranchTemplates(template.branchTemplates)
                .setMaterials(template.materials)
                .setEnemyTable(template.enemyTable)
                .setDifficulty(template.difficulty)
                .setDifficultyScaling(template.difficultyScaling)
                .setBedrockShell(template.hasBedrockShell)
                .setDestructionRule(template.destructionRule)
                .setOrigin(template.origin);
    }
}
