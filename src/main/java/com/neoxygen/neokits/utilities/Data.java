package com.neoxygen.neokits.utilities;

import com.google.gson.Gson;
import com.neoxygen.neokits.KitMod;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = KitMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Data {
    private static KitsContainer data;

    public static KitsContainer getData(){
        return data;
    }

    public static void loadData(){
        try{
            Gson g = new Gson();
            String jsonString = new String(Files.readAllBytes(Paths.get("config/kits.json")));
            data = g.fromJson(jsonString, KitsContainer.class);
            System.out.println(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getAllKitsName(){
        List<String> list = new ArrayList<>();
        for (Kit kit : Data.data.kits){
            list.add(kit.name);
        }
        Kit newKit = new Kit();
        newKit.name = "fdsfsd";//имя будущего кита;
        List<Item> itemList = new ArrayList<>();//список предметов на выдачу
        Item item = new Item();//временный предмет
        item.item = "dasdasd";//id итема который будет читаться с инвентаря
        item.count = 10;//количество предмета (id)
        itemList.add(item);





        return list;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event){
        loadData();
    }
}
