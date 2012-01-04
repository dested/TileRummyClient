package com.TileRummy;

import android.graphics.Canvas;
import com.TileRummy.Utils.Point;
import com.TileRummy.Utils.Rectangle;

import java.util.ArrayList;

public class PlayerInformation {
    public ArrayList<RummySet> Sets = new ArrayList<RummySet>();
    private RummyGameLogic logic;
    private Point Location;
    float Height;
    public float Width;
    public String name;
    public boolean EmptySet;

    public PlayerInformation(RummyGameLogic logic, String name) {
        this.logic = logic;
        this.name = name;
    }

    public void addSet(RummySet set) {
        Sets.add(set);
        set.Player = this;
        set.Logic = this.logic;
        set.setBucket(this.logic.Bucket);
    }

    public void addSet(int j, RummySet set) {
        Sets.add(j, set);
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
        Width = (RummyTile.Width + 7) * 6 + 25;

        float curY = Location.Y;
        canvas.drawText(name, Location.X + (Width / 4), curY, this.logic.Bucket.GetPaint("nameText"));
        curY += 7;
        for (RummySet Set : Sets) {
            Set.setPosition(new Point(Location.X, curY));
            curY += Set.draw(Width, canvas) + 5;
        }
        if (EmptySet) {

            canvas.drawRoundRect(new Rectangle(Location.X, curY, 35, RummyTile.Height).toRectF(), 3, 3, this.logic.Bucket.GetPaint("outerTileLongPressed"));
            curY += RummyTile.Height + 7;

        }
        Height = curY + 150;

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
