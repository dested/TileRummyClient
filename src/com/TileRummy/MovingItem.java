package com.TileRummy;

public class    MovingItem {
    public RummyTile tile;
    public RummySet set;
    public Point itemPosition;
    public Point draggingOffset;

    public MovingItem(RummyTile tile, Point p1, Point offset) {
        this.tile = tile;
        itemPosition = p1;
        draggingOffset = offset;


    }

    public MovingItem(RummySet set, Point p1, Point offset) {
        this.set = set;
        itemPosition = p1;
        draggingOffset = offset;


    }
}
