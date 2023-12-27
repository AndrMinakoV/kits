package com.neoxygen.neokits.cooldowns;

import java.util.List;

public class Player {
    private String name;
    List<KitsCooldown> kitsCooldown;


    public String getName() {
        return name;
    }
    public List<KitsCooldown> getKitsCooldown() {
        return kitsCooldown;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setKitsCooldown(List<KitsCooldown> kitsCooldown) {
        this.kitsCooldown = kitsCooldown;
    }
}



