package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
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
    private static final Map<ResourceKey<Level>, List<WDProtectedRegion>> PROTECTED_REGIONS = new HashMap<>();
    private static final Map<ResourceKey<Level>, Map<Long, List<WDProtectedRegion>>> CHUNK_REGIONS = new HashMap<>();

    private boolean active;
    private final ResourceKey<Level> dimension;
    private final List<BoundingBox> boxes;
    private final EnumSet<RegionPermission> permissions;

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

        Map<Long, List<WDProtectedRegion>> chunkMap = CHUNK_REGIONS.computeIfAbsent(region.dimension, d -> new HashMap<>());

        //boxes -> chunks for chunkmap
        for (BoundingBox box : region.boxes) {
            for (int cx = (box.minX() >> 4); cx <= (box.maxX() >> 4); cx++) {
                for (int cz = (box.minZ() >> 4); cz <= (box.maxZ() >> 4); cz++) {
                    long key = ChunkPos.asLong(cx, cz);
                    chunkMap.computeIfAbsent(key, k -> new ArrayList<>()).add(region);
                }
            }
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean denies(RegionPermission permission) {
        return !permissions.contains(permission);
    }

    private static boolean denies(Level level, BlockPos pos, RegionPermission permission) {
        Map<Long, List<WDProtectedRegion>> chunkMap = CHUNK_REGIONS.get(level.dimension()); //list of all registered chunks in the level passed as arg
        if (chunkMap == null || chunkMap.isEmpty()) return false;

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long key = ChunkPos.asLong(chunkX, chunkZ); //the chunk that we are checking

        List<WDProtectedRegion> regions = chunkMap.get(key); //list of regions in the chunk we are checking
        if (regions == null || regions.isEmpty()) return false;

        for (WDProtectedRegion region : regions) { //loops through regions in chunk containing pos
            if (!region.active) continue;
            if (region.contains(pos) && region.denies(permission)) {
                return true;
            }
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
        CHUNK_REGIONS.clear();
    }

    private boolean hasNeighbor(BoundingBox box, int dx, int dy, int dz) {
        for (BoundingBox otherBox : this.boxes) {
            if (otherBox == box || otherBox.intersects(box)) continue;

            if (otherBox.moved(dx, dy, dz).intersects(box)) {
                return true;
            }
        }
        return false;
    }

    public void toShell() {
        List<BoundingBox> newBoxes = new ArrayList<>();

        for (BoundingBox box : this.boxes) {
            boolean tall = box.maxY() > box.minY();
            boolean deep = box.maxZ() > box.minZ();

            if (!hasNeighbor(box, 0, 1, 0)) { //Y-
                newBoxes.add(new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.minY(), box.maxZ()));
            }

            if (!hasNeighbor(box, 0, -1, 0)) { //Y+
                newBoxes.add(new BoundingBox(box.minX(), box.maxY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()));
            }

            if (tall && !hasNeighbor(box, 0, 0, 1)) { //Z-
                newBoxes.add(new BoundingBox(box.minX(), box.minY() + 1, box.minZ(), box.maxX(), box.maxY() - 1, box.minZ()));
            }

            if (tall && !hasNeighbor(box, 0, 0, -1)) { //Z+
                newBoxes.add(new BoundingBox(box.minX(), box.minY() + 1, box.maxZ(), box.maxX(), box.maxY() - 1, box.maxZ()));
            }

            if (tall && deep && !hasNeighbor(box, 1, 0, 0)) { //X-
                newBoxes.add(new BoundingBox(box.minX(), box.minY() + 1, box.minZ() + 1, box.minX(), box.maxY() - 1, box.maxZ() - 1));
            }

            if (tall && deep && !hasNeighbor(box, -1, 0, 0)) { //X+
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
