package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.List;

public class EssenceOrb extends ExperienceOrb implements IEntityWithComplexSpawn {
    public String essence_type = "essence:nether";

    public EssenceOrb(EntityType<? extends ExperienceOrb> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(essence_type);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.essence_type = additionalData.readUtf();
    }

    @Override
    public void playerTouch(Player entity) {
        if (entity instanceof ServerPlayer serverplayer) {
            if (entity.takeXpDelay == 0) {
                entity.takeXpDelay = 2;
                entity.take(this, 1);
                giveEssence(serverplayer, this.essence_type, this.value);
                offsetCount(this,-1, false);
            }
        }
    }

    public static void award(ServerLevel level, Vec3 pos, String key, int amount) {
        while (amount > 0) {
            int i = getExperienceValue(amount);
            amount -= i;
            if (!tryMergeToExisting(level, pos, i, key)) {
                EssenceOrb orb = new EssenceOrb(WDEntities.ESSENCE_ORB.get(), level);
                orb.setPos(pos);
                orb.essence_type = key;
                orb.value = amount;
                level.addFreshEntity(orb);
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel level, Vec3 pos, int amount, String key) {
        AABB aabb = AABB.ofSize(pos, 1.0, 1.0, 1.0);
        int i = level.getRandom().nextInt(40);
        List<EssenceOrb> list = level.getEntities(EntityTypeTest.forClass(EssenceOrb.class), aabb, p_147081_ -> canMerge(p_147081_, i, amount, key));
        if (!list.isEmpty()) {
            EssenceOrb essenceOrb = list.get(0);
            offsetCount(essenceOrb, 1, true);
            return true;
        } else {
            return false;
        }
    }

    private static void offsetCount(EssenceOrb essenceOrb, int offset, boolean resetAge) {
        try {
            Field countField = ExperienceOrb.class.getDeclaredField("count");
            countField.setAccessible(true);
            int currentCount = countField.getInt(essenceOrb);
            countField.setInt(essenceOrb, currentCount + offset);

            if (resetAge) {
                Field ageField = ExperienceOrb.class.getDeclaredField("age");
                ageField.setAccessible(true);
                ageField.setInt(essenceOrb, 0);
            }

            // Discard the orb if `count` reaches zero
            if (currentCount - 1 == 0) {
                essenceOrb.discard();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean canMerge(EssenceOrb orb, int amount, int other, String key) {
        return !orb.isRemoved() && key.equals(orb.essence_type) && (orb.getId() - amount) % 40 == 0 && orb.value == other;
    }

    public static void giveEssence(ServerPlayer player, String type, int value) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(player);
        //TODO confusion about whether "essence:type" or "type" should be the common key
        String key = "essence:" + type;
        wdPlayer.giveEssencePoints(key, value);

        PacketDistributor.sendToPlayer(player, new ClientboundUpdateWDPlayerPacket(wdPlayer.toCompoundTag()));
    }
}

