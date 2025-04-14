package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.nbt.CompoundTag;
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
    public static int getHueOffset(Offering.CostType type) {
        return switch(type) {
            case OVERWORLD, ITEM -> 0xFFFFFFFF;
            case NETHER -> NETHER_HUE_OFFSET;
            case END -> END_HUE_OFFSET;
        };
    }

    public EssenceOrb(EntityType<? extends ExperienceOrb> entityType, Level level) {
        super(entityType, level);
    }

    public EssenceOrb(Level level, double x, double y, double z, int value, Type type) {
        this(WDEntities.ESSENCE_ORB.get(), level);
        this.setPos(x, y, z);
        this.setYRot((float)(this.random.nextDouble() * (double)360.0F));
        this.setDeltaMovement((this.random.nextDouble() * (double)0.2F - (double)0.1F) * (double)2.0F, this.random.nextDouble() * 0.2 * (double)2.0F, (this.random.nextDouble() * (double)0.2F - (double)0.1F) * (double)2.0F);
        this.value = value;
        this.essence_type = type;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(essence_type.toString());
        buffer.writeInt(this.value);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.essence_type = Type.valueOf(additionalData.readUtf());
        this.value = additionalData.readInt();
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
                level.addFreshEntity(new EssenceOrb(level, pos.x, pos.y, pos.z, i, type));
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel level, Vec3 pos, int amount, Type type) {
        AABB aabb = AABB.ofSize(pos, 1.0, 1.0, 1.0);
        int i = level.getRandom().nextInt(40);
        List<EssenceOrb> list = level.getEntities(EntityTypeTest.forClass(EssenceOrb.class), aabb, orb -> canMerge(orb, i, amount, type));
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

    private static boolean canMerge(EssenceOrb orb, int rand, int other, Type type) {
        boolean removedCondition = !orb.isRemoved();
        boolean typeCondition = type.equals(orb.essence_type);
        boolean randCondition = (orb.getId() - rand) % 40 == 0;
        boolean valueCondition = orb.value == other;
        return removedCondition && typeCondition && randCondition && valueCondition;
    }

    public static void giveEssence(ServerPlayer player, Type type, int value) {
        WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(player);
        wdPlayer.giveEssencePoints(type, value);
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ClientPacketHandler.Packets.UPDATE_WD_PLAYER.toString());
        tag.put("player", Serializer.toCompoundTag(wdPlayer));
        PacketDistributor.sendToPlayer(player, new SimplePacketManager.ClientboundTagPacket(tag));
    }
}

