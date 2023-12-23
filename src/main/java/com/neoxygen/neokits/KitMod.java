package com.neoxygen.neokits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mod("neokits") // Убедитесь, что здесь указан правильный modid
public class KitMod {
    // Если у вас есть другие переменные и методы, они должны быть здесь
    public static final String MODID = "neokits";
    @Mod.EventBusSubscriber(modid = KitMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommandRegistry {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
//            event.getDispatcher().register(Commands.literal("deletekit")
//                    .then(Commands.argument("kitName", StringArgumentType.word())
//                            .executes(context -> kitDelete(context.getSource(), StringArgumentType.getString(context, "kitName")))));
            // Создание китов
            event.getDispatcher().register(Commands.literal("createkit")
                    .then(Commands.argument("kitName", StringArgumentType.word())
                            .executes(context -> kitCreate(context.getSource(), StringArgumentType.getString(context, "kitName")))));
            // Получение китов
            event.getDispatcher().register(Commands.literal("kit")
                    .then(Commands.argument("kitName", StringArgumentType.word()).suggests((context, builder) -> {
                            Data.getAllKitsName().forEach(builder::suggest);
                            return builder.buildFuture();
                            })
                            .executes(context -> handleKit(context.getSource(), StringArgumentType.getString(context, "kitName")))));
        }
        private static int handleKit(CommandSourceStack source, String argument) {
            if (source.getEntity() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) source.getEntity();
                for(Kit kit : Data.getData().getKits()){
                    if (kit.getName().equals(argument)){
                        for (Item items : kit.getItems()){
                            //переписать цикл к хуям собачим и щенячим
                            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.getItem())), items.getCount()));
                        }
                    }
                }
            }
            return 1;
        }

//        private static int kitDelete(CommandSourceStack source, String argument){
//            if (source.getEntity() instanceof ServerPlayer){
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                ServerPlayer player = (ServerPlayer) source.getEntity();
//                        Data.getData().getKits().removeIf(kit -> kit.getName().equals(argument));
//
//
//
//            }
//
//        }

        private static int kitCreate(CommandSourceStack source, String argument){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ServerPlayer player = (ServerPlayer) source.getEntity();
            Kit newKit = new Kit();
            newKit.setName(argument);
            List<Item> itemList = new ArrayList<>();
            for (ItemStack itemStack : player.getInventory().items)
            {
                if (!itemStack.isEmpty())
                {
                    Item item = new Item();
                    ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
                    System.out.println(itemId.toString());
                    item.setItem(itemId.toString());
                    item.setCount(itemStack.getCount());
                    itemList.add(item);
                }
            }
            newKit.setItems(itemList);
            Data.getData().addKit(newKit);
            try(FileWriter writer = new FileWriter("config/kits.json")){
                gson.toJson(Data.getData(), writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return 1;
        }

    }

}
