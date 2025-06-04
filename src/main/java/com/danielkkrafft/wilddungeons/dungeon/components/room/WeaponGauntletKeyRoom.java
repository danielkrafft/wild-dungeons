package com.danielkkrafft.wilddungeons.dungeon.components.room;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorPoolRegistry;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import static com.danielkkrafft.wilddungeons.dungeon.registries.OfferingTemplateRegistry.GAUNTLET_RIFT;

public class WeaponGauntletKeyRoom extends DungeonRoom {

    private boolean setupRifts = false;

    public WeaponGauntletKeyRoom(DungeonBranch branch, String templateKey, BlockPos position, TemplateOrientation orientation) {
        super(branch, templateKey, position, orientation);
    }

    public void processRifts() {}

    @Override
    public void onEnter(WDPlayer player) {
        super.onEnter(player);
        setupRifts();
    }

    private void setupRifts() {
        if (!setupRifts) {
            getTemplate().rifts().forEach(pos -> {

                String destination = String.valueOf(getBranch().getFloor().getSession().generateDynamicFloor(getBranch().getFloor().getIndex(), DungeonFloorPoolRegistry.WEAPON_GAUNTLET_POOL));

                GAUNTLET_RIFT.setRiftDestination(getBranch().getFloor().getSession().getFloors().getLast().getTemplate().name());
                Offering rift = GAUNTLET_RIFT.asOffering(this.getBranch().getFloor().getLevel()).setOfferingId(destination).setSoundLoop(0);

                Vec3 pos1 = StructureTemplate.transform(pos, this.getSettings().getMirror(), this.getSettings().getRotation(), TemplateHelper.EMPTY_BLOCK_POS).add(this.position.getX(), this.position.getY(), this.position.getZ());
                WildDungeons.getLogger().info("ADDING RIFT AT {}", pos1);
                rift.setPos(pos1);
                this.getBranch().getFloor().getLevel().addFreshEntity(rift);
                this.riftUUIDs.add(rift.getStringUUID());
            });
            setupRifts = true;
        }
    }
}
