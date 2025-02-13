package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.CombatRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.KeyRequiredRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.LootRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.SecretRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DungeonRoomTemplate implements DungeonComponent {
    private Type type;
    private String name;
    private List<Pair<StructureTemplate, BlockPos>> templates;
    private List<ConnectionPoint> connectionPoints;
    private BlockPos spawnPoint;
    private List<Vec3> rifts;
    private List<Vec3> offerings;
    private List<StructureTemplate.StructureBlockInfo> lootBlocks;
    private List<StructureTemplate.StructureBlockInfo> dataMarkers;
    private DungeonRegistration.OfferingTemplate roomClearOffering = null;

    public enum Type {
        NONE, SECRET, COMBAT, SHOP, LOOT, KEYLOCKED
    }
    public enum DestructionRule {
        SHELL, NONE, SHELL_CLEAR
    }

    public HashMap<HierarchicalProperty<?>, Object> PROPERTIES = new HashMap<>();
    public <T> DungeonRoomTemplate set(HierarchicalProperty<T> property, T value) { this.PROPERTIES.put(property, value); return this; }
    public <T> T get(HierarchicalProperty<T> property) { return (T) this.PROPERTIES.get(property); }

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
        switch (this.type()) {
            case SECRET -> {
                return new SecretRoom(branch, this.name, position, settings, connectionPoints);
            }
            case COMBAT -> {
                return new CombatRoom(branch, this.name, position, settings, connectionPoints);
            }
            case LOOT -> {
                return new LootRoom(branch, this.name, position, settings, connectionPoints);
            }
            case KEYLOCKED -> {
                return new KeyRequiredRoom(branch, this.name, position, settings, connectionPoints);
            }
            case null, default -> {
                return new DungeonRoom(branch, this.name, position, settings, connectionPoints);
            }
        }

    }

    public Type type() {
        return type;
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

    public DungeonRegistration.OfferingTemplate roomClearOffering() {return roomClearOffering;}

    public DungeonRoomTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonRoomTemplate setType(Type type) {
        this.type = type;
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

    public DungeonRoomTemplate setDataMarkers(List<StructureTemplate.StructureBlockInfo> dataMarkers) {
        this.dataMarkers = dataMarkers;
        return this;
    }

    public DungeonRoomTemplate setRoomClearOffering(DungeonRegistration.OfferingTemplate roomClearOffering) {
        this.roomClearOffering = roomClearOffering;
        return this;
    }

    public DungeonRoomTemplate setProperties(HashMap<HierarchicalProperty<?>, Object> prop) {this.PROPERTIES = prop; return this;}

    public static DungeonRoomTemplate copyOf(DungeonRoomTemplate template, String newName) {
        return new DungeonRoomTemplate()
                .setType(template.type)
                .setName(newName)
                .setTemplates(template.templates)
                .setConnectionPoints(template.connectionPoints)
                .setSpawnPoint(template.spawnPoint)
                .setRifts(template.rifts)
                .setOfferings(template.offerings)
                .setLootBlocks(template.lootBlocks)
                .setDataMarkers(template.dataMarkers)
                .setRoomClearOffering(template.roomClearOffering)
                .setProperties(template.PROPERTIES);
    }
}
