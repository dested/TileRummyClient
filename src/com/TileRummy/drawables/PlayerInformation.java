package com.TileRummy.drawables;

import android.graphics.Canvas;
import com.TileRummy.RummyGameLogic;
import com.TileRummy.Utils.Point;
import com.TileRummy.Utils.Rectangle;

import java.util.ArrayList;

public class PlayerInformation {
    public ArrayList<RummySet> Sets = new ArrayList<RummySet>();
    private RummyGameLogic logic;
    public Point Location;
    public String name;
    public boolean EmptySet;

    public PlayerInformation(RummyGameLogic logic, String name) {
        this.logic = logic;
        this.name = name;
    }

    public float getHeight() {
        float curY = Location.Y;
        curY += 7;
        for (RummySet Set : Sets) {
            Set.setPosition(new Point(Location.X, curY));
            curY += Set.getHeight(getWidth(),false) + 5;
        }
        if (EmptySet) {
            curY += RummyTile.Height + 7;
        }
        return curY + 150;
    }

    public float getWidth() {
        int maxTiles = 1;

        for (RummySet Set : Sets) {
            if (maxTiles < Set.tiles.size()) {
                maxTiles = Set.tiles.size();
                if (maxTiles >= 6) maxTiles = 6;
            }

        }

        return (RummyTile.Width + 7) * maxTiles + 25;
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
        float w = getWidth();
        float curY = Location.Y;
        canvas.drawText(name, Location.X + (w / 4), curY, this.logic.Bucket.GetPaint("nameText"));
        curY += 7;
        for (RummySet Set : Sets) {
            Set.setPosition(new Point(Location.X, curY));
            Set.draw(w, canvas);
            curY += Set.getHeight(w,false) + 5;
        }
        if (EmptySet) {

            canvas.drawRoundRect(new Rectangle(Location.X, curY, 35, RummyTile.Height).toRectF(), 3, 3, this.logic.Bucket.GetPaint("outerTileLongPressed"));
            curY += RummyTile.Height + 7;

        }

    }

    public RummySet getSet(Point itemPosition) {
        if (new Rectangle(Location, getWidth(), getHeight()).Collides(itemPosition)) {
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
        Rectangle rt = Location.toRectangle(getWidth(), getHeight());
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
