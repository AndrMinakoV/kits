package com.neoxygen.neokits.kits;



import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KitsManager {
    private static final Map<String, Kit> KITS = new HashMap<>();
    public static void loadKits(){
        try{
            String jsonString = Files.readString(Path.of("config/kits.json"));
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            Gson gson = new Gson();

            for (String key : jsonObject.keySet()){
                List<ItemStack> items = new ArrayList<>();
                jsonObject.getAsJsonArray(key).forEach(jsonElement -> {
                    JsonObject itemObject = jsonElement.getAsJsonObject();
                    String itemName = itemObject.get("item").getAsString();
                    int count = itemObject.get("count").getAsInt();
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
                    if (item != null){
                        items.add(new ItemStack(item, count));
                    }
                });
                KITS.put(key, new Kit(items));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Kit getKit(String name) {
        return KITS.get(name);
    }



}
