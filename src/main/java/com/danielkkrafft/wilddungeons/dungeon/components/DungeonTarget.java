package com.danielkkrafft.wilddungeons.dungeon.components;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class DungeonTarget {
    public enum Type {BLOCK, ENTITY, SPAWNER} //TODO implement custom spawners (light level issue, max entity issue)
    public final String type;
    public final String entityTypeKey;
    public String uuid;
    public BlockPos startPos;
    public boolean spawned = false;
    public int helmetItem = -1;
    public int chestItem = -1;
    public int legsItem = -1;
    public int bootsItem = -1;
    public int mainHandItem = -1;
    public int offHandItem = -1;
    public double healthMultiplier = 1.0f;
    public Consumer<Object> behavior;
    public List<Pair<Integer, Integer>> mobEffects = new ArrayList<>();

    public DungeonTarget(Type type, String entityTypeKey) {
        this.type = type.toString();
        this.entityTypeKey = entityTypeKey;
    }

    public DungeonTarget(Entity entity) {
        this.type = Type.ENTITY.toString();
        this.entityTypeKey = EntityType.getKey(entity.getType()).toString();
        this.uuid = entity.getStringUUID();
        this.startPos = entity.blockPosition();
        this.spawned = true;
    }

    public DungeonTarget(BlockPos pos) {
        this.type = Type.BLOCK.toString();
        this.startPos = pos;
        this.spawned = true;
        this.entityTypeKey = "";
    }

    public void spawn(DungeonRoom room) {

        if (type.equals(Type.ENTITY.toString()))
        {
            Entity entity = EntityType.byString(this.entityTypeKey).get().create(room.getBranch().getFloor().getLevel());
            if (helmetItem != -1) entity.getSlot(100 + EquipmentSlot.HEAD.getIndex()).set(new ItemStack(Item.byId(helmetItem), 1));
            if (chestItem != -1) entity.getSlot(100 + EquipmentSlot.CHEST.getIndex()).set(new ItemStack(Item.byId(chestItem), 1));
            if (legsItem != -1) entity.getSlot(100 + EquipmentSlot.LEGS.getIndex()).set(new ItemStack(Item.byId(legsItem), 1));
            if (bootsItem != -1) entity.getSlot(100 + EquipmentSlot.FEET.getIndex()).set(new ItemStack(Item.byId(bootsItem), 1));
            if (mainHandItem != -1) entity.getSlot(98).set(new ItemStack(Item.byId(mainHandItem), 1));
            if (offHandItem != -1) entity.getSlot(99).set(new ItemStack(Item.byId(offHandItem), 1));
            if (entity instanceof LivingEntity livingEntity) {
                mobEffects.forEach(pair -> livingEntity.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(BuiltInRegistries.MOB_EFFECT.byId(pair.getFirst())), Integer.MAX_VALUE, pair.getSecond())));
                double targetHealth = livingEntity.getMaxHealth() * healthMultiplier;
                if (livingEntity.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                    livingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(targetHealth);
                    livingEntity.setHealth((float) targetHealth);
                }
            }

            List<BlockPos> validPoints = room.sampleSpawnablePositions(room.getBranch().getFloor().getLevel(), 10, Mth.ceil(Math.max(entity.getBoundingBox().getXsize(), entity.getBoundingBox().getZsize())));

            BlockPos finalPos = room.calculateFurthestPoint(validPoints,20);

            entity.setPos(Vec3.atCenterOf(finalPos));
            if (this.behavior != null) this.behavior.accept(entity);
            this.uuid = entity.getStringUUID();
            this.startPos = finalPos;
            this.spawned = true;
            room.getBranch().getFloor().getLevel().addWithUUID(entity);
        }

        if (type.equals(Type.SPAWNER.toString()))
        {
            List<BlockPos> validPoints = room.sampleSpawnablePositions(room.getBranch().getFloor().getLevel(), 3, 1);
            BlockPos finalPos = room.calculateFurthestPoint(validPoints, 20);

            ServerLevel level = room.getBranch().getFloor().getLevel();
            level.setBlock(finalPos, Blocks.SPAWNER.defaultBlockState(), 130);
            BlockEntity blockEntity = level.getBlockEntity(finalPos);
            if (blockEntity instanceof SpawnerBlockEntity spawnerBlockEntity) {
                spawnerBlockEntity.setEntityId(EntityType.byString(this.entityTypeKey).get(), level.random);
            }

            this.startPos = finalPos;
            this.spawned = true;
        }
    }

    public boolean isAlive(DungeonRoom room) {
        if (!spawned) return true;

        if (type.equals(Type.ENTITY.toString()))
        {
            Entity entity = room.getBranch().getFloor().getLevel().getEntity(UUID.fromString(this.uuid));
            if (entity == null) return false;
            if (!entity.isAlive()) return false;

            for (BoundingBox box : room.getBoundingBoxes()) {
                if (box.isInside(entity.blockPosition())) return true;
            }

            return false;
        }

        if (type.equals(Type.SPAWNER.toString()))
        {
            return room.getBranch().getFloor().getLevel().getBlockState(this.startPos).is(Blocks.SPAWNER);
        }

        if (type.equals(Type.BLOCK.toString()))
        {
            return room.getBranch().getFloor().getLevel().getBlockState(this.startPos).getBlock() != Blocks.AIR;
        }

        return false;
    }

    public void discard(DungeonRoom room) {
        if (!this.spawned) return;

        if (type.equals(Type.ENTITY.toString()))
        {
            Entity entity = room.getBranch().getFloor().getLevel().getEntity(UUID.fromString(this.uuid));
            if (entity != null) entity.discard();
        }

        if (type.equals(Type.SPAWNER.toString()))
        {
            room.getBranch().getFloor().getLevel().setBlock(this.startPos, Blocks.AIR.defaultBlockState(), 2);
        }

    }

}
