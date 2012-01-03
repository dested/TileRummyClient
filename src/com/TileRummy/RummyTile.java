package com.TileRummy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.TileRummy.LampLight.PaintBucket;


public class RummyTile {
    private TileColor color;
    public RummySet Set;
    private int number;
    public float Width = 63;
    public float Height = 35;
    public PaintBucket Bucket;
    public boolean highlighted;
    public boolean longPressed;

    public RummyTile(int i, TileColor red) {

        number = i;
        color = red;
    }

    public void setBucket(PaintBucket bucket) {

        Bucket = bucket;
    }

    public void draw(float x, float y, Canvas canvas) {

        RectF tileLoc;

        tileLoc = new Rectangle(x, y, Width, Height).toRectF();

        Paint paint;

         if (highlighted) {
            if (longPressed) {
                paint = Bucket.GetPaint("outerTileLongPressed");
            } else {
                paint = Bucket.GetPaint("outerTileHighlight");
            }
        } else {
            if (Set.Dragging) {
                paint = Bucket.GetPaint("outerTileHighlightSet");
            } else {
                paint = Bucket.GetPaint("outerTile");
            }
        }

        canvas.drawRoundRect(tileLoc, 3, 3, paint);
        canvas.drawRoundRect(tileLoc, 3, 3, Bucket.GetPaint("innerTile"));

        canvas.drawText(Integer.toString(number), tileLoc.left + Width / 9f, tileLoc.top + Height * .71f, Bucket.GetPaint("tileText1"));

        canvas.drawCircle(tileLoc.left + Width * .75f, tileLoc.top + Height * .5f, Height / 3, highlighted ? Bucket.GetPaint("tileCircleHighlight") : Bucket.GetPaint("tileCircle"));
    }

    public void longPress(float x, float y) {
        RummyGameLogic lgc = this.Set.Logic;


        RummySet st = new RummySet();
        st.X=x;
        st.Y=y;



        longPressed = true;
        this.Set.Dragging = false;
        this.Set.tiles.remove(this);

        lgc.addSet(st);
                        st.Dragging = true;
        lgc.draggingSet=st;
        st.addTile(this);
    }
}
