package com.TileRummy.drawables;

import android.graphics.Canvas;
import com.TileRummy.RummyGameLogic;
import com.TileRummy.Utils.Point;
import com.TileRummy.Utils.Rectangle;

import java.util.ArrayList;

public class GameMenu {
    float menuHeight = 24;
    float minHeight = menuHeight;
    float maxHeight = 70;
    private ArrayList<GameMenuButton> Buttons = new ArrayList<GameMenuButton>();
    private RummyGameLogic logic;

    public GameMenu(RummyGameLogic logic){

        this.logic = logic;
    }
    public void addButton(GameMenuButton button){
        button.logic=logic;
        Buttons.add(button);
    }
    public void draw(int width,Canvas canvas) {

        canvas.drawRect(new Rectangle(0,0,width,menuHeight ).toRectF(),logic.Bucket.GetPaint("menuBackground"));
        if(menuHeight==minHeight)return;
        for (GameMenuButton button : Buttons) {
            button.draw(canvas);
        }
    }
    public void touchDown(Point p) {
        if(menuHeight==minHeight)return;
        for (GameMenuButton button : Buttons) {
            if (button.collides(p)) {
                button.touchDown();
            }
        }
    }
    public boolean collides(Point p,float width) {
        return new Rectangle(0,0,width,menuHeight ).Collides(p);
    }

    public void doubleTap(Point p) {

        if(menuHeight == minHeight){
            menuHeight = maxHeight;
        }   else
        {
            menuHeight = minHeight;
        }
    }
    public void touchUp(Point p) {

        for (GameMenuButton button : Buttons) {
            button.touchUp();

        }
    }
}
