package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.*;

/**
 *  Dungeons contain a set of DungeonFloors, which contain a set of DungeonBranches, which contain a set of DungeonRooms
 *  Components are layered this way to allow for modular logic
 */
public class DungeonComponents {
    public interface DungeonComponent { String name(); }

    public record DungeonRoomTemplate(String name, List<Pair<StructureTemplate, BlockPos>> templates, List<ConnectionPoint> connectionPoints, BlockPos spawnPoint, List<BlockPos> rifts, List<StructureTemplate.StructureBlockInfo> materialBlocks, WeightedPool<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonRoomTemplate build(String name, List<Pair<String, BlockPos>> structures, WeightedPool<DungeonMaterial> materials) {

            List<Pair<StructureTemplate, BlockPos>> templates = new ArrayList<>();
            for (Pair<String, BlockPos> structure : structures) {
                WildDungeons.getLogger().info("TRYING TO LOAD STRUCTURE FILE {}", structure.getFirst());
                StructureTemplate template = DungeonSessionManager.getInstance().server.getStructureManager().getOrCreate(WildDungeons.rl(structure.getFirst()));
                templates.add(new Pair<>(template, structure.getSecond()));
            }

            List<ConnectionPoint> connectionPoints = TemplateHelper.locateConnectionPoints(templates);
            WildDungeons.getLogger().info("LOCATED {} CONNECTION POINTS FOR ROOM: {}", connectionPoints.size(), name);
            List<BlockPos> rifts = TemplateHelper.locateRifts(templates);
            BlockPos spawnPoint = TemplateHelper.locateSpawnPoint(templates);
            List<StructureTemplate.StructureBlockInfo> materialBlocks = TemplateHelper.locateMaterialBlocks(templates);
            return new DungeonRoomTemplate(name, templates, connectionPoints, spawnPoint, rifts, materialBlocks, materials);
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
            return new DungeonRoom(branch, this, level, position, TemplateHelper.EMPTY_BLOCK_POS, settings, connectionPoints);
        }
    }

    public record DungeonBranchTemplate(String name, DungeonRegistry.DungeonLayout<DungeonRoomTemplate> roomTemplates, WeightedPool<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonBranchTemplate build(String name, DungeonRegistry.DungeonLayout<DungeonRoomTemplate> roomTemplates, WeightedPool<DungeonMaterial> materials) {
            return new DungeonBranchTemplate(name, roomTemplates, materials);
        }

        public DungeonBranchTemplate pool(WeightedPool<DungeonBranchTemplate> pool, Integer weight) {pool.add(this, weight); return this;}

        public DungeonBranch placeInWorld(DungeonFloor floor, ServerLevel level, BlockPos origin) {
            return new DungeonBranch(this, floor, level, origin);
        }
    }

    public record DungeonFloorTemplate(String name, DungeonRegistry.DungeonLayout<DungeonBranchTemplate> branchTemplates, WeightedPool<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonFloorTemplate build(String name, DungeonRegistry.DungeonLayout<DungeonBranchTemplate> branchTemplates, WeightedPool<DungeonMaterial> materials) {
            return new DungeonFloorTemplate(name, branchTemplates, materials);
        }

        public DungeonFloorTemplate pool(WeightedPool<DungeonFloorTemplate> pool, Integer weight) {pool.add(this, weight); return this;}

        public DungeonFloor placeInWorld(DungeonSession session, BlockPos position, int index, WeightedPool<String> destinations) {
            WildDungeons.getLogger().info("PLACING FLOOR: {}", this.name());
            return new DungeonFloor(this, session, position, index, destinations);
        }
    }

    public record DungeonTemplate(String name, String openBehavior, DungeonRegistry.DungeonLayout<DungeonFloorTemplate> floorTemplates, WeightedPool<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonTemplate build(String name, String openBehavior, DungeonRegistry.DungeonLayout<DungeonFloorTemplate> floorTemplates, WeightedPool<DungeonMaterial> materials) {
            return new DungeonTemplate(name, openBehavior, floorTemplates, materials);
        }

        public DungeonTemplate pool(WeightedPool<DungeonTemplate> pool, Integer weight) {pool.add(this, weight); return this;}
    }
}