package com.TileRummy;

import MessageParseJunk.RummyGameGameRoomMessage;
import MessageParseJunk.TileData;
import android.content.Context;
import android.graphics.*;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.TileRummy.LampLight.PaintBucket;
import com.TileRummy.Service.MultiRunner;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class RummyGameLogic {


    public ArrayList<PlayerInformation> playerInformation = new ArrayList<PlayerInformation>();


    private Vibrator vib;
    private ClickMode clickMode;
    Point panning = new Point(50, 50);
    public float scaleFactor = 1f;

    int mCanvasHeight = 1;
    public int mCanvasWidth = 1;
    MovingItem draggingItem;

    public boolean gameReady;


    public RummySet draggingSet;

    Bitmap bg;
    public Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private MultiRunner runner;

    public PaintBucket Bucket = new PaintBucket();

    public RummyGameLogic(Vibrator vib, Context mcontext, SurfaceHolder mSurfaceHolder, MultiRunner runner) {

        this.vib = vib;
        this.mContext = mcontext;
        this.mSurfaceHolder = mSurfaceHolder;
        this.runner = runner;


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
        p.setDither(true);
        LinearGradient gradient = new LinearGradient(0, 0, RummyTile.Width, RummyTile.Height, Color.rgb(210, 196, 170), Color.rgb(255, 218, 190), Shader.TileMode.CLAMP);
        p.setShader(gradient);


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
        p = Bucket.AddPaint("tileText2");
        p.setARGB(255, 0, 127, 127);
        p.setAntiAlias(true);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));
        p = Bucket.AddPaint("tileText3");
        p.setARGB(255, 0, 255, 0);
        p.setAntiAlias(true);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));
        p = Bucket.AddPaint("tileText4");
        p.setARGB(255, 0, 0, 255);
        p.setAntiAlias(true);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));

        p = Bucket.AddPaint("nameText");
        p.setARGB(255, 255, 0, 0);
        p.setAntiAlias(true);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));


        bg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg);
    }


    public void setPlayerTiles(TileData[] playerTiles) {
        this.playerTiles = new RummySet();
        this.playerTiles.setBucket(Bucket);
        int i = 0;
        for (TileData pt : playerTiles) {
            this.playerTiles.addTile(new RummyTile(pt.Number, TileColor.getColor(pt.Color)));
        }
    }

    public enum DraggingTileState {
        Empty, PlayerInHand, PlayerInTile, FromTile
    }

    public DraggingTileState draggingState = DraggingTileState.Empty;

    public void draw(Canvas canvas) {


        canvas.drawBitmap(bg, 0, 0, null);
        if (!gameReady) {
            canvas.drawText("Waiting... ", 100, 60, Bucket.GetPaint("tileText1"));

            return;
        }
        Point c = new Point(Math.abs(mouseVector.X) * .6f, Math.abs(mouseVector.Y) * .6f);
        panning.Offset(Point.Min(mouseVector, c));
        mouseVector.OffsetToZero(c);
        canvas.save();


        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(panning.X, panning.Y);

        float playerOffset = 0;
        for (PlayerInformation inf : playerInformation) {
            inf.setPosition(playerOffset, 25f);
            inf.draw(canvas);
            playerOffset += inf.Width;
        }

        switch (draggingState) {

            case Empty:
                break;
            case PlayerInHand:

                break;
            case PlayerInTile:
                draggingItem.tile.setPosition(draggingItem.itemPosition.X - draggingItem.draggingOffset.X, draggingItem.itemPosition.Y - draggingItem.draggingOffset.Y);
                draggingItem.tile.draw(canvas);
                break;
            case FromTile:
                break;
        }


        canvas.restore();

        float pSize;
        _tileArea = new Rectangle(0, mCanvasHeight - (pSize = playerTiles.getHeight(mCanvasWidth)), mCanvasWidth, pSize);

        _tileArea.Y -= 9;
        _tileArea.Height += 18;
        canvas.drawRect(_tileArea.toRectF(), Bucket.GetPaint("tileArea"));

        if (playerTiles != null) {
            playerTiles.setPosition(0, mCanvasHeight - pSize);
            playerTiles.draw(_tileArea.Width, canvas);
        }

        switch (draggingState) {

            case Empty:
                break;
            case PlayerInHand:
                draggingItem.tile.setPosition(draggingItem.itemPosition.X - draggingItem.draggingOffset.X, draggingItem.itemPosition.Y - draggingItem.draggingOffset.Y);
                draggingItem.tile.draw(canvas);
                break;
            case PlayerInTile:
                break;
            case FromTile:
                break;
        }

        canvas.restore();


    }


    public void updateEngine() {
    }

    public void fling(float velocityX, float velocityY) {
    }

    public void longPress(MotionEvent e) {
        Point mousePoint = new Point(e.getX(), e.getY());
        sureUpPoint(mousePoint);

        for (PlayerInformation pl : playerInformation) {
            if (pl.longPress(mousePoint)) {
                return;
            }
        }
    }

    public boolean singleTap(MotionEvent e) {
        return false;
    }

    public boolean doubleTap(MotionEvent e) {
        return false;
    }


    public RummySet playerTiles;
    Timer longPressTimer;

    Point mouseLocation = new Point(0, 0);
    Point mouseVector = new Point(0, 0);

    Rectangle _tileArea;

    public void touchDown(MotionEvent event) {

        final Point mousePoint = new Point(event.getX(), event.getY());
        mouseLocation = new Point(mousePoint);


        if (_tileArea.Collides(mousePoint)) {
            touchDownOnPlayerTiles(mousePoint);
            return;
        }

        sureUpPoint(mousePoint);
        mouseVector.X = 0;
        mouseVector.Y = 0;

        clickMode = ClickMode.Panning;

    }

    private void touchDownOnPlayerTiles(Point mousePoint) {
        RummyTile tile = playerTiles.collideWithTile(mousePoint.X, mousePoint.Y);
        if (tile == null) return;

        tile.highlighted = true;
        clickMode = ClickMode.DraggingPlayerTile;

        draggingItem = new MovingItem(tile, mousePoint, new Point(mousePoint.X - tile.X, mousePoint.Y - tile.Y));
        draggingState = DraggingTileState.PlayerInHand;
        draggingSet = new RummySet();
        draggingSet.setBucket(tile.Set.Bucket);

        playerTiles.removeTile(tile);
        draggingSet.addTile(draggingItem.tile);


    }

    public void touchUp(MotionEvent event) {


        for (int c = playerTiles.tiles.size() - 1, setSize = 0; c >= setSize; c--) {
            if (playerTiles.tiles.get(c).dummy == 1) {
                playerTiles.removeTile(playerTiles.tiles.get(c));
            }
        }

        if (longPressTimer != null) longPressTimer.cancel();
        longPressTimer = null;
        if (playerTiles != null) {
            for (int i = 0; i < playerTiles.tiles.size(); i++) {
                playerTiles.tiles.get(i).highlighted = false;
            }
        }
        switch (draggingState) {

            case Empty:
                break;
            case PlayerInTile:
                PlayerInformation pl = getPlayer(draggingItem.itemPosition);
                if (pl == null) {

                    playerTiles.addTile(draggingItem.tile);


                } else {
                    RummySet set = pl.getSet(draggingItem.itemPosition);
                    if (set == null) {
                        RummySet rs = new RummySet();
                        rs.setPosition(draggingItem.itemPosition.X - draggingItem.draggingOffset.X, draggingItem.itemPosition.Y - draggingItem.draggingOffset.Y);
                        pl.addSet(rs);
                        rs.addTile(draggingItem.tile);
                        try {
                            runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddSetToPlayer, pl.name).GenerateMessage());
                            runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, new TileData[]{draggingItem.tile.getTileData()}, pl.name, pl.Sets.size() - 1).GenerateMessage());
                        } catch (XMPPException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    } else {
                        RummyTile tile = set.collideWithTile(draggingItem.itemPosition.X, draggingItem.itemPosition.Y);
                        if (tile == null) {
                            set.addTile(draggingItem.tile);
                            try {
                                runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, new TileData[]{draggingItem.tile.getTileData()}, pl.name, pl.Sets.indexOf(set)).GenerateMessage());
                            } catch (XMPPException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            
                        } else {
                            set.addTile(set.tiles.indexOf(tile), draggingItem.tile);

                            try {                                                                                                                 //TODO:::Index
                                runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, new TileData[]{draggingItem.tile.getTileData()}, pl.name, pl.Sets.indexOf(set)).GenerateMessage());
                            } catch (XMPPException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                        }
                    }
                }


                break;
            case PlayerInHand:

                draggingItem.tile.highlighted = false;
                Point m = new Point(event.getX(), event.getY());
                boolean added = false;
                if (_tileArea.Collides(m)) {
                    added = playerTiles.dropTile(draggingItem.tile, m);
                }

                if (!added) {
                    playerTiles.addTile(draggingItem.tile);
                }
                break;
        }

        draggingState = DraggingTileState.Empty;
        this.clickMode = ClickMode.None;
        if (draggingSet != null)

        {
            draggingSet.endDragging();
            draggingSet = null;
        }

        draggingItem = null;

        for (int i1 = playerInformation.size() - 1, playerInformationSize = 0; i1 >= playerInformationSize; i1--) {
            PlayerInformation infs = playerInformation.get(i1);
            ArrayList<RummySet> sets = infs.Sets;
            for (int c = sets.size() - 1, setSize = 0; c >= setSize; c--) {
                if (sets.get(c).dummy) {
                    sets.get(c).Player.removedSet(sets.get(c));
                    continue;
                }
                ArrayList<RummyTile> tiles = sets.get(c).tiles;
                for (int i = tiles.size() - 1, tilesSize = 0; i >= tilesSize; i--) {
                    RummyTile tile = tiles.get(i);
                    if (tile.dummy > 0) {
                        tile.Set.removeTile(tile);
                        continue;

                    }
                }

            }
        }


    }

    private PlayerInformation getPlayer(Point itemPosition) {
        for (PlayerInformation pls : playerInformation) {
            if (pls.collides(itemPosition)) {
                return pls;
            }
        }
        return null;
    }

    public void touchMove(MotionEvent event) {
        Point last = new Point(mouseLocation);
        sureUpPoint(last);
        synchronized (mSurfaceHolder) {

            mouseLocation = new Point(event.getX(), event.getY());
            Point mousePoint = new Point(mouseLocation);
            sureUpPoint(mousePoint);
            for (int i1 = playerInformation.size() - 1, playerInformationSize = 0; i1 >= playerInformationSize; i1--) {
                PlayerInformation infs = playerInformation.get(i1);
                ArrayList<RummySet> sets = infs.Sets;
                for (int c = sets.size() - 1, setSize = 0; c >= setSize; c--) {
                    if (sets.get(c).dummy) {
                        sets.remove(c);
                        continue;
                    }
                    ArrayList<RummyTile> tiles = sets.get(c).tiles;
                    for (int i = tiles.size() - 1, tilesSize = 0; i >= tilesSize; i--) {
                        RummyTile tile = tiles.get(i);
                        if (tile.dummy == 1) {
                            tile.dummy = 2;
                            continue;

                        }
                    }

                }
            }


            for (int c = playerTiles.tiles.size() - 1, setSize = 0; c >= setSize; c--) {
                if (playerTiles.tiles.get(c).dummy == 1) {
                    playerTiles.tiles.get(c).dummy = 2;
                }
            }


            switch (draggingState) {

                case Empty:
                    break;
                case PlayerInTile:
                    PlayerInformation pl = getPlayer(draggingItem.itemPosition);
                    if (pl == null) {


                    } else {
                        RummySet set = pl.getSet(draggingItem.itemPosition);
                        if (set == null) {


                            RummySet rs;
                            pl.addSet(rs = new RummySet(true));
                            rs.addTile(new RummyTile(true));
                        } else {
                            RummyTile tile = set.collideWithTile(draggingItem.itemPosition.X, draggingItem.itemPosition.Y);
                            if (tile == null) {
                                set.addTile(new RummyTile(true));
                            } else {
                                set.addTile(set.tiles.indexOf(tile), new RummyTile(true));
                            }
                        }
                    }


                    break;
                case PlayerInHand:

                    Point m = new Point(event.getX(), event.getY());
                    boolean added = false;
                    if (_tileArea.Collides(m)) {
                        added = playerTiles.dropTile(new RummyTile(true), m);
                    }

                    if (!added) {
                        playerTiles.addTile(new RummyTile(true));
                    }
                    break;
            }


            if (draggingItem != null) {
                {

                    if (_tileArea.Collides(mouseLocation)) {
                        draggingItem.itemPosition = mouseLocation;
                        draggingState = DraggingTileState.PlayerInHand;
                    } else {
                        draggingItem.itemPosition = mousePoint;
                        draggingState = DraggingTileState.PlayerInTile;
                    }
                }

            } else if (draggingSet != null) {
                draggingSet.draggingAround(mousePoint.X, mousePoint.Y);
            } else {
                if (clickMode == clickMode.Panning) {
                    last.Negate(mousePoint);
                    mouseVector.Negate(last);
                    mouseVector.Magnify(1.24f);
                    mouseVector.Limit(30f);
                }
            }


            for (int i1 = playerInformation.size() - 1, playerInformationSize = 0; i1 >= playerInformationSize; i1--) {
                PlayerInformation infs = playerInformation.get(i1);
                ArrayList<RummySet> sets = infs.Sets;
                for (int c = sets.size() - 1, setSize = 0; c >= setSize; c--) {
                    if (sets.get(c).dummy) {
                        sets.get(c).Player.removedSet(sets.get(c));
                        continue;
                    }
                    ArrayList<RummyTile> tiles = sets.get(c).tiles;
                    for (int i = tiles.size() - 1, tilesSize = 0; i >= tilesSize; i--) {
                        RummyTile tile = tiles.get(i);
                        if (tile.dummy == 2) {
                            tile.Set.removeTile(tile);
                            continue;

                        }
                    }

                }
            }


            for (int c = playerTiles.tiles.size() - 1, setSize = 0; c >= setSize; c--) {
                if (playerTiles.tiles.get(c).dummy == 2) {
                    playerTiles.removeTile(playerTiles.tiles.get(c));
                }
            }

        }
    }

    private void sureUpPoint(Point last) {
        last.Negate(panning);
    }

    public void resize(int width, int height) {

        mCanvasWidth = width;
        mCanvasHeight = height;

        bg = Bitmap.createScaledBitmap(bg, mCanvasWidth, mCanvasHeight, false);

    }


}


