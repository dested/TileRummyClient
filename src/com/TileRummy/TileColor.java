package com.TileRummy;

import org.xbill.DNS.Lookup;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Dested
 * Date: 1/1/12
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
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
