package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.ConnectionPoint;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateTableRegistry;
import com.danielkkrafft.wilddungeons.entity.Offering;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LootRoom extends TargetPurgeRoom {

    public LootRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
    }

    @Override
    public void processOfferings() {
        List<DungeonRegistration.OfferingTemplate> entries = OfferingTemplateTableRegistry.FREE_PERK_OFFERING_TABLE.randomResults(this.getTemplate().offerings().size(), (int) this.getDifficulty() * this.getTemplate().offerings().size(), 1.2f);
        getTemplate().offerings().forEach(pos -> {
            if (entries.isEmpty()) return;
            Offering next = entries.removeFirst().asOffering(this.getBranch().getFloor().getLevel());
            Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
            next.setPos(pos1);
            this.getBranch().getFloor().getLevel().addFreshEntity(next);
            this.getOfferingUUIDs().add(next.getStringUUID());
            this.targets.add(new DungeonTarget(next));
        });
    }

    @Override public ResourceLocation getDecalTexture() {return ConnectionPoint.CHEST_TEXTURE;}
    @Override public int getDecalColor() {return 0xFFffdd00;}

}
