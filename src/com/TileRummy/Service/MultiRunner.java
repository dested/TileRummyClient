package com.TileRummy.Service;

import java.util.*;

import com.TileRummy.Utils.Helping;
import MessageParseJunk.RummyGameGameRoomMessage;
import MessageParseJunk.TileData;
import MessageParseJunk.WaitingRoomMessage;
import android.content.Context;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import com.TileRummy.*;
import com.TileRummy.Utils.TileColor;
import com.TileRummy.drawables.PlayerInformation;
import com.TileRummy.drawables.RummySet;
import com.TileRummy.drawables.RummyTile;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Pair;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: Dested
 * Date: 1/1/12
 * Time: 2:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class MultiRunner extends Service {
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 546874650;
    private boolean isInRummyGame;


    public boolean inRummyGame() {
        return isInRummyGame;
    }

    Random random = new Random();
    RummyGameLogic rgl;

    public RummyGameLogic getRummyGameLogic(Context mcontext, SurfaceHolder msurfaceholder, MultiRunner runner) {

        if (rgl == null) {
            rgl = new RummyGameLogic((Vibrator) getSystemService(Context.VIBRATOR_SERVICE), mcontext, msurfaceholder, runner);

        }
        return rgl;
    }

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MultiRunner getService() {
            return MultiRunner.this;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (Updater != null) {
                Updater.SendUpdate(((MessageUpdate) msg.obj).Status, ((MessageUpdate) msg.obj).StringToUpdate);
            }
        }
    };

    public class MessageUpdate {
        public Messager.MessagerType Status;
        public String StringToUpdate;

        public MessageUpdate(Messager.MessagerType mt, String st) {
            Status = mt;
            StringToUpdate = st;
        }
    }

    public XMPPConnection xmpp;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        try {
            if (xmpp.isConnected()) {
                xmpp.disconnect(new Presence(Presence.Type.unavailable));
            }
            // Tell the user we stopped.
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();

        } catch (Exception gs) {
            Toast.makeText(this, "Disconnected failed", Toast.LENGTH_SHORT).show();

        } finally {
            // setResult(RESULT_OK);
        }

    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        ConnectionConfiguration hara = new ConnectionConfiguration(GameInformation.IP, 5222, GameInformation.getXMPPInfo());
        SmackConfiguration.setPacketReplyTimeout(10000);

        hara.setCompressionEnabled(true);
        xmpp = new XMPPConnection(hara);

        Thread tr = new Thread() {
            public void update(String d) {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.PushStatusUpdate, d);
                mHandler.sendMessage(m);
            }

            public void setAllowLogin() {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.AllowLogin, "");
                mHandler.sendMessage(m);
            }

            public void run() {
                update("Connecting");
                try {
                    if (!xmpp.isConnected())
                        xmpp.connect();
                    update("Connected.");
                    setAllowLogin();
                } catch (XMPPException e) {
                    update(e.toString());
                }

            }

        };
        tr.start();
    }

    public void Login(final String username, final String password) {
        if (!xmpp.isConnected())
            return;

        Thread t = new Thread() {

            public void update(String d) {

                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.PushStatusUpdate, d);
                mHandler.sendMessage(m);
            }

            public void setLogin() {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.Login, "");
                mHandler.sendMessage(m);
            }

            public void run() {
                try {

                    if (xmpp.isAuthenticated()) {
                        xmpp.disconnect(new Presence(Presence.Type.unavailable, "", 1, Presence.Mode.away));

                        update("Connecting");
                        xmpp.connect();
                        update("Connected");
                    }

                    update("Logging In");
                    xmpp.login(username, password);
                    update("Logged In");
                    setLogin();
                } catch (XMPPException e) {
                    update(e.toString());

                    e.printStackTrace();
                }
            }
        };
        t.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    public Messager Updater;
    public MultiUserChat waitingRoom;
    public MultiUserChat rummyGameRoom;


    public ArrayList<Pair<String, String>> waitChat = new ArrayList<Pair<String, String>>();

    public void LeaveWaitingRoom() {
        try {

            waitChat.add(new Pair<String, String>("*", GameInformation.UserName + " Has Left"));
            if (xmpp != null && xmpp.isConnected() && waitingRoom.isJoined())
                waitingRoom.leave();

            if (waitingRoomPing != null)
                waitingRoomPing.cancel();

            waitingRoom.removeParticipantListener(waitPartList);
            waitPartList = null;
            waitingRoom.removeMessageListener(waitMessageList);
            waitMessageList = null;
            waitingRoom = null;
        } catch (Exception gs) {

        }
    }

    PacketListener waitPartList;
    PacketListener waitMessageList;
    Timer waitingRoomPing;

    public void JoinWaitingRoom(final int waitingRoomIndex) {

        final String nam = "waitingroom" + waitingRoomIndex + "@" + GameInformation.getXMPPInfo();
        new Thread(new Runnable() {
            private void update(WaitingRoomMessage rw) {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.WaitingRoomNewMessage, rw.GenerateMessage());
                mHandler.sendMessage(m);
            }

            private void UserJoined(String name) {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.WaitingRoomUserLoggedIn, name);
                mHandler.sendMessage(m);

            }

            private void UserLeft(String name) {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.WaitingRoomUserLoggedOut, name);
                mHandler.sendMessage(m);

            }

            public void run() {
                try {

                    if (xmpp == null) {
                        return;
                    }

                    if (waitingRoom == null) {
                        waitingRoom = new MultiUserChat(xmpp, nam);

                        waitingRoom.join(GameInformation.UserName);
                    }

                    waitingRoomPing = new Timer();
                    waitingRoomPing.scheduleAtFixedRate(new TimerTask() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                waitingRoom.sendMessage(new WaitingRoomMessage(MessageType.Ping).GenerateMessage());
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }

                        }
                    }, 100, 50 * 1000);

                    boolean hasWatcher = false;
                    for (Iterator<String> it = waitingRoom.getOccupants(); it.hasNext(); ) {
                        String vf = it.next();
                        if (vf.toLowerCase().endsWith("sudoker")) {
                            hasWatcher = true;
                            continue;

                        }
                        UserJoined(Helping.GetNameFromLongName(vf));
                    }

                    if (!hasWatcher) {
                        GregorianCalendar gr = new GregorianCalendar();
                        gr.setTime(new Date());
                        waitChat.add(new Pair<String, String>("*", "Watcher is not available"));
                    }
                    if (waitPartList != null) {
                        waitingRoom.removeParticipantListener(waitPartList);
                    }
                    waitingRoom.addParticipantListener(waitPartList = new PacketListener() {
                        @Override
                        public void processPacket(Packet arg0) {
                            if (arg0.getFrom().toLowerCase().endsWith("sudoker")) {
                                return;
                            }
                            Presence pre = (Presence) arg0;

                            switch (pre.getType()) {
                                case available:
                                    UserJoined(Helping.GetNameFromLongName(arg0.getFrom()));
                                    break;
                                case unavailable:
                                    UserLeft(Helping.GetNameFromLongName(arg0.getFrom()));
                                    break;
                            }
                        }
                    });
                    if (waitMessageList != null) {
                        waitingRoom.removeMessageListener(waitMessageList);
                    }
                    waitingRoom.addMessageListener(waitMessageList = new PacketListener() {

                        @Override
                        public void processPacket(Packet message) {
                            String d = ((Message) message).getBody();
                            // charsReiceved += d.length();
                            update(WaitingRoomMessage.Parse(d));
                        }
                    });
                } catch (XMPPException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (Exception er) {

                }
            }

        }).start();

    }

    public String GetStatus() {
        // TODO Auto-generated method stub
        if (xmpp == null) {
            return "Waiting To Connect";
        }
        if (xmpp.isConnected()) {
            return "Connected";
        }
        return "Not Connected";
    }

    PacketListener drawMessageListener;
    PacketListener drawPartListener;
    public static int charsReiceved;


    public void JoinRummyGameGameRoom(final String roomToJoin) {

        if (!xmpp.isConnected()) return;
        isInRummyGame = true;

        new Thread(new Runnable() {
            private void startGame() {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.StartRummyGameGame, "");
                mHandler.sendMessage(m);
            }

            private void finish() {
                android.os.Message m = new android.os.Message();
                m.obj = new MessageUpdate(Messager.MessagerType.FinishRummyGameGame, "");
                mHandler.sendMessage(m);
            }

            public void run() {
                if (!xmpp.isConnected()) return;
                rummyGameRoom = new MultiUserChat(xmpp, roomToJoin + "@" + GameInformation.getXMPPInfo());

                try {
                    rummyGameRoom.join(GameInformation.UserName);
                    for (Iterator<String> it = rummyGameRoom.getOccupants(); it.hasNext(); ) {
                        String vf = it.next();
                        if (vf.endsWith(GameInformation.UserName) || vf.toLowerCase().endsWith("sudoker")) {
                            continue;
                        }
                        //FriendsPlaying fp = new FriendsPlaying();
                        //fp.Name = vf;
                        // rummyGameGame.friends.add(fp);
                    }
                } catch (XMPPException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                rummyGameRoom.addParticipantListener(rummyGamePartListener = new PacketListener() {
                    @Override
                    public void processPacket(Packet arg0) {
                        if (arg0.getFrom().endsWith(GameInformation.UserName) || arg0.getFrom().toLowerCase().endsWith("sudoker")) {
                            return;
                        }

                        Presence pre = (Presence) arg0;

                        switch (pre.getType()) {
                            case available:
                                /*
                              for (FriendsPlaying f : rummyGameGame.friends) {
                                  if (f.Name.equals(arg0.getFrom()))
                                      return;
                              }
                              FriendsPlaying fp = new FriendsPlaying();
                              fp.Name = arg0.getFrom();
                              rummyGameGame.friends.add(fp);  */
                                break;
                            case unavailable:
                                /*
for (FriendsPlaying f : rummyGameGame.friends) {
    if (f.Name.equals(arg0.getFrom())) {
        rummyGameGame.friends.remove(f);
        break;
    }
}                                */

                                break;
                        }
                    }
                });

                rummyGameRoom.addMessageListener(rummyGameMessageListener = new PacketListener() {
                    @Override
                    public void processPacket(Packet message) {
                        if (message.getFrom().toLowerCase().endsWith(GameInformation.UserName))
                            return;
                        String d = ((Message) message).getBody();

                        RummyGameGameRoomMessage gm = RummyGameGameRoomMessage.Parse(d);

                        switch (gm.Type) {
                            case PlayerTiles:
                                if (rgl.playerTiles == null) {
                                    rgl.setPlayerTiles(gm.TileData);
                                }

                                rgl.playerInformation = new ArrayList<PlayerInformation>();


                                break;
                            case AddSetToPlayer:
                                for (PlayerInformation infs : rgl.playerInformation) {
                                    if (infs.name.equals(gm.PlayerName)) {
                                        infs.addSet(new RummySet());
                                    }
                                }
                                break;
                            case AddTileToSet:
                                for (PlayerInformation infs : rgl.playerInformation) {
                                    if (infs.name.equals(gm.PlayerName)) {
                                        infs.Sets.get(gm.SetIndex).addTile(new RummyTile(gm.TileData[0].Number, TileColor.getColor(gm.TileData[0].Color)));
                                    }
                                }
                                break;
                            case SplitSet:
                                for (PlayerInformation infs : rgl.playerInformation) {
                                    if (infs.name.equals(gm.PlayerName)) {
                                        RummySet set = infs.Sets.get(gm.SetIndex);

                                        RummySet newSet = new RummySet();
                                        set.Player.addSet(gm.SetIndex + 1, newSet);

                                        for (int c = set.tiles.size() - 1; c >= gm.MoveToSetIndex; c--) {
                                            RummyTile tile = set.tiles.get(c);
                                            set.removeTile(tile);
                                            newSet.addTile(tile);
                                        }
                                    }
                                }
                                break;
                            case MoveTile:

                                break;
                            case AddTileToPlayer:
                                if (!gm.PlayerName.equals(GameInformation.UserName)) {
                                    return;
                                }
                                if (rgl.playerTiles != null) {
                                    TileData pt = gm.TileData[0];
                                    rgl.playerTiles.addTile(new RummyTile(pt.Number, TileColor.getColor(pt.Color)));
                                }
                                break;
                            case GameStarted:

                                for (String name : gm.PlayerNames) {
                                    PlayerInformation pi;
                                    rgl.playerInformation.add(pi = new PlayerInformation(rgl, name));
                                }

                                startGame();

                                break;
                            case GameFinish:

                                finish();
                                break;

                            case Ping:
                                break;
                            case Leave:
                                break;
                        }

                    }
                });
            }

        }).start();

    }

    PacketListener rummyGameMessageListener;
    PacketListener rummyGamePartListener;
    ParticipantStatusListener rummyGamePartStatusListener;

    public void LeaveRummyGameGameRoom() {
        if (rummyGameRoom != null)
            rummyGameRoom.leave();

        if (rummyGameRoom != null && rummyGameMessageListener != null)
            rummyGameRoom.removeMessageListener(rummyGameMessageListener);
        if (rummyGameRoom != null && rummyGamePartListener != null)
            rummyGameRoom.removeParticipantListener(rummyGamePartListener);
        if (rummyGameRoom != null && rummyGamePartStatusListener != null)
            rummyGameRoom.removeParticipantStatusListener(rummyGamePartStatusListener);
        rummyGameRoom = null;

    }
}
