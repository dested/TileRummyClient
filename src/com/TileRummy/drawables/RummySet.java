package com.TileRummy.drawables;


import MessageParseJunk.TileData;
import android.graphics.Canvas;
import com.TileRummy.LampLight.PaintBucket;
import com.TileRummy.RummyGameLogic;
import com.TileRummy.Utils.Point;
import com.TileRummy.Utils.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RummySet {

    public ArrayList<RummyTile> tiles = new ArrayList<RummyTile>();
    public float X;
    public float Y;
    public PaintBucket Bucket;
    public RummyGameLogic Logic;
    public PlayerInformation Player;
    public int EmptyTileIndex = -1;
    private float Width;

    public RummySet() {
    }


    public void setBucket(PaintBucket bucket) {
        Bucket = bucket;
    }

    public void setPosition(Point p) {
        X = p.X;
        Y = p.Y;
    }

    public void addTile(int index, RummyTile tile) {
        tiles.add(index, tile);
        tile.Set = this;
        tile.setBucket(Bucket);
    }

    public void removeTile(RummyTile tile) {
        tiles.remove(tile);
        tile.Set = null;
    }

    public void addTile(RummyTile tile) {
        tiles.add(tile);
        tile.Set = this;
        tile.setBucket(Bucket);
    }


    public void draw(float width, Canvas canvas) {

        Width = width;
        int wrap = (int) (width / (RummyTile.Width + 7));
        float emptyXOffset = 0;
        boolean skip = false;
        for (int i = 0; i < tiles.size(); i++) {

            if (!skip && i % wrap == 0 && emptyXOffset > 0) emptyXOffset = 0;
            float x = (RummyTile.Width + 7) * (i % wrap) + this.X + emptyXOffset;
            float y = (RummyTile.Height + 7) * ((float) Math.floor(i / wrap)) + this.Y;


            if (!skip && EmptyTileIndex == i) {
                canvas.drawRoundRect(new Rectangle(x, y, 32, RummyTile.Height).toRectF(), 3, 3, Bucket.GetPaint("outerTileLongPressed"));
                emptyXOffset += 45;
                skip = true;
                i--;

                continue;
            }
            skip = false;
            tiles.get(i).setPosition(new Point(x, y));
            tiles.get(i).draw(canvas);

        }
        if (EmptyTileIndex == tiles.size()) {

            float x = (RummyTile.Width + 7) * ((tiles.size() - 1) % wrap) + this.X + emptyXOffset;
            float y = (RummyTile.Height + 7) * ((float) Math.floor((tiles.size() - 1) / wrap)) + this.Y;
            canvas.drawRoundRect(new Rectangle(x, y, 32, RummyTile.Height).toRectF(), 3, 3, Bucket.GetPaint("outerTileLongPressed"));

        }
    }

    public boolean collides(Point p) {
        return collideWithTile(p) != null;
    }

    public RummyTile collideWithTile(Point p) {
        List<Double> distances = new ArrayList<Double>();
        if (tiles.size() == 0) return null;


        int wrap = (int) (Width / (RummyTile.Width + 7));
        float emptyXOffset = 0;
        boolean skip = false;

        double emptyDistance = Float.MAX_VALUE;

        for (int i = 0, tilesSize = tiles.size(); i < tilesSize; i++) {
            RummyTile tile = tiles.get(i);

            if (!skip && i % wrap == 0 && emptyXOffset > 0) emptyXOffset = 0;

            if (!skip && EmptyTileIndex == i) {

                emptyDistance = Math.sqrt(Math.pow(((tile.X + RummyTile.Width / 2) - p.X) + (45f / 2f), 2) + Math.pow((tile.Y + RummyTile.Height / 2) - p.Y, 2));
                emptyXOffset += 45;
                skip = true;
                i--;

                continue;
            }

            distances.add(Math.sqrt(Math.pow(((tile.X + RummyTile.Width / 2) - p.X) + emptyXOffset, 2) + Math.pow((tile.Y + RummyTile.Height / 2) - p.Y, 2)));
            //if (tile.collides(xx, yy)) {
            //    return tile;
            //}
        }
        List<Double> distances2 = new ArrayList<Double>(distances);
        Collections.sort(distances2);
        RummyTile returnValue;
        if (distances2.get(0) > emptyDistance) {
            return tiles.get(EmptyTileIndex);
        }

        if (distances2.get(0) > 60)
            returnValue = null;
        else
            returnValue = tiles.get(distances.indexOf(distances2.get(0)));
        return returnValue;
    }


    public void dropTile(RummySet set, Point pos) {
        RummyTile tl = collideWithTile(pos);
        if (tl != null) {
            int index = tiles.indexOf(tl);

            if (pos.X > tl.X + RummyTile.Width / 2)
                index++;
            if (set == null) {

                EmptyTileIndex = index;
            } else {
                for (RummyTile tile : set.tiles)
                    addTile(index, tile);
            }
            return;
        }

        if (set == null) {
            EmptyTileIndex = tiles.size() - 1;
        } else {
            for (RummyTile tile : set.tiles)
                addTile(tile);
        }


        return;
    }

    public float getHeight(float width, boolean min) {
        int wrap = (int) (width / (RummyTile.Width + 7));
        float c = 1 + ((float) Math.floor((tiles.size() - 1) / wrap));

        if (c == 1 && min) {
            c++;
        }
        float f = (RummyTile.Height + 7) * (c);
        return f;
    }


    public void longPress(Point mousePoint) {
        for (RummyTile tile : tiles) {
            if (tile.collides(mousePoint)) {
                tile.longPress(mousePoint);
                return;
            }
        }
    }

    public TileData[] getTileData() {
        ArrayList<TileData> td = new ArrayList<TileData>();
        for (RummyTile tile : tiles) {
            td.add(tile.getTileData());
        }
        TileData[] ts = new TileData[td.size()];
        td.toArray(ts);
        return ts;
    }
}