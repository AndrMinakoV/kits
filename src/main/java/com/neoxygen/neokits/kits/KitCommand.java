package com.neoxygen.neokits.kits;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;


public class KitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("kit")
                .then(Commands.argument("kitName", StringArgumentType.word())
                        .executes(KitCommand::handleCommand)));
    }
    private static int kitTest(CommandSourceStack source) throws CommandSyntaxException {
        System.out.println("KIT TEST IS WORKING");

        source.sendSuccess(Component.literal("Test"), true);
        return 1;
    }

    private static int handleCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String kitName = StringArgumentType.getString(context, "kitName");
        Kit kit = KitsManager.getKit(kitName);
        if (kit == null) {
            context.getSource().sendFailure(Component.literal("Kit not found."));
            return 0;
        }

        for (ItemStack itemStack : kit.getItems()) {
            if (!player.addItem(itemStack)) {
                player.drop(itemStack, false);
            }
        }

        context.getSource().sendSuccess(Component.literal("Given kit " + kitName + " to " + player.getName().getString()), true);
        return 1;
    }
}

