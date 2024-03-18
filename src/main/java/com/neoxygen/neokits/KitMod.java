package com.neoxygen.neokits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.neoxygen.neokits.cooldowns.CooldownManager;
import com.neoxygen.neokits.utilities.Data;
import com.neoxygen.neokits.utilities.Item;
import com.neoxygen.neokits.utilities.Kit;
import com.neoxygen.neokits.utilities.MessageFunctions;
import net.luckperms.api.LuckPerms;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;

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

        public static void register(CommandDispatcher<CommandSourceStack> source){
            LiteralCommandNode<CommandSourceStack> literalCommandNode = source.register(Commands.literal("kit")
                    .then(Commands.literal("create")
                            .then(Commands.argument("kitName", StringArgumentType.word())
                                    .then(Commands.argument("cooldown", StringArgumentType.word())
                                            .suggests(COOLDOWN_SUGGESTIONS)
                                            .executes(context -> {
                                                String kitName = StringArgumentType.getString(context, "kitName");
                                                String cooldown = StringArgumentType.getString(context, "cooldown");
                                                return kitCreate(context.getSource(), kitName, cooldown);
                                            }))))
                    .then(Commands.literal("list")
                                    .executes(context -> kitList(context.getSource())))
//                    .then(Commands.literal("list"))
//                            .executes(context -> kitList(context.getSource()))
                    .then(Commands.literal("claim")
                            .then(Commands.argument("kitName", StringArgumentType.word())
                                    .suggests(KIT_SUGGESTIONS_PROVIDER)
                                    .executes(context -> handleKit(context.getSource(), StringArgumentType.getString(context, "kitName")))))
                    .then(Commands.literal("delete")
                            .then(Commands.argument("kitName", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        Data.getAllKitsName().forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> kitDelete(context.getSource(), StringArgumentType.getString(context, "kitName")))))
                    .then(Commands.literal("reload")
                            .then(Commands.argument("kitName", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        Data.getAllKitsName().forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .then(Commands.argument("nick", StringArgumentType.word())
                                            .suggests(PLAYER_SUGGESTIONS_PROVIDER)
                                            .executes(context -> {
                                                String kitName = StringArgumentType.getString(context, "kitName");
                                                String nick = StringArgumentType.getString(context, "nick");
                                                return kitReload(context.getSource(), kitName, nick);
                                    }))))
            );
        }
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event){
            register(event.getDispatcher());
        }
        private static final SuggestionProvider<CommandSourceStack> PLAYER_SUGGESTIONS_PROVIDER
                = CommandRegistry::suggestPlayerName;
        private static final SuggestionProvider<CommandSourceStack> COOLDOWN_SUGGESTIONS
                = (context, builder) -> getSuggestionsCD(builder);
        private static final SuggestionProvider<CommandSourceStack> KIT_SUGGESTIONS_PROVIDER = (context, builder) ->
                suggestKitsBasedOnPermissions(context, builder, "command.kit.claim.");
        private static CompletableFuture<Suggestions> suggestKitsBasedOnPermissions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder, String permissionPrefix) {
            ServerPlayer player = context.getSource().getPlayer();
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUUID());
            if (user != null) {
                Data.getAllKitsName().forEach(kitName -> {
                    String permission = permissionPrefix + kitName;
                    if (user.getCachedData().getPermissionData().checkPermission(permission).asBoolean()) {
                        builder.suggest(kitName);
                    }
                });
            }
            return builder.buildFuture();
        }
        private static CompletableFuture<Suggestions> suggestPlayerName(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder){
            String input = builder.getRemaining().toLowerCase();
            if (input.isEmpty()){
                context.getSource().getServer().getPlayerList().getPlayers().forEach(serverPlayer ->
                        builder.suggest(serverPlayer.getName().getString()));
            } else {
                context.getSource().getServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
                    String playerName = serverPlayer.getName().getString();
                    if (playerName.toLowerCase().startsWith(input)){
                        builder.suggest(playerName);
                    }
                });
            }
            return builder.buildFuture();
        }
        private static CompletableFuture<Suggestions> getSuggestionsCD(SuggestionsBuilder builder) {
            String input = builder.getRemaining().toLowerCase();
            if (input.matches("\\d+")) {
                builder.suggest(input + "d");
                builder.suggest(input + "h");
            }
            return builder.buildFuture();
        }
        private static int kitReload(CommandSourceStack source, String argument, String nick){
            for (Kit kit : Data.getData().getKits()){
                if (kit.getName().equals(argument)){
                    if (CooldownManager.isOnCooldown(nick, argument)){
                        CooldownManager.updateOrAddCooldown(nick, argument, 0);
                        String message = "Вы успешно перезагрузили кит " + "§a" + argument + " §7для " + "§a" + nick;
                        MessageFunctions.broadcastMcSkillMessage(source.getPlayer(), message);
                        return 1;
                    } else {
                        if (findPlayerByName(source.getServer(), nick)){
                            String message = "Вы успешно перезагрузили кит " + "§a" + argument + " §7для " + "§a" + nick;
                            MessageFunctions.broadcastMcSkillMessage(source.getPlayer(), message);
                            return 0;
                        } else {
                            String message = "Игрока с таким ником не существует";
                            MessageFunctions.broadcastMcSkillMessage(source.getPlayer(), message);
                            return -1;
                        }
                    }
                }
            }
            String message = "Кита с таким названием не существует";
            MessageFunctions.broadcastMcSkillMessage(source.getPlayer(), message);
            return -1;
        }
        private static boolean findPlayerByName(MinecraftServer server, String name){
            for (ServerPlayer player : server.getPlayerList().getPlayers()){
                if (player.getName().getString().equalsIgnoreCase(name)){
                    return true;
                }
            }
            return false;
        }
        private static int handleKit(CommandSourceStack source, String argument) {
            if (source.getEntity() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) source.getEntity();
                User user = LuckPermsProvider.get().getUserManager().getUser(player.getUUID());
                String permission = "command.kit.claim." + argument;
                if (user != null && user.getCachedData().getPermissionData().checkPermission(permission).asBoolean()){
                    for (Kit kit : Data.getData().getKits()) {
                        if (kit.getName().equals(argument)) {
                            if (CooldownManager.isOnCooldown(player.getName().getString(), argument)) {
                                String message = "Кит будет доступен через §6" + parseRemainCooldown(CooldownManager.getRemainCooldwon(player.getName().getString(), argument));
                                MessageFunctions.broadcastMcSkillMessage(player, message);
                                return 0;
                            } else {
                                for (Kit kit1 : Data.getData().getKits()) {
                                    if (kit1.getName().equals(argument)) {
                                        for (Item items : kit.getItems()) {
                                            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.getItem())), items.getCount()));
                                        }
                                        CooldownManager.updateOrAddCooldown(player.getName().getString(), argument, kit.getCooldown());
                                        String message = "Вы получили кит " + "§a" + argument;
                                        MessageFunctions.broadcastMcSkillMessage(player, message);
                                        return 1;
                                    }
                                }
                            }
                        }
                    }
                }
                String message = "У вас нет прав на использование данного кита";
                MessageFunctions.broadcastMcSkillMessage(player, message);
                return -1;
            } else {
                System.out.println("You are dolbaeb? Комманду выполнять может только игрок");
            }
            return -2;
        }
        private static int kitDelete(CommandSourceStack source, String argument){
            if (source.getEntity() instanceof ServerPlayer){
                String message;
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                ServerPlayer player = (ServerPlayer) source.getEntity();
                if (Data.getData().getKits().removeIf(kit -> kit.getName().equals(argument))){
                    message = "Вы успешно удалили кит " + "§a" + argument;
                    try(FileWriter writer = new FileWriter("config/kits.json")){
                        gson.toJson(Data.getData(), writer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    MessageFunctions.broadcastMcSkillMessage(player, message);
                    //Data.loadData();
                    return 1;
                } else {
                    message = "Такого кита не существует";
                    MessageFunctions.broadcastMcSkillMessage(player, message);
                    return -1;
                }
            } else {
                System.out.println("You are dolbaeb? Комманду выполнять может только игрок");
            }
            return -2;
        }
        private static int kitReCreate(CommandSourceStack source, Kit kit, String cooldown){
            ServerPlayer player = (ServerPlayer) source.getEntity();
            assert player != null;
            kit.setItems(readInventoryToList(player));
            kit.setCooldown(parseCooldown(cooldown));
            return 1;
        }
        private static int kitCreate(CommandSourceStack source, String argument, String cooldown){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ServerPlayer player = (ServerPlayer) source.getEntity();
            for (Kit kit : Data.getData().getKits()){
                if (kit.getName().equals(argument)){
                    //тут будет обновление кита
                    kitReCreate(source, kit, cooldown);
                    String message = "Вы успешно обновили кит " + "§a" + argument;
                    MessageFunctions.broadcastMcSkillMessage(player, message);
                    try(FileWriter writer = new FileWriter("config/kits.json")){
                        gson.toJson(Data.getData(), writer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                }
            }
            assert player != null;
            Kit newKit = new Kit(argument, readInventoryToList(player), parseCooldown(cooldown));
            Data.getData().addKit(newKit);
            String message = "Вы успешно создали кит " + "§a" + argument;
            MessageFunctions.broadcastMcSkillMessage(player, message);
            try(FileWriter writer = new FileWriter("config/kits.json")){
                gson.toJson(Data.getData(), writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //Data.loadData();
            return 1;
        }

        private static int kitList(CommandSourceStack source){
            if(!(source.getEntity() instanceof ServerPlayer)){
                System.out.println("Tu dolbaeb? Tu ne igrok a mamkin hacker");
                return 0;
            }
            StringBuilder message = new StringBuilder();
            ServerPlayer player = source.getPlayer();
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUUID());
            if (user != null) {
                Data.getAllKitsName().forEach(kitName -> {
                    String permission = "command.kit.claim." + kitName;
                    if (user.getCachedData().getPermissionData().checkPermission(permission).asBoolean()) {
                        if (CooldownManager.isOnCooldown(player.getName().getString(), kitName)){
                            message.append("§c" + kitName + ", ");
                        } else {
                            message.append("§a" + kitName + ", ");
                        }

                    }
                });
            }
            MessageFunctions.broadcastMcSkillMessage(player, message.toString());
            return 1;
        }


        private static List<Item> readInventoryToList(ServerPlayer player){
            List<Item> itemList = new ArrayList<>();
            for (ItemStack itemStack : player.getInventory().items){
                if (!itemStack.isEmpty()){
                    ResourceLocation itemdId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
                    assert itemdId != null;
                    Item item = new Item(itemdId.toString(), itemStack.getCount());
                    itemList.add(item);
                }
            }
            return itemList;
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
//            hours =  TimeUnit.SECONDS.toHours(seconds) - (day*24);
//            minutes = TimeUnit.SECONDS.toMinutes(seconds) - (hours * 60);
//            second = TimeUnit.SECONDS.toSeconds(seconds) - (minutes * 60);
            hours = (seconds % 86400) / 3600;
            minutes = ((seconds % 86400) % 3600) / 60;
            second = ((seconds % 86400) % 3600) % 60;
            return (day + "д. " + hours + "ч. " + minutes + "м. " + second + "c. ");
        }

    }
}
