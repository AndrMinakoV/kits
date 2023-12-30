package com.neoxygen.neokits.utilities;

public class Item {
    private String item;
    private int count;
    public Item(String item, int count){
        this.item = item;
        this.count = count;
    }
    public Item(){}
    public String getItem(){
        return item;
    }
    public int getCount() {
        return count;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
