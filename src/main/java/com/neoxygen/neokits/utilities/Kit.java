package com.neoxygen.neokits.utilities;

import java.util.List;

public class Kit {
    public String name;
    public List<Item> items;

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

