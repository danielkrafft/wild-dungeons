package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class UtilityMethods
{
    private UtilityMethods(){}
    public static int RNG(int min,int max){return (int)Math.round(RNG((double)min,max));}
    public static double RNG(double min,double max){return (max-min)*Math.random()+min;}
    public static <T extends ParticleOptions>void sendParticles(@NotNull ServerLevel server, T t, boolean visible,int count,double x,double y,double z,float dispX,float dispY,float dispZ,float speed)
    {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(t,visible,x,y,z,dispX,dispY,dispZ,speed,count);
        server.getPlayers(sp->true).forEach(p->p.connection.send(packet));
    }

    /**
     * Sets spawn potentials for a spawner
     * @param spawner The BaseSpawner to modify
     * @param weightedSpawnData List of spawn data entries with weights
     */
    public static void setSpawnPotentials(DungeonRoom room, BaseSpawner spawner, SimpleWeightedRandomList<SpawnData> weightedSpawnData) {
        // Create NBT compound to hold spawner data
        CompoundTag spawnerTag = new CompoundTag();

        // Save current spawner state to NBT
        spawnerTag = spawner.save(spawnerTag);

        // Encode the spawn potentials list to NBT
        Tag spawnPotentialsTag = SpawnData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, weightedSpawnData)
                .getOrThrow(error -> new IllegalStateException("Invalid SpawnPotentials: " + error));

        // Update the tag with our new spawn potentials
        spawnerTag.put("SpawnPotentials", spawnPotentialsTag);
        spawnerTag.putShort("MinSpawnDelay", (short) (200 / room.getDifficulty()));
        spawnerTag.putShort("MaxSpawnDelay", (short) (800 / room.getDifficulty()));
        spawnerTag.putShort("SpawnCount", (short) 1);
        spawnerTag.putShort("MaxNearbyEntities", (short) 6);
        spawnerTag.putShort("RequiredPlayerRange", (short) 16);

        // Load the modified data back into the spawner
        AtomicReference<BlockPos> pos = new AtomicReference<>(BlockPos.ZERO);
        AtomicReference<Level> level = new AtomicReference<>();
        spawner.getOwner().ifLeft((blockEntity -> {
            pos.set(blockEntity.getBlockPos());
            level.set(blockEntity.getLevel());
        })).ifRight((entity -> {
            pos.set(entity.blockPosition());
            level.set(entity.level());
        }));
        spawner.load(level.get(), pos.get(), spawnerTag);
        EntityType<?> entityType = EntityType.by(weightedSpawnData.getRandom(level.get().getRandom()).get().data().entityToSpawn()).orElse(EntityType.ZOMBIE);
        spawner.setEntityId(entityType,level.get(), level.get().getRandom(), pos.get());
    }

    public static SimpleWeightedRandomList<SpawnData> createSpawnDataWeightedList(WeightedPool<DungeonRegistration.TargetTemplate> pool){
        SimpleWeightedRandomList.Builder<SpawnData> builder = SimpleWeightedRandomList.builder();

        for (Pair<DungeonRegistration.TargetTemplate, Integer> entry : pool.getAllWithWeights()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", entry.getFirst().getEntityTypeString());
            SpawnData spawnData = new SpawnData(tag, Optional.of(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15),new InclusiveRange<>(0, 15))), Optional.empty());
            builder.add(spawnData, entry.getSecond());
        }

        return builder.build();
    }

    /**
     * Instantiates a class of the given type using its fully-qualified name.
     *
     * @param className Fully-qualified class name (e.g. "com.yourmod.projectile.LightningArrow")
     * @param type The expected type (interface or superclass) to cast to
     * @return A new instance of the class, cast to the specified type
     * @throws Exception If class not found, type mismatch, or instantiation error
     */
    public static <T> T instantiate(String className, Class<T> type) throws Exception {
        Class<?> clazz = Class.forName(className);
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + className + " is not a subtype of " + type.getName());
        }

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return type.cast(constructor.newInstance());
    }
}
