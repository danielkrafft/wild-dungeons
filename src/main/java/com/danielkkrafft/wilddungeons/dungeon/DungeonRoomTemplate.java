package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.*;
import com.danielkkrafft.wilddungeons.dungeon.components.room.*;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public record DungeonRoomTemplate(Type type, String name, List<Pair<StructureTemplate, BlockPos>> templates, List<ConnectionPoint> connectionPoints, BlockPos spawnPoint, List<Vec3> rifts, List<Vec3> offerings, List<StructureTemplate.StructureBlockInfo> materialBlocks, List<StructureTemplate.StructureBlockInfo> lootBlocks, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) implements DungeonComponent {

    public enum Type {
        NONE, SECRET, COMBAT, SHOP, LOOT
    }

    public static DungeonRoomTemplate build(Type type, String name, List<Pair<String, BlockPos>> structures, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) {

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
        List<StructureTemplate.StructureBlockInfo> materialBlocks = TemplateHelper.locateMaterialBlocks(templates);
        List<StructureTemplate.StructureBlockInfo> lootBlocks = TemplateHelper.locateLootTargets(templates);
        return new DungeonRoomTemplate(type, name, templates, connectionPoints, spawnPoint, rifts, offerings, materialBlocks, lootBlocks, materials, enemyTable, difficulty);
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

    public DungeonRoomTemplate pool(WeightedPool<DungeonRoomTemplate> pool, Integer weight) {pool.add(this, weight); return this;}

    public DungeonRoom placeInWorld(DungeonBranch branch, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> connectionPoints) {
        switch (this.type()) {
            case SECRET -> {
                return new SecretRoom(branch, this.name, level, position, settings, connectionPoints);
            }
            case COMBAT -> {
                return new CombatRoom(branch, this.name, level, position, settings, connectionPoints);
            }
            case LOOT -> {
                return new LootRoom(branch, this.name, level, position, settings, connectionPoints);
            }
            case null, default -> {
                return new DungeonRoom(branch, this.name, level, position, settings, connectionPoints);
            }
        }

    }
}
