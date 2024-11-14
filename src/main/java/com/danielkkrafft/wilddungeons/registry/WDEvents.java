package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.Dungeon;
import com.danielkkrafft.wilddungeons.dungeon.Dungeons;
import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public class WDEvents {

    @SubscribeEvent
    public static void onXP(PlayerXpEvent.PickupXp event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (!(event.getOrb() instanceof EssenceOrb)) {
                WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateWDPlayer(serverPlayer.getStringUUID());
                wdPlayer.setRecentEssence("essence:overworld");
            }
        }
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        FileUtil.setWorldPath(event.getServer().getWorldPath(LevelResource.ROOT));
        FileUtil.SaveFile.load();

    }

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        Dungeons.setupDungeons(event.getServer());
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (event.getLevel().isClientSide() || !event.getLevel().registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).get(BuiltinDimensionTypes.OVERWORLD).equals(event.getLevel().dimensionType()))
            return;

        FileUtil.SaveFile.save();

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

}
