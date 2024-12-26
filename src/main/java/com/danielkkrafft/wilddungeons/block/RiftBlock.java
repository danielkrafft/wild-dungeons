package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponents;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.blockentity.RiftBlockEntity;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverplayer.getStringUUID());
            if (wdPlayer.getRiftCooldown() != 0) {return;}

            RiftBlockEntity riftBlockEntity = (RiftBlockEntity) level.getBlockEntity(pos);
            if (riftBlockEntity == null || riftBlockEntity.destination == null) {return;}

            if (riftBlockEntity.destination.equals("exit")) {

                WildDungeons.getLogger().info("TRYING TO EXIT {}", wdPlayer.getCurrentFloor());

                DungeonSession dungeon = DungeonSessionManager.getInstance().getDungeonSession(wdPlayer.getCurrentDungeon());
                dungeon.exitFloor(serverplayer);
                wdPlayer.setRiftCooldown(100);

            } else if (riftBlockEntity.destination.equals("win")) {

                WildDungeons.getLogger().info("TRYING TO WIN {}", wdPlayer.getCurrentDungeon());

                DungeonSession dungeon = DungeonSessionManager.getInstance().getDungeonSession(wdPlayer.getCurrentDungeon());
                dungeon.exitDungeon(serverplayer);
                wdPlayer.setRiftCooldown(100);
                serverplayer.addItem(new ItemStack(Items.DIAMOND.asItem(), 1));


            } else if (riftBlockEntity.destination.equals("random")) {

                DungeonComponents.DungeonTemplate dungeonTemplate = DungeonRegistry.DUNGEON_POOL.getRandom();
                WildDungeons.getLogger().info("TRYING TO ENTER {}", dungeonTemplate.name());

                DungeonSession dungeon = DungeonSessionManager.getInstance().getOrCreateDungeonSession(riftBlockEntity.getBlockPos(), dungeonTemplate);
                dungeon.enterDungeon(serverplayer);
                wdPlayer.setRiftCooldown(100);

            } else {

                DungeonSession dungeon = DungeonSessionManager.getInstance().getDungeonSession(wdPlayer.getCurrentDungeon());
                dungeon.enterFloor(serverplayer, riftBlockEntity.destination);
                wdPlayer.setRiftCooldown(100);

            }

        }
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return true;
    }
}
