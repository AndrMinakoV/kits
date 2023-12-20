package com.neoxygen.neokits.events;

import com.neoxygen.neokits.KitMod;
import com.neoxygen.neokits.kits.KitCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KitMod.MODID)
public class ModEvent {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        new KitCommand(event.getDispatcher());
    }
}
