package com.neoxygen.neokits;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.neoxygen.neokits.utilities.Data;
import com.neoxygen.neokits.utilities.KitsContainer;
import com.neoxygen.neokits.utilities.Item;
import com.neoxygen.neokits.utilities.Kit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

@Mod("neokits") // Убедитесь, что здесь указан правильный modid
public class KitMod {
    // Если у вас есть другие переменные и методы, они должны быть здесь
    public static final String MODID = "neokits";
    @Mod.EventBusSubscriber(modid = KitMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommandRegistry {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            // Регистрируем новую команду
            event.getDispatcher().register(Commands.literal("kit")
                    .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
                            Data.getAllKitsName().forEach(builder::suggest);
                            return builder.buildFuture();
                            })
                            .executes(context -> kitPenis(context.getSource(), StringArgumentType.getString(context, "kitName")))));
        }
        private static int kitPenis(CommandSourceStack source, String argument) {
            if (source.getEntity() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) source.getEntity();
                for(Kit kit : Data.getData().kits){
                    if (kit.name.equals(argument)){
                        for (Item items : kit.items){
                            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.item)), items.count));
                        }
                    }
                }
            }
            return 1;
        }

        private static int kitCreate(){
            Data newData = new Data();
            return 1;
        }
    }

}
