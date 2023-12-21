package com.neoxygen.neokits;

import com.google.gson.Gson;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
                    .then(Commands.literal("penis")
                            .executes(context -> kitPenis(context.getSource()))));
        }

        private static int kitPenis(CommandSourceStack source) {
            if (source.getEntity() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) source.getEntity();
                Gson g = new Gson();
                try {
                    String jsonString = new String(Files.readAllBytes(Paths.get("config/kits.json")));
                    if (!jsonString.isEmpty()){
                        source.sendFailure(Component.literal(jsonString));
                    }
                        KitsContainer kitsContainer = g.fromJson(jsonString, KitsContainer.class);
                        for(Kit kit : kitsContainer.kits){
                            source.sendSuccess(Component.literal("Good kitsList"), true);
                            if (kit.name.equals("start")){
                                source.sendSuccess(Component.literal("Good check if == name"), true);
                                for (Item items : kit.items){
                                    source.sendSuccess(Component.literal("Good Items List"), true);
                                    player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.item)), items.count));
                                }
                            }
                        }

                } catch (IOException e) {
                    source.sendFailure(Component.literal("Bad directory"));
                    throw new RuntimeException(e);
                }

            }
            return 1;
        }


    }
}
