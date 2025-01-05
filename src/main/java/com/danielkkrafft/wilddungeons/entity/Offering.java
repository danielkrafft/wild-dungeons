package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.DungeonPerks;
import com.danielkkrafft.wilddungeons.dungeon.components.room.LootRoom;
import com.danielkkrafft.wilddungeons.entity.renderer.OfferingRenderer;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class Offering extends Entity implements IEntityWithComplexSpawn {

    public Type type = Type.ITEM;
    public int amount;
    public int id;
    public boolean purchased = false;

    public CostType costType;
    public int costAmount;

    public static final int BUBBLE_ANIMATION_TIME = 10;
    public float bubbleTimer = BUBBLE_ANIMATION_TIME;

    public enum Type {
        ITEM, PERK
    }

    public enum CostType {
        XP_LEVEL, NETHER_XP_LEVEL, END_XP_LEVEL
    }

    public Offering(EntityType<Offering> entityType, Level level) {
        super(entityType, level);
    }

    public Offering(Level level, Type type, int amount, int id, CostType costType, int costAmount) {
        super(WDEntities.OFFERING.get(), level);
        this.type = type;
        this.amount = amount;
        this.id = id;
        this.costType = costType;
        this.costAmount = costAmount;
    }

    private ItemStack itemStack = null;
    public ItemStack getItemStack() {
        if (this.itemStack == null) {
            itemStack = new ItemStack(Item.byId(this.id), this.amount);
        }
        return itemStack;
    }

    private DungeonPerks.Perks perk = null;
    public DungeonPerks.Perks getPerk() {
        if (this.perk == null) {
            perk = DungeonPerks.getById(this.id);
        }
        return perk;
    }

    public boolean isLookingAtMe(Player player) {
        Vec3 vec3 = player.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
        double length = vec31.length();
        vec31 = vec31.normalize();
        double dot = vec3.dot(vec31);
        return length < 10 && dot > 1.0 - 0.5 / length && player.hasLineOfSight(this);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("Type", String.valueOf(this.type));
        compound.putInt("Amount", this.amount);
        compound.putInt("ID", this.id);
        compound.putString("CostType", String.valueOf(this.costType));
        compound.putInt("CostAmount", this.costAmount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.type = Type.valueOf(compound.getString("Type"));
        this.amount = compound.getInt("Amount");
        this.id = compound.getInt("ID");
        this.costType = CostType.valueOf(compound.getString("CostType"));
        this.costAmount = compound.getInt("CostAmount");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(String.valueOf(this.type));
        buffer.writeInt(this.amount);
        buffer.writeInt(this.id);
        buffer.writeUtf(String.valueOf(this.costType));
        buffer.writeInt(this.costAmount);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.type = Type.valueOf(buffer.readUtf());
        this.amount = buffer.readInt();
        this.id = buffer.readInt();
        this.costType = CostType.valueOf(buffer.readUtf());
        this.costAmount = buffer.readInt();
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (player instanceof ServerPlayer serverPlayer) {
            WildDungeons.getLogger().info("TOUCHING OFFERING");
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);
            attemptPurchase(wdPlayer);
        }
    }

    public void attemptPurchase(WDPlayer player) {
        if (purchased) return;

        int levels = switch(this.costType) {
            case XP_LEVEL -> player.getServerPlayer().experienceLevel;
            case NETHER_XP_LEVEL -> Mth.floor(player.getEssenceLevel("essence:nether"));
            case END_XP_LEVEL -> Mth.floor(player.getEssenceLevel("essence:end"));
        };

        if (this.costAmount == 0 || this.costAmount <= levels) {
            this.purchased = true;

            if (this.type == Type.ITEM) {
                player.getServerPlayer().addItem(this.getItemStack());
            }

            if (this.type == Type.PERK) {
                DungeonPerk.addPerk(player.getCurrentDungeon(), this.getPerk());
            }

            switch (this.costType) {
                case XP_LEVEL -> player.getServerPlayer().giveExperienceLevels(-this.costAmount);
                case NETHER_XP_LEVEL -> player.giveEssenceLevels(-this.costAmount, "essence:nether");
                case END_XP_LEVEL -> player.giveEssenceLevels(-this.costAmount, "essence:end");
            }

            WildDungeons.getLogger().info("PURCHASED OFFERING");
            if (player.getCurrentRoom() instanceof LootRoom lootRoom) {
                WildDungeons.getLogger().info("REMOVING FROM ROOM");
                lootRoom.alive.remove(this);
            }
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public record OfferingTemplate(Type type, int amount, int id, CostType costType, int costAmount, float deviance) {

        public Offering asOffering(Level level) {
            int adjustedAmount = RandomUtil.randIntBetween((int) (amount / deviance), (int) (amount * deviance));
            int adjustedCost = RandomUtil.randIntBetween((int) (costAmount / deviance), (int) (costAmount * deviance));
            return new Offering(level, type, adjustedAmount, id, costType, adjustedCost);
        }
    }

}
