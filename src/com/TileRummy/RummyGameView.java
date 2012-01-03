package com.TileRummy;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import com.TileRummy.LampLight.PaintBucket;
import com.TileRummy.Service.MultiRunner;

class RummyGameView extends SurfaceView implements SurfaceHolder.Callback {

    public Vibrator mVibrate;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        int be = event.getActionMasked();
        switch (be) {

            case MotionEvent.ACTION_DOWN:
                // mStatusText.setVisibility(View.VISIBLE);
                // mStatusText.setText("doing Mouse");

                thread.logic.touchDown(event);


                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                thread.logic.touchUp(event);

                break;
            case MotionEvent.ACTION_MOVE:
                thread.logic.touchMove(event);
                break;
        }

        return true; // indicate event was handled
    }


    /**
     * Handle to the application context, used to e.g. fetch Drawables.
     */
    public Context mContext;

    /**
     * Pointer to the text view to display "Paused.." etc.
     */
    public TextView mStatusText;

    /**
     * The thread that actually draws the animation
     */
    private RummyGameThread thread;
    private Handler handler;
    protected MultiRunner runner;

    public RummyGameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());


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

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public RummyGameThread getThread() {
        return thread;
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
      * Callback invoked when the Surface has been created and is ready to be
      * used.
      */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
      * Callback invoked when the Surface has been destroyed and must no longer
      * be touched. WARNING: after this method returns, the Surface/Canvas must
      * never be touched again!
      */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
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

    class RummyGameThread extends Thread {

        public PaintBucket Bucket = new PaintBucket();
        RummyGameLogic logic;


        private boolean mRun = false;

        private SurfaceHolder mSurfaceHolder;


        public RummyGameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mContext = context;

        }


        public void CombineMazePos(Point negative) {
            // MazePos.Combine(negative);

        }


        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {

                        updateEngine();
                        doDraw(c);
                    }
                } catch (Exception ee) {
                    Log.d(ee.toString(), "");

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
            synchronized (mSurfaceHolder) {
                logic.resize(width,height);
            }
        }


        private void doDraw(Canvas canvas) {
                          if(this.logic==null)return;
            this.logic.draw(canvas);
        }

        private void updateEngine() {


        }

        public void StartGame() {
            synchronized (mSurfaceHolder) {
                              logic.gameReady=true;
            }
        }
    }
}

