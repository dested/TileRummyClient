package com.TileRummy;

import MessageParseJunk.TileData;
import android.content.Context;
import android.graphics.*;
import android.os.Vibrator;
import android.view.MotionEvent;
import com.TileRummy.LampLight.PaintBucket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class RummyGameLogic {



    public ArrayList<PlayerInformation> playerInformations = new ArrayList<PlayerInformation>();
    private PaintBucket Bucket;
    private Vibrator vib;
    private ClickMode clickMode;
    public RummySet draggingSet;
    Point panning = new Point(0, 0);
    public float scaleFactor = 1f;

    int mCanvasHeight = 1;
    public int mCanvasWidth = 1;
    private RummyTile movingPlayerTile;
    private Point movingPlayerTilePosition;
    private Point movingPlayerHandPosition;
    private Point draggingOffset;
    public boolean gameReady;

    Bitmap bg;
    public Context mContext;


    public RummyGameLogic(PaintBucket bucket, Vibrator vib,Context mcontext) {

        Bucket = bucket;
        this.vib = vib;
                                 this.mContext=mcontext;


        Paint p = Bucket.AddPaint("outerTile");
        p.setARGB(255, 181, 167, 146);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.STROKE);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(153, 141, 123));

        p = Bucket.AddPaint("outerTileHighlight");
        p.setARGB(255, 255, 212, 84);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.STROKE);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(255, 212, 84));


        p = Bucket.AddPaint("outerTileHighlightSet");
        p.setARGB(255, 224, 208, 82);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.STROKE);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(224, 208, 82));


        p = Bucket.AddPaint("tileArea");
        p.setARGB(255, 160, 154, 188);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(224, 208, 82));


        p = Bucket.AddPaint("outerTileLongPressed");
        p.setARGB(255, 255, 151, 66);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.STROKE);
        p.setShadowLayer(1f, 1, 1, Color.rgb(255, 151, 66));


        p = Bucket.AddPaint("innerTile");
        p.setARGB(255, 210, 196, 170);

        p = Bucket.AddPaint("tileCircle");
        p.setARGB(255, 181, 167, 146);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(153, 141, 123));

        p = Bucket.AddPaint("tileCircleHighlight");
        p.setARGB(255, 255, 212, 84);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(153, 141, 123));


        p = Bucket.AddPaint("tileText1");
        p.setARGB(255, 255, 0, 0);
        p.setAntiAlias(true);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));




        bg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg);
    }


    RummySet playerSet = new RummySet();

    public void setPlayerTiles(TileData[] playerTiles) {

        playerSet.setBucket(Bucket);
        RummyTile[] tc = new RummyTile[playerTiles.length];
        int i = 0;
        for (TileData pt : playerTiles) {
            tc[i++] = new RummyTile(pt.Number, TileColor.getColor(pt.Color));

            playerSet.addTile(tc[i - 1]);
        }

        this.playerTiles = new ArrayList<RummyTile>(Arrays.asList(tc));
    }

    public void addSet(RummySet set) {
        Sets.add(set);
        set.Logic = this;
        set.setBucket(Bucket);
    }

    public void draw(Canvas canvas) {


        canvas.drawBitmap(bg, 0, 0, null);
        if(!gameReady){
            canvas.drawText("Waiting... ", 100,60, Bucket.GetPaint("tileText1"));

            return;
        }
        panning.Offset(Point.Min(mouseVector,new Point(12,12)));
        mouseVector .OffsetToZero(new Point(12,12));
        canvas.save();
 
        
        
        
        
        
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(panning.X, panning.Y);

        for (int i = 0; i < Sets.size(); i++) {
            Sets.get(i).draw(canvas);
        }
        if (movingPlayerTile != null) {
            if (movingPlayerHandPosition == null) {
                movingPlayerTile.draw(movingPlayerTilePosition.X - draggingOffset.X, movingPlayerTilePosition.Y - draggingOffset.Y, canvas);
            }
        }

        canvas.restore();

        Rectangle tileArea = getTileArea();
        Rectangle rec = new Rectangle(tileArea);
        rec.Y -= 9;
        rec.Height += 18;
        canvas.drawRect(rec.toRectF(), Bucket.GetPaint("tileArea"));

        if (playerTiles != null && playerTiles.size() > 0) {

            Point tilz = new Point(tileArea.X, tileArea.Y);
            int wrap = (int) (tileArea.Width / (playerTiles.get(0).Width + 7));
            for (int i = 0; i < playerTiles.size(); i++) {
                playerTiles.get(i).draw(tilz.X + (playerTiles.get(i).Width + 7) * (i % wrap), tilz.Y + (playerTiles.get(i).Height + 7) * ((float) Math.floor(i / wrap)), canvas);
            }
        }

        if (movingPlayerTile != null) {
            if (movingPlayerHandPosition != null) {
                movingPlayerTile.draw(movingPlayerHandPosition.X - draggingOffset.X, movingPlayerHandPosition.Y - draggingOffset.Y, canvas);
            }
        }

        canvas.restore();


    }

    public RummySet checkSetCollision(float x, float y) {
        for (int i = 0; i < Sets.size(); i++) {
            if (Sets.get(i).collides(x, y)) {
                return Sets.get(i);
            }
        }
        return null;
    }

    Timer longPressTimer;

    Point mouseLocation = new Point(0, 0);
    Point mouseVector = new Point(0, 0);

    Rectangle _tileArea;

    private Rectangle getTileArea() {
        if (_tileArea == null)
            if (playerTiles != null && playerTiles.size() > 0) {

                int wrap = (int) (mCanvasWidth / (playerTiles.get(0).Width + 7));
                float pSize = (playerTiles.get(0).Height + 7) * (1+(float) Math.ceil((playerTiles.size()-1) / wrap));
                _tileArea = new Rectangle(0, mCanvasHeight - pSize, mCanvasWidth, pSize);
            }
        return _tileArea;
    }

    ArrayList<RummyTile> playerTiles;

    public void touchDown(MotionEvent event) {
        final Point mousePoint = new Point(event.getX(), event.getY());
        mouseLocation = new Point(mousePoint);
        Rectangle tileArea = getTileArea();

        if (tileArea.Collides(mousePoint)) {
            touchDownOnPlayerTiles(mousePoint, tileArea);
            return;
        }

        sureUpPoint(mousePoint);
        final RummySet set = checkSetCollision(mousePoint.X, mousePoint.Y);

        if (set != null) {
            set.beginDragging(mousePoint.X, mousePoint.Y);
            final RummyTile collidedTile = set.collideWithTile(mousePoint.X, mousePoint.Y);
            longPressTimer = new Timer();

            longPressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (longPressTimer == null) return;
                    sureUpPoint(mouseLocation);
                    if (Math.abs(mousePoint.X - mouseLocation.X) < 5 && Math.abs(mousePoint.Y - mouseLocation.Y) < 5) {
                        collidedTile.longPress(mouseLocation.X, mouseLocation.Y);
                        vib.vibrate(60l);
                        clickMode = ClickMode.LongPressed;
                    }
                }
            }, 750);
            draggingSet = set;
            clickMode = ClickMode.DraggingSet;
        } else {
            mouseVector.X = 0;
            mouseVector.Y = 0;

            clickMode = ClickMode.Panning;
        }
    }

    private void touchDownOnPlayerTiles(Point mousePoint, Rectangle tileArea) {
        if (playerTiles != null && playerTiles.size() > 0) {

            Point tilz = new Point(tileArea.X, tileArea.Y);
            int wrap = (int) (tileArea.Width / (playerTiles.get(0).Width + 7));
            for (int i = 0; i < playerTiles.size(); i++) {


                float px = tilz.X + (playerTiles.get(i).Width + 7) * (i % wrap);
                float py = tilz.Y + (playerTiles.get(i).Height + 7) * ((float) Math.floor(i / wrap));
                if (new Rectangle(tilz.X + (playerTiles.get(i).Width + 7) * (i % wrap), tilz.Y + (playerTiles.get(i).Height + 7) * ((float) Math.floor(i / wrap)), playerTiles.get(i).Width, playerTiles.get(i).Height).Collides(mousePoint)) {


                    playerTiles.get(i).highlighted = true;
                    clickMode = ClickMode.DraggingPlayerTile;

                    movingPlayerTile = playerTiles.get(i);


                    draggingOffset = new Point(mousePoint.X - px, mousePoint.Y - py);

                    movingPlayerHandPosition = mousePoint;


                    RummySet empty = new RummySet();
                    empty.setBucket(playerTiles.get(i).Set.Bucket);

                    playerTiles.get(i).Set.removeTile(playerTiles.get(i));

                    playerTiles.remove(i);


                    empty.addTile(movingPlayerTile);


                    break;
                }

            }

        }
        clickMode = ClickMode.InTileArea;
    }

    public void touchUp(MotionEvent event) {

        if (longPressTimer != null) longPressTimer.cancel();
        longPressTimer = null;
        if (playerTiles != null) {
            for (int i = 0; i < playerTiles.size(); i++) {
                playerTiles.get(i).highlighted = false;
            }
        }

        if (movingPlayerTile != null) {
            movingPlayerTile.highlighted = false;
            Point m = new Point(event.getX(), event.getY());

            if (_tileArea.Collides(m)) {

                boolean added = false;
                if (playerTiles != null && playerTiles.size() > 0) {

                    Point tilz = new Point(_tileArea.X, _tileArea.Y);
                    int wrap = (int) (_tileArea.Width / (playerTiles.get(0).Width + 7));
                    for (int i = 0; i < playerTiles.size(); i++) {
                        Rectangle rect = new Rectangle(tilz.X + (playerTiles.get(i).Width + 7) * (i % wrap), tilz.Y + (playerTiles.get(i).Height + 7) * ((float) Math.floor(i / wrap)),
                                playerTiles.get(i).Width, playerTiles.get(i).Height);
                        if (movingPlayerHandPosition != null && rect.Collides(movingPlayerHandPosition)) {
                            playerSet.addTile(i, movingPlayerTile);
                            playerTiles.add(i, movingPlayerTile);
                            added = true;
                        }
                    }
                }

                if(!added){
                    playerSet.addTile(movingPlayerTile);
                    playerTiles.add(movingPlayerTile);
                }


            } else {

                RummySet rs = new RummySet();
                rs.setPosition(movingPlayerTilePosition.X - draggingOffset.X, movingPlayerTilePosition.Y - draggingOffset.Y);
                addSet(rs);
                rs.addTile(movingPlayerTile);
            }

            movingPlayerTile = null;
        }


        this.clickMode = ClickMode.None;
        if (draggingSet != null) {
            draggingSet.endDragging();
            draggingSet = null;
        }


    }

    public void touchMove(MotionEvent event) {
        Point last = new Point(mouseLocation);
        sureUpPoint(last);

        mouseLocation = new Point(event.getX(), event.getY());
        Point mousePoint = new Point(mouseLocation);
        sureUpPoint(mousePoint);

        if (movingPlayerTile != null) {
            {

                if (_tileArea.Collides(mouseLocation)) {
                    movingPlayerHandPosition =mouseLocation;
                    movingPlayerTilePosition = null;
                } else {
                    movingPlayerTilePosition =  mousePoint;
                    movingPlayerHandPosition = null;
                }
            }

        } else if (draggingSet != null) {
            draggingSet.draggingAround(mousePoint.X, mousePoint.Y);
        } else {
            if (clickMode == clickMode.Panning) {
                last.Negate(mousePoint);
                mouseVector.Negate(last);
                mouseVector.Magnify(1.12f);
            }
        }

    }

    private void sureUpPoint(Point last) {
        last.Negate(panning);
    }

    public void resize(int width, int height) {

        mCanvasWidth = width;
        mCanvasHeight = height;
        _tileArea = null;
        getTileArea();
        bg = Bitmap.createScaledBitmap(bg, mCanvasWidth, mCanvasHeight, false);

    }

}


public class PlayerInformation{
    public ArrayList<RummySet> Sets = new ArrayList<RummySet>();
}