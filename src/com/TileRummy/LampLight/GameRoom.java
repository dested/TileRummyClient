package com.TileRummy.LampLight;

import com.TileRummy.Utils.Horiz;
import MessageParseJunk.WaitingRoomMessage;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.TileRummy.R;
import com.TileRummy.drawables.RummyGame;
import com.TileRummy.Service.GameInformation;
import com.TileRummy.Service.MessageType;
import com.TileRummy.Service.Messager;
import com.TileRummy.Service.MultiRunner;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.HashMap;

public class GameRoom extends Activity {
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((MultiRunner.LocalBinder) service).getService();

			assignUpdater();

			mBoundService.JoinWaitingRoom(WaitingRoomIndex);
			for (Pair<String, String> vm : mBoundService.waitChat) {
				SendMessage(vm.first, vm.second);
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
		}
	};

	private void assignUpdater() {
		mBoundService.Updater = new Messager() {
			@Override
			public void SendUpdate(MessagerType mt, String d) {
				switch (mt) {
				case WaitingRoomUserLoggedOut:
					removeUserFromDisplay(d);
					break;
				case WaitingRoomUserLoggedIn:
					addUserToDisplay(d);
					break;
				case WaitingRoomNewMessage:
					doThings(WaitingRoomMessage.Parse(d));
					break;
				}
			}
		};
	}

	private MultiRunner mBoundService;
	boolean mIsBound;

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isFinishing()) {
			mBoundService.LeaveWaitingRoom();
			setResult(RESULT_OK);
			doUnbindService();
		}

	}

	static int WaitingRoomIndex;

	// UI Widgets
	ListView chatList;
	ListView chatUserList;
	SimpleAdapter chatAdapter;
	SimpleAdapter chatUsersAdapter;
	Button chatSendButton;
	EditText chatInput;

	ArrayList<HashMap<String, Object>> chatListItems = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> chatUserListItems = new ArrayList<HashMap<String, Object>>();

	final Runnable mUpdateFriendsList = new Runnable() {
		public void run() {
			chatUsersAdapter.notifyDataSetChanged();
		}
	};

	Button sudokuSend;

	Horiz horzView;

	InputMethodManager imm;

	GridView grid_main;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		int display_mode = getResources().getConfiguration().orientation;

		if (display_mode == 1) {
			setContentView(R.layout.gameroom_layoutport);
		} else {
			setContentView(R.layout.gameroom_layout);
		}

		if (imm == null)
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		horzView = (Horiz) findViewById(R.id.horzView);

		sudokuSend = (Button) findViewById(R.id.sudokuSend);

		chatAdapter = new SimpleAdapter(this, chatListItems, R.layout.chat_list_item, new String[] { "chatAuthor", "chatText" }, new int[] { R.id.chatAuthor, R.id.chatText });
		chatUsersAdapter = new SimpleAdapter(this, chatUserListItems, R.layout.chat_user_list_item, new String[] { "chatUser" }, new int[] { R.id.chatUser });

		chatList = (ListView) findViewById(R.id.chatList);
		chatList.setTextFilterEnabled(true);
		chatList.setAdapter(chatAdapter);

		chatUserList = (ListView) findViewById(R.id.chatUserList);
		chatUserList.setTextFilterEnabled(true);
		chatUserList.setAdapter(chatUsersAdapter);

		chatInput = (EditText) findViewById(R.id.chatInput);

		chatInput.setOnKeyListener(new TextView.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (event.getAction() != KeyEvent.ACTION_DOWN)
						return true;
					WaitingRoomMessage vm = new WaitingRoomMessage(MessageType.Chat, GameInformation.UserName, chatInput.getText().toString());
					try {
						mBoundService.waitingRoom.sendMessage(vm.GenerateMessage());
						imm.hideSoftInputFromWindow(chatInput.getWindowToken(), 0);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					chatInput.setText("");

					return true;
				}
				return false;
			}
		});

		chatSendButton = (Button) findViewById(R.id.chatSend);
		chatSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				WaitingRoomMessage vm = new WaitingRoomMessage(MessageType.Chat, GameInformation.UserName, chatInput.getText().toString());
				try {
					mBoundService.waitingRoom.sendMessage(vm.GenerateMessage());
					imm.hideSoftInputFromWindow(chatSendButton.getWindowToken(), 0);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chatInput.setText("");
			}
		});

		sudokuSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sudokuSend.setClickable(false);
				sudokuSend.setText("Sudoku Game Starting Shortly!");
				WaitingRoomMessage vm = new WaitingRoomMessage(MessageType.TurnRummyGameStatusOn);
				try {
					mBoundService.waitingRoom.sendMessage(vm.GenerateMessage());
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		doBindService();
	}

	private void SendMessage(String author, String st) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("chatAuthor", author);
		map.put("chatText", st);
		chatListItems.add(map);
		chatAdapter.notifyDataSetChanged();
		chatList.setSelection(chatList.getCount() - 1);
	}

	final Handler mHandler = new Handler();

	private void addUserToDisplay(String username) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("chatUser", username);
		chatUserListItems.add(map);
		mHandler.post(mUpdateFriendsList);
	}

	private boolean removeUserFromDisplay(String username) {
		for (HashMap<String, Object> user : chatUserListItems) {
			if (user.get("chatUser").toString().equalsIgnoreCase(username)) {
				chatUserListItems.remove(user);
				chatUsersAdapter.notifyDataSetChanged();
				return true;
			}
		}

		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		horzView.scrollTo(201, 0);
		if (mBoundService != null) {
			chatUserListItems.clear();
			chatUsersAdapter.notifyDataSetChanged();

			assignUpdater();
			mBoundService.JoinWaitingRoom(WaitingRoomIndex);
		}
	}

	private void doThings(WaitingRoomMessage lastMessage) {
		switch (lastMessage.Type) {
		case Chat:
			// write the entire lastMessage.Argument
			mBoundService.waitChat.add(new Pair<String, String>(lastMessage.Argument, lastMessage.Argument2));
			SendMessage(lastMessage.Argument, lastMessage.Argument2);
			break;
		case JoinRummyGameRoom:
			if (GameInformation.UserName.equals(lastMessage.Argument)) {
				// join new activity, argument will be the roomname
				RummyGame.RoomToJoin = lastMessage.Argument2;
                Intent myIntent = new Intent(getBaseContext(), RummyGame.class);
				SendMessage("*", GameInformation.UserName + " Has Left");

				startActivityForResult(myIntent, 0);

				mBoundService.LeaveWaitingRoom();
				sudokuSend.setClickable(true);
				// SendMessage(StartingRoom.xmpp.getUser().split("@")[0],chatInput.getText().toString());
				sudokuSend.setText("Ready To Sudoku!");
			}

			break;

		}
	}
}
