package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession.DungeonExitBehavior;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;

import java.awt.*;
import java.util.Objects;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorPoolRegistry.TEST_FLOOR_POOL;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry.ALL_MATERIAL_POOL;
import static com.danielkkrafft.wilddungeons.dungeon.registries.EnemyTableRegistry.BASIC_ENEMY_TABLE;
import static com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession.DungeonExitBehavior.DESTROY;

public final class DungeonTemplate implements DungeonComponent {
    private String name;
    private String displayName;

    private String icon;
    private int primaryColor = 0xFFFFFFFF;
    private int secondaryColor = 0xFFFFFFFF;
    private int targetTime;
    private int targetDeaths;
    private int targetScore;
    private String openBehavior = DungeonOpenBehavior.NONE;
    private DungeonLayout<DungeonFloorTemplate> floorTemplates = new DungeonLayout<DungeonFloorTemplate>().add(TEST_FLOOR_POOL, 1);
    private WeightedPool<DungeonMaterial> materials = ALL_MATERIAL_POOL;
    private WeightedTable<DungeonRegistration.TargetTemplate> enemyTable = BASIC_ENEMY_TABLE;
    private double difficulty = 1.0;
    private double difficultyScaling = 1.1;
    private DungeonExitBehavior exitBehavior = DESTROY;
    private WeightedPool<DungeonTemplate> nextDungeon;
    private boolean hasBedrockShell = true;
    private DungeonRoomTemplate.DestructionRule destructionRule = DungeonRoomTemplate.DestructionRule.NONE;

    public static DungeonTemplate create(String name) {
        return new DungeonTemplate().setName(name);
    }

    @Override
    public String name() {
        return name;
    }

    public String openBehavior() {
        return openBehavior;
    }

    public DungeonLayout<DungeonFloorTemplate> floorTemplates() {
        return floorTemplates;
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

    public DungeonExitBehavior exitBehavior() {
        return exitBehavior;
    }

    public WeightedPool<DungeonTemplate> nextDungeon() {
        return nextDungeon;
    }

    public int primaryColor() {
        return primaryColor;
    }

    public int secondaryColor() {
        return secondaryColor;
    }

    public boolean hasBedrockShell() {return this.hasBedrockShell;}

    public DungeonRoomTemplate.DestructionRule getDestructionRule() {return this.destructionRule;}

    public String getIcon() {return icon;}

    public String displayName() {
        return displayName;
    }

    public int targetTime() {
        return targetTime;
    }

    public int targetDeaths() {
        return targetDeaths;
    }

    public int targetScore() {
        return targetScore;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DungeonTemplate) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.openBehavior, that.openBehavior) &&
                Objects.equals(this.floorTemplates, that.floorTemplates) &&
                Objects.equals(this.materials, that.materials) &&
                Objects.equals(this.enemyTable, that.enemyTable) &&
                Double.doubleToLongBits(this.difficulty) == Double.doubleToLongBits(that.difficulty) &&
                Double.doubleToLongBits(this.difficultyScaling) == Double.doubleToLongBits(that.difficultyScaling) &&
                Objects.equals(this.exitBehavior, that.exitBehavior) &&
                Objects.equals(this.nextDungeon, that.nextDungeon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, openBehavior, floorTemplates, materials, enemyTable, difficulty, difficultyScaling, exitBehavior, nextDungeon);
    }
    @Override
    public String toString() {
        return "DungeonTemplate[" +
                "name=" + name + ", " +
                "openBehavior=" + openBehavior + ", " +
                "floorTemplates=" + floorTemplates + ", " +
                "materials=" + materials + ", " +
                "enemyTable=" + enemyTable + ", " +
                "difficulty=" + difficulty + ", " +
                "difficultyScaling=" + difficultyScaling + ", " +
                "exitBehavior=" + exitBehavior + ", " +
                "nextDungeon=" + nextDungeon + ']';
    }
    public DungeonTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonTemplate setOpenBehavior(DungeonOpenBehavior openBehavior) {
        this.openBehavior = openBehavior.toString();
        return this;
    }

    public DungeonTemplate setFloorTemplates(DungeonLayout<DungeonFloorTemplate> floorTemplates) {
        this.floorTemplates = floorTemplates;
        return this;
    }

    public DungeonTemplate setMaterials(WeightedPool<DungeonMaterial> materials) {
        this.materials = materials;
        return this;
    }

    public DungeonTemplate setEnemyTable(WeightedTable<DungeonRegistration.TargetTemplate> enemyTable) {
        this.enemyTable = enemyTable;
        return this;
    }

    public DungeonTemplate setDifficulty(double difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public DungeonTemplate setDifficultyScaling(double difficultyScaling) {
        this.difficultyScaling = difficultyScaling;
        return this;
    }

    public DungeonTemplate setExitBehavior(DungeonExitBehavior exitBehavior) {
        this.exitBehavior = exitBehavior;
        return this;
    }

    public DungeonTemplate setNextDungeon(WeightedPool<DungeonTemplate> nextDungeon) {
        this.nextDungeon = nextDungeon;
        return this;
    }

    public DungeonTemplate setPrimaryColor(Color primaryColor) {
        this.primaryColor = primaryColor.getRGB();
        return this;
    }

    public DungeonTemplate setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor.getRGB();
        return this;
    }
    /**
     * @deprecated Use {@link #setPrimaryColor(Color)} instead
     */
    @Deprecated
    public DungeonTemplate setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    /**
     * @deprecated Use {@link #setSecondaryColor(Color)} instead
     */
    @Deprecated
    public DungeonTemplate setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
        return this;
    }

    public DungeonTemplate setBedrockShell(boolean hasBedrockShell) {
        this.hasBedrockShell = hasBedrockShell;
        return this;
    }

    public DungeonTemplate setDestructionRule(DungeonRoomTemplate.DestructionRule rule) {
        this.destructionRule = rule;
        return this;
    }

    public DungeonTemplate setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public DungeonTemplate setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public DungeonTemplate setTargetTime(int targetTime) {
        this.targetTime = targetTime;
        return this;
    }

    public DungeonTemplate setTargetDeaths(int targetDeaths) {
        this.targetDeaths = targetDeaths;
        return this;
    }

    public DungeonTemplate setTargetScore(int targetScore) {
        this.targetScore = targetScore;
        return this;
    }
}
