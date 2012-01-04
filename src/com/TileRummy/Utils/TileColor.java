package com.TileRummy.Utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TileColor {
    Red(0),Blue(1),Green(2),Purple(3);
                int index;
    TileColor(int i) {
        index=i;
    }
    private static final Map<Integer,TileColor> lookup
            = new HashMap<Integer,TileColor>();
    
    static {
        for(TileColor s : EnumSet.allOf(TileColor.class))
            lookup.put(s.index, s);
    }
    public static TileColor getColor(int color) {
        return lookup.get(color);
    }
}
