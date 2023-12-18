package com.neoxygen.neokits.kits;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.neoxygen.neokits.Kit;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KitsManager {
    private static final Map<String, Kit> kits = new HashMap<>();

    public static void loadKitsFromFile(String fileName) {
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();

            for (String key : json.keySet()) {
                List<ItemStack> kitItems = new ArrayList<>();
                json.getAsJsonArray(key).forEach(itemElement -> {
                    JsonObject itemObject = itemElement.getAsJsonObject();
                    String itemID = itemObject.get("item").getAsString();
                    int count = itemObject.get("count").getAsInt();

                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemID));
                    if (item != null) {
                        kitItems.add(new ItemStack(item, count));
                    }
                });
                kits.put(key, new Kit(kitItems));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Kit getKit(String name) {
        return kits.get(name);
    }
}
