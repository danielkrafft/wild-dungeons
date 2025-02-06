package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.entity.boss.BreezeGolem;
import com.danielkkrafft.wilddungeons.entity.boss.MutantBogged;
import com.danielkkrafft.wilddungeons.network.clientbound.ClientboundUpdateWDPlayerPacket;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.util.SaveSystem;
import com.danielkkrafft.wilddungeons.util.Serializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
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
                wdPlayer.setRecentEssence("essence:overworld");
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

        if (event.getEntity() instanceof Blaze) {
            EssenceOrb.award((ServerLevel) event.getEntity().level(), event.getEntity().position(), "nether", 5);
        }

        if (event.getEntity() instanceof EnderMan) {
            EssenceOrb.award((ServerLevel) event.getEntity().level(), event.getEntity().position(), "end", 5);
        }
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

}
