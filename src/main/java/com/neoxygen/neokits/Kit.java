package com.neoxygen.neokits;

import com.neoxygen.neokits.kits.KitCommand;
import com.neoxygen.neokits.kits.KitsManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("neokits")

public class Kit
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "neokits";

    public Kit(){
        KitsManager.loadKitsFromFile("config/kits.json");
    }

    private List<ItemStack> items = new ArrayList<>();
    public Kit(List<ItemStack> items){
        this.items = items;
    }

    public List<ItemStack> getItems(){
        return items;
    }

    @SubscribeEvent
    public void onRegisterCommand(RegisterCommandsEvent event){
        KitCommand.register(event.getDispatcher());
    }





}
