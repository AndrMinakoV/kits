package com.neoxygen.neokits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.neoxygen.neokits.cooldowns.CooldownManager;
import com.neoxygen.neokits.utilities.Data;
import com.neoxygen.neokits.utilities.Item;
import com.neoxygen.neokits.utilities.Kit;
import com.neoxygen.neokits.utilities.MessageFunctions;
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

import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mod("neokits") // Убедитесь, что здесь указан правильный modid
public class KitMod {
    // Если у вас есть другие переменные и методы, они должны быть здесь
    public static final String MODID = "neokits";
    @Mod.EventBusSubscriber(modid = KitMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommandRegistry {
//        public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//            dispatcher.register(Commands.literal("kit")
//                    .then(Commands.literal("claim")
//                            .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
//                                Data.getAllKitsName().forEach(builder::suggest);
//                                return builder.buildFuture();
//                            }))
//                            .executes(context ->  handleKit(context.getSource(), StringArgumentType.getString(context, "kitName"))))
//                    .then(Commands.literal("create")
//                            .then(Commands.argument("kitName", StringArgumentType.word()))
//                            .then(Commands.argument("cooldown", StringArgumentType.word()).suggests(COOLDOWN_SUGGESTIONS))
//                            .executes(context -> {
//                                String kitName = StringArgumentType.getString(context, "kitName");
//                                String cooldown = StringArgumentType.getString(context, "cooldown");
//                                return kitCreate(context.getSource(), kitName, cooldown);
//                            }))
//                    .then(Commands.literal("delete")
//                            .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
//                                Data.getAllKitsName().forEach(builder::suggest);
//                                return builder.buildFuture();
//                            }))
//                            .executes(context -> kitDelete(context.getSource(), StringArgumentType.getString(context, "kitName"))
//                            ))
//            );
//        }
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            //register(event.getDispatcher());
//            event.getDispatcher().register(Commands.literal("kit")
//                    .then(Commands.literal("claim")
//                            .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
//                                System.out.println("DASSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
//                                Data.getAllKitsName().forEach(builder::suggest);
//                                return builder.buildFuture();
//                            }))
//                            .executes(context ->  handleKit(context.getSource(), StringArgumentType.getString(context, "kitName"))))
//                    .then(Commands.literal("create")
//                            .then(Commands.argument("kitName", StringArgumentType.word()))
//                            .then(Commands.argument("cooldown", StringArgumentType.word()).suggests(COOLDOWN_SUGGESTIONS))
//                            .executes(context -> {
//                                String kitName = StringArgumentType.getString(context, "kitName");
//                                String cooldown = StringArgumentType.getString(context, "cooldown");
//                                return kitCreate(context.getSource(), kitName, cooldown);
//                            }))
//                    .then(Commands.literal("delete")
//                            .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
//                                Data.getAllKitsName().forEach(builder::suggest);
//                                return builder.buildFuture();
//                            }))
//                            .executes(context -> kitDelete(context.getSource(), StringArgumentType.getString(context, "kitName"))
//                            ))
//            );
            event.getDispatcher().register(Commands.literal("deletekit")
                    .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
                        Data.getAllKitsName().forEach(builder::suggest);
                        return builder.buildFuture();
                        })
                            .executes(context -> kitDelete(context.getSource(), StringArgumentType.getString(context, "kitName")))));
            // Создание китов
            event.getDispatcher().register(Commands.literal("createkit")
                    .then(Commands.argument("kitName", StringArgumentType.word())
                            .then(Commands.argument("cooldown", StringArgumentType.word()).suggests(COOLDOWN_SUGGESTIONS)
                            .executes(context -> {
                                String kitName = StringArgumentType.getString(context, "kitName");
                                String cooldown = StringArgumentType.getString(context, "cooldown");
                                return kitCreate(context.getSource(), kitName, cooldown);
                            }))));
            // Получение китов
            event.getDispatcher().register(Commands.literal("kit")
                    .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
                        Data.getAllKitsName().forEach(builder::suggest);
                        return builder.buildFuture();
                        })
                            .executes(context -> handleKit(context.getSource(), StringArgumentType.getString(context, "kitName")))));


        }

        private static final SuggestionProvider<CommandSourceStack> COOLDOWN_SUGGESTIONS
                = (context, builder) -> getSuggestions(builder);
        private static CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
            String input = builder.getRemaining().toLowerCase();
            if (input.matches("\\d+")) {
                builder.suggest(input + "d");
                builder.suggest(input + "h");
            }
            return builder.buildFuture();
        }
        private static int handleKit(CommandSourceStack source, String argument) {
            if (source.getEntity() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) source.getEntity();
                if (CooldownManager.isOnCooldown(player.getName().getString(), argument)){
                    String message = "Вы не можете использовать этот кит. Следующее использование будет доступно через " + parseRemainCooldown(CooldownManager.getRemainCooldwon(player.getName().getString(), argument));
                    MessageFunctions.broadcastMcSkillMessage(player, message);
                    return 0;
                }
                for(Kit kit : Data.getData().getKits()){
                    if (kit.getName().equals(argument)){
                        for (Item items : kit.getItems()){
                            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.getItem())), items.getCount()));
                        }
                        CooldownManager.updateOrAddCooldown(player.getName().getString(), argument, kit.getCooldown());
                        String message = "Вы получили кит " + "§a " + argument;
                        MessageFunctions.broadcastMcSkillMessage(player, message);
                    }
                }
                return 1;
            }
            return 0;

        }
        private static int kitDelete(CommandSourceStack source, String argument){
            if (source.getEntity() instanceof ServerPlayer){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                ServerPlayer player = (ServerPlayer) source.getEntity();
                Data.getData().getKits().removeIf(kit -> kit.getName().equals(argument));
                try(FileWriter writer = new FileWriter("config/kits.json")){
                    gson.toJson(Data.getData(), writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String message = "Вы успешно удалили кит " + "§a " + argument;
                MessageFunctions.broadcastMcSkillMessage(player, message);
                return 1;
            }
            return 0;
        }
        private static int kitCreate(CommandSourceStack source, String argument, String cooldown){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ServerPlayer player = (ServerPlayer) source.getEntity();
            Kit newKit = new Kit();
            newKit.setCooldown(parseCooldown(cooldown));
            newKit.setName(argument);
            List<Item> itemList = new ArrayList<>();
            assert player != null;
            for (ItemStack itemStack : player.getInventory().items)
            {
                if (!itemStack.isEmpty())
                {
                    Item item = new Item();
                    ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
                    assert itemId != null;
                    System.out.println(itemId.toString());
                    item.setItem(itemId.toString());
                    item.setCount(itemStack.getCount());
                    itemList.add(item);
                }
            }
            newKit.setItems(itemList);
            Data.getData().addKit(newKit);
            String message = "Вы успешно создали кит " + "§a " + argument;
            MessageFunctions.broadcastMcSkillMessage(player, message);
            try(FileWriter writer = new FileWriter("config/kits.json")){
                gson.toJson(Data.getData(), writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }

        private static long parseCooldown(String cooldown){
            int i = Integer.parseInt(cooldown.substring(0, cooldown.length() - 1));
            if (cooldown.endsWith("d")){
                return (long) i * 24 * 60 * 60;
            } else if(cooldown.endsWith("h")){
                return (long) i * 60 * 60;
            }
            throw new IllegalArgumentException("Неверный формат");
        }

        private static String parseRemainCooldown(long seconds){
            int day;
            long hours, minutes, second;
            day = (int) TimeUnit.SECONDS.toDays(seconds);
            hours =  TimeUnit.SECONDS.toHours(seconds) - (day*24);
            minutes = TimeUnit.SECONDS.toMinutes(seconds) - (hours * 60);
            second = TimeUnit.SECONDS.toSeconds(seconds) - (minutes * 60);
//            hours = (seconds % 86400) / 3600;
//            minutes = ((seconds % 86400) % 3600) / 60;
//            second = ((seconds % 86400) % 3600) % 60;
            return "§6" + (day + "д. " + hours + "ч. " + minutes + "м. " + second + "c. ");
        }
    }
}
