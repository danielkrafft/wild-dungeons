package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.HAS_BEDROCK_SHELL;

public class AddBedrockShellStep extends PostProcessingStep {

    @Override
    public void handle(List<DungeonRoom> rooms) {
        for (DungeonRoom room : rooms) {
            if (room.getProperty(HAS_BEDROCK_SHELL)) room.surroundWith(Blocks.BEDROCK.defaultBlockState());
        }
    }
}
