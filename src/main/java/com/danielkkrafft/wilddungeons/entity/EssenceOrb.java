package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
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
    public enum Type { OVERWORLD, NETHER, END }
    public Type essence_type = Type.NETHER;

    public static final ResourceLocation NETHER_ESSENCE_BAR = WildDungeons.rl("hud/nether_essence_bar_background");
    public static final ResourceLocation END_ESSENCE_BAR = WildDungeons.rl("hud/end_essence_bar_background");
    public static ResourceLocation getBarBackground(Type type) {
        return switch(type) {
            case OVERWORLD -> null;
            case NETHER -> NETHER_ESSENCE_BAR;
            case END -> END_ESSENCE_BAR;
        };
    }

    public static final ResourceLocation NETHER_ESSENCE_PROGRESS = WildDungeons.rl("hud/nether_essence_bar_progress");
    public static final ResourceLocation END_ESSENCE_PROGRESS = WildDungeons.rl("hud/end_essence_bar_progress");
    public static ResourceLocation getBarProgress(Type type) {
        return switch(type) {
            case OVERWORLD -> null;
            case NETHER -> NETHER_ESSENCE_PROGRESS;
            case END -> END_ESSENCE_PROGRESS;
        };
    }

    public static final int NETHER_FONT_COLOR = 0xfa4b4b;
    public static final int END_FONT_COLOR = 0xfa4bda;
    public static int getFontColor(Type type) {
        return switch(type) {
            case OVERWORLD -> 0xFFFFFFFF;
            case NETHER -> NETHER_FONT_COLOR;
            case END -> END_FONT_COLOR;
        };
    }

    public static final int NETHER_HUE_OFFSET = 270;
    public static final int END_HUE_OFFSET = 135;
    public static int getHueOffset(Type type) {
        return switch(type) {
            case OVERWORLD -> 0xFFFFFFFF;
            case NETHER -> NETHER_HUE_OFFSET;
            case END -> END_HUE_OFFSET;
        };
    }

    public EssenceOrb(EntityType<? extends ExperienceOrb> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(essence_type.toString());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.essence_type = Type.valueOf(additionalData.readUtf());
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

    public static void award(ServerLevel level, Vec3 pos, Type type, int amount) {
        while (amount > 0) {
            int i = getExperienceValue(amount);
            amount -= i;
            if (!tryMergeToExisting(level, pos, i, type)) {
                EssenceOrb orb = new EssenceOrb(WDEntities.ESSENCE_ORB.get(), level);
                orb.setPos(pos);
                orb.essence_type = type;
                orb.value = amount;
                level.addFreshEntity(orb);
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel level, Vec3 pos, int amount, Type type) {
        AABB aabb = AABB.ofSize(pos, 1.0, 1.0, 1.0);
        int i = level.getRandom().nextInt(40);
        List<EssenceOrb> list = level.getEntities(EntityTypeTest.forClass(EssenceOrb.class), aabb, p_147081_ -> canMerge(p_147081_, i, amount, type));
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

    private static boolean canMerge(EssenceOrb orb, int amount, int other, Type type) {
        return !orb.isRemoved() && type.equals(orb.essence_type) && (orb.getId() - amount) % 40 == 0 && orb.value == other;
    }

    public static void giveEssence(ServerPlayer player, Type type, int value) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(player);
        wdPlayer.giveEssencePoints(type, value);
        PacketDistributor.sendToPlayer(player, new ClientboundUpdateWDPlayerPacket(Serializer.toCompoundTag(wdPlayer)));
    }
}

