package com.neoxygen.neokits.utilities;

import com.google.gson.Gson;
import com.neoxygen.neokits.KitMod;
import com.neoxygen.neokits.cooldowns.CooldownManager;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<String> getAllKitsName(){
        List<String> list = new ArrayList<>();
        for (Kit kit : Data.data.getKits()){
            list.add(kit.getName());
        }
        return list;
    }
    @SubscribeEvent
    public static void onServerStarted(ServerStartingEvent event)
    {
        loadData();
        CooldownManager.loadCooldowns();
    }
    @SubscribeEvent
    public static void onServerStarter(ServerStoppingEvent event){
        CooldownManager.saveCooldowns();
    }
}
