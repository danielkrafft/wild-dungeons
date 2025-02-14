package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.room.TargetPurgeRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.SaveSystem;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class WDEvents {

    @SubscribeEvent
    public static void onXP(PlayerXpEvent.PickupXp event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (!(event.getOrb() instanceof EssenceOrb)) {
                WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
                wdPlayer.setRecentEssence(EssenceOrb.Type.OVERWORLD);
                WDPlayerManager.syncAll(List.of(wdPlayer.getUUID()));
            }
        }
    }

    private static CompletableFuture<Void> asyncLoad;

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        FileUtil.setWorldPath(event.getServer().getWorldPath(LevelResource.ROOT));
        DungeonSessionManager.getInstance().server = event.getServer();
        DungeonRegistration.setupRegistries();
        if (SaveSystem.isLoading()) return;
        WildDungeons.getLogger().info("STARTING DUNGEON FILE LOADING...");
        asyncLoad = CompletableFuture.runAsync(SaveSystem::Load);
        asyncLoad.thenAccept(v -> {
            WildDungeons.getLogger().info("DUNGEON FILE LOADING COMPLETE!");
        }).exceptionally(throwable -> {
            WildDungeons.getLogger().error("DUNGEON FILE LOADING FAILED!");
            SaveSystem.failLoading();
            throwable.printStackTrace();
            return null;
        });
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer.getStringUUID());
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundUpdateWDPlayerPacket(Serializer.toCompoundTag(wdPlayer)));
        }
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        DungeonSessionManager.getInstance().server = event.getServer();
        asyncLoad.thenAccept((v) -> DungeonSessionManager.ValidateSessions());
    }

    @SubscribeEvent
    public static void onServerShutdown(ServerStoppingEvent event) {
        DungeonSessionManager.onShutdown();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        WDPlayerManager.getInstance().getServerPlayers().forEach((key, value) -> value.tick());
        DungeonSessionManager.tick();
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (event.getLevel().isClientSide()
                || !Objects.equals(event.getLevel().registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).get(WDDimensions.WILDDUNGEON), event.getLevel().dimensionType()))
            return;
        SaveSystem.Save();
    }

    @SubscribeEvent
    public static void onLivingDropExperience(LivingExperienceDropEvent event) {
        if (event.getEntity().level().isClientSide) {return;}

        // NETHER

        int netherAmount = 0;

        if (event.getEntity() instanceof Blaze) netherAmount = RandomUtil.randIntBetween(3, 7);
        else if (event.getEntity() instanceof Chicken) netherAmount = RandomUtil.sample(0.9) ? RandomUtil.randIntBetween(1, 2) : 0;
        else if (event.getEntity() instanceof EnderMan) netherAmount = RandomUtil.sample(0.8) ? RandomUtil.randIntBetween(2, 4) : 0;
        else if (event.getEntity() instanceof Ghast) netherAmount = RandomUtil.randIntBetween(8, 15);
        else if (event.getEntity() instanceof Hoglin) netherAmount = RandomUtil.randIntBetween(4, 8);
        else if (event.getEntity() instanceof MagmaCube) netherAmount = RandomUtil.randIntBetween(1, 5);
        else if (event.getEntity() instanceof Piglin) netherAmount = RandomUtil.randIntBetween(2, 5);
        else if (event.getEntity() instanceof PiglinBrute) netherAmount = RandomUtil.randIntBetween(4, 10);
        else if (event.getEntity() instanceof Skeleton) netherAmount = RandomUtil.sample(0.9) ? RandomUtil.randIntBetween(1, 3) : 0;
        else if (event.getEntity() instanceof Pig) netherAmount = RandomUtil.sample(0.95) ? RandomUtil.randIntBetween(1, 10) : 0;
        else if (event.getEntity() instanceof Strider) netherAmount = RandomUtil.randIntBetween(3, 7);
        else if (event.getEntity() instanceof WitherSkeleton) netherAmount = RandomUtil.randIntBetween(4, 7);
        else if (event.getEntity() instanceof ZombifiedPiglin) netherAmount = RandomUtil.randIntBetween(4, 7);
        else if (event.getEntity() instanceof WitherBoss) netherAmount = RandomUtil.randIntBetween(100, 200);

        if (netherAmount > 0) EssenceOrb.award((ServerLevel) event.getEntity().level(), event.getEntity().position(), EssenceOrb.Type.NETHER, netherAmount);

        // END

        int endAmount = 0;

        if (event.getEntity() instanceof EnderMan) endAmount = RandomUtil.randIntBetween(3, 7);
        else if (event.getEntity() instanceof Endermite) endAmount = RandomUtil.randIntBetween(2, 4);
        else if (event.getEntity() instanceof EnderDragon) endAmount = RandomUtil.randIntBetween(300, 2000);
        else if (event.getEntity() instanceof Shulker) endAmount = RandomUtil.randIntBetween(3, 7);
        else if (event.getEntity() instanceof WitherBoss) endAmount = RandomUtil.randIntBetween(10, 40);
        else if (event.getEntity() instanceof Phantom) endAmount = RandomUtil.sample(0.6) ? RandomUtil.randIntBetween(1, 7) : 0;
        else if (event.getEntity() instanceof Silverfish) endAmount = RandomUtil.sample(0.9) ? RandomUtil.randIntBetween(1, 2) : 0;

        if (endAmount > 0) EssenceOrb.award((ServerLevel) event.getEntity().level(), event.getEntity().position(), EssenceOrb.Type.END, endAmount);

    }

    @SubscribeEvent
    public static void placeBlockEvent(BlockEvent.EntityPlaceEvent e)
    {
        BlockState state=e.getPlacedBlock();
        if(state.is(Blocks.HEAVY_CORE))
        {
            BlockPos pos=e.getPos();
            Level level=(Level)e.getLevel();
            BlockPos below=pos.below();
            if(level.getBlockState(below).is(WDBlocks.HEAVY_RUNE))
            {
                BreezeGolem golem = WDEntities.BREEZE_GOLEM.get().create(level);
                if(golem!=null)
                {
                    golem.moveTo(below.getX() + 0.5,below.getY() + 0.55,below.getZ() + 0.5);
                    level.destroyBlock(pos,false);level.destroyBlock(below,false);
                    level.addFreshEntity(golem);
                }
            }
        }
    }

    @SubscribeEvent
    public static void lightningStrike(EntityStruckByLightningEvent e)
    {
        Entity en=e.getEntity();
        Level level=en.level();
        if(!level.isClientSide)
        {
            Vec3 pos=en.position();
            if(en instanceof LivingEntity li)
            {
                if(li instanceof Bogged b)
                {
                    b.remove(Entity.RemovalReason.DISCARDED);
                    MutantBogged bogged = WDEntities.MUTANT_BOGGED.get().create(level);
                    if(bogged!=null)
                    {
                        bogged.setPos(pos);
                        bogged.setXRot(en.getXRot());
                        bogged.setYRot(en.getYRot());
                        level.addFreshEntity(bogged);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getPlayer() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            WildDungeons.getLogger().info("FOUND BLOCK BREAK");
            event.setCanceled(WDPlayerManager.isProtectedBlock(serverPlayer, event.getPos(), serverLevel));
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).blocksBroken += 1;

            if (wdPlayer.getCurrentRoom() instanceof TargetPurgeRoom enemyPurgeRoom) {
                if (event.getLevel().getBlockState(event.getPos()).is(Blocks.SPAWNER)) {
                    enemyPurgeRoom.discardByBlockPos(event.getPos());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer.getStringUUID());
            if (wdPlayer.getCurrentDungeon() == null) return;
            event.setCanceled(WDPlayerManager.isProtectedBlock(serverPlayer, event.getPos(), serverLevel));
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).blocksPlaced += 1;
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(player);
            if (wdPlayer.getCurrentDungeon() == null) return;

            WildDungeons.getLogger().info("TESTING DEATH WITH DUNGEON LIVES: {}", wdPlayer.getCurrentDungeon().getLives());

            if (wdPlayer.getCurrentDungeon().getLives() > 0) {

                CriteriaTriggers.USED_TOTEM.trigger(player, new ItemStack(Items.TOTEM_OF_UNDYING));
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);

                player.setHealth(1.0f);
                player.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                player.level().broadcastEntityEvent(player, (byte)35);
                BlockPos respawnPoint;
                DungeonRoom room = wdPlayer.getCurrentRoom();

                if (room == null) {
                    DungeonFloor floor = wdPlayer.getCurrentFloor();
                    int index = 0;
                    for (DungeonBranch branch : floor.getBranches()) {
                        if (branch.hasPlayerVisited(wdPlayer.getUUID())) {
                            if (branch.getIndex() > index) {
                                index = branch.getIndex();
                            }
                        }
                    }
                    room = floor.getBranches().get(index).getRooms().getFirst();
                }

                if (room.getBranch().getIndex() == 0) {
                    respawnPoint = room.getBranch().getRooms().getFirst().getSpawnPoint(room.getBranch().getFloor().getLevel());
                } else {
                    respawnPoint = room.getBranch().getFloor().getBranches().get(room.getBranch().getIndex()-1).getRooms().getLast().getSpawnPoint(room.getBranch().getFloor().getLevel());
                }

                wdPlayer.setRiftCooldown(140);
                player.teleportTo(respawnPoint.getX(), respawnPoint.getY(), respawnPoint.getZ());
                wdPlayer.getCurrentDungeon().getStats(wdPlayer).deaths += 1;
                wdPlayer.getCurrentDungeon().offsetLives(-1);
            } else {
                wdPlayer.getCurrentDungeon().offsetLives(-1);
                wdPlayer.getCurrentDungeon().fail();
            }

            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);

            if (wdPlayer.getCurrentDungeon() == null) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).mobsKilled += 1;
            if (wdPlayer.getCurrentRoom() instanceof TargetPurgeRoom room) room.discardByUUID(event.getEntity().getStringUUID());
        }
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null || event.getSource().typeHolder().equals(DamageTypes.GENERIC_KILL)) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).damageDealt += event.getNewDamage();
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            wdPlayer.getCurrentDungeon().getStats(wdPlayer).damageTaken += event.getNewDamage();
        }
    }

}
