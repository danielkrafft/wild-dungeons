package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.ui.RoomExportScreen;
import com.danielkkrafft.wilddungeons.world.structure.WDStructureTemplate;
import com.danielkkrafft.wilddungeons.world.structure.WDStructureTemplateManager;
import com.google.common.collect.Lists;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RoomExportWand extends Item {
    private BlockPos firstPos;
    private BlockPos secondPos;
    private boolean setFirstPos = true;
    private boolean withEntities;
    private String roomName;


    public RoomExportWand(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        if (level.isClientSide) {
            // Only open screen when right-clicking in air (not targeting a block)
            if (!player.isShiftKeyDown() && Minecraft.getInstance().hitResult.getType() == HitResult.Type.MISS) {
                Minecraft.getInstance().setScreen(new RoomExportScreen(this));
                return InteractionResultHolder.success(itemStack);
            }
        } else if (player instanceof ServerPlayer serverPlayer) {
            // Server-side handling: reset positions on shift-click
            if (serverPlayer.isShiftKeyDown()) {
                firstPos = null;
                secondPos = null;
                setFirstPos = true;
                serverPlayer.sendSystemMessage(Component.translatable("message.room_export_wand.reset"));
                return InteractionResultHolder.success(itemStack);
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (context.getPlayer() instanceof ServerPlayer serverPlayer) {

            if (serverPlayer.isShiftKeyDown()) return super.useOn(context);

            if (setFirstPos) {
                if (!checkDistance(context, serverPlayer, secondPos)) return InteractionResult.FAIL;
                setFirstPos = false;
                firstPos = context.getClickedPos();
                serverPlayer.sendSystemMessage(Component.translatable("message.room_export_wand.first_pos_set", firstPos.getX(), firstPos.getY(), firstPos.getZ()));
            } else {
                if (!checkDistance(context, serverPlayer, firstPos)) return InteractionResult.FAIL;
                setFirstPos = true;
                secondPos = context.getClickedPos();
                serverPlayer.sendSystemMessage(Component.translatable("message.room_export_wand.second_pos_set", secondPos.getX(), secondPos.getY(), secondPos.getZ()));
            }
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }

    private boolean checkDistance(@NotNull UseOnContext context, ServerPlayer serverPlayer, BlockPos checkAgainst) {
        if (checkAgainst != null) {
            //check to make sure the second pos isn't more than 48 blocks from the first pos in any direction
            if (Math.abs(checkAgainst.getX() - context.getClickedPos().getX()) > 48 || Math.abs(checkAgainst.getY() - context.getClickedPos().getY()) > 48 || Math.abs(checkAgainst.getZ() - context.getClickedPos().getZ()) > 48) {
                serverPlayer.sendSystemMessage(Component.translatable("message.room_export_wand.too_far"));
                return false;
            }
        }
        return true;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (isSelected && level.isClientSide && entity instanceof Player player) {
            if (player.tickCount % 2 == 0) {
                if (firstPos != null && secondPos != null) {
                    BoundingBox boundingBox = BoundingBox.fromCorners(firstPos, secondPos);
                    renderBoundingBoxEdges(level, boundingBox);
                } else {
                    if (firstPos != null) {
                        renderBoundingBoxEdges(level,new BoundingBox(firstPos));
                    }
                    if (secondPos != null) {
                        renderBoundingBoxEdges(level, new BoundingBox(secondPos));
                    }
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private void renderBoundingBoxEdges(@NotNull Level level, @NotNull BoundingBox box) {
        // Extract the corners of the box
        int minX = box.minX();
        int minY = box.minY();
        int minZ = box.minZ();
        int maxX = box.maxX() + 1; // Add 1 to reach the outer edge of the last block
        int maxY = box.maxY() + 1; // Add 1 to reach the top of the last block
        int maxZ = box.maxZ() + 1; // Add 1 to reach the outer edge of the last block

        // Render particles along all 12 edges of the box
        // Bottom edges
        renderLineParticles(level, minX, minY, minZ, maxX, minY, minZ);
        renderLineParticles(level, minX, minY, maxZ, maxX, minY, maxZ);
        renderLineParticles(level, minX, minY, minZ, minX, minY, maxZ);
        renderLineParticles(level, maxX, minY, minZ, maxX, minY, maxZ);

        // Top edges
        renderLineParticles(level, minX, maxY, minZ, maxX, maxY, minZ);
        renderLineParticles(level, minX, maxY, maxZ, maxX, maxY, maxZ);
        renderLineParticles(level, minX, maxY, minZ, minX, maxY, maxZ);
        renderLineParticles(level, maxX, maxY, minZ, maxX, maxY, maxZ);

        // Vertical edges
        renderLineParticles(level, minX, minY, minZ, minX, maxY, minZ);
        renderLineParticles(level, maxX, minY, minZ, maxX, maxY, minZ);
        renderLineParticles(level, minX, minY, maxZ, minX, maxY, maxZ);
        renderLineParticles(level, maxX, minY, maxZ, maxX, maxY, maxZ);
    }

    private void renderLineParticles(@NotNull Level level, int startX, int startY, int startZ, int endX, int endY, int endZ) {
        // Calculate the total distance between start and end points
        double totalDistance = Math.sqrt(
                Math.pow(endX - startX, 2) +
                        Math.pow(endY - startY, 2) +
                        Math.pow(endZ - startZ, 2)
        );

        // Determine how many particles to place along the line
        // Using a spacing of approximately 0.5 blocks between particles
        int particleCount = (int) Math.ceil(totalDistance * 2);

        // Place particles evenly along the line
        for (int i = 0; i <= particleCount; i++) {
            double ratio = (particleCount == 0) ? 0 : (double) i / particleCount;
            double x = startX + (endX - startX) * ratio;
            double y = startY + (endY - startY) * ratio;
            double z = startZ + (endZ - startZ) * ratio;

            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
        }
    }

    public boolean saveStructure(ServerLevel serverLevel) {
        if (this.roomName == null || this.firstPos == null || this.secondPos == null) {
            return false;
        } else {
            ResourceLocation resourceLocation = WildDungeons.rl(this.roomName);
            WDStructureTemplateManager wdStructureTemplateManager = WDStructureTemplateManager.INSTANCE;

            WDStructureTemplate wdStructureTemplate;
            try {
                wdStructureTemplate = wdStructureTemplateManager.getOrCreate(resourceLocation);
            } catch (ResourceLocationException e) {
                return false;
            }

            fillFromWorld(wdStructureTemplate, serverLevel, firstPos, secondPos, withEntities);
            wdStructureTemplate.setAuthor(serverLevel.getServer().getServerModName());
            try {
                return wdStructureTemplateManager.save(resourceLocation);
            } catch (ResourceLocationException e) {
                return false;
            }
        }
    }

    public void fillFromWorld(StructureTemplate structureTemplate, ServerLevel level, BlockPos pos1, BlockPos pos2, boolean withEntities) {
        List<StructureTemplate.StructureBlockInfo> normalBlocks = Lists.newArrayList();
        List<StructureTemplate.StructureBlockInfo> blocksWithNbt = Lists.newArrayList();
        List<StructureTemplate.StructureBlockInfo> blocksWithSpecialShape = Lists.newArrayList();
        BlockPos minPos = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
        BlockPos maxPos = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
        structureTemplate.size = new Vec3i(Math.abs(maxPos.getX() - minPos.getX()) + 1, Math.abs(maxPos.getY() - minPos.getY()) + 1, Math.abs(maxPos.getZ() - minPos.getZ()) + 1);

        for(BlockPos blockPos : BlockPos.betweenClosed(minPos, maxPos)) {
            BlockPos blockPos1 = blockPos.subtract(minPos);
            BlockState blockstate = level.getBlockState(blockPos);
            if (!blockstate.is(Blocks.STRUCTURE_VOID)) {
                BlockEntity blockentity = level.getBlockEntity(blockPos);
                StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo;
                if (blockentity != null) {
                    structuretemplate$structureblockinfo = new StructureTemplate.StructureBlockInfo(blockPos1, blockstate, blockentity.saveWithId(level.registryAccess()));
                } else {
                    structuretemplate$structureblockinfo = new StructureTemplate.StructureBlockInfo(blockPos1, blockstate, (CompoundTag) null);
                }

                StructureTemplate.addToLists(structuretemplate$structureblockinfo, normalBlocks, blocksWithNbt, blocksWithSpecialShape);

            }
        }

        structureTemplate.palettes.clear();
        structureTemplate.palettes.add(new StructureTemplate.Palette(StructureTemplate.buildInfoList(normalBlocks,blocksWithNbt,blocksWithSpecialShape)));
        if (withEntities) {
            structureTemplate.fillEntityList(level, minPos, maxPos);
        } else {
            structureTemplate.entityInfoList.clear();
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public void setName(String roomName) {
        this.roomName = roomName;
    }
}
