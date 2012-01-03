package com.TileRummy;


import android.graphics.Canvas;
import android.graphics.Paint;
import com.TileRummy.LampLight.PaintBucket;

import java.util.ArrayList;
import java.util.List;

public class RummySet {

    protected ArrayList<RummyTile> tiles = new ArrayList<RummyTile>();
    private float Rotation = 0;
    public float X;
    public float Y;
    public PaintBucket Bucket;
    private Point draggingOffset;
    public RummyGameLogic Logic;
    public boolean Dragging;

    public RummySet() {
    }

    public void setBucket(PaintBucket bucket) {
        Bucket = bucket;
    }

    public void setPosition(float x, float y) {
        X = x;
        Y = y;
    }

    public void addTile(int index,RummyTile tile) {
        tiles.add(index,tile);
        tile.Set=this;
        tile.setBucket(Bucket);
    }

    public void removeTile(RummyTile tile) {
        tiles.remove(tile);
        tile.Set=null;
        tile.setBucket(null);
    }
    public void addTile(RummyTile tile) {
        tiles.add(tile);
        tile.Set=this;
        tile.setBucket(Bucket);
    }


    public void draw(Canvas canvas) {


        float x = this.X ;
        float y = this.Y ;
        for (int i = 0; i < tiles.size(); i++) {

            tiles.get(i).draw(x, y, canvas);
            x += (tiles.get(i).Width+10) * Math.cos(Rotation) ;
            y += tiles.get(i).Height * Math.sin(Rotation);
        }
    }

    public boolean collides(float xx, float yy) {
        return collideWithTile(xx,yy)!=null;
    }    
    public RummyTile collideWithTile(float xx, float yy) {
        float x = this.X;
        float y = this.Y;
        for (int i = 0; i < tiles.size(); i++) {
            float w = (10+tiles.get(i).Width) * 1;
            float h = tiles.get(i).Height * 1;
            if (xx > x && xx < x + w && yy > y && yy < y + h) {
                return tiles.get(i);
            }
            x += w;
            // y += h;

        }
        return null;
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

        if(draggingOffset==null){
            draggingOffset=new Point(0,0)               ;
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

}