package com.neoxygen.neokits;

import com.neoxygen.neokits.kits.KitCommand;
import com.neoxygen.neokits.kits.KitsManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KitMod.MODID)

public class KitMod
{
    public static final String MODID = "neokits";
    public KitMod(){

    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event){
        KitsManager.loadKits();
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        KitCommand.register(event.getDispatcher());
    }







}
