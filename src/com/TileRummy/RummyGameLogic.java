package com.TileRummy;

import MessageParseJunk.RummyGameGameRoomMessage;
import MessageParseJunk.TileData;
import android.content.Context;
import android.graphics.*;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.TileRummy.LampLight.PaintBucket;
import com.TileRummy.Service.GameInformation;
import com.TileRummy.Service.MultiRunner;
import com.TileRummy.Utils.*;
import com.TileRummy.Utils.Point;
import com.TileRummy.drawables.*;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;


public class RummyGameLogic {


    public ArrayList<PlayerInformation> playerInformation = new ArrayList<PlayerInformation>();

    public TouchDownState touchState = TouchDownState.Empty;
    private GameMenu menu;
    private Vibrator vib;
    Point panning = new Point(50, 50);
    public float scaleFactor = 1f;

    int mCanvasHeight = 1;
    public int mCanvasWidth = 1;
    public MovingItem draggingItem;

    public RummySet playerTiles;

    Point mouseVector = new Point(0, 0);

    Rectangle _tileArea;
    public boolean gameReady;


    Bitmap bg;
    public Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private MultiRunner runner;

    public PaintBucket Bucket = new PaintBucket();
    private Point gravity = new Point(10, 10);

    public RummyGameLogic(Vibrator vib, final Context mcontext, SurfaceHolder mSurfaceHolder, final MultiRunner runner) {

        menu = new GameMenu(this);
        menu.addButton(new GameMenuButton("Take Tile", new Point(15, 35), "buttonPaint", new Runnable() {
            @Override
            public void run() {
                try {
                    runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.GiveMeTile, GameInformation.UserName).GenerateMessage());
                } catch (XMPPException e) {
                    e.printStackTrace();
                }

            }
        }));
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


        p = Bucket.AddPaint("menuBackground");
        p.setARGB(255, 11, 208, 82);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.FILL_AND_STROKE);


        p = Bucket.AddPaint("linePaint");
        p.setARGB(255, 224, 208, 82);
        p.setStrokeWidth(3);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(255, 212, 84));
        p.setStyle(Paint.Style.FILL_AND_STROKE);


        p = Bucket.AddPaint("buttonPaint");
        p.setARGB(255, 255, 208, 82);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        p = Bucket.AddPaint("buttonText");
        p.setARGB(255, 0, 127, 127);
        p.setTextSize(13);
        p.setTypeface(Typeface.DEFAULT_BOLD);


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
        LinearGradient gradient = new LinearGradient(0, 0, RummyTile.Width, RummyTile.Height, Color.rgb(210, 196, 170), Color.rgb(255, 218, 190), Shader.TileMode.CLAMP);
        p.setShader(gradient);


        p = Bucket.AddPaint("tileCircle");
        p.setARGB(255, 181, 167, 146);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(153, 141, 123));

        p = Bucket.AddPaint("tileCircleHighlight");
        p.setARGB(255, 255, 212, 84);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(153, 141, 123));


        p = Bucket.AddPaint("tileText1");
        p.setARGB(255, TileColor.Red.colorInfo[0], TileColor.Red.colorInfo[1], TileColor.Red.colorInfo[2]);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));

        p = Bucket.AddPaint("tileText2");
        p.setARGB(255, TileColor.Blue.colorInfo[0], TileColor.Blue.colorInfo[1], TileColor.Blue.colorInfo[2]);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));

        p = Bucket.AddPaint("tileText3");
        p.setARGB(255, TileColor.Green.colorInfo[0], TileColor.Green.colorInfo[1], TileColor.Green.colorInfo[2]);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));

        p = Bucket.AddPaint("tileText4");
        p.setARGB(255, TileColor.Purple.colorInfo[0], TileColor.Purple.colorInfo[1], TileColor.Purple.colorInfo[2]);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));

        p = Bucket.AddPaint("nameText");
        p.setARGB(255, 255, 0, 0);
        p.setTextSize(24);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setShadowLayer(0.5f, 1, 1, Color.rgb(209, 0, 0));


        bg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg);
    }


    public void setPlayerTiles(TileData[] playerTiles) {
        synchronized (mSurfaceHolder) {
            this.playerTiles = new RummySet();
            this.playerTiles.setBucket(Bucket);
            int i = 0;
            for (TileData pt : playerTiles) {
                this.playerTiles.addTile(new RummyTile(pt.Number, TileColor.getColor(pt.Color)));
            }
        }

    }


    public void draw(Canvas canvas) {


        canvas.drawBitmap(bg, 0, 0, null);
        if (!gameReady) {
            canvas.drawText("Waiting... ", 100, 60, Bucket.GetPaint("tileText1"));

            return;
        }


        panning.Offset(mouseVector);
        mouseVector.OffsetToZero(gravity);

        float playerOffset = 15;
        float minHeight = 0;
        for (PlayerInformation inf : playerInformation) {
            inf.setPosition(playerOffset, 25f);
            if (minHeight < inf.getHeight()) {
                minHeight = inf.getHeight();
            }
            playerOffset += inf.getWidth();

        }
        limitPanning(minHeight, playerOffset);

        canvas.save();

        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(panning.X, panning.Y);

        for (PlayerInformation inf : playerInformation) {
            inf.draw(canvas);

            canvas.drawLine(inf.Location.X - 15, 25, inf.Location.X - 15, minHeight, Bucket.GetPaint("linePaint"));
        }

        canvas.drawLine(playerOffset, 15, playerOffset, minHeight, Bucket.GetPaint("linePaint"));

        switch (touchState) {

            case Empty:
                break;
            case PlayerInHand:

                break;
            case PlayerInTile:
            case FromTile:
                draggingItem.set.setPosition(draggingItem.getRealPosition());
                draggingItem.set.draw(1000, canvas);
                break;
        }


        canvas.restore();


        if (playerTiles != null) {
            float pSize;
            _tileArea = new Rectangle(0, mCanvasHeight - (pSize = playerTiles.getHeight(mCanvasWidth, true)), mCanvasWidth, pSize);

            _tileArea.Y -= 9;
            _tileArea.Height += 18;
            canvas.drawRect(_tileArea.toRectF(), Bucket.GetPaint("tileArea"));

            playerTiles.setPosition(new Point(6, mCanvasHeight - pSize));
            playerTiles.draw(_tileArea.Width, canvas);
        }

        switch (touchState) {

            case Empty:
                break;
            case PlayerInHand:
                draggingItem.set.setPosition(draggingItem.getRealPosition());
                draggingItem.set.draw(1000, canvas);
                break;
            case PlayerInTile:
                break;
            case FromTile:
                break;
        }
        menu.draw(mCanvasWidth, canvas);

        canvas.restore();


    }

    private void limitPanning(float minHeight, float playerOffset) {
        if (panning.X > 50)
            panning.X = 50;
        if (panning.Y > 30)
            panning.Y = 30;
        if (panning.Y < -(minHeight - 150))
            panning.Y = -(minHeight - 150);
        if (panning.X < -(playerOffset - 250))
            panning.X = -(playerOffset - 250);
    }


    public void updateEngine() {
    }

    public boolean drag(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Point mouseLocation = new Point(e2.getX(), e2.getY());
        Point mousePoint = new Point(mouseLocation);

        if (_tileArea.Collides(mousePoint) && draggingItem == null) {
            RummyTile tile = playerTiles.collideWithTile(mousePoint);
            if (tile != null)
                touchDownOnPlayerTiles(tile, mousePoint);
        }


        if (draggingItem == null) {
            offsetPanning(-((distanceX) * .05f), -((distanceY) * .05f));
        }

        sureUpPoint(mousePoint);

        if (draggingItem != null) {
            synchronized (mSurfaceHolder) {
                if (_tileArea.Collides(mouseLocation)) {
                    updateDraggingPosition(mouseLocation);
                    setTouchState(TouchDownState.PlayerInHand);
                } else {
                    updateDraggingPosition(mousePoint);
                    setTouchState(TouchDownState.PlayerInTile);
                }
            }
        } else {
            if ((touchState == TouchDownState.Empty || touchState == TouchDownState.Panning)) {
                setTouchState(TouchDownState.Panning);
            }
        }


        switch (touchState) {

            case Empty:
                break;

            case PlayerInTile:
            case FromTile:
                PlayerInformation pl = getPlayer(draggingItem.getRealPosition());

                resetEmptyTile();

                if (pl != null) {
                    RummySet set = pl.getSet(draggingItem.getRealPosition());
                    if (set == null) {
                        pl.EmptySet = true;
                    } else {
                        set.dropTile(null, draggingItem.getRealPosition());

                    }
                }


                break;
            case PlayerInHand:

                Point m = new Point(e2.getX(), e2.getY());
                resetEmptyTile();
                if (_tileArea.Collides(m)) {
                    playerTiles.dropTile(null, m);
                }

                break;
            case Panning:
                offsetPanning(distanceX, distanceY);
                break;
        }


        return true;
    }

    private void updateDraggingPosition(Point loc) {
        synchronized (mSurfaceHolder) {
            draggingItem.updatePosition(loc);
        }
    }

    private void resetEmptyTile() {

        synchronized (mSurfaceHolder) {
            for (PlayerInformation inf : playerInformation) {
                inf.EmptySet = false;
                for (RummySet set : inf.Sets) {
                    set.EmptyTileIndex = -1;
                }
            }

            if (playerTiles != null)
                playerTiles.EmptyTileIndex = -1;
        }
    }


    private void offsetPanning(float distanceX, float distanceY) {
        synchronized (mSurfaceHolder) {
            panning.X += distanceX;
            panning.Y += distanceY;
        }
    }

    public void fling(float velocityX, float velocityY) {
        if (touchState == TouchDownState.Empty || touchState == TouchDownState.Panning) {
            setMouseVector(-velocityX * .05f, -velocityY * .05f);
            setTouchState(TouchDownState.Panning);
        }
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

    public boolean singleTap(MotionEvent event) {
        final Point mousePoint = new Point(event.getX(), event.getY());

        if (menu.collides(mousePoint, mCanvasWidth)) {
            menu.touchDown(mousePoint);
        }


        if (_tileArea.Collides(mousePoint)) {
            RummyTile tile = playerTiles.collideWithTile(mousePoint);
            if (tile == null) return false;
            tile.Selected = !tile.Selected;

        }
        return true;

    }

    public boolean doubleTap(MotionEvent e) {
        Point p = new Point(e.getX(), e.getY());

        if (menu.collides(p, mCanvasWidth)) {
            menu.doubleTap(p);
        }
        return false;
    }

    private void setMouseVector(float i, float i1) {
        synchronized (mSurfaceHolder) {
            mouseVector = new Point(i, i1);
        }
    }

    private void setTouchState(TouchDownState tds) {
        synchronized (mSurfaceHolder) {
            touchState = tds;
        }
    }

    public void onDown(MotionEvent event) {

    }

    private void touchDownOnPlayerTiles(RummyTile tile, Point mousePoint) {
        synchronized (mSurfaceHolder) {

            tile.highlighted = true;
            RummySet draggingSet = new RummySet();
            draggingSet.setBucket(tile.Bucket);

            ArrayList<RummyTile> tiles = playerTiles.tiles;
            for (int i = tiles.size() - 1; i >= 0; i--) {
                RummyTile tl = tiles.get(i);
                if (tl.Selected || tl.highlighted) {
                    playerTiles.removeTile(tl);
                    tl.highlighted = false;
                    tl.Selected = false;
                    draggingSet.addTile(tl);
                }
            }

            draggingItem = new MovingItem(draggingSet, mousePoint, new Point(mousePoint.X - tile.X, mousePoint.Y - tile.Y));
            setTouchState(TouchDownState.PlayerInHand);

        }
    }

    public BackupState lastState;

    public void touchUp(MotionEvent event) {
        Point mp = new Point(event.getX(), event.getY());
        menu.touchUp(mp);
        resetEmptyTile();
        synchronized (mSurfaceHolder) {
            if (playerTiles != null) {
                for (int i = 0; i < playerTiles.tiles.size(); i++) {
                    playerTiles.tiles.get(i).highlighted = false;
                }

            }
            resetEmptyTile();
        }

        if (touchState != TouchDownState.PlayerInHand) {
            if (!_tileArea.Collides(mp)) {
                for (RummyTile tile : playerTiles.tiles) {
                    tile.Selected = false;
                }
            }
        }

        switch (touchState) {

            case Empty:
                break;
            case FromTile:
            case PlayerInTile:
                PlayerInformation pl = getPlayer(draggingItem.getRealPosition());
                if (pl == null) {
                    if (touchState == TouchDownState.FromTile) {
                        PlayerInformation pi = getPlayerInfoByName(lastState.PlayerName);
                        RummySet st = pi.Sets.get(lastState.SetIndex);
                        int c = 0;
                        for (RummyTile tile : draggingItem.set.tiles) {
                            st.addTile(lastState.TileIndex + (c++), tile);
                        }

                    } else {
                        for (RummyTile tile : draggingItem.set.tiles) {
                            playerTiles.addTile(tile);
                        }
                    }

                } else {
                    RummySet set = pl.getSet(draggingItem.getRealPosition());
                    if (set == null) {
                        RummySet rs = new RummySet();
                        pl.addSet(rs);
                        for (RummyTile tile : draggingItem.set.tiles) {
                            rs.addTile(tile);
                        }

                        try {
                            runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddSetToPlayer, pl.name).GenerateMessage());
                            runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, draggingItem.set.getTileData(), pl.name, pl.Sets.size() - 1, 0).GenerateMessage());
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    } else {
                        RummyTile tile = set.collideWithTile(draggingItem.getRealPosition());
                        if (tile == null) {
                            for (RummyTile ttile : draggingItem.set.tiles) {
                                set.addTile(ttile);
                            }

                            try {
                                runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, draggingItem.set.getTileData(), pl.name, pl.Sets.indexOf(set), 0).GenerateMessage());
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }

                        } else {
                            int index = set.tiles.indexOf(tile);

                            if (draggingItem.getRealPosition().X > tile.X + RummyTile.Width / 2)
                                index++;
                            int c = 0;
                            for (RummyTile ttile : draggingItem.set.tiles) {
                                set.addTile(index + (c++), ttile);
                            }


                            try {                                                                                                                 //TODO:::Index

                                runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, draggingItem.set.getTileData(), pl.name, pl.Sets.indexOf(set), index).GenerateMessage());
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }


                break;
            case PlayerInHand:
                Point m = new Point(event.getX(), event.getY());

                if (_tileArea.Collides(m)) {

                    playerTiles.dropTile(draggingItem.set, m);
                }

                break;
        }

        touchState = TouchDownState.Empty;

        draggingItem = null;


    }

    private PlayerInformation getPlayerInfoByName(String playerName) {

        for (PlayerInformation pi : playerInformation) {
            if (pi.name.equals(playerName)) return pi;
        }
        return null;
    }

    private PlayerInformation getPlayer(Point itemPosition) {
        for (PlayerInformation pls : playerInformation) {
            if (pls.collides(itemPosition)) {
                return pls;
            }
        }
        return null;
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

