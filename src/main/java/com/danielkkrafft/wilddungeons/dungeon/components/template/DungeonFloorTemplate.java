package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class DungeonFloorTemplate implements DungeonComponent {
    private String name;
    private DungeonRegistration.DungeonLayout<DungeonBranchTemplate> branchTemplates;
    private WeightedPool<DungeonMaterial> materials = null;
    private WeightedTable<EntityType<?>> enemyTable = null;
    private double difficulty = 1.0;



    public static DungeonFloorTemplate create(String name) {
        return new DungeonFloorTemplate().setName(name);
    }

    public DungeonFloorTemplate pool(WeightedPool<DungeonFloorTemplate> pool, Integer weight) {
        pool.add(this, weight);
        return this;
    }

    public CompletableFuture<Void> placeInWorld(DungeonSession session, BlockPos position, Consumer<Void> onFirstBranchComplete, Consumer<DungeonBranch> onSequentialBranchComplete, Consumer<Void> onComplete) {
        WildDungeons.getLogger().info("PLACING FLOOR: {}", this.name());
        DungeonFloor newFloor = new DungeonFloor(this.name, session.getSessionKey(), position);
        return newFloor.asyncGenerateBranches(onFirstBranchComplete, onSequentialBranchComplete, onComplete);
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

    public DungeonFloorTemplate setEnemyTable(WeightedTable<EntityType<?>> enemyTable) {
        this.enemyTable = enemyTable;
        return this;
    }

    public DungeonFloorTemplate setDifficulty(double difficulty) {
        this.difficulty = difficulty;
        return this;
    }
}
