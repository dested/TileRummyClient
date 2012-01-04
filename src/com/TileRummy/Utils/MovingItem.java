package com.TileRummy.Utils;

import com.TileRummy.RummySet;
import com.TileRummy.RummyTile;

public class    MovingItem {
    public RummyTile tile;
    public RummySet set;
    private Point itemPosition;
    private Point draggingOffset;
    public Point getRealPosition(){
        return new Point(itemPosition.X-draggingOffset.X, itemPosition.Y-draggingOffset.Y);
    }

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

    public void updatePosition(Point mouseLocation) {
        itemPosition = mouseLocation;
    }
}
