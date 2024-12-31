package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DungeonRoom {
    public DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate;
    public ServerLevel level;
    public BlockPos position;
    public BlockPos offset;
    public BlockPos spawnPoint = null;
    public StructurePlaceSettings settings;
    public List<ConnectionPoint> connectionPoints = new ArrayList<>();
    public List<BlockPos> rifts = new ArrayList<>();
    public List<BoundingBox> boundingBoxes;
    public DungeonBranch branch;
    public boolean rotated;
    public DungeonMaterial material;
    public int index;

    public DungeonRoom(DungeonBranch branch, DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        dungeonRoomTemplate.templates().forEach(template -> {
            BlockPos newOffset = StructureTemplate.transform(template.getSecond(), settings.getMirror(), settings.getRotation(), TemplateHelper.EMPTY_BLOCK_POS);
            BlockPos newPosition = position.offset(newOffset);
            template.getFirst().placeInWorld(level, newPosition, template.getSecond(), settings, DungeonSessionManager.getInstance().server.overworld().getRandom(), 2);
        });
        this.dungeonRoomTemplate = dungeonRoomTemplate;
        this.branch = branch;
        this.level = level;
        this.position = position;
        this.offset = offset;
        this.settings = settings;
        this.rotated = settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90;
        this.boundingBoxes = dungeonRoomTemplate.getBoundingBoxes(settings, position);
        this.material = dungeonRoomTemplate.materials() == null ?
                this.branch.materials.get(RandomUtil.randIntBetween(0, this.branch.materials.size() - 1)) :
                dungeonRoomTemplate.materials().get(RandomUtil.randIntBetween(0, dungeonRoomTemplate.materials().size() - 1));

        dungeonRoomTemplate.rifts().forEach(pos -> {
            this.rifts.add(StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset).offset(position));
        });
        this.processMaterialBlocks(this.material);

        if (dungeonRoomTemplate.spawnPoint() != null) {
            this.spawnPoint = TemplateHelper.transform(dungeonRoomTemplate.spawnPoint(), this);
            level.setBlock(spawnPoint, Blocks.AIR.defaultBlockState(), 2);
        }


        for (ConnectionPoint point : allConnectionPoints) {
            ConnectionPoint newPoint = ConnectionPoint.copy(point);
            newPoint.setRoom(this);
            newPoint.transform(settings, position, offset);
            this.connectionPoints.add(newPoint);
        }
        int posTotal = 0;
        for (ConnectionPoint point : this.connectionPoints) {
            posTotal += point.getPositions().size();
        }
        WildDungeons.getLogger().info("PLACED ROOM: {} WITH {} CONNECTION POINTS WITH A TOTAL OF {} POSITIONS", this.dungeonRoomTemplate.name(), this.connectionPoints.size(), posTotal);
    }

    public void processConnectionPoints() {
        for (ConnectionPoint point : connectionPoints) {
            point.setupBlockstates(this.level, this.settings);
            if (point.isConnected()) point.unBlock(this.level);
            if (!point.isConnected()) point.block(this.level);
        }
    }

    public void processMaterialBlocks(DungeonMaterial material) {
        List<StructureTemplate.StructureBlockInfo> materialBlocks = this.dungeonRoomTemplate.materialBlocks();
        materialBlocks.forEach(structureBlockInfo -> {
            BlockPos newPos = TemplateHelper.transform(structureBlockInfo.pos(), this);
            level.setBlock(newPos, TemplateHelper.fixBlockStateProperties(material.replace(structureBlockInfo.state()), this.settings), 2);
        });
    }

    public List<ConnectionPoint> getValidExitPoints(ConnectionPoint entrancePoint) {
        List<ConnectionPoint> exitPoints = new ArrayList<>();
        for (ConnectionPoint point : connectionPoints) {
            if (ConnectionPoint.arePointsCompatible(entrancePoint, point)) {
                exitPoints.add(point);
            }
        }
        return exitPoints;
    }

    public void onGenerate() {
        if (this.spawnPoint != null) {
            WildDungeons.getLogger().info("SPAWNING ZOMBIE AT {}", this.spawnPoint);
            Zombie zombie = new Zombie(level);
            zombie.setPos(Vec3.atCenterOf(spawnPoint));
            level.addWithUUID(zombie);
        }
    }
    public void onEnter() {}
    public void onExit() {}
    public boolean isClear() {return true;}
    public void onClear() {}
    public void tick() {}

}
