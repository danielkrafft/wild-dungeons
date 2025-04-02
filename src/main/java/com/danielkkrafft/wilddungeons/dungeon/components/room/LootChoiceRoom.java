package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class LootChoiceRoom extends LootRoom {
    public LootChoiceRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
    }

    @Override
    public void discardByUUID(String uuid) {
        super.discardByUUID(uuid);

        List<DungeonTarget> toRemove = new ArrayList<>();
        this.targets.forEach(target -> {
            if (!target.spawned) return;
            toRemove.add(target);
        });
        toRemove.forEach(target -> {
            target.discard(this);
            targets.remove(target);
        });
    }
}
