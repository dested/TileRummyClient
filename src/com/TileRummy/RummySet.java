package com.TileRummy;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.FloatMath;
import com.TileRummy.LampLight.PaintBucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RummySet {

    public ArrayList<RummyTile> tiles = new ArrayList<RummyTile>();
    public float X;
    public float Y;
    public PaintBucket Bucket;
    private Point draggingOffset;
    public RummyGameLogic Logic;
    public boolean Dragging;
    public PlayerInformation Player;
    private float height;
    public int EmptyTileIndex=-1;

    public RummySet() {
    }


    public void setBucket(PaintBucket bucket) {
        Bucket = bucket;
    }

    public void setPosition(float x, float y) {
        X = x;
        Y = y;
    }

    public void addTile(int index, RummyTile tile) {
        tiles.add(index, tile);
        tile.Set = this;
        tile.setBucket(Bucket);
    }

    public void removeTile(RummyTile tile) {
        tiles.remove(tile);
        tile.Set = null;
        tile.setBucket(null);
    }

    public void addTile(RummyTile tile) {
        tiles.add(tile);
        tile.Set = this;
        tile.setBucket(Bucket);
    }


    public float draw(float width, Canvas canvas) {


        int wrap = (int) (width / (RummyTile.Width + 7));
        float emptyXOffset = 0;
        boolean skip=false;
        for (int i = 0; i < tiles.size(); i++) {

            if (i % wrap == 0 && emptyXOffset > 0) emptyXOffset = 0;
            float x = (RummyTile.Width + 7) * (i % wrap) + this.X + emptyXOffset;
            float y = (RummyTile.Height + 7) * ((float) Math.floor(i / wrap)) + this.Y;


            if (!skip && EmptyTileIndex == i ) {
                canvas.drawRoundRect(new Rectangle(x, y, 32, RummyTile.Height).toRectF(), 3, 3, Bucket.GetPaint("outerTileLongPressed"));
                emptyXOffset += 45;
                skip=true;
                i--;

                continue;
            }

            tiles.get(i).setPosition(x, y);
            tiles.get(i).draw(canvas);

        }
        return (RummyTile.Height + 7) * (((float) Math.floor((tiles.size() - 1) / wrap)) + 1) + 7;
    }

    public boolean collides(float xx, float yy) {
        return collideWithTile(xx, yy) != null;
    }

    public RummyTile collideWithTile(float xx, float yy) {
        List<Double> distances = new ArrayList<Double>();
        if (tiles.size() == 0) return null;

        for (RummyTile tile : tiles) {

            distances.add(Math.sqrt(Math.pow((tile.X + RummyTile.Width / 2) - xx, 2) + Math.pow((tile.Y + RummyTile.Height / 2) - yy, 2)));
            //if (tile.collides(xx, yy)) {
            //    return tile;
            //}
        }
        List<Double> distances2 = new ArrayList<Double>(distances);
        Collections.sort(distances2);
        RummyTile returnValue;
        if (distances2.get(0) > 65)
            returnValue = null;
        else
            returnValue = tiles.get(distances.indexOf(distances2.get(0)));
        return returnValue;
    }

    public void beginDragging(float cx, float cy) {
        draggingOffset = new Point(cx - this.X, cy - this.Y);

        float x = this.X;
        float y = this.Y;
        for (int i = 0; i < tiles.size(); i++) {
            float w = tiles.get(i).Width * 1;
            float h = tiles.get(i).Height * 1;
            if (cx > x && cx < x + w && cy > y && cy < y + h) {
                tiles.get(i).highlighted = true;
            }
            x += w;
            // y += h;

        }
    }

    public void draggingAround(float x, float y) {

        Dragging = true;

        if (draggingOffset == null) {
            draggingOffset = new Point(0, 0);
        }
        setPosition(x - draggingOffset.X, y - draggingOffset.Y);
    }

    public void endDragging() {
        Dragging = false;
        draggingOffset = null;
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).highlighted = false;

            tiles.get(i).longPressed = false;
        }
    }

    public void dropTile(RummyTile tile, Point pos) {
        RummyTile tl = collideWithTile(pos.X, pos.Y);
        if (tl != null) {
            if (tile == null) {
                EmptyTileIndex = tiles.indexOf(tl);
            } else
                addTile(tiles.indexOf(tl), tile);
            return;
        }

        if (tile == null) {
            EmptyTileIndex = tiles.size() - 1;
        } else
            addTile(tile);


        return;
    }

    public float getHeight(float width) {
        float h = 0;
        int wrap = (int) (width / (RummyTile.Width + 7));
        for (int i = 0; i < tiles.size(); i++) {
            h += (RummyTile.Height + 7) * ((float) Math.floor(i / wrap));
        }
        return (RummyTile.Height + 7) * (1 + ((float) Math.floor((tiles.size() - 1) / wrap)));
    }

    public boolean collides(Point itemPosition) {
        return collides(itemPosition.X, itemPosition.Y);
    }

    public void longPress(Point mousePoint) {
        for (RummyTile tile : tiles) {
            if (tile.collides(mousePoint)) {
                tile.longPress(mousePoint);
            }
        }
    }
}