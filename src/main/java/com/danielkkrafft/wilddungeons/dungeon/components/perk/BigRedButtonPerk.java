package com.danielkkrafft.wilddungeons.dungeon.components.perk;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.PrimedTnt;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class BigRedButtonPerk extends DungeonPerk {
    public BigRedButtonPerk(String sessionKey) {
        super(sessionKey);
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (event.getNewDamage() < 0.5f) return;
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() == null) return;
            DungeonPerk perk = wdPlayer.getCurrentDungeon().getPerkByClass(BigRedButtonPerk.class);
            if (perk == null) return;
            DungeonRoom room = wdPlayer.getCurrentRoom();
            if (room != null) {
                for (int i = 0; i < perk.count; i++) {
                    List<BlockPos> validPoints = room.sampleSpawnablePositions(room.getBranch().getFloor().getLevel(), 2, 1);
                    BlockPos finalPos = room.calculateFurthestPoint(validPoints, 20);

                    PrimedTnt primedTnt = new PrimedTnt(serverPlayer.serverLevel(), finalPos.getX(), finalPos.getY(), finalPos.getZ(), serverPlayer);
                    serverPlayer.serverLevel().addFreshEntity(primedTnt);
                }
            }
        }
    }

}
