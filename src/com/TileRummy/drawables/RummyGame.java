package com.TileRummy.drawables;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;
import com.TileRummy.R;
import com.TileRummy.RummyGameView;
import com.TileRummy.Service.Messager;
import com.TileRummy.Service.MultiRunner;


public class RummyGame extends Activity {

    private RummyGameView.RummyGameThread mLunarThread;

    /**
     * A handle to the View in which the game is running.
     */
    private RummyGameView mLunarView;
    private static final String TAG = "HelloFormStuffActivity";

    private ServiceConnection mConnection;
    private MultiRunner mBoundService;
    boolean mIsBound;

    void doBindService() {

        bindService(new Intent(this, MultiRunner.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public static String RoomToJoin;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            mBoundService.LeaveRummyGameGameRoom();
            setResult(RESULT_OK);
        }

        doUnbindService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)                         ;

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBoundService = ((MultiRunner.LocalBinder) service).getService();
                mLunarView.runner = mBoundService;
                mBoundService.Updater = new Messager() {
                    @Override
                    public void SendUpdate(MessagerType mt, String d) {
                        switch (mt) {
                            case StartRummyGameGame:
                                mLunarThread.StartGame();
                                break;
                            case FinishRummyGameGame:
                                finish();
                                break;
                        }
                    }
                };
                if (!mBoundService.inRummyGame()) {
                    mBoundService.JoinRummyGameGameRoom(RoomToJoin);
                }
                mLunarThread.logic = mBoundService.getRummyGameLogic(mLunarView.mContext,mLunarView.thread.mSurfaceHolder,mLunarView.runner);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBoundService = null;
            }
        };


        // tell system to use the layout defined in our XML file
        setContentView(R.layout.rummygame);

        // get handles to the LunarView from XML, and its LunarThread
        mLunarView = (RummyGameView) findViewById(R.id.RummyGame);
        mLunarThread = mLunarView.thread;
        mLunarView.mVibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // mLunarThread.setState(GrameThread.STATE_INIT);


        doBindService();

    }

}