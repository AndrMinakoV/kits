package com.neoxygen.neokits.utilities;

import java.util.List;

public class KitsContainer {
    private List<Kit> kits;

    public List<Kit> getKits(){
        return kits;
    }
    public void addKit(Kit kit){
        kits.add(kit);
    }
    public void setKits(List<Kit> kits) {
          this.kits = kits;
    }

}
