package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DungeonRoomTemplate implements DungeonComponent {
    private Class<?> clazz = DungeonRoom.class;
    private String name;
    private List<Pair<StructureTemplate, BlockPos>> templates;
    private List<ConnectionPoint> connectionPoints;
    private BlockPos spawnPoint;
    private List<Vec3> rifts;
    private List<Vec3> offerings;
    private List<StructureTemplate.StructureBlockInfo> lootBlocks;
    private List<StructureTemplate.StructureBlockInfo> dataMarkers;
    private WeightedPool<DungeonMaterial> materials;
    private WeightedTable<DungeonRegistration.TargetTemplate> enemyTable;
    private double difficulty = 1.0;
    private Boolean hasBedrockShell = null;
    private DestructionRule destructionRule = null;
    private int blockingMaterialIndex = -1;
    private DungeonRegistration.OfferingTemplate roomClearOffering = null;

    public enum DestructionRule {
        SHELL, NONE, SHELL_CLEAR
    }

    public static DungeonRoomTemplate create(String name, List<Pair<String, BlockPos>> structures) {

        List<Pair<StructureTemplate, BlockPos>> templates = new ArrayList<>();
        for (Pair<String, BlockPos> structure : structures) {
            WildDungeons.getLogger().info("TRYING TO LOAD STRUCTURE FILE {}", structure.getFirst());
            StructureTemplate template = DungeonSessionManager.getInstance().server.getStructureManager().getOrCreate(WildDungeons.rl(structure.getFirst()));
            templates.add(new Pair<>(template, structure.getSecond()));
        }

        List<ConnectionPoint> connectionPoints = TemplateHelper.locateConnectionPoints(templates);
        WildDungeons.getLogger().info("LOCATED {} CONNECTION POINTS FOR ROOM: {}", connectionPoints.size(), name);
        List<Vec3> rifts = TemplateHelper.locateRifts(templates);
        BlockPos spawnPoint = TemplateHelper.locateSpawnPoint(templates);
        List<Vec3> offerings = TemplateHelper.locateOfferings(templates);
        List<StructureTemplate.StructureBlockInfo> lootBlocks = TemplateHelper.locateLootTargets(templates);
        List<StructureTemplate.StructureBlockInfo> locatedDataMarkers = TemplateHelper.locateDataMarkers(templates);
        return new DungeonRoomTemplate()
                .setName(name)
                .setTemplates(templates)
                .setConnectionPoints(connectionPoints)
                .setSpawnPoint(spawnPoint)
                .setRifts(rifts)
                .setOfferings(offerings)
                .setLootBlocks(lootBlocks)
                .setDataMarkers(locatedDataMarkers);
    }

    public List<BoundingBox> getBoundingBoxes(StructurePlaceSettings settings, BlockPos position) {
        List<BoundingBox> boundingBoxes = new ArrayList<>();
        templates.forEach(template -> {
            BlockPos newOffset = StructureTemplate.transform(template.getSecond(), settings.getMirror(), settings.getRotation(), TemplateHelper.EMPTY_BLOCK_POS);
            BlockPos newPosition = position.offset(newOffset);
            boundingBoxes.add(template.getFirst().getBoundingBox(settings, newPosition));
        });
        return boundingBoxes;
    }


    public DungeonRoom placeInWorld(DungeonBranch branch, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> connectionPoints) {
        try {
            return (DungeonRoom) this.clazz.getConstructor(DungeonBranch.class, String.class, BlockPos.class, StructurePlaceSettings.class, List.class)
                    .newInstance(branch, this.name, position, settings, connectionPoints);
        } catch (Exception e) {
            WildDungeons.getLogger().error("Failed to create instance of DungeonRoom class for room template: {}", this.name);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String name() {
        return name;
    }

    public List<Pair<StructureTemplate, BlockPos>> templates() {
        return templates;
    }

    public List<ConnectionPoint> connectionPoints() {
        return connectionPoints;
    }

    public BlockPos spawnPoint() {
        return spawnPoint;
    }

    public List<Vec3> rifts() {
        return rifts;
    }

    public List<Vec3> offerings() {
        return offerings;
    }


    public List<StructureTemplate.StructureBlockInfo> lootBlocks() {
        return lootBlocks;
    }

    public List<StructureTemplate.StructureBlockInfo> dataMarkers() {
        return dataMarkers;
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

    public Boolean hasBedrockShell() {return this.hasBedrockShell;}

    public DestructionRule getDestructionRule() {return this.destructionRule;}

    public int blockingMaterialIndex() {return blockingMaterialIndex;}

    public DungeonRegistration.OfferingTemplate roomClearOffering() {return roomClearOffering;}

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DungeonRoomTemplate) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.templates, that.templates) &&
                Objects.equals(this.connectionPoints, that.connectionPoints) &&
                Objects.equals(this.spawnPoint, that.spawnPoint) &&
                Objects.equals(this.rifts, that.rifts) &&
                Objects.equals(this.offerings, that.offerings) &&
                Objects.equals(this.lootBlocks, that.lootBlocks) &&
                Objects.equals(this.materials, that.materials) &&
                Objects.equals(this.enemyTable, that.enemyTable) &&
                Double.doubleToLongBits(this.difficulty) == Double.doubleToLongBits(that.difficulty);
    }

    @Override
    public int hashCode() {
        return Objects.hash( name, templates, connectionPoints, spawnPoint, rifts, offerings, lootBlocks, materials, enemyTable, difficulty);
    }

    @Override
    public String toString() {
        return "DungeonRoomTemplate[" +
                "name=" + name + ", " +
                "templates=" + templates + ", " +
                "connectionPoints=" + connectionPoints + ", " +
                "spawnPoint=" + spawnPoint + ", " +
                "rifts=" + rifts + ", " +
                "offerings=" + offerings + ", " +
                "lootBlocks=" + lootBlocks + ", " +
                "materials=" + materials + ", " +
                "enemyTable=" + enemyTable + ", " +
                "difficulty=" + difficulty + ']';
    }
    public DungeonRoomTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonRoomTemplate setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    private DungeonRoomTemplate setTemplates(List<Pair<StructureTemplate, BlockPos>> templates) {
        this.templates = templates;
        return this;
    }

    private DungeonRoomTemplate setConnectionPoints(List<ConnectionPoint> connectionPoints) {
        this.connectionPoints = connectionPoints;
        return this;
    }

    private DungeonRoomTemplate setSpawnPoint(BlockPos spawnPoint) {
        this.spawnPoint = spawnPoint;
        return this;
    }

    private DungeonRoomTemplate setRifts(List<Vec3> rifts) {
        this.rifts = rifts;
        return this;
    }

    private DungeonRoomTemplate setOfferings(List<Vec3> offerings) {
        this.offerings = offerings;
        return this;
    }


    private DungeonRoomTemplate setLootBlocks(List<StructureTemplate.StructureBlockInfo> lootBlocks) {
        this.lootBlocks = lootBlocks;
        return this;
    }

    public DungeonRoomTemplate setMaterials(WeightedPool<DungeonMaterial> materials) {
        this.materials = materials;
        return this;
    }

    public DungeonRoomTemplate setEnemyTable(WeightedTable<DungeonRegistration.TargetTemplate> enemyTable) {
        this.enemyTable = enemyTable;
        return this;
    }

    public DungeonRoomTemplate setDifficulty(double difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public DungeonRoomTemplate setBedrockShell(boolean bedrockShell) {
        this.hasBedrockShell = bedrockShell;
        return this;
    }

    public DungeonRoomTemplate setDestructionRule(DestructionRule rule) {
        this.destructionRule = rule;
        return this;
    }

    public DungeonRoomTemplate setBlockingMaterialIndex(int blockingMaterialIndex) {
        this.blockingMaterialIndex = blockingMaterialIndex;
        return this;
    }

    public DungeonRoomTemplate setDataMarkers(List<StructureTemplate.StructureBlockInfo> dataMarkers) {
        this.dataMarkers = dataMarkers;
        return this;
    }

    public DungeonRoomTemplate setRoomClearOffering(DungeonRegistration.OfferingTemplate roomClearOffering) {
        this.roomClearOffering = roomClearOffering;
        return this;
    }

    public static DungeonRoomTemplate copyOf(DungeonRoomTemplate template, String newName) {
        DungeonRoomTemplate newTemplate = new DungeonRoomTemplate()
                .setClazz(template.clazz)
                .setName(newName)
                .setTemplates(template.templates)
                .setConnectionPoints(template.connectionPoints)
                .setSpawnPoint(template.spawnPoint)
                .setRifts(template.rifts)
                .setOfferings(template.offerings)
                .setLootBlocks(template.lootBlocks)
                .setMaterials(template.materials)
                .setEnemyTable(template.enemyTable)
                .setDifficulty(template.difficulty)
                .setBlockingMaterialIndex(template.blockingMaterialIndex)
                .setDataMarkers(template.dataMarkers)
                .setRoomClearOffering(template.roomClearOffering);
        if (template.hasBedrockShell != null) newTemplate.setBedrockShell(template.hasBedrockShell);
        if (template.destructionRule != null) newTemplate.setDestructionRule(template.destructionRule);
        return newTemplate;
    }
}
