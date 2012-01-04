package com.TileRummy.drawables;

import MessageParseJunk.TileData;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.TileRummy.BackupState;
import com.TileRummy.LampLight.PaintBucket;
import com.TileRummy.RummyGameLogic;
import com.TileRummy.Utils.*;


public class RummyTile {
    private TileColor color;
    public RummySet Set;
    private int number;
    public static float Width = 63;
    public static float Height = 35;
    public float X;
    public float Y;
    public PaintBucket Bucket;
    public boolean highlighted;
    public boolean longPressed;
    public boolean Selected;

    public RummyTile(int i, TileColor red) {

        number = i;
        color = red;
    }


    public void setBucket(PaintBucket bucket) {

        Bucket = bucket;
    }

    public void setPosition(Point p) {
        X = p.X;
        Y = p.Y;
    }

    public void draw(Canvas canvas) {

        RectF tileLoc;

        Paint paint;
        tileLoc = new Rectangle(X, Y, Width, Height).toRectF();


        if (highlighted || Selected) {
            if (longPressed) {
                paint = Bucket.GetPaint("outerTileLongPressed");
            } else {
                paint = Bucket.GetPaint("outerTileHighlight");
            }
        } else {
            /*     if (Set.Dragging) {
                paint = Bucket.GetPaint("outerTileHighlightSet");
            } else {
                paint = Bucket.GetPaint("outerTile");
            }*/
            paint = Bucket.GetPaint("outerTile");
        }

        canvas.drawRoundRect(tileLoc, 3, 3, paint);
        canvas.drawRoundRect(tileLoc, 3, 3, Bucket.GetPaint("innerTile"));

        Paint colorPaint = Bucket.GetPaint("tileText1");
        switch (color) {
            case Red:
                colorPaint = Bucket.GetPaint("tileText1");
                break;
            case Blue:
                colorPaint = Bucket.GetPaint("tileText2");
                break;
            case Green:
                colorPaint = Bucket.GetPaint("tileText3");
                break;
            case Purple:
                colorPaint = Bucket.GetPaint("tileText4");
                break;
        }

        canvas.drawText(Integer.toString(number), tileLoc.left + Width / 9f, tileLoc.top + Height * .71f, colorPaint);

        canvas.drawCircle(tileLoc.left + Width * .75f, tileLoc.top + Height * .5f, Height / 3, highlighted ? Bucket.GetPaint("tileCircleHighlight") : Bucket.GetPaint("tileCircle"));
    }


    public boolean collides(float xx, float yy) {
        return new Rectangle(X, Y, Width, Height).Collides(xx, yy);
    }

    public boolean collides(Point mousePoint) {

        return collides(mousePoint.X, mousePoint.Y);
    }

    public void longPress(Point mousePoint) {
        RummyGameLogic lgc = this.Set.Logic;
        lgc.touchState = TouchDownState.FromTile;

        lgc.lastState = new BackupState(this.Set.Player.name, this.Set.Player.Sets.indexOf(this.Set), this.Set.tiles.indexOf(this));

        Point offset = new Point(this.X - mousePoint.X, this.Y - mousePoint.Y);
        longPressed = true;

        this.Set.removeTile(this);

        RummySet set = new RummySet();
        set.setBucket(this.Bucket);
        set.addTile(this);
        lgc.draggingItem = new MovingItem(set, new Point(mousePoint.X, mousePoint.Y), offset);

    }

    public TileData getTileData() {
        return new TileData(number, color.index);
    }
}
