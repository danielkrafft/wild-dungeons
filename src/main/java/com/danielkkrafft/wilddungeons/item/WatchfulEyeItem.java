package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.GuardianLaserBeamEntity;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class WatchfulEyeItem extends Item {

    public WatchfulEyeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        int wantedLasers = countInInventory(player);
        if (wantedLasers <= 0) {
            removeAllLasers(level, player);
            return;
        }

        List<GuardianLaserBeamEntity> lasers = getPlayerLasers(level, player);

        while (lasers.size() < wantedLasers) {
            spawnLaser(level, player);
            lasers = getPlayerLasers(level, player);
        }

        while (lasers.size() > wantedLasers) {
            lasers.get(0).discard();
            lasers = getPlayerLasers(level, player);
        }
    }

    private int countInInventory(Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(this)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private List<GuardianLaserBeamEntity> getPlayerLasers(Level level, Player player) {
        return level.getEntitiesOfClass(
                GuardianLaserBeamEntity.class,
                player.getBoundingBox().inflate(32),
                laser -> laser.getOwner() == player
        );
    }

    private void spawnLaser(Level level, Player player) {
        GuardianLaserBeamEntity laser =
                WDEntities.GUARDIAN_LASER_BEAM.get().create(level);

        if (laser == null) return;

        laser.setPos(
                player.getX(),
                player.getEyeY() - 0.15,
                player.getZ()
        );

        laser.setOwner(player);

        level.addFreshEntity(laser);

    }

    private void removeAllLasers(Level level, Player player) {
        for (GuardianLaserBeamEntity laser : getPlayerLasers(level, player)) {
            laser.discard();
        }
    }
}