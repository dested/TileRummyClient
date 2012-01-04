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
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Timer;


public class RummyGameLogic {


    public ArrayList<PlayerInformation> playerInformation = new ArrayList<PlayerInformation>();

    private GameMenu menu;
    private Vibrator vib;
    private ClickMode clickMode;
    com.TileRummy.Utils.Point panning = new com.TileRummy.Utils.Point(50, 50);
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

    public RummyGameLogic(Vibrator vib, final Context mcontext, SurfaceHolder mSurfaceHolder, final MultiRunner runner) {

        menu = new GameMenu(this);
        menu.addButton(new GameMenuButton("Take Tile", new com.TileRummy.Utils.Point(15, 35),"buttonPaint", new Runnable() {
            @Override
            public void run() {
                try {
                    runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.GiveMeTile, GameInformation.UserName).GenerateMessage());
                } catch (XMPPException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

        p = Bucket.AddPaint("buttonPaint");
        p.setARGB(255, 255, 208, 82);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        p = Bucket.AddPaint("buttonText");
        p.setARGB(255, 0, 127, 127);
        p.setAntiAlias(true);
        p.setTextSize(17);
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
        com.TileRummy.Utils.Point c = new com.TileRummy.Utils.Point(Math.abs(mouseVector.X) * .6f, Math.abs(mouseVector.Y) * .6f);
        panning.Offset(com.TileRummy.Utils.Point.Min(mouseVector, c));
        mouseVector.OffsetToZero(c);
        canvas.save();


        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(panning.X, panning.Y );

        float playerOffset = 0;
        float minHeight = 0;
        for (PlayerInformation inf : playerInformation) {
            inf.setPosition(playerOffset, 25f);
            inf.draw(canvas);
            if (minHeight < inf.Height) {
                minHeight = inf.Height;
            }
            playerOffset += inf.Width;
        }

        if (panning.X < -15)
            panning.X = -15;
        if (panning.Y < -15)
            panning.Y = -15;
        if (panning.Y > minHeight - 150)
            panning.Y = minHeight - 150;
        if (panning.X > playerOffset - 150)
            panning.X = playerOffset - 150;

        switch (draggingState) {

            case Empty:
                break;
            case PlayerInHand:

                break;
            case PlayerInTile:
                draggingItem.tile.setPosition(draggingItem.getRealPosition());
                draggingItem.tile.draw(canvas);
                break;
            case FromTile:
                break;
        }

        if(draggingItem!=null){
        com.TileRummy.Utils.Point pos=draggingItem.getRealPosition();
        canvas.drawCircle(pos.X,pos.Y,10,Bucket.GetPaint("tileArea"));
        }
                
        canvas.restore();

        float pSize;
        _tileArea = new Rectangle(0, mCanvasHeight - (pSize = playerTiles.getHeight(mCanvasWidth)), mCanvasWidth, pSize);

        _tileArea.Y -= 9;
        _tileArea.Height += 18;
        canvas.drawRect(_tileArea.toRectF(), Bucket.GetPaint("tileArea"));

        if (playerTiles != null) {
            playerTiles.setPosition(new com.TileRummy.Utils.Point(6, mCanvasHeight - pSize));
            playerTiles.draw(_tileArea.Width, canvas);
        }

        switch (draggingState) {

            case Empty:
                break;
            case PlayerInHand:
                draggingItem.tile.setPosition(draggingItem.getRealPosition());
                draggingItem.tile.draw(canvas);
                break;
            case PlayerInTile:
                break;
            case FromTile:
                break;
        }
        menu.draw(mCanvasWidth, canvas);
        canvas.restore();


    }


    public void updateEngine() {
    }

    public void fling(float velocityX, float velocityY) {
    }

    public void longPress(MotionEvent e) {
        com.TileRummy.Utils.Point mousePoint = new com.TileRummy.Utils.Point(e.getX(), e.getY());
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
        com.TileRummy.Utils.Point p = new com.TileRummy.Utils.Point(e.getX(), e.getY());

        if (menu.collides(p, mCanvasWidth)) {
            menu.doubleTap(p);
        }
        return false;
    }


    public RummySet playerTiles;
    Timer longPressTimer;

    com.TileRummy.Utils.Point mouseLocation = new com.TileRummy.Utils.Point(0, 0);
    com.TileRummy.Utils.Point mouseVector = new com.TileRummy.Utils.Point(0, 0);

    Rectangle _tileArea;

    public void touchDown(MotionEvent event) {

        final com.TileRummy.Utils.Point mousePoint = new com.TileRummy.Utils.Point(event.getX(), event.getY());

        if (menu.collides(mousePoint, mCanvasWidth)) {
            menu.touchDown(mousePoint);
        }

        mouseLocation = new com.TileRummy.Utils.Point(mousePoint);


        if (_tileArea.Collides(mousePoint)) {
            touchDownOnPlayerTiles(mousePoint);
            return;
        }

        sureUpPoint(mousePoint);
        mouseVector.X = 0;
        mouseVector.Y = 0;

        clickMode = ClickMode.Panning;

    }

    private void touchDownOnPlayerTiles(com.TileRummy.Utils.Point mousePoint) {
        RummyTile tile = playerTiles.collideWithTile(mousePoint);
        if (tile == null) return;

        tile.highlighted = true;
        clickMode = ClickMode.DraggingPlayerTile;

        draggingItem = new MovingItem(tile, mousePoint, new com.TileRummy.Utils.Point(mousePoint.X - tile.X, mousePoint.Y - tile.Y));
        draggingState = DraggingTileState.PlayerInHand;
        draggingSet = new RummySet();
        draggingSet.setBucket(tile.Set.Bucket);

        playerTiles.removeTile(tile);
        draggingSet.addTile(draggingItem.tile);


    }

    public void touchUp(MotionEvent event) {
        menu.touchUp(new com.TileRummy.Utils.Point(event.getX(), event.getY()));

        if (longPressTimer != null) longPressTimer.cancel();
        longPressTimer = null;
        if (playerTiles != null) {
            playerTiles.EmptyTileIndex = -1;
            for (int i = 0; i < playerTiles.tiles.size(); i++) {
                playerTiles.tiles.get(i).highlighted = false;

            }
        }
        for (PlayerInformation inf : playerInformation) {
            inf.EmptySet = false;
            for (RummySet set : inf.Sets) {
                set.EmptyTileIndex = -1;
            }
        }
        switch (draggingState) {

            case Empty:
                break;
            case PlayerInTile:
                PlayerInformation pl = getPlayer(draggingItem.getRealPosition());
                if (pl == null) {

                    playerTiles.addTile(draggingItem.tile);


                } else {
                    RummySet set = pl.getSet(draggingItem.getRealPosition());
                    if (set == null) {
                        RummySet rs = new RummySet();
                        rs.setPosition(draggingItem.getRealPosition());
                        pl.addSet(rs);
                        rs.addTile(draggingItem.tile);
                        try {
                            runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddSetToPlayer, pl.name).GenerateMessage());
                            runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, new TileData[]{draggingItem.tile.getTileData()}, pl.name, pl.Sets.size() - 1).GenerateMessage());
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    } else {
                        RummyTile tile = set.collideWithTile(draggingItem.getRealPosition());
                        if (tile == null) {
                            set.addTile(draggingItem.tile);
                            try {
                                runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, new TileData[]{draggingItem.tile.getTileData()}, pl.name, pl.Sets.indexOf(set)).GenerateMessage());
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }

                        } else {
                            int index = set.tiles.indexOf(tile);

                            if (draggingItem.getRealPosition().X > tile.X + RummyTile.Width / 2)
                                index++;

                            set.addTile(index, draggingItem.tile);

                            try {                                                                                                                 //TODO:::Index
                                runner.rummyGameRoom.sendMessage(new RummyGameGameRoomMessage(RummyGameGameRoomMessage.GameRoomMessageType.AddTileToSet, new TileData[]{draggingItem.tile.getTileData()}, pl.name, pl.Sets.indexOf(set)).GenerateMessage());
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }


                break;
            case PlayerInHand:

                draggingItem.tile.highlighted = false;
                com.TileRummy.Utils.Point m = new com.TileRummy.Utils.Point(event.getX(), event.getY());

                if (_tileArea.Collides(m)) {
                    playerTiles.dropTile(draggingItem.tile, m);
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


    }

    private PlayerInformation getPlayer(com.TileRummy.Utils.Point itemPosition) {
        for (PlayerInformation pls : playerInformation) {
            if (pls.collides(itemPosition)) {
                return pls;
            }
        }
        return null;
    }

    public void touchMove(MotionEvent event) {
        com.TileRummy.Utils.Point last = new com.TileRummy.Utils.Point(mouseLocation);
        sureUpPoint(last);

        mouseLocation = new com.TileRummy.Utils.Point(event.getX(), event.getY());
        com.TileRummy.Utils.Point mousePoint = new com.TileRummy.Utils.Point(mouseLocation);
        sureUpPoint(mousePoint);


        switch (draggingState) {

            case Empty:
                break;
            case PlayerInTile:
                PlayerInformation pl = getPlayer(draggingItem.getRealPosition());
                if (pl == null) {

                    for (PlayerInformation inf : playerInformation) {
                        inf.EmptySet = false;
                        for (RummySet set : inf.Sets) {
                            set.EmptyTileIndex = -1;
                        }
                    }
                    playerTiles.EmptyTileIndex = -1;

                } else {

                    for (PlayerInformation inf : playerInformation) {
                        inf.EmptySet = false;
                        for (RummySet set : inf.Sets) {
                            set.EmptyTileIndex = -1;
                        }
                    }
                    playerTiles.EmptyTileIndex = -1;


                    RummySet set = pl.getSet(draggingItem.getRealPosition());
                    if (set == null) {
                        pl.EmptySet = true;
                    } else {
                        set.dropTile(null, draggingItem.getRealPosition());

                    }
                }


                break;
            case PlayerInHand:

                com.TileRummy.Utils.Point m = new com.TileRummy.Utils.Point(event.getX(), event.getY());

                synchronized (mSurfaceHolder) {
                    for (PlayerInformation inf : playerInformation) {
                        inf.EmptySet = false;
                        for (RummySet set : inf.Sets) {
                            set.EmptyTileIndex = -1;
                        }
                    }
                    playerTiles.EmptyTileIndex = -1;

                    if (_tileArea.Collides(m)) {
                        playerTiles.dropTile(null, m);

                    }
                }

                break;
        }


        if (draggingItem != null) {
            {

                synchronized (mSurfaceHolder) {
                    if (_tileArea.Collides(mouseLocation)) {
                        
                        draggingItem.updatePosition( mouseLocation);
                        draggingState = DraggingTileState.PlayerInHand;
                    } else {
                        draggingItem.updatePosition( mousePoint);
                        draggingState = DraggingTileState.PlayerInTile;
                    }
                }
            }

        } else if (draggingSet != null) {

            synchronized (mSurfaceHolder) {
                draggingSet.draggingAround(mousePoint.X, mousePoint.Y);
            }
        } else {
            if (clickMode == clickMode.Panning) {

                synchronized (mSurfaceHolder) {
                    last.Negate(mousePoint);
                    mouseVector.Negate(last);
                    mouseVector.Magnify(1.74f);
                    mouseVector.Limit(30f);
                }
            }
        }

    }

    private void sureUpPoint(com.TileRummy.Utils.Point last) {
        last.Negate(panning);
    }

    public void resize(int width, int height) {

        mCanvasWidth = width;
        mCanvasHeight = height;

        bg = Bitmap.createScaledBitmap(bg, mCanvasWidth, mCanvasHeight, false);

    }


}


