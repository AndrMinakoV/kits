package com.neoxygen.neokits.utilities;

import java.util.List;

public class Kit {
    private String name;
    private List<Item> items;
    private long cooldown;
    public Kit(String name, List<Item> items, long cooldown){
        this.name = name;
        this.items = items;
        this.cooldown = cooldown;
    }
    public Kit(){}
    public long getCooldown(){return cooldown;}
    public void setCooldown(long cooldown){this.cooldown = cooldown;}
    public List<Item> getItems() {
        return items;
    }
    public  String getName() {
        return name;
    }
    public  void setItems(List<Item> items) {
        this.items = items;
    }
    public void setName(String name) {
        this.name = name;
    }
}

