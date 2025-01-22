package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.blockentity.RiftBlockEntity;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RiftBlock extends Block implements EntityBlock {
    public RiftBlock(Properties properties) {
        super(properties);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftBlockEntity(pos, state);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ServerPlayer serverplayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverplayer);
            if (wdPlayer.getRiftCooldown() > 0) {return;}

            RiftBlockEntity riftBlockEntity = (RiftBlockEntity) level.getBlockEntity(pos);
            if (riftBlockEntity == null || riftBlockEntity.destination == null) {return;}

            WildDungeons.getLogger().info("TESTING RIFT BLOCK {} WITH RIFT COOLDOWN {}", riftBlockEntity.destination, wdPlayer.getRiftCooldown());

            if (riftBlockEntity.destination.equals("-1")) {

                WildDungeons.getLogger().info("TRYING TO LEAVE {} WITH PLAYER {}", wdPlayer.getCurrentDungeon(), wdPlayer);

                DungeonSession dungeon = wdPlayer.getCurrentDungeon();
                dungeon.onExit(wdPlayer);
                wdPlayer.setRiftCooldown(100);

            } else if (riftBlockEntity.destination.equals("win")) {

                WildDungeons.getLogger().info("TRYING TO WIN {}", wdPlayer.getCurrentDungeon());

                DungeonSession dungeon = wdPlayer.getCurrentDungeon();
                dungeon.win();


            } else if (riftBlockEntity.destination.equals("random")) {

                DungeonTemplate dungeonTemplate = DungeonRegistry.DUNGEON_POOL.getRandom();
                WildDungeons.getLogger().info("TRYING TO ENTER {}", dungeonTemplate.name());

                DungeonSession dungeon = DungeonSessionManager.getInstance().getOrCreateDungeonSession(riftBlockEntity.getBlockPos(), level.dimension(), dungeonTemplate.name());
                dungeon.onEnter(wdPlayer);
                wdPlayer.setRiftCooldown(100);

            } else if (riftBlockEntity.destination.split("-")[0].equals("wd")) {

                DungeonTemplate dungeonTemplate = DungeonRegistry.DUNGEON_REGISTRY.get(riftBlockEntity.destination.split("-")[1]);
                WildDungeons.getLogger().info("TRYING TO ENTER {}", dungeonTemplate.name());

                if (dungeonTemplate != null) {
                    DungeonSession dungeon = DungeonSessionManager.getInstance().getOrCreateDungeonSession(riftBlockEntity.getBlockPos(), level.dimension(), dungeonTemplate.name());
                    dungeon.onEnter(wdPlayer);
                    wdPlayer.setRiftCooldown(100);
                }

            } else {

                DungeonSession dungeon = wdPlayer.getCurrentDungeon();
                while (dungeon.getFloors().size() <= Integer.parseInt(riftBlockEntity.destination)) dungeon.generateFloor(dungeon.getFloors().size());
                WildDungeons.getLogger().info("TRYING TO ENTER FLOOR: {}", Integer.parseInt(riftBlockEntity.destination));
                DungeonFloor newFloor = dungeon.getFloors().get(Integer.parseInt(riftBlockEntity.destination));
                newFloor.onEnter(wdPlayer);

            }
        }
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return true;
    }
}
