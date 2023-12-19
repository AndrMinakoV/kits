package com.neoxygen.neokits.kits;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Kit {
    private final List<ItemStack> items;

    public Kit(List<ItemStack> items){
        this.items = items;
    }
    public List<ItemStack> getItems(){
        return items;
    }

}
