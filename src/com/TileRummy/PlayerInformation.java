package com.TileRummy;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.logging.XMLFormatter;

public class PlayerInformation {
    public ArrayList<RummySet> Sets = new ArrayList<RummySet>();
    private RummyGameLogic logic;
    private Point Location;
    private float Height;
    public float Width;
    public String name;

    public PlayerInformation(RummyGameLogic logic, String name) {
        this.logic = logic;
        this.name = name;
    }

    public void addSet(RummySet set) {
        Sets.add(set);
        set.Player = this;
        set.Logic = this.logic;
        set.setBucket(this.logic.Bucket);
    }    public void addSet(int j,RummySet set) {
        Sets.add(j,set);
        set.Player = this;
        set.Logic = this.logic;
        set.setBucket(this.logic.Bucket);
    }
    public void removedSet(RummySet set) {
        Sets.remove(set);
        set.Player = null;
        set.Logic = null;
        set.setBucket(null);
    }

    public void draw(Canvas canvas) {
        Width = (RummyTile.Width + 7) * 6+25;

        int curY = 0;
        canvas.drawText(name, Location.X +(Width/4), curY, this.logic.Bucket.GetPaint("nameText"));
        curY += 7;
        for (RummySet Set : Sets) {
            Set.setPosition(Location.X, curY);
            curY += Set.draw(Width, canvas)+5;
        }
        Height = curY+150;

    }

    public RummySet getSet(Point itemPosition) {
        if (new Rectangle(Location, Width, Height).Collides(itemPosition)) {
            for (RummySet Set : Sets) {
                if (Set.collides(itemPosition)) {
                    return Set;
                }
            }
        }
        return null;

    }

    public void setPosition(float x, float y) {
        Location = new Point(x, y);
    }

    public boolean collides(Point itemPosition) {
        Rectangle rt = Location.toRectangle(Width, Height);
        return rt.Collides(itemPosition);
    }

    public boolean longPress(Point mousePoint) {

        if (collides(mousePoint)) {
            RummySet set = getSet(mousePoint);

            if (set != null) {
                set.longPress(mousePoint);
            }
        }
        return false;
    }
}
