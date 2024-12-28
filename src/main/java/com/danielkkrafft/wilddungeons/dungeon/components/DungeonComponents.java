package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
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

    public record DungeonRoomTemplate(String name, List<Pair<StructureTemplate, BlockPos>> templates, List<ConnectionPoint> connectionPoints, BlockPos spawnPoint, List<BlockPos> rifts, List<StructureTemplate.StructureBlockInfo> materialBlocks, List<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonRoomTemplate build(String name, List<Pair<String, BlockPos>> structures, List<DungeonMaterial> materials) {

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

        public DungeonRoomTemplate pool(DungeonRegistry.DungeonComponentPool<DungeonRoomTemplate> pool) {pool.add(this); return this;}

        public DungeonRoom placeInWorld(DungeonBranch branch, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> connectionPoints) {
            return new DungeonRoom(branch, this, level, position, TemplateHelper.EMPTY_BLOCK_POS, settings, connectionPoints);
        }
    }

    public record DungeonBranchTemplate(String name, List<DungeonRegistry.DungeonComponentPool<DungeonRoomTemplate>> roomPools, DungeonRoomTemplate endingRoom, int roomCount, List<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonBranchTemplate build(String name, List<DungeonRegistry.DungeonComponentPool<DungeonRoomTemplate>> roomPools, DungeonRoomTemplate endingRoom, int roomCount, List<DungeonMaterial> materials) {
            return new DungeonBranchTemplate(name, roomPools, endingRoom, roomCount, materials);
        }

        public DungeonBranchTemplate pool(DungeonRegistry.DungeonComponentPool<DungeonBranchTemplate> pool) {pool.add(this); return this;}

        public DungeonBranch placeInWorld(DungeonFloor floor, ServerLevel level, BlockPos origin) {
            return new DungeonBranch(this, floor, level, origin);
        }

        public DungeonRoomTemplate getRandomRoom() {
            return this.roomPools().get(RandomUtil.randIntBetween(0, this.roomPools().size()-1)).getRandom();
        }
    }

    public record DungeonFloorTemplate(String name, DungeonRegistry.DungeonComponentPool<DungeonBranchTemplate> branchPool, DungeonBranchTemplate startingBranch, DungeonBranchTemplate endingBranch, int branchCount, List<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonFloorTemplate build(String name, DungeonRegistry.DungeonComponentPool<DungeonBranchTemplate> branchPool, DungeonBranchTemplate startingBranch, DungeonBranchTemplate endingBranch, int branchCount, List<DungeonMaterial> materials) {
            return new DungeonFloorTemplate(name, branchPool, startingBranch, endingBranch, branchCount, materials);
        }

        public DungeonFloorTemplate pool(DungeonRegistry.DungeonComponentPool<DungeonFloorTemplate> pool) {pool.add(this); return this;}

        public DungeonFloor placeInWorld(DungeonSession session, BlockPos position, int id, List<String> destinations) {
            WildDungeons.getLogger().info("PLACING FLOOR: {}", this.name());
            return new DungeonFloor(this, session, position, id, destinations);
        }
    }

    public record DungeonTemplate(String name, String openBehavior, List<DungeonFloorTemplate> floorTemplates, List<DungeonMaterial> materials) implements DungeonComponent {

        public static DungeonTemplate build(String name, String openBehavior, List<DungeonFloorTemplate> floorTemplates, List<DungeonMaterial> materials) {
            return new DungeonTemplate(name, openBehavior, floorTemplates, materials);
        }

        public DungeonTemplate pool(DungeonRegistry.DungeonComponentPool<DungeonTemplate> pool) {pool.add(this); return this;}
    }
}