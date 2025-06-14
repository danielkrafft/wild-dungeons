package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LockableEventRoom extends DungeonRoom {

    public static final int START_COOLDOWN = 100;
    public int startCooldown = START_COOLDOWN;

    public boolean clear = false;
    public boolean started = false;
    public boolean generated = false;

    public LockableEventRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
    }

    public void start() {
        if (this.started) return;
        WildDungeons.getLogger().info("STARTING LOCKABLE ROOM");
        this.started = true;
        this.getBranch().getFloor().getLevel().playSound(null, this.getPosition(), SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, .5f, 1f);

        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) {
                point.block(2);
                point.removeDecal();
            }
        });
    }

    @Override
    public void onExit(WDPlayer wdPlayer) {
        super.onExit(wdPlayer);
        if (this.getActivePlayers().isEmpty() && !this.isClear()) {
            this.reset();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getActivePlayers().isEmpty() || this.isClear() || this.started) return;

        WildDungeons.getLogger().info("LOCKABLE ROOM TICKING");

        int branchIndex = Math.max(0, this.getBranch().getIndex() - 1);

        if (!this.started && this.startCooldown > 0) {
            this.startCooldown -= 1;
            if (this.getActivePlayers().size() >= this.getBranch().getActivePlayers().size() + this.getBranch().getFloor().getBranches().get(branchIndex).getActivePlayers().size()) {
                this.startCooldown -= 5;
            }
        }

        if (!this.started && (this.startCooldown <= 0)) {
            this.start();
        }
    }

    @Override
    public void onClear() {
        super.onClear();
        clear = true;
        WildDungeons.getLogger().info("LOCKABLE ROOM CLEARING");
        this.getBranch().getFloor().getLevel().playSound(null, this.getPosition(), SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, .5f, 1f);
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected() && !(point.getConnectedPoint().getRoom() instanceof SecretRoom)) {
                point.unBlock();
            }
        });
        processDataMarkers();
    }

    @Override
    public void reset() {
        super.reset();
        WildDungeons.getLogger().info("LOCKABLE ROOM RESETTING");
        this.started = false;
        startCooldown = START_COOLDOWN;
        setPreviewDoorways();
    }

    @Override
    public void onBranchComplete() {
        super.onBranchComplete();
        this.generated = true;
        this.setPreviewDoorways();
    }

    @Override
    public void onBranchEnter(WDPlayer player) {
        super.onBranchEnter(player);
    }

    public void setPreviewDoorways() {
        if (isClear()) return;
        this.getConnectionPoints().forEach(point -> {
            if (point.isConnected()) {
                //this code only works when the *next* branch is generated too
                if (point.getConnectedPoint().getBranchIndex() > this.getBranch().getIndex() ||
                        (point.getConnectedPoint().getRoom().getIndex() > this.getIndex() && point.getConnectedPoint().getBranchIndex() == this.getBranch().getIndex())) {
                    point.block(2);
                    point.removeDecal();
                } else {
                    point.unBlock();
                    point.addDecal();
                }
            }
        });
    }

    @Override
    public void processDataMarker(BlockPos pos, String metadata) {
        super.processDataMarker(pos, metadata);
        if (metadata.equals("wd_gate")) {
            BlockState state = this.isClear() ? Blocks.AIR.defaultBlockState() : WDBlocks.IRON_GRATE.get().defaultBlockState();
            this.getBranch().getFloor().getLevel().setBlock(pos, state, 130);
        }
    }
}