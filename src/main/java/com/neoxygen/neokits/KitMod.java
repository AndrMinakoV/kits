package com.neoxygen.neokits;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.neoxygen.neokits.utilities.MessageFunctions;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import org.json.JSONObject;
import java.io.IOException;

import java.awt.*;
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
                ItemStack itemStack = new ItemStack(Items.STONE, 64); // 64 камня

                if (player.getInventory().add(itemStack)) {
                    try {
                        // Путь к файлу JSON в папке mods
                        Path pathToJson = Paths.get("mods/config/123.json");

                        // Чтение файла
                        String jsonContent = new String(Files.readAllBytes(pathToJson), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(jsonContent);

                        // Получаем значение из JSON
                        String item = jsonObject.getString("item");

                        // ... (остальная часть кода)
                    } catch (Exception e) {
                        // Обработка исключений
                        source.sendFailure(Component.literal("§cПроизошла ошибка при чтении файла конфигурации."));
                        e.printStackTrace();
                    }
                } else {
                    source.sendFailure(Component.literal("§cНедостаточно места в инвентаре для камней."));
                }
            } else {
                source.sendFailure(Component.literal("§cЭту команду может использовать только игрок."));
            }
            return 1;
        }


    }
}
