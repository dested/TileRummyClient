package com.TileRummy.drawables;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.TileRummy.RummyGameLogic;
import com.TileRummy.Utils.Point;
import com.TileRummy.Utils.Rectangle;

public class GameMenuButton {
    public String Text;
    public Point Location;
    public Runnable Click;
    public static float WIDTH = 75;
    public static float HEIGHT = 29;
    public RummyGameLogic logic;
    private String PaintName;

    public GameMenuButton(String text, Point loc,String paintName,Runnable click)
    {
    Click=click;

                                     Text=text;
        Location = loc;
        PaintName=paintName;
    }

    public boolean collides(Point p) {
        if (Location.toRectangle(WIDTH, HEIGHT).Collides(p)) {
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas) {
        
        Paint paint=logic.Bucket.GetPaint(PaintName);
        RectF loc = new Rectangle(Location, WIDTH, HEIGHT).toRectF();
        canvas.drawRoundRect(loc, 3, 3, paint);
        Paint colorPaint=logic.Bucket.GetPaint("buttonText");
        canvas.drawText(Text, Location.X + WIDTH / 9f, Location.Y + HEIGHT * .71f, colorPaint);
    }

    public void touchDown() {
        if (Click != null) {
            Click.run();
        }
    }

    public void touchUp() {
    }
}
