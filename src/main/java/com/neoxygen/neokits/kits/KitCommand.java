package com.neoxygen.neokits.kits;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;


public class KitCommand {
    public KitCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("kit")
                .then(Commands.argument("kitName", StringArgumentType.word()).executes(context -> handleCommand(context, StringArgumentType.getString(context, "kitName")))));
    }
    private static int kitTest(CommandSourceStack source) throws CommandSyntaxException {
        System.out.println("KIT TEST IS WORKING");
        ServerPlayer serverPlayer = source.getPlayerOrException();
        serverPlayer.getInventory().add(new ItemStack(Items.ACACIA_BOAT));
        source.sendSuccess(Component.literal("Test"), true);
        return 1;
    }

    private static int handleCommand(CommandContext<CommandSourceStack> context, String kitName) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Kit kit = KitsManager.getKit(kitName);
        if (kit == null) {
            context.getSource().sendFailure(Component.literal("Kit not found."));
            return 0;
        }

        for (ItemStack itemStack : kit.getItems()) {
            if (!player.getInventory().add(itemStack.copy())) {
                player.drop(itemStack.copy(), false);
            }
        }

        context.getSource().sendSuccess(Component.literal("Given kit " + kitName + " to " + player.getName().getString()), true);
        return 1;
    }



}

