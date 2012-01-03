package com.TileRummy.Service;

public abstract class Messager {
	public enum MessagerType {
		PushStatusUpdate, AllowLogin, Login, WaitingRoomUserLoggedOut, WaitingRoomUserLoggedIn, WaitingRoomNewMessage, StartSquareGame, StartSudokuGame, FinishSudokuGame, StartMazeGame, FinishMazeGame    ,StartRummyGameGame,FinishRummyGameGame
	}

	public abstract void SendUpdate(MessagerType mt, String d);
}
