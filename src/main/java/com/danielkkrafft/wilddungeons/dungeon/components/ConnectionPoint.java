package com.danielkkrafft.wilddungeons.dungeon.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionPoint {
    public DungeonRoom room = null;
    public BoundingBox boundingBox;
    public List<BlockPos> positions;
    public HashMap<BlockPos, BlockState> unBlockedBlockStates = new HashMap<>();
    public Direction direction;
    public String type = "both";
    public StructurePlaceSettings settings;

    public String pool = "all";
    public boolean connected = false;
    public int failures = 0;
    public int score = 0;
    public int distanceToBranchOrigin = 0;
    public int distanceToFloorOrigin = 0;
    public int distanceToYTarget = 0;

    public ConnectionPoint(BlockPos position, Direction direction) {
        this.boundingBox = new BoundingBox(position);
        this.positions = new ArrayList<>();
        positions.add(position);
        this.direction = direction;
    }

    public ConnectionPoint(ConnectionPoint point) {
        this.room = point.room;
        this.boundingBox = point.boundingBox;
        this.positions = point.positions;
        this.direction = point.direction;

        this.pool = point.pool;
        this.connected = point.connected;
        this.failures = point.failures;
        this.type = point.type;
    }

    public ConnectionPoint transformed(StructurePlaceSettings settings, BlockPos position, BlockPos offset) {
        ConnectionPoint newPoint = new ConnectionPoint(this);

        newPoint.direction = TemplateHelper.mirrorDirection(newPoint.direction, settings.getMirror());
        newPoint.direction = TemplateHelper.rotateDirection(newPoint.direction, settings.getRotation());

        newPoint.positions = newPoint.positions.stream().map(pos -> StructureTemplate.transform(pos, settings.getMirror(), settings.getRotation(), offset).offset(position)).toList();
        newPoint.boundingBox = new BoundingBox(newPoint.positions.getFirst());
        newPoint.positions.forEach((pos) -> newPoint.boundingBox.encapsulate(pos));

        return newPoint;
    }

    public void block(ServerLevel level, BlockState blockState) {
        positions.forEach((pos) -> {
            level.setBlock(pos, blockState, 2);
        });
    }

    public void unBlock(ServerLevel level) {
        unBlockedBlockStates.forEach((pos, blockState) -> {
            level.setBlock(pos, blockState, 2);
        });
    }

    public Vector2i getSize() {
        int x = this.boundingBox.getXSpan();
        int y = this.boundingBox.getYSpan();
        int z = this.boundingBox.getZSpan();

        return switch (this.direction) {
            case UP, DOWN -> new Vector2i(x, z);
            case NORTH, SOUTH -> new Vector2i(x, y);
            case EAST, WEST -> new Vector2i(z, y);
        };
    }
}
