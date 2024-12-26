package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
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

    public record DungeonRoomTemplate(String name, StructureTemplate template, List<ConnectionPoint> connectionPoints, BlockPos spawnPoint, List<BlockPos> rifts, List<StructureTemplate.StructureBlockInfo> materialBlocks) implements DungeonComponent {

        public static DungeonRoomTemplate build(String name) {

            StructureTemplate template = DungeonSessionManager.getInstance().server.getStructureManager().getOrCreate(WildDungeons.rl(name));

            List<ConnectionPoint> connectionPoints = TemplateHelper.locateConnectionPoints(template);
            List<BlockPos> rifts = TemplateHelper.locateRifts(template);
            BlockPos spawnPoint = TemplateHelper.locateSpawnPoint(template);
            List<StructureTemplate.StructureBlockInfo> materialBlocks = TemplateHelper.locateMaterialBlocks(template);
            return new DungeonRoomTemplate(name, template, connectionPoints, spawnPoint, rifts, materialBlocks);
        }

        public BoundingBox getBoundingBox() {return template.getBoundingBox(new StructurePlaceSettings(), TemplateHelper.EMPTY_BLOCK_POS);}
        public DungeonRoomTemplate pool(DungeonRegistry.DungeonComponentPool<DungeonRoomTemplate> pool) {pool.add(this); return this;}

        public DungeonRoom placeInWorld(DungeonBranch branch, ServerLevel level, BlockPos position, StructurePlaceSettings settings, List<ConnectionPoint> connectionPoints) {
            return new DungeonRoom(branch, this, level, position, TemplateHelper.EMPTY_BLOCK_POS, settings, connectionPoints);
        }
    }

    public record DungeonBranchTemplate(String name, DungeonRegistry.DungeonComponentPool<DungeonRoomTemplate> roomPool, DungeonRoomTemplate endingRoom, int roomCount) implements DungeonComponent {

        public static DungeonBranchTemplate build(String name, DungeonRegistry.DungeonComponentPool<DungeonRoomTemplate> roomPool, DungeonRoomTemplate endingRoom, int roomCount) {
            return new DungeonBranchTemplate(name, roomPool, endingRoom, roomCount);
        }

        public DungeonBranchTemplate pool(DungeonRegistry.DungeonComponentPool<DungeonBranchTemplate> pool) {pool.add(this); return this;}

        public DungeonBranch placeInWorld(DungeonFloor floor, ServerLevel level, BlockPos origin) {
            return new DungeonBranch(this, floor, level, origin);
        }
    }

    public record DungeonFloorTemplate(String name, DungeonRegistry.DungeonComponentPool<DungeonBranchTemplate> branchPool, DungeonBranchTemplate startingBranch, DungeonBranchTemplate endingBranch, int branchCount) implements DungeonComponent {

        public static DungeonFloorTemplate build(String name, DungeonRegistry.DungeonComponentPool<DungeonBranchTemplate> branchPool, DungeonBranchTemplate startingBranch, DungeonBranchTemplate endingBranch, int branchCount) {
            return new DungeonFloorTemplate(name, branchPool, startingBranch, endingBranch, branchCount);
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