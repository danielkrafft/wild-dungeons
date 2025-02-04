package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonPoolRegistry;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundLoadingScreenPacket;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.danielkkrafft.wilddungeons.dungeon.registries.PerkRegistry.DUNGEON_PERK_REGISTRY;

public class Offering extends Entity implements IEntityWithComplexSpawn {

    public static final int BUBBLE_ANIMATION_TIME = 10;

    public enum Type {ITEM, PERK, RIFT}
    public enum CostType {XP_LEVEL, NETHER_XP_LEVEL, END_XP_LEVEL}

    private String type;
    private String costType;
    private String purchaseBehavior;
    private String offerID;
    private int amount;
    private int costAmount;
    private boolean purchased = false;
    private float bubbleTimer = BUBBLE_ANIMATION_TIME;

    public Offering(EntityType<Offering> entityType, Level level) {
        super(entityType, level);
    }

    public Type getOfferingType() {return Type.valueOf(this.type);}
    public CostType getOfferingCostType() {return CostType.valueOf(this.costType);}
    public String getOfferingId() {return this.offerID;}
    public void setOfferingId(String offerID) {this.offerID = offerID;}
    public int getAmount() {return this.amount;}
    public int getCostAmount() {return this.costAmount;}
    public boolean isPurchased() {return this.purchased;}
    public float getBubbleTimer() {return this.bubbleTimer;}
    public void setBubbleTimer(float time) {this.bubbleTimer = time;}
    public void overrideCost(int cost) {this.costAmount = cost;}
    public Offering(Level level) {super(WDEntities.OFFERING.get(), level);}


    public Offering(Level level, Type type, int amount, String offerID, CostType costType, int costAmount) {
        super(WDEntities.OFFERING.get(), level);
        this.type = type.toString();
        this.amount = amount;
        this.offerID = offerID;
        this.costType = costType.toString();
        this.costAmount = costAmount;
    }

    private ItemStack itemStack = null;

    public ItemStack getItemStack() {
        if (this.itemStack == null) {
            WildDungeons.getLogger().info("Getting itemstack of ID: {}", this.offerID);
            itemStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.withDefaultNamespace(this.offerID))), this.amount);
        }
        return itemStack;
    }

    private DungeonPerkTemplate perk = null;

    public DungeonPerkTemplate getPerk() {
        if (this.perk == null) {
            perk = DUNGEON_PERK_REGISTRY.get(this.offerID);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
//        WildDungeons.getLogger().info("ADDING ADDITIONAL SAVE DATA");
        compound.putString("type", this.type);
        compound.putString("costType", this.costType);
        compound.putString("offerID", this.offerID);
        compound.putInt("amount", this.amount);
        compound.putInt("costAmount", this.costAmount);
        compound.putBoolean("purchased", this.purchased);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
//        WildDungeons.getLogger().info("READING ADDITIONAL SAVE DATA");
        if (compound.getString("type").isEmpty()) {
            this.type = Type.ITEM.toString();
            this.costType = CostType.XP_LEVEL.toString();
            this.offerID = "dirt";
            this.amount = 1;
            this.costAmount = 0;
            this.purchased = false;
        } else {
            this.type = compound.getString("type");
            this.costType = compound.getString("costType");
            this.offerID = compound.getString("offerID");
            this.amount = compound.getInt("amount");
            this.costAmount = compound.getInt("costAmount");
            this.purchased = compound.getBoolean("purchased");
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        WildDungeons.getLogger().info("WRITING SPAWN DATA");
        buffer.writeUtf(this.type);
        buffer.writeUtf(this.costType);
        buffer.writeUtf(this.offerID);
        buffer.writeInt(this.amount);
        buffer.writeInt(this.costAmount);
        buffer.writeBoolean(this.purchased);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        WildDungeons.getLogger().info("READING SPAWN DATA");
        this.type = buffer.readUtf();
        this.costType = buffer.readUtf();
        this.offerID = buffer.readUtf();
        this.amount = buffer.readInt();
        this.costAmount = buffer.readInt();
        this.purchased = buffer.readBoolean();
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (player instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer);
            attemptPurchase(wdPlayer);
        }
    }

    public void attemptPurchase(WDPlayer player) {
        if (!purchased) {
            int levels = switch (this.getOfferingCostType()) {
                case XP_LEVEL -> player.getServerPlayer().experienceLevel;
                case NETHER_XP_LEVEL -> Mth.floor(player.getEssenceLevel("essence:nether"));
                case END_XP_LEVEL -> Mth.floor(player.getEssenceLevel("essence:end"));
            };

            if (this.costAmount == 0 || this.costAmount <= levels) {
                this.purchased = true;

                switch (this.getOfferingCostType()) {
                    case XP_LEVEL -> player.getServerPlayer().giveExperienceLevels(-this.costAmount);
                    case NETHER_XP_LEVEL -> player.giveEssenceLevels(-this.costAmount, "essence:nether");
                    case END_XP_LEVEL -> player.giveEssenceLevels(-this.costAmount, "essence:end");
                }

                this.costAmount = 0;

                if (player.getCurrentRoom() != null) {
                    player.getCurrentRoom().getOfferingUUIDs().remove(this.getStringUUID());
                }

                if (this.getOfferingType() == Type.ITEM) {
                    this.remove(RemovalReason.DISCARDED);
                    player.getServerPlayer().addItem(this.getItemStack());
                }

                if (this.getOfferingType() == Type.PERK) {
                    this.remove(RemovalReason.DISCARDED);
                    player.getCurrentDungeon().givePerk(this.getPerk());
                }
            }
        }

        if (this.getOfferingType() == Type.RIFT && purchased) {
            handleRift(player);
        }

    }

    public void handleRift(WDPlayer wdPlayer) {
        if (wdPlayer.getRiftCooldown() > 0 || this.offerID == null) {
            return;
        }

        switch (this.offerID) {

            case "-1" -> {
                WildDungeons.getLogger().info("TRYING TO LEAVE {} WITH PLAYER {}", wdPlayer.getCurrentDungeon(), wdPlayer);

                DungeonSession dungeon = wdPlayer.getCurrentDungeon();
                dungeon.onExit(wdPlayer);
            }

            case "win" -> {
                WildDungeons.getLogger().info("TRYING TO WIN {}", wdPlayer.getCurrentDungeon());

                DungeonSession dungeon = wdPlayer.getCurrentDungeon();
                dungeon.win();
            }

            case "random" -> {
                DungeonTemplate dungeonTemplate = DungeonPoolRegistry.TEST_DUNGEON_POOL.getRandom();
                WildDungeons.getLogger().info("TRYING TO ENTER {}", dungeonTemplate.name());

                DungeonSession dungeon = DungeonSessionManager.getInstance().getOrCreateDungeonSession(this.getStringUUID(), this.level().dimension(), dungeonTemplate.name());
                wdPlayer.setLastGameMode(wdPlayer.getServerPlayer().gameMode.getGameModeForPlayer());
                PacketDistributor.sendToPlayer(wdPlayer.getServerPlayer(), new ClientboundLoadingScreenPacket(new CompoundTag()));
                wdPlayer.getServerPlayer().setGameMode(GameType.SPECTATOR);
                dungeon.onEnter(wdPlayer);
                wdPlayer.setRiftCooldown(100);
            }

            default -> {
                if (offerID.split("-")[0].equals("wd")) {

                    DungeonTemplate dungeonTemplate = DungeonRegistry.DUNGEON_REGISTRY.get(offerID.split("-")[1]);
                    WildDungeons.getLogger().info("TRYING TO ENTER {}", dungeonTemplate.name());

                    if (dungeonTemplate != null) {
                        DungeonSession dungeon = DungeonSessionManager.getInstance().getOrCreateDungeonSession(this.getStringUUID(), this.level().dimension(), dungeonTemplate.name());
                        wdPlayer.setLastGameMode(wdPlayer.getServerPlayer().gameMode.getGameModeForPlayer());
                        PacketDistributor.sendToPlayer(wdPlayer.getServerPlayer(), new ClientboundLoadingScreenPacket(new CompoundTag()));
                        wdPlayer.getServerPlayer().setGameMode(GameType.SPECTATOR);
                        dungeon.onEnter(wdPlayer);
                        wdPlayer.setRiftCooldown(100);
                    }

                } else {
                    purchased = true;//prevent spamming the rift
                    DungeonSession dungeon = wdPlayer.getCurrentDungeon();
                    while (dungeon.getFloors().size() <= Integer.parseInt(this.offerID))
                        dungeon.generateFloor(dungeon.getFloors().size(), (v) -> {
                            WildDungeons.getLogger().info("TRYING TO ENTER FLOOR: {}", Integer.parseInt(this.offerID));
                            DungeonFloor newFloor = dungeon.getFloors().get(Integer.parseInt(this.offerID));
                            wdPlayer.setLastGameMode(wdPlayer.getServerPlayer().gameMode.getGameModeForPlayer());
                            PacketDistributor.sendToPlayer(wdPlayer.getServerPlayer(), new ClientboundLoadingScreenPacket(new CompoundTag()));
                            wdPlayer.getServerPlayer().setGameMode(GameType.SPECTATOR);
                            newFloor.onEnter(wdPlayer);
                        });
                }
            }
        }

    }
}