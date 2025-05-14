package com.danielkkrafft.wilddungeons.command;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class WDCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("lives").requires(src -> src.hasPermission(2))
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player == null) {
                                context.getSource().sendFailure(Component.literal("This command must be sent by a player"));
                                return 0;
                            }
                            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(player);
                            if (wdPlayer.isInsideDungeon()) {
                                int amount = IntegerArgumentType.getInteger(context, "amount");
                                wdPlayer.getCurrentDungeon().offsetLives(amount);
                                if (wdPlayer.getCurrentDungeon().getLives() <= 0) wdPlayer.getCurrentDungeon().fail();
                                context.getSource().sendSuccess(() -> Component.literal("Added " + amount + " lives"), false);
                                return 1;
                            }
                            context.getSource().sendFailure(Component.literal("This command must be sent in a dungeon"));
                            return 0;
                        }))
        );
    }

}
