package com.TileRummy.Utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TileColor {
    Red(0, new int[]{255,0,0}), Blue(1, new int[]{0,0,255}), Green(2, new int[]{0,255,0}), Purple(3, new int[]{127,127,0});
    public int[] colorInfo;
    public int index;

    TileColor(int i, int[] colorInfo) {
        this.colorInfo = colorInfo;
        index = i;
    }

    private static final Map<Integer, TileColor> lookup
            = new HashMap<Integer, TileColor>();

    static {
        for (TileColor s : EnumSet.allOf(TileColor.class))
            lookup.put(s.index, s);
    }

    public static TileColor getColor(int color) {
        return lookup.get(color);
    }
}