package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class CommandUtil {

    public static int executeCommand(Level level, String command) {

        MinecraftServer server = level.getServer();

        if (server == null)
            return 0;

        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, server.createCommandSourceStack().withSuppressedOutput());

        System.out.println("Performing command: "+command);

        server.getCommands().performCommand(parseResults, command);
        return 1;
    }

    public static void executeTeleportCommand(ServerPlayer player, SavedTransform transform) {
        if (player == null) return;

        String command;
        String targetDim = transform.getDimension().location().toString();
        double targetX = transform.getX();
        double targetY = transform.getY();
        double targetZ = transform.getZ();
        double targetYaw = transform.getYaw();
        double targetPitch = transform.getPitch();

        command = "execute in "+targetDim+" run tp "+player.getName().getString()+" "+targetX+" "+targetY+" "+targetZ+" "+targetYaw+" "+targetPitch;
        executeCommand(player.level(), command);
    }

}