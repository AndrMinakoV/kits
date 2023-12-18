package com.neoxygen.neokits.kits;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.neoxygen.neokits.Kit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class KitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("kit")
                .then(Commands.argument("kitName", StringArgumentType.word())
                        .executes(context -> giveKit(context.getSource(),
                                StringArgumentType.getString(context, "kitName"),
                                context.getSource().getPlayerOrException()))));
    }

    private static int giveKit(CommandSourceStack source, String kitName, ServerPlayer player) {
        Kit kit = KitsManager.getKit(kitName);
        if (kit == null) {
            source.sendFailure(Component.literal("Kit not found."));
            return 0;
        }

        for (ItemStack itemStack : kit.getItems()) {
            if (!player.getInventory().add(itemStack)) {
                player.drop(itemStack, false);
            }
        }

        source.sendSuccess(Component.literal("Given kit " + kitName + " to " + player.getName().getString()), true);
        return 1;
    }
}

