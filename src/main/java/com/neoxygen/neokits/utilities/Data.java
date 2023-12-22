package com.neoxygen.neokits.utilities;

import com.google.gson.Gson;
import com.neoxygen.neokits.KitMod;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event){
        loadData();
    }
}
