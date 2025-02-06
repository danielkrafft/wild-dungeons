package com.danielkkrafft.wilddungeons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;


public class DoorwayBlock extends Block {
    public static EnumProperty<DoorType> DOOR_TYPE = EnumProperty.create("door_type", DoorType.class);
    public DoorwayBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOOR_TYPE);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.isCreative()) {
            DoorType doorType = state.getValue(DOOR_TYPE);
            DoorType newDoorType = DoorType.values()[(doorType.ordinal() + 1) % DoorType.values().length];
            level.setBlock(pos, state.setValue(DOOR_TYPE, newDoorType), 2);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    public enum DoorType implements StringRepresentable {
        COMBAT("combat"),
        LOOT("loot");

        private final String name;

        DoorType(String name) {
            this.name = name;
        }
        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public static BlockState of(DoorType doorType) {
        return WDBlocks.WD_DOORWAY.get().getStateDefinition().any().setValue(DOOR_TYPE, doorType);
    }
}
