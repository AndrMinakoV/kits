package com.neoxygen.neokits.cooldowns;

public class KitsCooldown {
    private String name;
    private long cooldownEnd;

    public KitsCooldown(String kitName, long cooldownEnd) {
        this.name = kitName;
        this.cooldownEnd = cooldownEnd;
    }
    public KitsCooldown(){}

    public long getCooldownEnd() {
        return cooldownEnd;
    }
    public String getName() {
        return name;
    }
    public void setCooldownEnd(long cooldownEnd) {
        this.cooldownEnd = cooldownEnd;
    }
    public void setName(String name) {
        this.name = name;
    }
}
