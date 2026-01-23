package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.PistonEvent;

import java.util.*;

@EventBusSubscriber(modid = WildDungeons.MODID)
public final class WDProtectedRegion {

    private boolean active;
    private final ResourceKey<Level> dimension;
    private final List<BoundingBox> boxes;
    private final EnumSet<RegionPermission> permissions;

    public void setActive(boolean active) {
        this.active = active;
    }

    public enum RegionPermission {
        BLOCK_BREAK,
        BLOCK_PLACE,
        EXPLOSION,
        PEARL,
        MOB_GRIEF,
        PISTON,
        FIRE,
        NONE
    }

    private static final Map<ResourceKey<Level>, List<WDProtectedRegion>> PROTECTED_REGIONS = new HashMap<>();

    public WDProtectedRegion(ResourceKey<Level> dimension, List<BoundingBox> boxes, EnumSet<RegionPermission> permissions, boolean active) {
        this.dimension = dimension;
        this.boxes = new ArrayList<>(boxes);
        this.permissions = permissions.clone();
        this.active = active;
        register(this);
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public static void register(WDProtectedRegion region) {
        PROTECTED_REGIONS.computeIfAbsent(region.dimension, d -> new ArrayList<>()).add(region);
    }

    private boolean denies(RegionPermission permission) {
        return !permissions.contains(permission);
    }

    private static boolean denies(Level level, BlockPos pos, RegionPermission permission) {
        List<WDProtectedRegion> list = PROTECTED_REGIONS.get(level.dimension());
        if (list == null || list.isEmpty()) return false;

        for (WDProtectedRegion region : list) {
            if (!region.active) continue;
            if (region.contains(pos) && region.denies(permission)) return true;
        }
        return false;
    }

    public void setPermissions(EnumSet<RegionPermission> permissions) {
        this.permissions.clear();
        this.permissions.addAll(permissions);
    }

    public void allow(RegionPermission permission) {
        permissions.add(permission);
    }

    public void deny(RegionPermission permission) {
        permissions.remove(permission);
    }

    private boolean contains(BlockPos pos) {
        for (BoundingBox box : boxes) {
            if (box.isInside(pos)) {
                return true;
            }
        }
        return false;
    }

    public static Map<ResourceKey<Level>, List<WDProtectedRegion>> getAllRegions() {
        return PROTECTED_REGIONS;
    }

    public static void clearAllRegions() {
        PROTECTED_REGIONS.clear();
    }

    public void toShell() {
        List<BoundingBox> newBoxes = new ArrayList<>();

        for (BoundingBox box : this.boxes) {
            boolean bottomFaceAdjacent = false;
            for (BoundingBox otherBox : this.boxes) {
                if (otherBox == box) continue;
                if (otherBox.maxY() == box.minY() - 1 &&
                    otherBox.minX() <= box.maxX() && otherBox.maxX() >= box.minX() &&
                    otherBox.minZ() <= box.maxZ() && otherBox.maxZ() >= box.minZ()) {
                    bottomFaceAdjacent = true;
                    break;
                }
            }
            if (!bottomFaceAdjacent) {
                newBoxes.add(new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.minY(), box.maxZ()));
            }

            boolean topFaceAdjacent = false;
            for (BoundingBox otherBox : this.boxes) {
                if (otherBox == box) continue;
                if (otherBox.minY() == box.maxY() + 1 &&
                    otherBox.minX() <= box.maxX() && otherBox.maxX() >= box.minX() &&
                    otherBox.minZ() <= box.maxZ() && otherBox.maxZ() >= box.minZ()) {
                    topFaceAdjacent = true;
                    break;
                }
            }
            if (!topFaceAdjacent) {
                newBoxes.add(new BoundingBox(box.minX(), box.maxY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()));
            }

            boolean northFaceAdjacent = false;
            for (BoundingBox otherBox : this.boxes) {
                if (otherBox == box) continue;
                if (otherBox.maxZ() == box.minZ() - 1 &&
                    otherBox.minX() <= box.maxX() && otherBox.maxX() >= box.minX() &&
                    otherBox.minY() <= box.maxY() && otherBox.maxY() >= box.minY()) {
                    northFaceAdjacent = true;
                    break;
                }
            }
            if (!northFaceAdjacent && box.maxY() > box.minY()) {
                newBoxes.add(new BoundingBox(box.minX(), box.minY() + 1, box.minZ(), box.maxX(), box.maxY() - 1, box.minZ()));
            }

            boolean southFaceAdjacent = false;
            for (BoundingBox otherBox : this.boxes) {
                if (otherBox == box) continue;
                if (otherBox.minZ() == box.maxZ() + 1 &&
                    otherBox.minX() <= box.maxX() && otherBox.maxX() >= box.minX() &&
                    otherBox.minY() <= box.maxY() && otherBox.maxY() >= box.minY()) {
                    southFaceAdjacent = true;
                    break;
                }
            }
            if (!southFaceAdjacent && box.maxY() > box.minY()) {
                newBoxes.add(new BoundingBox(box.minX(), box.minY() + 1, box.maxZ(), box.maxX(), box.maxY() - 1, box.maxZ()));
            }

            boolean westFaceAdjacent = false;
            for (BoundingBox otherBox : this.boxes) {
                if (otherBox == box) continue;
                if (otherBox.maxX() == box.minX() - 1 &&
                    otherBox.minZ() <= box.maxZ() && otherBox.maxZ() >= box.minZ() &&
                    otherBox.minY() <= box.maxY() && otherBox.maxY() >= box.minY()) {
                    westFaceAdjacent = true;
                    break;
                }
            }
            if (!westFaceAdjacent && box.maxY() > box.minY() && box.maxZ() > box.minZ()) {
                newBoxes.add(new BoundingBox(box.minX(), box.minY() + 1, box.minZ() + 1, box.minX(), box.maxY() - 1, box.maxZ() - 1));
            }

            boolean eastFaceAdjacent = false;
            for (BoundingBox otherBox : this.boxes) {
                if (otherBox == box) continue;
                if (otherBox.minX() == box.maxX() + 1 &&
                    otherBox.minZ() <= box.maxZ() && otherBox.maxZ() >= box.minZ() &&
                    otherBox.minY() <= box.maxY() && otherBox.maxY() >= box.minY()) {
                    eastFaceAdjacent = true;
                    break;
                }
            }
            if (!eastFaceAdjacent && box.maxY() > box.minY() && box.maxZ() > box.minZ()) {
                newBoxes.add(new BoundingBox(box.maxX(), box.minY() + 1, box.minZ() + 1, box.maxX(), box.maxY() - 1, box.maxZ() - 1));
            }
        }
        
        this.boxes.clear();
        this.boxes.addAll(newBoxes);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        if (level.isClientSide()) return;
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;

        if (denies(level, pos, RegionPermission.BLOCK_BREAK)) {
            serverPlayer.sendSystemMessage(Component.translatable("message.wilddungeons.block_break_fail"),true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        if (level.isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        if (event.getPlacedBlock().is(Blocks.FIRE) || event.getPlacedBlock().is(Blocks.SOUL_FIRE)) {
            if (denies(level, pos, RegionPermission.FIRE)) {
                event.setCanceled(true);
            }
        }

        if (denies(level, pos, RegionPermission.BLOCK_PLACE)) {
            event.setCanceled(true);
            serverPlayer.sendSystemMessage(Component.translatable("message.wilddungeons.block_place_fail"), true);
        }
    }

    @SubscribeEvent
    public static void onEnderPearlTeleport(EntityTeleportEvent.EnderPearl event) {
        Level level = event.getEntity().level();
        if (level.isClientSide()) return;

        BlockPos targetLocation = BlockPos.containing(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        BlockPos previousLocation = BlockPos.containing(event.getPrevX(), event.getPrevY(), event.getPrevZ());

        if (denies(level, targetLocation, RegionPermission.PEARL) || denies(level, previousLocation, RegionPermission.PEARL)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        event.getAffectedBlocks().removeIf(pos -> denies(level, pos, RegionPermission.EXPLOSION));
    }

    @SubscribeEvent
    public static void onPistonMovePre(PistonEvent.Pre event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;

        event.setCanceled(denies(level, event.getPos(), RegionPermission.PISTON));
    }

    @SubscribeEvent
    public static void onMobGrief(EntityMobGriefingEvent event){
        Level level = event.getEntity().level();
        BlockPos pos = event.getEntity().getOnPos();
        if (level.isClientSide()) return;

        if (denies(level, pos, RegionPermission.MOB_GRIEF)) event.setCanGrief(false);
    }
}
