package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.components.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class DungeonRoom {
    public DungeonComponents.DungeonRoomTemplate template;
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
    public boolean clear = false;
    public Set<WDPlayer> players = new HashSet<>();
    public HashMap<WDPlayer, Boolean> innerPlayers = new HashMap<>();
    public Set<BlockPos> alwaysBreakable = new HashSet<>();

    public List<BlockPos> spawnablePosList = new ArrayList<>();
    public EnemyTable enemyTable;
    public double difficulty;

    public DungeonRoom(DungeonBranch branch, DungeonComponents.DungeonRoomTemplate dungeonRoomTemplate, ServerLevel level, BlockPos position, BlockPos offset, StructurePlaceSettings settings, List<ConnectionPoint> allConnectionPoints) {
        dungeonRoomTemplate.templates().forEach(template -> {
            BlockPos newOffset = StructureTemplate.transform(template.getSecond(), settings.getMirror(), settings.getRotation(), TemplateHelper.EMPTY_BLOCK_POS);
            BlockPos newPosition = position.offset(newOffset);
            template.getFirst().placeInWorld(level, newPosition, template.getSecond(), settings, DungeonSessionManager.getInstance().server.overworld().getRandom(), 2);
        });
        this.template = dungeonRoomTemplate;
        this.branch = branch;
        this.enemyTable = dungeonRoomTemplate.enemyTable() == null ? branch.enemyTable : dungeonRoomTemplate.enemyTable();
        this.difficulty = branch.difficulty * dungeonRoomTemplate.difficulty();
        this.level = level;
        this.position = position;
        this.offset = offset;
        this.settings = settings;
        this.rotated = settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90;
        this.boundingBoxes = dungeonRoomTemplate.getBoundingBoxes(settings, position);
        this.material = dungeonRoomTemplate.materials() == null ?
                this.branch.materials.getRandom() :
                this.template.materials().getRandom();

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
        this.handleChunkMap();

    }

    public void processConnectionPoints() {
        for (ConnectionPoint point : connectionPoints) {
            point.setupBlockstates(this.level, this.settings);
            if (point.isConnected()) point.unBlock(this.level);
            if (!point.isConnected()) point.block(this.level);
        }
    }

    public void processMaterialBlocks(DungeonMaterial material) {
        List<StructureTemplate.StructureBlockInfo> materialBlocks = this.template.materialBlocks();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        materialBlocks.forEach(structureBlockInfo -> {
            mutableBlockPos.set(TemplateHelper.transform(structureBlockInfo.pos(), this));
            level.setBlock(mutableBlockPos, TemplateHelper.fixBlockStateProperties(material.replace(structureBlockInfo.state()), this.settings), 2);

            if (level.getBlockState(mutableBlockPos.move(0, 1,0)) == Blocks.AIR.defaultBlockState() && level.getBlockState(mutableBlockPos.move(0, 1,0)) == Blocks.AIR.defaultBlockState()) {
                spawnablePosList.add(mutableBlockPos.offset(0, -1, 0));
            }
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

    public void handleChunkMap() {
        Set<ChunkPos> chunkPosSet = new HashSet<>();
        for (BoundingBox box : this.boundingBoxes) {
            ChunkPos min = new ChunkPos(new BlockPos(box.minX(), box.minY(), box.minZ()));
            ChunkPos max = new ChunkPos(new BlockPos(box.maxX(), box.maxY(), box.maxZ()));

            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    ChunkPos newPos = new ChunkPos(x, z);
                    chunkPosSet.add(newPos);
                }
            }
        }
        chunkPosSet.forEach(pos -> this.branch.floor.chunkMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(this));
    }

    public boolean isPosInsideShell(BlockPos pos) {
        for (BoundingBox box : this.boundingBoxes) {
            if (box.isInside(pos)) {
                if (box.inflatedBy(-1).isInside(pos)) {
                    return true;
                }
                for (BoundingBox otherBox : this.boundingBoxes) {
                    if (otherBox == box) continue;
                    boolean xConnected = otherBox.inflatedBy(1, 0, 0).isInside(pos);
                    boolean yConnected = otherBox.inflatedBy(0, 1, 0).isInside(pos);
                    boolean zConnected = otherBox.inflatedBy(0, 0, 1).isInside(pos);

                    //Only one axis is connected, indicating it's adjacent to another box, but not a corner
                    if ((xConnected ? 1 : 0) + (yConnected ? 1: 0) + (zConnected ? 1 : 0) == 1) {
                        if (box.inflatedBy(xConnected ? 0 : -1, yConnected ? 0 : -1, zConnected ? 0 : -1).isInside(pos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



    public enum DestructionRule {
        DEFAULT, SHELL, NONE
    }
    public DestructionRule getDestructionRule() {return DestructionRule.SHELL;}

    public void onGenerate() {}
    public void onEnter(WDPlayer player) {
        this.players.add(player);
        this.innerPlayers.put(player, false);
    }
    public void onEnterInner(WDPlayer player) {}
    public void onExit(WDPlayer player) {
        this.players.remove(player);
        this.innerPlayers.remove(player);
    }
    public void onClear() {
        this.clear = true;
    }
    public void reset() {}
    public void tick() {
        if (this.players.isEmpty()) return;
        innerPlayers.forEach((player, inside) -> {
            if (!inside) {
                if (this.isPosInsideShell(player.getServerPlayer().blockPosition())) {
                    innerPlayers.put(player, true);
                    this.onEnterInner(player);
                }
            }
        });
    }

}
