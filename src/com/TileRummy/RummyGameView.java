package com.TileRummy;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.TileRummy.LampLight.PaintBucket;
import com.TileRummy.Service.MultiRunner;

public class RummyGameView extends SurfaceView implements SurfaceHolder.Callback {

    public Vibrator mVibrate;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!thread.logic.gameReady) return false;
        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        int be = event.getActionMasked();
        switch (be) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                thread.logic.touchUp(event);
                break;
        }
        return true;
    }

    public Context mContext;
    public TextView mStatusText;

    public RummyGameThread thread;
    public MultiRunner runner;
    private Handler handler;

    public RummyGameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new RummyGameThread(holder, context, handler = new Handler() {
            @Override
            public void handleMessage(Message m) {

                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }


    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //    thread.logic. scaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            //  thread.logic.scaleFactor = Math.max(0.1f, Math.min(thread.logic.scaleFactor, 5.0f));

            //thread.logic.panning.Offset(((detector.getPreviousSpan()-detector.getCurrentSpan())*4),((detector.getPreviousSpan()-detector.getCurrentSpan())*4));

            return true;
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public GestureListener() {
        }

        public boolean onSingleTapUp(android.view.MotionEvent e) {


            return false;
        }

        public void onLongPress(android.view.MotionEvent e) {
            thread.logic.longPress(e);


        }

        public boolean onScroll(android.view.MotionEvent e1, android.view.MotionEvent e2, float distanceX, float distanceY) {
            return thread.logic.drag(e1, e2, distanceX, distanceY);
        }

        public boolean onFling(android.view.MotionEvent e1, android.view.MotionEvent e2, float velocityX, float velocityY) {
            thread.logic.fling(velocityX, velocityY);

            return true;
        }

        public boolean onDown(android.view.MotionEvent e) {
            thread.logic.onDown(e);
            return true;
        }

        public boolean onDoubleTap(android.view.MotionEvent e) {
            return thread.logic.doubleTap(e);

        }


        public boolean onSingleTapConfirmed(android.view.MotionEvent e) {
            return thread.logic.singleTap(e);

        }
    }

    public class RummyGameThread extends Thread {


        public RummyGameLogic logic;


        private boolean mRun = false;

        public SurfaceHolder mSurfaceHolder;


        public RummyGameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mSurfaceHolder = surfaceHolder;
            mContext = context;

        }


        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    if (this.logic == null) continue;
                    c = mSurfaceHolder.lockCanvas(null);


                    synchronized (mSurfaceHolder) {
                        updateEngine();
                        doDraw(c);
                    }
                } catch (Exception ee) {
                    Log.d(ee.toString(), Log.getStackTraceString(ee));

                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b) {
            mRun = b;
        }

        public void setSurfaceSize(int width, int height) {
            if (this.logic == null) return;
            synchronized (mSurfaceHolder) {
                logic.resize(width, height);
            }
        }


        private void doDraw(Canvas canvas) {
            this.logic.draw(canvas);
        }

        private void updateEngine() {

            this.logic.updateEngine();
        }

        public void StartGame() {
            synchronized (mSurfaceHolder) {
                logic.gameReady = true;
            }
        }
    }
}

